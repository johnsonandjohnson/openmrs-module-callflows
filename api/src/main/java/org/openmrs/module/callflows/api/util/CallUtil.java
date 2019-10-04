package org.openmrs.module.callflows.api.util;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.exception.VelocityException;
import org.codehaus.jackson.map.ObjectMapper;
import org.openmrs.module.callflows.api.contract.JsonExecutionResponse;
import org.openmrs.module.callflows.api.dao.CallDao;
import org.openmrs.module.callflows.api.domain.Call;
import org.openmrs.module.callflows.api.domain.CallFlow;
import org.openmrs.module.callflows.api.domain.Config;
import org.openmrs.module.callflows.api.domain.Constants;
import org.openmrs.module.callflows.api.domain.Renderer;
import org.openmrs.module.callflows.api.domain.flow.Node;
import org.openmrs.module.callflows.api.domain.flow.UserNode;
import org.openmrs.module.callflows.api.domain.types.CallDirection;
import org.openmrs.module.callflows.api.domain.types.CallStatus;
import org.openmrs.module.callflows.api.event.CallFlowEvent;
import org.openmrs.module.callflows.api.service.CallFlowEventService;
import org.openmrs.module.callflows.api.service.CallFlowSchedulerService;
import org.openmrs.module.callflows.api.task.CallFlowScheduledTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.supercsv.io.CsvMapWriter;
import org.supercsv.prefs.CsvPreference;

import javax.naming.OperationNotSupportedException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.apache.commons.lang.CharEncoding.UTF_8;


/**
 * Collection of utility methods in managing and handling calls
 *
 * @author bramak09
 */
@Component("callUtil")
public class CallUtil {

    private static final String DOT = ".";

    private static final String JSON = "json";

    private static final Log LOGGER = LogFactory.getLog(CallUtil.class);

    private static final String ERROR_RESPONSE_JSON = "{error: true, body: \"%s\"}";

    private static final String ERROR_RESPONSE = "error:%s:%s";

    private static final Charset UTF8_CHARSET = Charset.forName("UTF-8");

    private static final MediaType DEFAULT_MEDIA_TYPE = new MediaType("text", "plain", Charset.forName("UTF-8"));

    private static final MediaType JSON_MEDIA_TYPE = new MediaType("application", "json",
            Charset.forName("UTF-8"));

    private static final String REPLACEMENT_PATTERN = "[%s]";

    private static final String ACTOR_ID = "actorId";

    private static final String ACTOR_TYPE = "actorType";

    private static final String EXTERNAL_ID = "externalId";

    private static final String EXTERNAL_TYPE = "externalType";

    private static final String REF_KEY = "refKey";

    private static final String INTERNAL = "internal";

    private ObjectMapper objectMapper = new ObjectMapper();

    private static final Collection<CallStatus> ACTIVE_OUTBOUND_CALL_STATUSES = Arrays
            .asList(CallStatus.INITIATED, CallStatus.IN_PROGRESS, CallStatus.MOTECH_INITIATED);

    private static final String MESSAGE_KEY = "messageKey";
    private static final String CSV = ".csv";
    private static final String FILE_NAME_INITIALS = "cfl_calls_";

    private final String[] headers = { "id", "actorId", "phone", "actorType", "callId", "direction", "creationDate",
            "callReference", "status", "statusText", "startTime", "endTime" };

    private static final String DATE_TIME_PATTERN_1 = "MM/dd/yyyy HH:mm:ss";

    private static final String DATE_TIME_PATTERN_2 = "yyMMddHHmm";

    @Autowired
    private CallDao callDao;

    @Autowired
    @Qualifier("callflow.schedulerService")
    private CallFlowSchedulerService schedulerService;

    @Autowired
    @Qualifier("callFlow.eventService")
    private CallFlowEventService callFlowEventService;

    @Autowired
    private AuthUtil authUtil;


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
        String actorId;
        String externalId;
        String refKey;
        String keyString;
        for (Object key : context.getKeys()) {
            keyString = (String) key;
            Object val = context.get(keyString);
            // we are not going to capture domain objects or other complex objects, just simple objects
            if (val != null) {
                if (isAllowedToPersist(keyString, val) || ClassUtils.isPrimitiveOrWrapper(val.getClass()) ||
                        ClassUtils.isPrimitiveArray(val.getClass()) ||
                        ClassUtils.isPrimitiveWrapperArray(val.getClass())) {
                    callContext.put((String) key, val);
                }
            }
        }

