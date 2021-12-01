package org.openmrs.module.callflows.web.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.callflows.api.domain.Call;
import org.openmrs.module.callflows.api.domain.Constants;
import org.openmrs.module.callflows.api.domain.types.CallStatus;
import org.openmrs.module.callflows.api.service.CallService;
import org.openmrs.module.callflows.api.util.CallUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.net.HttpURLConnection;
import java.util.Map;

/**
 * This controller is used typically by a CCXML event handler to update the call status
 * periodically. It can also be used by other IVR providers
 *
 * @author nanakapa
 */
@Api(
    value = "Update the call status",
    tags = {"REST API to update the call status periodically"})
@Controller
@RequestMapping("/callflows")
public class CallStatusController extends RestController {

  private static final Log LOGGER = LogFactory.getLog(CallStatusController.class);

  private static final String OK_RESPONSE = "";

  private static final String ERROR_RESPONSE = "error";

  private static final String PARAM_STATUS = "status";

  private static final String PARAM_REASON = "reason";

  private static final String EXTERNAL_ID_PARAM_NAME = "externalIdParamName";

  @Autowired
  @Qualifier("callflows.callService")
  private CallService callService;

  @Autowired
  @Qualifier("callflows.callUtil")
  private CallUtil callUtil;

  /**
   * API to update the current call status IVR Providers like voxeo accept a string response for
   * CCXML status handlers. The response is used to typically transition to another section of the
   * CCXML document, and hence this status handler returns a string containing "error" in case of an
   * error Sends out a call status event via the OpenMRS event system
   *
   * @param callId callId to lookup by
   * @param params call status parameters
   * @return blank response if the call status update is successful, otherwise returns a string
   *     containing error
   */
  @ApiOperation(value = "Update the current call status", notes = "Update the current call status")
  @ApiResponses(
      value = {
        @ApiResponse(
            code = HttpURLConnection.HTTP_OK,
            message = "Successfully updated the current call status")
      })
  @ResponseStatus(HttpStatus.OK)
  @RequestMapping(value = "/status/{callId}", method = RequestMethod.GET)
  @ResponseBody
  public String handleStatus(
      @ApiParam(name = "callId", value = "CallId to lookup by") @PathVariable(value = "callId")
          String callId,
      @ApiParam(name = "params", value = "Call status parameters") @RequestParam
          Map<String, String> params) {
    LOGGER.debug(String.format("handleStatus(callId=%s, params=%s", callId, params));
    Call call = callService.findByCallId(callId);
    if (null == call) {
      return ERROR_RESPONSE;
    }
    // When ever we update the status, we always have to update the reason as well.
    // convert to upper case in case the status received in lower case
    CallStatus status = CallStatus.valueOf(params.get(PARAM_STATUS).toUpperCase());
    call.setStatus(status);

    String externalId = null;
    if (!StringUtils.isBlank(params.get(EXTERNAL_ID_PARAM_NAME))) {
      externalId = params.get(params.get(EXTERNAL_ID_PARAM_NAME));
      LOGGER.debug(
          String.format(
              "Reading external id value using: %s and value is : %s",
              params.get(EXTERNAL_ID_PARAM_NAME), externalId));
    } else {
      externalId = params.get(Constants.PARAM_EXTERNAL_ID);
    }

    call.setStatusText(params.get(PARAM_REASON));
    // The Provider specific id and type, if passed then updated for this call record
    if (validateExternalIdToUpdateCall(
        call.getExternalId(), externalId, params.get(Constants.PARAM_EXTERNAL_TYPE))) {
      call.setExternalId(externalId);
      call.setExternalType(params.get(Constants.PARAM_EXTERNAL_TYPE));
    }

    // Update the call record with all the details
    callService.update(call);
    // There is a status change in the call, broadcast that across the system
    callUtil.sendStatusEvent(call);
    return OK_RESPONSE;
  }

  private boolean validateExternalIdToUpdateCall(String dbExtId, String extId, String extType) {
    return StringUtils.isBlank(dbExtId)
        && (StringUtils.isNotBlank(extId) && StringUtils.isNotBlank(extType));
  }
}
