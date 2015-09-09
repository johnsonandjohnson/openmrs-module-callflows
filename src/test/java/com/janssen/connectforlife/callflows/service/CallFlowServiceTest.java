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

    private CallFlow existingMainFlow;

    @InjectMocks
    private CallFlowService callFlowService = new CallFlowServiceImpl();

    @Mock
    private CallFlowDataService callFlowDataService;

    @Before
    public void setUp() {
        mainFlow = CallFlowHelper.createMainFlow();
        badCallFlow = CallFlowHelper.createBadFlow();

        existingMainFlow = CallFlowHelper.createMainFlow();
        existingMainFlow.setId(1L);
    }

    @Test
    public void shouldCreateCallFlow() throws CallFlowAlreadyExistsException {
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

    @Test
    public void shouldUpdateCallFlow() throws CallFlowAlreadyExistsException {

        // Given a Main Flow exists
        given(callFlowDataService.findByName(Constants.CALLFLOW_MAIN)).willReturn(existingMainFlow);

        // And the update on the data service returns the updated callflow
        ArgumentCaptor<CallFlow> callFlowArgumentCaptor = ArgumentCaptor.forClass(CallFlow.class);
        given(callFlowDataService.update(callFlowArgumentCaptor.capture())).willReturn(existingMainFlow);

        // When we try to update it
        existingMainFlow.setDescription(existingMainFlow.getDescription() + Constants.UPDATED);
        CallFlow updatedFlow = callFlowService.update(existingMainFlow);

        // Then
        verify(callFlowDataService, times(1)).update(existingMainFlow);
        assertNotNull(updatedFlow);
        assertThat(updatedFlow.getName(), equalTo(existingMainFlow.getName()));
        assertThat(updatedFlow.getDescription(), equalTo(existingMainFlow.getDescription()));
        assertThat(updatedFlow.getStatus(), equalTo(existingMainFlow.getStatus()));
        assertThat(updatedFlow.getRaw(), equalTo(existingMainFlow.getRaw()));
    }

    @Test
    public void shouldThrowIllegalArgumentIfCallFlowNameDoesNotHaveAlphanumericCharactersDuringUpdate() throws CallFlowAlreadyExistsException {
        expectException(IllegalArgumentException.class);
        // Given A bad call flow
        try {
            // When
            CallFlow updatedCallFlow = callFlowService.update(badCallFlow);
        } finally {
            // Then since it's a bad name, no need to perform any DB operations
            verifyZeroInteractions(callFlowDataService);
        }
    }

    @Test
    public void shouldThrowIllegalArgumentIfCallFlowNameIsNullDuringUpdate() throws CallFlowAlreadyExistsException {
        expectException(IllegalArgumentException.class);
        // Given
        badCallFlow.setName(null);
        try {
            // When
            CallFlow updatedCallFlow = callFlowService.update(badCallFlow);
        } finally {
            // Then since it's a bad name, no need to perform any DB operations
            verifyZeroInteractions(callFlowDataService);
        }
    }

    @Test
    public void shouldThrowIllegalArgumentIfCallFlowNameIsBlankDuringUpdate() throws CallFlowAlreadyExistsException {
        expectException(IllegalArgumentException.class);
        // Given
        badCallFlow.setName(StringUtils.EMPTY);
        try {
            // When
            CallFlow updatedCallFlow = callFlowService.update(badCallFlow);
        } finally {
            // Then since it's a bad name, no need to perform any DB operations
            verifyZeroInteractions(callFlowDataService);
        }
    }

    @Test
    public void shouldThrowCallFlowAlreadyExistsIfAnotherCallFlowExistsWithDuplicateNameDuringUpdate() throws CallFlowAlreadyExistsException {
        expectException(CallFlowAlreadyExistsException.class);
        // Given
        given(callFlowDataService.findByName(Constants.CALLFLOW_MAIN)).willReturn(existingMainFlow);
        // And that we make the bad call flow good and duplicate by just changing the name
        CallFlow duplicateFlow = badCallFlow;
        duplicateFlow.setName(existingMainFlow.getName());
        try {
            // When we try to update this duplicate call flow
            CallFlow updatedFlow = callFlowService.update(duplicateFlow);
        } finally {
            // Then we expect an exception
            // And the below
            verify(callFlowDataService, times(1)).findByName(mainFlow.getName());
        }
    }
}
