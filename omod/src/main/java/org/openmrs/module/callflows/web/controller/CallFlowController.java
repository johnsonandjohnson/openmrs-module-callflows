package org.openmrs.module.callflows.web.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiParam;
import org.openmrs.module.callflows.api.builder.CallFlowBuilder;
import org.openmrs.module.callflows.api.builder.CallFlowResponseBuilder;
import org.openmrs.module.callflows.api.contract.CallFlowRequest;
import org.openmrs.module.callflows.api.contract.CallFlowResponse;
import org.openmrs.module.callflows.api.contract.SearchResponse;
import org.openmrs.module.callflows.api.domain.CallFlow;
import org.openmrs.module.callflows.api.exception.CallFlowAlreadyExistsException;
import org.openmrs.module.callflows.api.service.CallFlowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Call Flow Controller to manage call flows
 *
 * @author bramak09
 */
@Api(
    value = "CallFlow",
    tags = {"REST API to manage call flows"})
@Controller
@RequestMapping("/callflows")
public class CallFlowController extends RestController {

  private static final String LOOKUP_BY_NAME_PREFIX = "By Name";

  @Autowired
  @Qualifier("callflows.callFlowService")
  private CallFlowService callFlowService;

  /**
   * REST API to create a call flow if no duplicate (by name) exists
   *
   * @param callFlowRequest with name, description and raw fields
   * @return a response contract of the created callflow
   * @throws CallFlowAlreadyExistsException if a callflow by the same name already exists in the
   *     system
   */
  @ApiOperation(
      value = "Create a call flow",
      notes = "Create a call flow if no duplicate (by name) exists",
      response = CallFlowResponse.class)
  @ApiResponses(
      value = {
        @ApiResponse(
            code = HttpURLConnection.HTTP_OK,
            message = "Successfully created a call flow"),
        @ApiResponse(
            code = HttpURLConnection.HTTP_CONFLICT,
            message = "Call flow with the same name already exists"),
        @ApiResponse(
            code = HttpURLConnection.HTTP_BAD_REQUEST,
            message = "Call flow name is not an alphanumeric")
      })
  @RequestMapping(value = "/flows", method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public CallFlowResponse createFlow(
      @ApiParam(
              name = "callFlowRequest",
              value = "Call flow request with name, description and raw fields")
          @RequestBody
          CallFlowRequest callFlowRequest)
      throws CallFlowAlreadyExistsException {
    CallFlow callFlow = callFlowService.create(CallFlowBuilder.createFrom(callFlowRequest));
    return CallFlowResponseBuilder.createFrom(callFlow);
  }

  /**
   * REST API to update a call flow
   *
   * @param callFlowRequest with name, description, status and raw fields
   * @return a response contract of the updated callflow
   * @throws CallFlowAlreadyExistsException if a callflow by the same name already exists in the
   *     system
   */
  @ApiOperation(
      value = "Update a call flow",
      notes = "Update a call flow",
      response = CallFlowResponse.class)
  @ApiResponses(
      value = {
        @ApiResponse(
            code = HttpURLConnection.HTTP_OK,
            message = "Successfully updated a call flow"),
        @ApiResponse(
            code = HttpURLConnection.HTTP_CONFLICT,
            message = "Call flow with the same name already exists"),
        @ApiResponse(
            code = HttpURLConnection.HTTP_BAD_REQUEST,
            message = "Call flow name is not an alphanumeric")
      })
  @RequestMapping(value = "/flows/{id}", method = RequestMethod.PUT)
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public CallFlowResponse updateFlow(
      @ApiParam(name = "id", value = "Call flow id to update", required = true)
          @PathVariable(value = "id")
          Long id,
      @ApiParam(
              name = "callFlowRequest",
              value = "Call flow request with name, description and raw fields")
          @RequestBody
          CallFlowRequest callFlowRequest)
      throws CallFlowAlreadyExistsException {
    CallFlow callflow = CallFlowBuilder.createFrom(callFlowRequest);
    callflow.setId(id != null ? id.intValue() : null);
    return CallFlowResponseBuilder.createFrom(callFlowService.update(callflow));
  }

  /**
   * REST API to delete a callflow
   *
   * @param id of callflow to delete
   * @throws IllegalArgumentException if id is invalid
   */
  @ApiOperation(value = "Delete a call flow", notes = "Delete a call flow")
  @ApiResponses(
      value = {
        @ApiResponse(
            code = HttpURLConnection.HTTP_OK,
            message = "Successfully deleted a call flow"),
        @ApiResponse(
            code = HttpURLConnection.HTTP_BAD_REQUEST,
            message = "Unsuccessful to delete a call flow")
      })
  @RequestMapping(value = "/flows/{id}", method = RequestMethod.DELETE)
  @ResponseStatus(HttpStatus.OK)
  public void deleteFlow(
      @ApiParam(name = "id", value = "Call flow id to delete", required = true)
          @PathVariable(value = "id")
          Long id) {
    callFlowService.delete(id != null ? id.intValue() : null);
  }

  /**
   * REST API to search for callflows
   *
   * @param lookup a lookup term - currently only "By Name" is supported
   * @param term a search term that is interpreted by the concerned lookup function invoked
   * @return a list of found callflows
   */
  @ApiOperation(
      value = "Search for call flow",
      notes = "Search for call flow",
      response = SearchResponse.class)
  @ApiResponses(
      value = {
        @ApiResponse(
            code = HttpURLConnection.HTTP_OK,
            message = "Successfully searches a call flow")
      })
  @RequestMapping(value = "/flows", method = RequestMethod.GET)
  @ResponseBody
  public SearchResponse searchFlowsByName(
      @ApiParam(name = "lookup", value = "Lookup By Name") @RequestParam(value = "lookup")
          String lookup,
      @ApiParam(
              name = "term",
              value = "A search term that is interpreted by the concerned lookup function")
          @RequestParam(value = "term")
          String term) {
    List<CallFlowResponse> callFlowResponses = new ArrayList<>();
    if (LOOKUP_BY_NAME_PREFIX.equals(lookup)) {
      List<CallFlow> callFlows = callFlowService.findAllByNamePrefix(term);
      for (CallFlow callFlow : callFlows) {
        callFlowResponses.add(CallFlowResponseBuilder.createFrom(callFlow));
      }
    }
    return new SearchResponse(callFlowResponses);
  }
}
