package com.janssen.connectforlife.callflows.web;

import com.janssen.connectforlife.callflows.builder.CallFlowBuilder;
import com.janssen.connectforlife.callflows.builder.CallFlowResponseBuilder;
import com.janssen.connectforlife.callflows.contract.CallFlowCreationRequest;
import com.janssen.connectforlife.callflows.contract.CallFlowResponse;
import com.janssen.connectforlife.callflows.domain.CallFlow;
import com.janssen.connectforlife.callflows.exception.CallFlowAlreadyExistsException;
import com.janssen.connectforlife.callflows.service.CallFlowService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Call Flow Controller to manage call flows
 *
 * @author bramak09
 */
@Controller
public class CallFlowController extends RestController {

    @Autowired
    private CallFlowService callFlowService;

    @Autowired
    private CallFlowBuilder callFlowBuilder;

    @Autowired
    private CallFlowResponseBuilder callFlowResponseBuilder;

    /**
     * REST API to create a call flow if no duplicate (by name) exists
     *
     * @param callFlowCreationRequest with name, description and raw fields
     * @return a response contract of the created callflow
     * @throws CallFlowAlreadyExistsException if a callflow by the same name already exists in the system
     */
    @RequestMapping(value = "/flows", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public CallFlowResponse createFlow(@RequestBody CallFlowCreationRequest callFlowCreationRequest)
            throws CallFlowAlreadyExistsException {
        CallFlow callFlow = callFlowService.create(callFlowBuilder.createFrom(callFlowCreationRequest));
        return callFlowResponseBuilder.createFrom(callFlow);
    }

}
