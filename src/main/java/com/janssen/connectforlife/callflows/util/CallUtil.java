package com.janssen.connectforlife.callflows.util;

import com.janssen.connectforlife.callflows.contract.JsonExecutionResponse;
import com.janssen.connectforlife.callflows.domain.Call;
import com.janssen.connectforlife.callflows.domain.CallFlow;
import com.janssen.connectforlife.callflows.domain.Config;
import com.janssen.connectforlife.callflows.domain.Renderer;
import com.janssen.connectforlife.callflows.domain.flow.Node;
import com.janssen.connectforlife.callflows.domain.flow.UserNode;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.exception.VelocityException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * Collection of utility methods in managing and handling calls
 *
 * @author bramak09
 */
@Component("callUtil")
public class CallUtil {

    private static final String DOT = ".";

    private static final String JSON = "json";

    private static final Logger LOGGER = LoggerFactory.getLogger(CallUtil.class);

    private static final String ERROR_RESPONSE_JSON = "{error: true, body: \"%s\"}";

    private static final String ERROR_RESPONSE = "error:%s:%s";

    private static final Charset UTF8_CHARSET = Charset.forName("UTF-8");

    private static final MediaType DEFAULT_MEDIA_TYPE = new MediaType("text", "plain", UTF8_CHARSET);

    private static final MediaType JSON_MEDIA_TYPE = new MediaType("application", JSON, UTF8_CHARSET);

    private ObjectMapper objectMapper = new ObjectMapper();


