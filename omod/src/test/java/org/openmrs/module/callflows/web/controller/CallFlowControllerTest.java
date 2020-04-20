package org.openmrs.module.callflows.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openmrs.module.callflows.BaseTest;
import org.openmrs.module.callflows.Constants;
import org.openmrs.module.callflows.api.builder.CallFlowBuilder;
import org.openmrs.module.callflows.api.builder.CallFlowResponseBuilder;
import org.openmrs.module.callflows.api.contract.CallFlowRequest;
import org.openmrs.module.callflows.api.contract.CallFlowResponse;
import org.openmrs.module.callflows.api.contract.SearchResponse;
import org.openmrs.module.callflows.api.domain.CallFlow;
import org.openmrs.module.callflows.api.exception.CallFlowAlreadyExistsException;
import org.openmrs.module.callflows.api.helper.CallFlowContractHelper;
import org.openmrs.module.callflows.api.helper.CallFlowHelper;
import org.openmrs.module.callflows.api.service.CallFlowService;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Call Flow Controller Unit Tests
 *
 * @author bramak09
 */

@RunWith(PowerMockRunner.class)
public class CallFlowControllerTest extends BaseTest {


    private MockMvc mockMvc;

    @InjectMocks
    private CallFlowController callFlowController = new CallFlowController();

    @Mock
    private CallFlowService callFlowService;

    @Mock
    private CallFlowResponseBuilder callFlowResponseBuilder;

    @Mock
    private CallFlowBuilder callFlowBuilder;

    private CallFlow mainFlow;

    private CallFlow badFlow;

    private CallFlow existingMainFlow;

    private CallFlow flow1;

    private CallFlow flow2;

    private List<CallFlow> searchedFlows;

    private CallFlowResponse mainFlowResponse;

    private CallFlowRequest mainFlowRequest;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(callFlowController).build();
        // flows to be created
        mainFlow = CallFlowHelper.createMainFlow();
        badFlow = CallFlowHelper.createBadFlow();
        // flows to be updated
        existingMainFlow = CallFlowHelper.createMainFlow();
        existingMainFlow.setId(1);
        // requests
        mainFlowRequest = CallFlowContractHelper.createMainFlowRequest();
        mainFlowResponse = CallFlowContractHelper.createMainFlowResponse();

