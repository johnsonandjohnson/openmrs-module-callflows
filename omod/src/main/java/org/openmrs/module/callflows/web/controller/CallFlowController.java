package org.openmrs.module.callflows.web.controller;

import org.openmrs.module.callflows.api.builder.CallFlowBuilder;
import org.openmrs.module.callflows.api.builder.CallFlowResponseBuilder;
import org.openmrs.module.callflows.api.contract.CallFlowRequest;
import org.openmrs.module.callflows.api.contract.CallFlowResponse;
import org.openmrs.module.callflows.api.contract.SearchResponse;
import org.openmrs.module.callflows.api.domain.CallFlow;
import org.openmrs.module.callflows.api.exception.CallFlowAlreadyExistsException;
import org.openmrs.module.callflows.api.service.CallFlowService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import java.util.ArrayList;
import java.util.List;

/**
 * Call Flow Controller to manage call flows
 *
 * @author bramak09
 */
@Controller
public class CallFlowController extends RestController {

    private static final String LOOKUP_BY_NAME_PREFIX = "By Name";

    @Autowired
    private CallFlowService callFlowService;

    @Autowired
    private CallFlowBuilder callFlowBuilder;

    @Autowired
    private CallFlowResponseBuilder callFlowResponseBuilder;

    /**
     * REST API to create a call flow if no duplicate (by name) exists
     *
     * @param callFlowRequest with name, description and raw fields
     * @return a response contract of the created callflow
     * @throws CallFlowAlreadyExistsException if a callflow by the same name already exists in the system
     */
    @RequestMapping(value = "/flows", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public CallFlowResponse createFlow(@RequestBody CallFlowRequest callFlowRequest)
            throws CallFlowAlreadyExistsException {
        CallFlow callFlow = callFlowService.create(callFlowBuilder.createFrom(callFlowRequest));
        return callFlowResponseBuilder.createFrom(callFlow);
    }

    /**
     * REST API to update a call flow
     *
     * @param callFlowRequest with name, description, status and raw fields
     * @return a response contract of the updated callflow
     * @throws CallFlowAlreadyExistsException if a callflow by the same name already exists in the system
     */
    @RequestMapping(value = "/flows/{id}", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public CallFlowResponse updateFlow(@PathVariable Long id, @RequestBody CallFlowRequest callFlowRequest)
            throws CallFlowAlreadyExistsException {
        CallFlow callflow = callFlowBuilder.createFrom(callFlowRequest);
        callflow.setId(id.intValue());
        return callFlowResponseBuilder.createFrom(callFlowService.update(callflow));
    }

    /**
     * REST API to delete a callflow
     *
     * @param id of callflow to delete
     * @throws IllegalArgumentException if id is invalid
     */
    @RequestMapping(value = "/flows/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    public void deleteFlow(@PathVariable Long id) {
        callFlowService.delete(id);
    }

    /**
     * REST API to search for callflows
     *
     * @param lookup a lookup term - currently only "By Name" is supported
     * @param term   a search term that is interpreted by the concerned lookup function invoked
     * @return a list of found callflows
     */
    @RequestMapping(value = "/flows", method = RequestMethod.GET)
    @ResponseBody
    public SearchResponse searchFlowsByName(@RequestParam String lookup, @RequestParam String term) {
        List<CallFlowResponse> callFlowResponses = new ArrayList<>();
        if (LOOKUP_BY_NAME_PREFIX.equals(lookup)) {
            List<CallFlow> callFlows = callFlowService.findAllByNamePrefix(term);
            for (CallFlow callFlow : callFlows) {
                callFlowResponses.add(callFlowResponseBuilder.createFrom(callFlow));
            }
        }
        return new SearchResponse(callFlowResponses);
    }

}