    /**
     * Merge call properties with a context.
     *
     * @param call    object
     * @param context to load call properties into
     */
    public void mergeCallWithContext(Call call, VelocityContext context) {
        // set back in velocity context from call
        Map<String, Object> callContext = call.getContext();
        for (Map.Entry<String, Object> entry : callContext.entrySet()) {
            context.put(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Merge context information with a call object.
     *
     * @param context that we want to copy
     * @param call    whose properties we want to update
     */
    public void mergeContextWithCall(VelocityContext context, Call call) {
        // capture back into call
        Map<String, Object> callContext = call.getContext();
        for (Object key : context.getKeys()) {
            Object val = context.get((String) key);
            // we are not going to capture domain objects or other complex objects, just simple objects
            if (val != null) {
                if (val instanceof String ||
                        ClassUtils.isPrimitiveOrWrapper(val.getClass()) ||
                        ClassUtils.isPrimitiveArray(val.getClass()) ||
                        ClassUtils.isPrimitiveWrapperArray(val.getClass())) {
                    callContext.put((String) key, val);
                }
            }
        }
    }

    /**
     * Builds a call continuation URL. This is dependent on how the current request is accessed
     *
     * @param request   that is current
     * @param call      that is current
     * @param extension that is one of the renderers or json
     * @return a string containing the absolute path of the continuation URL
     */
    public String buildContinuationUrl(HttpServletRequest request, Call call, String extension) {
        StringBuilder url = new StringBuilder();
        url.append(request.getScheme());
        url.append("://");
        url.append(request.getHeader("Host"));
        url.append(request.getContextPath());
        url.append("/callflows/calls/");
        url.append(call.getCallId());
        url.append(DOT).append(extension);
        return url.toString();
    }

    /**
     * Builds a full node path
     *
     * @param flow to use in the path
     * @param node or step to use in the path
     * @return a string containing the full node path in the format flow.nodeStep
     */
    public String buildFullNodePath(CallFlow flow, Node node) {
        StringBuilder path = new StringBuilder();
        path.append(flow.getName());
        path.append(DOT);
        path.append(node.getStep());
        return path.toString();
    }

    /**
     * Builds a output string for the given call and extension
     *
     * @param error     to indicate any exception
     * @param content   that has been evaluated so far
     * @param node      current node
     * @param call      current call
     * @param extension that is being currently processed
     * @return a string that is dependent on the extension that is being processed
     */
    public String buildOutput(Exception error, String content, Node node, Call call, String extension) {
        try {
            // If error
            if (error != null) {
                return buildError(error, node, extension);
            }

            // If non JSON response required
            if (!JSON.equals(extension)) {
                return content;
            }

            // JSON response required for runner
            JsonExecutionResponse response = new JsonExecutionResponse();
            if (node != null) {
                if (call.getStartFlow().equals(call.getEndFlow())) {
                    response.setNode(node.getStep());
                } else {
                    response.setNode(buildFullNodePath(call.getEndFlow(), node));
                }
                if (node instanceof UserNode) {
                    response.setContinueNode(((UserNode) node).isContinueNode());
                } else {
                    response.setContinueNode(false);
                }
            }
            response.setCallId(call.getCallId());
            response.setBody(content);
            return objectMapper.writeValueAsString(response);
        } catch (IOException e) {
            LOGGER.error(e.toString(), e);
            return ERROR_RESPONSE_JSON;
        }
    }

    /**
     * Builds a error string in JSON format
     *
     * @param node  current
     * @param error indicating the exception that led to invoking this method
     * @param extension that was requested
     * @return a JSON formatted error string
     * @throws IOException if there is a issue in generating the JSON response
     */
    public String buildError(Exception error, Node node, String extension) throws IOException {
        if (JSON.equals(extension)) {
            JsonExecutionResponse response = new JsonExecutionResponse();
            response.setIsError(true);
            response.setBody(error.getMessage());
            if (node != null) {
                response.setNode(node.getStep());
            }
            return objectMapper.writeValueAsString(response);
        } else {
            return buildTextError(error);
        }
    }

    /**
     * Sets the mime-type based on the requested extension.
     * The mime-types can be configured in the configuration object per IVR and set at run time
     *
     * @param error     any exception that happened before building headers
     * @param extension that is being currently processed
     * @param config    to use
     * @return HttpHeaders
     */
    public HttpHeaders buildHeaders(Exception error, String extension, Config config) {
        HttpHeaders responseHeaders = new HttpHeaders();
        if (JSON.equals(extension)) {
            responseHeaders.setContentType(JSON_MEDIA_TYPE);
        } else if (null == config || null != error) {
            // If we didn't get any configuration or we have a error, default to plain text
            // cause the renderers and mime types are all part of the config!
            responseHeaders.setContentType(DEFAULT_MEDIA_TYPE);
        } else {
            Map<String, Renderer> rendererMap = config.getRenderersMap();
            if (rendererMap.get(extension) != null) {
                String[] mimeParts = rendererMap.get(extension).getMimeType().split("/");
                responseHeaders.setContentType(new MediaType(mimeParts[0], mimeParts[1], UTF8_CHARSET));
            } else {
                // default type
                responseHeaders.setContentType(MediaType.TEXT_PLAIN);
            }
        }
        return responseHeaders;
    }

    /**
     * Build a specific Http Status response based on any exception that happened during the call processing
     * Typically returns HttpStatus.OK if error is null, HttpStatus.BAD_REQUEST if IllegalArgumentException happened,
     * HttpStatus.INTERNAL_SERVER_ERROR in all other cases
     *
     * @param error any exception that happened or null if none happened
     * @return HttpStatus
     */
    public HttpStatus buildStatus(Exception error) {
        if (null == error) {
            return HttpStatus.OK;
        } else if (error instanceof IllegalArgumentException) {
            return HttpStatus.BAD_REQUEST;
        }
        // The rest are all 500 errors for now
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    private String buildTextError(Exception error) {
        if (error instanceof VelocityException) {
            return String.format(ERROR_RESPONSE, "SCRIPT", "error in script");
        } else if (error instanceof IllegalArgumentException) {
            return String.format(ERROR_RESPONSE, "BAD_INPUT", error.getMessage());
        } else {
            return String.format(ERROR_RESPONSE, "SYSTEM", "system error");
        }
    }

}