        searchedFlows = new ArrayList<>();
        flow1 = CallFlowHelper.createMainFlow();
        flow1.setName("MainFlow");
        flow1.setId(1);
        flow2 = CallFlowHelper.createMainFlow();
        flow2.setName("MainFlow2");
        flow2.setId(2);
        searchedFlows.add(flow1);
        searchedFlows.add(flow2);
    }

    @Test
    public void shouldReturnNewlyCreatedCallflowAsJSON() throws Exception {
        // Given
        given(callFlowBuilder.createFrom(any(CallFlowRequest.class))).willReturn(mainFlow);
        given(callFlowService.create(mainFlow)).willReturn(mainFlow);
        given(callFlowResponseBuilder.createFrom(mainFlow)).willReturn(mainFlowResponse);

        // When and Then
        mockMvc.perform(post("/callflows/flows").contentType(MediaType.APPLICATION_JSON).content(jsonBytes(mainFlowRequest)))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(content().contentType(Constants.APPLICATION_JSON_UTF8))
                .andExpect(content().string(json(mainFlowResponse)));
    }

    @Test
    public void shouldReturnHttpConflictStatusIfDuplicateCallflowIsBeingCreated() throws Exception {
        // Given
        given(callFlowBuilder.createFrom(any(CallFlowRequest.class))).willReturn(mainFlow);
        given(callFlowService.create(mainFlow)).willThrow(new CallFlowAlreadyExistsException("Callflow already exists! "));
        given(callFlowResponseBuilder.createFrom(mainFlow)).willReturn(mainFlowResponse);

        // When and Then
        mockMvc.perform(post("/callflows/flows").contentType(MediaType.APPLICATION_JSON).content(jsonBytes(mainFlowRequest)))
                .andExpect(status().is(HttpStatus.CONFLICT.value()))
                .andExpect(content().contentType(Constants.APPLICATION_JSON_UTF8));
    }

    @Test
    public void shouldReturnHttpBadRequestStatusIfCallflowNameIsNotAlphanumeric() throws Exception {
        // Given
        given(callFlowBuilder.createFrom(any(CallFlowRequest.class))).willReturn(badFlow);
        given(callFlowService.create(badFlow)).willThrow(new IllegalArgumentException("bad Callflow ! "));

        // When and Then
        mockMvc.perform(post("/callflows/flows").contentType(MediaType.APPLICATION_JSON).content(jsonBytes(mainFlowRequest)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(content().contentType(Constants.APPLICATION_JSON_UTF8));
    }

    // Update
    @Test
    public void shouldReturnNewlyUpdatedCallflowAsJSON() throws Exception {
        // Given
        given(callFlowBuilder.createFrom(any(CallFlowRequest.class))).willReturn(existingMainFlow);
        given(callFlowService.update(existingMainFlow)).willReturn(existingMainFlow);
        given(callFlowResponseBuilder.createFrom(mainFlow)).willReturn(mainFlowResponse);

        // When and Then
        mockMvc.perform(put("/callflows/flows/" + existingMainFlow.getId()).contentType(MediaType.APPLICATION_JSON).content(jsonBytes(mainFlowRequest)))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(content().contentType(Constants.APPLICATION_JSON_UTF8))
                .andExpect(content().string(json(mainFlowResponse)));
    }

    @Test
    public void shouldReturnHttpConflictStatusIfCallflowWithSameNameExistsDuringUpdate() throws Exception {
        // Given
        given(callFlowBuilder.createFrom(any(CallFlowRequest.class))).willReturn(existingMainFlow);
        given(callFlowService.update(existingMainFlow)).willThrow(new CallFlowAlreadyExistsException("Callflow already exists! "));

        // When and Then
        mockMvc.perform(put("/callflows/flows/" + existingMainFlow.getId()).contentType(MediaType.APPLICATION_JSON).content(jsonBytes(mainFlowRequest)))
                .andExpect(status().is(HttpStatus.CONFLICT.value()))
                .andExpect(content().contentType(Constants.APPLICATION_JSON_UTF8));
    }

    @Test
    public void shouldReturnHttpBadRequestStatusIfCallflowNameIsNotAlphanumericDuringUpdate() throws Exception {
        // Given
        given(callFlowBuilder.createFrom(any(CallFlowRequest.class))).willReturn(badFlow);
        given(callFlowService.update(badFlow)).willThrow(new IllegalArgumentException("bad Callflow ! "));

        // When and Then
        mockMvc.perform(put("/callflows/flows/1").contentType(MediaType.APPLICATION_JSON).content(jsonBytes(mainFlowRequest)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(content().contentType(Constants.APPLICATION_JSON_UTF8));
    }

    @Test
    public void shouldReturnStatusOKForSuccessfulCallFlowSearches() throws Exception {
        // Given two flows found for a valid search term
        given(callFlowService.findAllByNamePrefix(Constants.CALLFLOW_MAIN_PREFIX)).willReturn(searchedFlows);
        given(callFlowResponseBuilder.createFrom(flow1)).willReturn(CallFlowContractHelper.createFlow1Response());
        given(callFlowResponseBuilder.createFrom(flow2)).willReturn(CallFlowContractHelper.createFlow2Response());

        List<CallFlowResponse> responses = searchedFlows.stream()
                .map(f ->
                        new CallFlowResponse(f.getId(),
                                f.getName(),
                                f.getDescription(),
                                f.getStatus().toString(),
                                f.getRaw()))
                .collect(Collectors.toList());

        mockMvc.perform(get("/callflows/flows")
                .param("lookup", "By Name")
                .param("term", Constants.CALLFLOW_MAIN_PREFIX))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(content().contentType(Constants.APPLICATION_JSON_UTF8))
                .andExpect(content().string(json(new SearchResponse(responses))));
    }

    @Test
    public void shouldReturnStatusOKForUnSuccessfulCallFlowSearches() throws Exception {
        // Given two flows found for a valid search term
        given(callFlowService.findAllByNamePrefix(Constants.CALLFLOW_MAIN_PREFIX)).willReturn(searchedFlows);
        given(callFlowResponseBuilder.createFrom(flow1)).willReturn(CallFlowContractHelper.createFlow1Response());
        given(callFlowResponseBuilder.createFrom(flow2)).willReturn(CallFlowContractHelper.createFlow2Response());

        // When we search for a invalid term , Then
        mockMvc.perform(get("/callflows/flows?lookup=By Name&term=" + Constants.CALLFLOW_INVALID_PREFIX))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(content().contentType(Constants.APPLICATION_JSON_UTF8))
                .andExpect(content().string(json(new SearchResponse(new ArrayList<CallFlow>()))));

    }

    @Test
    public void shouldReturnStatusOKForSuccessfulDelete() throws Exception {
        // Given a valid flow by name mainFlow

        // When we delete by a valid ID , Then
        mockMvc.perform(delete("/callflows/flows/1"))
                .andExpect(status().is(HttpStatus.OK.value()));

    }

    @Test
    public void shouldReturnStatusBadRequestForUnSuccessfulDelete() throws Exception {
        // Given that deleting by a bad id will throw illegal argument
        doThrow(new IllegalArgumentException()).when(callFlowService).delete(-1);

        // When we delete by a invalid ID , Then
        mockMvc.perform(delete("/callflows/flows/-1"))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    }

}
