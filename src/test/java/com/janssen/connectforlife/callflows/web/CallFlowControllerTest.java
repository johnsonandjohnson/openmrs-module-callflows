package com.janssen.connectforlife.callflows.web;

import com.janssen.connectforlife.callflows.BaseTest;
import com.janssen.connectforlife.callflows.Constants;
import com.janssen.connectforlife.callflows.builder.CallFlowBuilder;
import com.janssen.connectforlife.callflows.builder.CallFlowResponseBuilder;
import com.janssen.connectforlife.callflows.contract.CallFlowRequest;
import com.janssen.connectforlife.callflows.contract.CallFlowResponse;
import com.janssen.connectforlife.callflows.domain.CallFlow;
import com.janssen.connectforlife.callflows.exception.CallFlowAlreadyExistsException;
import com.janssen.connectforlife.callflows.helper.CallFlowContractHelper;
import com.janssen.connectforlife.callflows.helper.CallFlowHelper;
import com.janssen.connectforlife.callflows.service.CallFlowService;

import org.motechproject.mds.util.SecurityUtil;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.server.MockMvc;
import org.springframework.test.web.server.setup.MockMvcBuilders;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;

/**
 * Call Flow Controller Unit Tests
 *
 * @author bramak09
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest(SecurityUtil.class)
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

    private CallFlowResponse mainFlowResponse;

    private CallFlowRequest mainFlowRequest;

    @Before
    public void setUp() {
        PowerMockito.mockStatic(SecurityUtil.class);
        mockMvc = MockMvcBuilders.standaloneSetup(callFlowController).build();
        // flows to be created
        mainFlow = CallFlowHelper.createMainFlow();
        badFlow = CallFlowHelper.createBadFlow();
        // flows to be updated
        existingMainFlow = CallFlowHelper.createMainFlow();
        existingMainFlow.setId(1L);
        // requests
        mainFlowRequest = CallFlowContractHelper.createMainFlowRequest();
        mainFlowResponse = CallFlowContractHelper.createMainFlowResponse();

    }

    @Test
    public void shouldReturnNewlyCreatedCallflowAsJSON() throws Exception {
        // Given
        given(callFlowBuilder.createFrom(any(CallFlowRequest.class))).willReturn(mainFlow);
        given(callFlowService.create(mainFlow)).willReturn(mainFlow);
        given(callFlowResponseBuilder.createFrom(mainFlow)).willReturn(mainFlowResponse);

        // When and Then
        mockMvc.perform(post("/flows").contentType(MediaType.APPLICATION_JSON).body(jsonBytes(mainFlowRequest)))
               .andExpect(status().is(HttpStatus.OK.value()))
               .andExpect(content().type(Constants.APPLICATION_JSON_UTF8))
               .andExpect(content().string(json(mainFlowResponse)));
    }

    @Test
    public void shouldReturnHttpConflictStatusIfDuplicateCallflowIsBeingCreated() throws Exception {
        // Given
        given(callFlowBuilder.createFrom(any(CallFlowRequest.class))).willReturn(mainFlow);
        given(callFlowService.create(mainFlow)).willThrow(new CallFlowAlreadyExistsException("Callflow already exists! "));
        given(callFlowResponseBuilder.createFrom(mainFlow)).willReturn(mainFlowResponse);

        // When and Then
        mockMvc.perform(post("/flows").contentType(MediaType.APPLICATION_JSON).body(jsonBytes(mainFlowRequest)))
               .andExpect(status().is(HttpStatus.CONFLICT.value()))
               .andExpect(content().type(Constants.APPLICATION_JSON_UTF8));
    }

    @Test
    public void shouldReturnHttpBadRequestStatusIfCallflowNameIsNotAlphanumeric() throws Exception {
        // Given
        given(callFlowBuilder.createFrom(any(CallFlowRequest.class))).willReturn(badFlow);
        given(callFlowService.create(badFlow)).willThrow(new IllegalArgumentException("bad Callflow ! "));

        // When and Then
        mockMvc.perform(post("/flows").contentType(MediaType.APPLICATION_JSON).body(jsonBytes(mainFlowRequest)))
               .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
               .andExpect(content().type(Constants.APPLICATION_JSON_UTF8));
    }

    // Update
    @Test
    public void shouldReturnNewlyUpdatedCallflowAsJSON() throws Exception {
        // Given
        given(callFlowBuilder.createFrom(any(CallFlowRequest.class))).willReturn(existingMainFlow);
        given(callFlowService.update(existingMainFlow)).willReturn(existingMainFlow);
        given(callFlowResponseBuilder.createFrom(mainFlow)).willReturn(mainFlowResponse);

        // When and Then
        mockMvc.perform(put("/flows/" + existingMainFlow.getId()).contentType(MediaType.APPLICATION_JSON).body(jsonBytes(mainFlowRequest)))
               .andExpect(status().is(HttpStatus.OK.value()))
               .andExpect(content().type(Constants.APPLICATION_JSON_UTF8))
               .andExpect(content().string(json(mainFlowResponse)));
    }

    @Test
    public void shouldReturnHttpConflictStatusIfCallflowWithSameNameExistsDuringUpdate() throws Exception {
        // Given
        given(callFlowBuilder.createFrom(any(CallFlowRequest.class))).willReturn(existingMainFlow);
        given(callFlowService.update(existingMainFlow)).willThrow(new CallFlowAlreadyExistsException("Callflow already exists! "));

        // When and Then
        mockMvc.perform(put("/flows/" + existingMainFlow.getId()).contentType(MediaType.APPLICATION_JSON).body(jsonBytes(mainFlowRequest)))
               .andExpect(status().is(HttpStatus.CONFLICT.value()))
               .andExpect(content().type(Constants.APPLICATION_JSON_UTF8));
    }

    @Test
    public void shouldReturnHttpBadRequestStatusIfCallflowNameIsNotAlphanumericDuringUpdate() throws Exception {
        // Given
        given(callFlowBuilder.createFrom(any(CallFlowRequest.class))).willReturn(badFlow);
        given(callFlowService.update(badFlow)).willThrow(new IllegalArgumentException("bad Callflow ! "));

        // When and Then
        mockMvc.perform(put("/flows/1").contentType(MediaType.APPLICATION_JSON).body(jsonBytes(mainFlowRequest)))
               .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
               .andExpect(content().type(Constants.APPLICATION_JSON_UTF8));
    }
}
