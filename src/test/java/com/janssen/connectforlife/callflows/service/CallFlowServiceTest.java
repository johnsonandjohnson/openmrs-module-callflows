package com.janssen.connectforlife.callflows.service;

import com.janssen.connectforlife.callflows.BaseTest;
import com.janssen.connectforlife.callflows.Constants;
import com.janssen.connectforlife.callflows.domain.CallFlow;
import com.janssen.connectforlife.callflows.domain.types.CallFlowStatus;
import com.janssen.connectforlife.callflows.exception.CallFlowAlreadyExistsException;
import com.janssen.connectforlife.callflows.helper.CallFlowHelper;
import com.janssen.connectforlife.callflows.repository.CallFlowDataService;
import com.janssen.connectforlife.callflows.service.impl.CallFlowServiceImpl;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static junit.framework.TestCase.assertNotNull;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

/**
 * Call Flow Service Test
 *
 * @author bramak09
 */
@RunWith(MockitoJUnitRunner.class)
public class CallFlowServiceTest extends BaseTest {

    private CallFlow mainFlow;

    private CallFlow badCallFlow;

    @InjectMocks
    private CallFlowService callFlowService = new CallFlowServiceImpl();

    @Mock
    private CallFlowDataService callFlowDataService;

    @Before
    public void setUp() {
        mainFlow = CallFlowHelper.createMainFlow();
        badCallFlow = CallFlowHelper.createBadFlow();
    }

    @Test
    public void shouldcreateCallFlow() throws CallFlowAlreadyExistsException {
        // Given
        ArgumentCaptor<CallFlow> callFlowArgumentCaptor = ArgumentCaptor.forClass(CallFlow.class);
        given(callFlowDataService.create(callFlowArgumentCaptor.capture())).willReturn(mainFlow);

        // When
        CallFlow createdCallFlow = callFlowService.create(mainFlow);

        // Then
        verify(callFlowDataService, times(1)).create(mainFlow);
        assertNotNull(createdCallFlow);
        assertThat(createdCallFlow.getName(), equalTo(mainFlow.getName()));
        assertThat(createdCallFlow.getDescription(), equalTo(mainFlow.getDescription()));
        assertThat(createdCallFlow.getStatus(), equalTo(CallFlowStatus.DRAFT));
        assertThat(createdCallFlow.getRaw(), equalTo(mainFlow.getRaw()));
    }

    @Test
    public void shouldThrowIllegalArgumentIfCallFlowNameDoesNotHaveAlphanumericCharacters() throws CallFlowAlreadyExistsException {
        expectException(IllegalArgumentException.class);
        // Given
        try {
            // When
            CallFlow createdCallFlow = callFlowService.create(badCallFlow);
        } finally {
            // Then since it's a bad name, no need to perform any DB operations
            verifyZeroInteractions(callFlowDataService);
        }
    }

    @Test
    public void shouldThrowIllegalArgumentIfCallFlowNameIsNull() throws CallFlowAlreadyExistsException {
        expectException(IllegalArgumentException.class);
        // Given
        badCallFlow.setName(null);
        try {
            // When
            CallFlow createdCallFlow = callFlowService.create(badCallFlow);
        } finally {
            // Then since it's a bad name, no need to perform any DB operations
            verifyZeroInteractions(callFlowDataService);
        }
    }

    @Test
    public void shouldThrowIllegalArgumentIfCallFlowNameIsBlank() throws CallFlowAlreadyExistsException {
        expectException(IllegalArgumentException.class);
        // Given
        badCallFlow.setName(StringUtils.EMPTY);
        try {
            // When
            CallFlow createdCallFlow = callFlowService.create(badCallFlow);
        } finally {
            // Then since it's a bad name, no need to perform any DB operations
            verifyZeroInteractions(callFlowDataService);
        }
    }

    @Test
    public void shouldThrowCallFlowAlreadyExistsIfCallFlowIsAddedWithDuplicateName() throws CallFlowAlreadyExistsException {
        expectException(CallFlowAlreadyExistsException.class);
        // Given
        given(callFlowDataService.findByName(Constants.CALLFLOW_MAIN)).willReturn(mainFlow);
        try {
            // When
            CallFlow duplicateFlow = callFlowService.create(mainFlow);
        } finally {
            // Then
            verify(callFlowDataService, times(1)).findByName(mainFlow.getName());
        }
    }

}