        Map<String, String> internalCtx = (Map<String, String>) context.get(INTERNAL);
        actorId = internalCtx.get(ACTOR_ID);
        if (null != actorId) {
            call.setActorId(actorId);
            call.setActorType(internalCtx.get(ACTOR_TYPE));
        }

        externalId = internalCtx.get(EXTERNAL_ID);
        if (null != externalId) {
            call.setExternalId(externalId);
            call.setExternalType(internalCtx.get(EXTERNAL_TYPE));
        }

        refKey = internalCtx.get(REF_KEY);
        if (null != refKey) {
            call.setRefKey(refKey);
        }
    }

    private boolean isAllowedToPersist(String key, Object val) {
        return key.equals(INTERNAL) || val instanceof String || val instanceof String[] ||
                isCollectionOfStringOrPrimitive(val);
    }

    private boolean isCollectionOfStringOrPrimitive(Object val) {
        if (val instanceof Collection) {
            Collection collection = (Collection) val;
            for (Object obj : collection) {
                return obj instanceof String || ClassUtils.isPrimitiveOrWrapper(obj.getClass());
            }
        }
        return false;
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
     * Builds a base link to the server until the context path
     *
     * @param request HttpServletRequest
     * @return a partial url string until the context path
     */
    public String buildBaseUrl(HttpServletRequest request) {
        StringBuilder url = new StringBuilder();
        url.append(request.getScheme());
        url.append("://");
        url.append(request.getHeader("Host"));
        url.append(request.getContextPath());
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

            if (JSON.equals(extension)) {
                // This handles both OK and error responses
                return buildJsonResponse(error, content, node, call);
            } else {
                // At this point we have to worry only about other non JSON formats
                // If error
                if (error != null) {
                    return buildError(error, extension);
                }
            }
        } catch (IOException e) {
            LOGGER.error(e.toString(), e);
            return ERROR_RESPONSE_JSON;
        }
        LOGGER.debug(String.format("callId=%s, content=%s", call.getCallId(), content));
        return content;
    }

    /**
     * Builds a error string in a format particular to the extension provided
     *
     * @param error     indicating the exception that led to invoking this method
     * @param extension current extension used in the call
     * @return a error message depending on the extension that was passed
     * @throws IOException if there is a issue in generating the JSON response
     */
    public String buildError(Exception error, String extension) throws IOException {
        if (JSON.equals(extension)) {
            return error.getMessage();
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
     * @param renderer  indicating the mime type to use
     * @return HttpHeaders
     */
    public HttpHeaders buildHeaders(Exception error, String extension, Config config, Renderer renderer) {
        HttpHeaders responseHeaders = new HttpHeaders();
        if (JSON.equals(extension)) {
            responseHeaders.setContentType(JSON_MEDIA_TYPE);
        } else if (null == config || null != error) {
            // If we didn't get any configuration or we have a error, default to plain text
            // cause the renderers and mime types are all part of the config!
            responseHeaders.setContentType(DEFAULT_MEDIA_TYPE);
        } else {
            if (renderer != null) {
                String[] mimeParts = renderer.getMimeType().split("/");
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

    /**
     * Hook before a call can be made. Checks outbound call limit and number of retries before placing the call
     *
     * @param call   object
     * @param config to use
     * @param params that were passed during call initiation
     * @throws OperationNotSupportedException when the call can not be placed
     */
    public void checkCallCanBePlaced(Call call, Config config, Map<String, Object> params)
            throws OperationNotSupportedException {

        Integer retryAttempts = 0;
        LOGGER.debug(String.format("pre-call-hook => call : %s, config: %s, params: %s", call, config, params));

        // Only if outbound call limit is set, we have to worry about no of active calls, retries, etc
        if (config.getOutboundCallLimit() > 0) {
            // Check how many current active calls are there
            Set<CallStatus> callStatusSet = new HashSet<>(ACTIVE_OUTBOUND_CALL_STATUSES);
            long currentOutboundCallCount = callDao
                    .countFindCallsByDirectionAndStatus(CallDirection.OUTGOING, callStatusSet);
            // Do we have enough bandwidth to make this call?
            if (currentOutboundCallCount > config.getOutboundCallLimit()) {
                // No we don't!
                // So let's retry after some time and check again
                // but before that how many retries have we made?
                retryAttempts = null != params.get(Constants.PARAM_RETRY_ATTEMPTS) ?
                        (Integer) params.get(Constants.PARAM_RETRY_ATTEMPTS) : 0;
                if (retryAttempts >= config.getOutboundCallRetryAttempts()) {
                    // We have exceeded anyway , so only one thing to do
                    if (!config.getCallAllowed()) {
                        throw new OperationNotSupportedException("Outbound call limit is exceeded");
                    }
                    // otherwise we allow the call to do even though retries have been exceeded cause the configuration says so!
                } else {
                    // retry after some time
                    params.put(Constants.PARAM_RETRY_ATTEMPTS, retryAttempts + 1);
                    scheduleOutboundCall(call.getCallId(), config, params);
                    // Exception is thrown at this point in time to avoid placing of call directly and
                    // to place the call only at the recall time set for call queuing
                    throw new OperationNotSupportedException("Outbound call limit is exceeded");
                }
            }
        }
    }

    /**
     * Builds a outbound call request for a given phone number and a created call using a specific configuration
     * The configuration's outbound URL is used to place the call, if a test user's URL is not registered
     * If a test user's URL is registered in the IVR configuration, it over-rides the generic configuration URL
     * <p/>
     * The outbound URL can also have placeholders in the form of [variable] and
     * those will be replaced from the parameters available before creating the request
     * Additionally the callId and phone can also be used as part of this syntax as [internal.callId] and [internal.phone]
     * <p/>
     * Note: This method was inspired and adapted from the MOTECH IVR module
     *
     * @param phone  to call
     * @param call   created
     * @param config to use while making the call
     * @param params that are associated with this call
     * @return
     */
    public HttpUriRequest buildOutboundRequest(String phone, Call call, Config config, Map<String, Object> params)
            throws URISyntaxException {
        // parameters that will be used for replacement in the IVR outbound URL
        // Just because all parameters are available doesn't mean that all should be sent
        // Mostly only callId, jumpTo are required
        // Use discretion accordingly while setting the outbound call url in the configuration
        Map<String, Object> completeParams = new HashMap<>();

        completeParams.putAll(params);
        completeParams.put("internal.callId", call.getCallId());
        completeParams.put("internal.jumpTo", call.getStartFlow().getName());

        // The uri can be a test user's URI that can connect to a individual simulator
        // or the IVR provider's actual outbound Uri set globally in the configuration
        // If a test user is set, it will always over-ride the value set in the global configuration
        String uri;
        Map<String, String> testUsersMap = config.getTestUsersMap();

        if (testUsersMap != null && testUsersMap.containsKey(phone)) {
            LOGGER.debug(String.format("TestURL for user, phone = %s, url = %s", phone, testUsersMap.get(phone)));
            uri = mergeUriAndRemoveParams(config.getTestUsersMap().get(phone), completeParams);
        } else {
            uri = mergeUriAndRemoveParams(config.getOutgoingCallUriTemplate(), completeParams);
        }
        LOGGER.debug(String.format("user = %s, uri = %s", phone, uri));

        HttpUriRequest request;
        URIBuilder builder;
        try {
            builder = new URIBuilder(uri);

            if (HttpMethod.GET.name().equals(config.getOutgoingCallMethod())) {
                request = new HttpGet(builder.build());
            } else {
                HttpPost post = new HttpPost(uri);

                for (Map.Entry<String, String> entry : config.getOutgoingCallPostHeadersMap().entrySet()) {
                    // Set headers
                    post.setHeader(entry.getKey(), entry.getValue() == null ? "" : entry.getValue().toString());
                }
                boolean hasAuthRequired = config.getHasAuthRequired();
                LOGGER.debug(String.format("Is authentication required: %s", hasAuthRequired));
                String postParams = config.getOutgoingCallPostParams();

                // this method throws exception if the authentication configurations are not present in he server
                if (hasAuthRequired) {
                    if (isTokenNotValid(config)) {
                        LOGGER.debug("Generating the token for the first time or when it is expired");
                        config.setAuthToken(authUtil.generateToken());
                    }
                    //set authorization token in the header
                    post.setHeader("Authorization", "Bearer " + config.getAuthToken());
                    postParams = mergeUriAndRemoveParams(config.getOutgoingCallPostParams(), completeParams);
                }
                StringEntity entity = new StringEntity(mergeUriAndRemoveParams(postParams, completeParams));
                entity.setContentType(new BasicHeader("Content-Type", "application/x-www-form-urlencoded"));
                post.setEntity(entity);

                request = post;
            }
            LOGGER.debug(String.format("Generated headers: %s, request: %s for call %s",
                request.getAllHeaders(), request.toString(), call.getCallId()));
        } catch (URISyntaxException | UnsupportedEncodingException e) {
            throw new IllegalArgumentException("Unexpected error creating a URI", e);
        }

        LOGGER.debug(String.format("Generated %s for call %s", request.toString(), call.getCallId()));

        return request;
    }

    /**
     * Merges a URI string by replacing params in the form [x] with actual values
     *
     * @param uriTemplate to replace
     * @param params      to use during replace
     * @return a replaced string
     */
    public String mergeUriAndRemoveParams(String uriTemplate, Map<String, Object> params) {
        String mergedURI = uriTemplate;

        Iterator<Map.Entry<String, Object>> it = params.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Object> entry = it.next();
            String placeholder = String.format(REPLACEMENT_PATTERN, entry.getKey());
            if (mergedURI.contains(placeholder)) {
                mergedURI = mergedURI.replace(placeholder, entry.getValue().toString());
                it.remove();
            }
        }
        return mergedURI;
    }

    /**
     * Responsible for sending the call status of a call via the MOTECH event system
     *
     * @param call that is the current call
     */
    public void sendStatusEvent(Call call) {
        Map<String, Object> data = new HashMap<>();

        if (null != call) {
            LOGGER.debug(String.format("Triggering call status changed event for call=%s, status=%s, reason=%s", call.getCallId(),
                         call.getStatus(), call.getStatusText()));
            data.put(Constants.PARAM_CALL_ID, call.getCallId());
            // We are sending status to clients, to whom we shouldn't expose our domain objects
            data.put(Constants.PARAM_STATUS, call.getStatus().name());
            data.put(Constants.PARAM_REASON, call.getStatusText());
            data.put(Constants.PARAM_PARAMS, call.getContext());
            CallFlowEvent statusChangedEvent = new CallFlowEvent(CallFlowEventSubjects.CALLFLOWS_CALL_STATUS, data);
            callFlowEventService.sendEventMessage(statusChangedEvent);
        }
    }

    /**
     * Responsible for sending a call status event for a call that could not be initialized properly
     * The callId would be sent as unknown by this method.
     *
     * @param status to send
     * @param reason to send
     * @param params to send, typically the initial params used to trigger the call needs to be sent back
     */
    public void sendStatusEvent(CallStatus status, String reason, Map<String, Object> params) {
        Map<String, Object> data = new HashMap<>();

        data.put(Constants.PARAM_CALL_ID, "unknown");
        data.put(Constants.PARAM_STATUS, status.name());
        data.put(Constants.PARAM_REASON, reason);
        data.put(Constants.PARAM_PARAMS, params);
        CallFlowEvent statusChangedEvent = new CallFlowEvent(CallFlowEventSubjects.CALLFLOWS_CALL_STATUS, data);
        callFlowEventService.sendEventMessage(statusChangedEvent);
    }

    /**
     * Generate csv report out of the list of calls with pre-configured headers
     *
     * @param currentFilePath absolute path of the file where file will be generated
     * @param calls           list of call to traverse for report generation
     * @throws IOException
     */
    public void generateReports(String currentFilePath, List<Call> calls) throws IOException {

        try (Writer writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(currentFilePath), Charset.forName(UTF_8)))) {
            try (CsvMapWriter csvMapWriter = new CsvMapWriter(writer, CsvPreference.STANDARD_PREFERENCE)) {
                csvMapWriter.writeHeader(headers);
                final LinkedHashMap<String, Object> callMap = new LinkedHashMap<>(headers.length);

                for (Call call : calls) {
                    try {
                        callMap.clear();
                        initializeCallMap(callMap, call);
                        if (null != callMap) {
                            csvMapWriter.write(callMap, headers);
                        }
                    } catch (Exception e) {
                        LOGGER.error(String.format("Exception occurred for call record having id:%s with exception message: %s",
                                     call.getId(), e.getMessage()));
                    }
                }
            }
        }
    }


    public void deleteTempFile(String tempDir, Map<String, String> fileNames) throws IOException {

        for (String fileName : fileNames.keySet()) {
            Files.delete(Paths.get(fileNames.get(fileName)));
        }
        Files.delete(Paths.get(tempDir));
    }

    public String generateFileName(String tempDir, int fileNumber) {

        return new StringBuilder(tempDir).append(File.separator).append(FILE_NAME_INITIALS).append(fileNumber)
                                         .append(CSV).toString();
    }

    public void createZip(HttpServletResponse response, Map<String, String> fileNames) throws IOException {
        int length = 0;
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(response.getOutputStream())) {
            for (String currentFileName : fileNames.keySet()) {
                try (FileInputStream is = new FileInputStream(fileNames.get(currentFileName))) {
                    ZipEntry zipEntry = new ZipEntry(currentFileName + CSV);
                    zipOutputStream.putNextEntry(zipEntry);
                    byte[] b = new byte[1024];
                    while ((length = is.read(b)) != -1) {
                        zipOutputStream.write(b, 0, length);
                    }
                } catch (Exception e) {
                    LOGGER.error(String.format("Error in creating zip entry for file: %s with error message: %s",
                                 currentFileName + CSV, e.getMessage()));
                }
            }
        }
    }

    private void initializeCallMap(Map<String, Object> callMap, Call call) {

        callMap.put(headers[0], call.getId());
        callMap.put(headers[1], call.getActorId());
        callMap.put(headers[2], (null != call.getContext()) ? call.getContext().get("phone") : null);
        callMap.put(headers[3], call.getActorType());
        callMap.put(headers[4], call.getCallId());
        callMap.put(headers[5], call.getDirection());
        callMap.put(headers[6], null != call.getCreationDate() ?
                DateUtil.dateToString(call.getCreationDate(), DATE_TIME_PATTERN_2) : null);

        if (null != call.getContext() && null != call.getContext().get(MESSAGE_KEY) &&
                StringUtils.isNotEmpty(call.getContext().get(MESSAGE_KEY).toString())) {
            try {
                Date dateTimeMessageKey = DateUtil.parse(call.getContext().get(MESSAGE_KEY).toString(), DATE_TIME_PATTERN_1);
                callMap.put(headers[7], DateUtil.dateToString(dateTimeMessageKey, DATE_TIME_PATTERN_2));
            } catch (IllegalArgumentException e) {
                LOGGER.error(String.format(
                        "Invalid input format to parse messageKey to dateTime for calls record having id: %s with messageKey value: %s",
                        call.getId(), call.getContext().get(MESSAGE_KEY)));
                callMap.put(headers[7], null);
            }
        } else {
            callMap.put(headers[7], null);
        }
        callMap.put(headers[8], call.getStatus());
        callMap.put(headers[9], call.getStatusText());
        callMap.put(headers[10], call.getStartTime());
        callMap.put(headers[11], call.getEndTime());
    }

    private String buildJsonResponse(Exception error, String content, Node node, Call call) throws IOException {
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
        if (call != null) {
            response.setCallId(call.getCallId());
        }
        response.setIsError(error != null);
        response.setBody(error == null ? content : buildError(error, JSON));
        return objectMapper.writeValueAsString(response);
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

    private void scheduleOutboundCall(String callId, Config config, Map<String, Object> params) {
        Map<String, Object> eventParams = new HashMap<>();
        // set the flow name to be invoked
        eventParams.put(Constants.PARAM_FLOW_NAME, (String) params.get(Constants.PARAM_FLOW_NAME));
        // set the config name to place the call
        eventParams.put(Constants.PARAM_CONFIG, config.getName());
        //set the params
        eventParams.put(Constants.PARAM_PARAMS, params);
        eventParams.put(Constants.PARAM_HEADERS, config.getOutgoingCallPostHeadersMap());
        eventParams.put(Constants.PARAM_JOB_ID, callId);
        CallFlowEvent event = new CallFlowEvent(CallFlowEventSubjects.CALLFLOWS_INITIATE_CALL, eventParams);
        schedulerService.scheduleRunOnceJob(event, DateUtil.plusSeconds(DateUtil.now(),
                config.getOutboundCallRetrySeconds()), new CallFlowScheduledTask());
    }

    private boolean isTokenNotValid(Config config) {
        return (null == config.getAuthToken() || !authUtil.isTokenValid(config.getAuthToken()));
    }
}
