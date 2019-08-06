package org.openmrs.module.callflows.web.controller;

import org.openmrs.module.callflows.api.domain.Call;
import org.openmrs.module.callflows.api.domain.Constants;
import org.openmrs.module.callflows.api.domain.types.CallStatus;
import org.openmrs.module.callflows.api.service.CallService;
import org.openmrs.module.callflows.api.util.CallUtil;

import org.apache.commons.lang.StringUtils;
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

    private static final String OK_RESPONSE = "";

    private static final String ERROR_RESPONSE = "error";

    private static final String PARAM_STATUS = "status";

    private static final String PARAM_REASON = "reason";

    @Autowired
    private CallService callService;

    @Autowired
    private CallUtil callUtil;

    /**
     * API to update the current call status
     * IVR Providers like voxeo accept a string response for CCXML status handlers.
     * The response is used to typically transition to another section of the CCXML document,
     * and hence this status handler returns a string containing "error" in case of an error
     * Sends out a call status event via the MOTECH event system
     *
     * @param callId callId to lookup by
     * @param params call status parameters
     * @return blank response if the call status update is successful, otherwise returns a string containing error
     */
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/status/{callId}")
    @ResponseBody
    public String handleStatus(@PathVariable String callId, @RequestParam Map<String, String> params) {
        LOGGER.debug("handleStatus(callId={}, params={}", callId, params);
        Call call = callService.findByCallId(callId);
        if (null == call) {
            return ERROR_RESPONSE;
        }
        // When ever we update the status, we always have to update the reason as well
        CallStatus status = CallStatus.valueOf(params.get(PARAM_STATUS));
        call.setStatus(status);

        call.setStatusText(params.get(PARAM_REASON));
        // The Provider specific id and type, if passed then updated for this call record
        if (validateExternalIdToUpdateCall(call.getExternalId(), params.get(Constants.PARAM_EXTERNAL_ID),
                                           params.get(Constants.PARAM_EXTERNAL_TYPE))) {
            call.setExternalId(params.get(Constants.PARAM_EXTERNAL_ID));
            call.setExternalType(params.get(Constants.PARAM_EXTERNAL_TYPE));
        }

        // Update the call record with all the details
        callService.update(call);
        // There is a status change in the call, broadcast that across the system
        callUtil.sendStatusEvent(call);
        return OK_RESPONSE;
    }

    private boolean validateExternalIdToUpdateCall(String dbExtId, String extId, String extType) {
        return StringUtils.isBlank(dbExtId) && (StringUtils.isNotBlank(extId) && StringUtils.isNotBlank(extType));
    }

}
