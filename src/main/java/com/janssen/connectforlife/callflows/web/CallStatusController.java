package com.janssen.connectforlife.callflows.web;

import com.janssen.connectforlife.callflows.domain.Call;
import com.janssen.connectforlife.callflows.domain.types.CallStatus;
import com.janssen.connectforlife.callflows.service.CallService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import java.util.Map;

/**
 * This controller is used typically by a CCXML event handler to update the call status periodically. It can also be used by other IVR providers
 *
 * @author nanakapa
 */
@Controller
public class CallStatusController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CallStatusController.class);

    private static final String XML_OK_RESPONSE = "<?xml version=\"1.0\"?><response>OK</response>";

    private static final String XML_ERROR_RESPONSE = "<?xml version=\"1.0\"?><response>ERROR</response>";

    private static final String PARAM_STATUS = "status";

    private static final String PARAM_REASON = "reason";

    @Autowired
    private CallService callService;

    /**
     * API to update the current call status
     *
     * @param callId callId to lookup by
     * @param params call status parameters
     * @return xml ok response if the call status update is successful, otherwise returns xml error response
     */
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/status/{callId}")
    @ResponseBody
    public String handleStatus(@PathVariable String callId, @RequestParam Map<String, String> params) {
        LOGGER.debug("handleStatus(callId={}, params={}", callId, params);
        Call call = callService.findByCallId(callId);
        if (null == call) {
            return XML_ERROR_RESPONSE;
        }
        // When ever we update the status, we always have to update the reason as well
        CallStatus status = CallStatus.valueOf(params.get(PARAM_STATUS));
        call.setStatus(status);
        call.setStatusText(params.get(PARAM_REASON));
        callService.update(call);
        return XML_OK_RESPONSE;
    }
}
