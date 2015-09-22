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
import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertNotNull;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
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

    private List<CallFlow> searchedFlows;

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

        searchedFlows = new ArrayList<>();
        searchedFlows.add(CallFlowHelper.createMainFlow());
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
    public void shouldThrowIllegalArgumentIfCallFlowNameDoesNotHaveAlphanumericCharacters()
            throws CallFlowAlreadyExistsException {
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
    public void shouldThrowCallFlowAlreadyExistsIfCallFlowIsAddedWithDuplicateName()
            throws CallFlowAlreadyExistsException {
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
    public void shouldUpdateCallFlowToNewName() throws CallFlowAlreadyExistsException {

        // Given a Main Flow exists
        given(callFlowDataService.findByName(Constants.CALLFLOW_MAIN)).willReturn(existingMainFlow);

        // And the update on the data service returns the updated callflow
        ArgumentCaptor<CallFlow> callFlowArgumentCaptor = ArgumentCaptor.forClass(CallFlow.class);
        given(callFlowDataService.update(callFlowArgumentCaptor.capture())).willReturn(existingMainFlow);
        given(callFlowDataService.findById(1L)).willReturn(existingMainFlow);

        // When we try to update it
        existingMainFlow.setName(Constants.CALLFLOW_MAIN2);
        CallFlow updatedFlow = callFlowService.update(existingMainFlow);

        // Then
        verify(callFlowDataService, times(1)).findByName(Constants.CALLFLOW_MAIN2);
        verify(callFlowDataService, times(1)).findById(1L);
        verify(callFlowDataService, times(1)).update(existingMainFlow);
        assertNotNull(updatedFlow);
        assertThat(updatedFlow.getName(), equalTo(Constants.CALLFLOW_MAIN2));
        assertThat(updatedFlow.getDescription(), equalTo(existingMainFlow.getDescription()));
        assertThat(updatedFlow.getStatus(), equalTo(existingMainFlow.getStatus()));
        assertThat(updatedFlow.getRaw(), equalTo(existingMainFlow.getRaw()));
    }

    @Test
    public void shouldThrowIllegalArgumentIfCallFlowNameDoesNotHaveAlphanumericCharactersDuringUpdate()
            throws CallFlowAlreadyExistsException {
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
    public void shouldThrowCallFlowAlreadyExistsIfAnotherCallFlowExistsWithDuplicateNameDuringUpdate()
            throws CallFlowAlreadyExistsException {
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

    @Test
    public void shouldFindCallFlowsForValidSearchTerm() {
        // Given
        given(callFlowDataService.findAllByName(Constants.CALLFLOW_MAIN_PREFIX)).willReturn(searchedFlows);

        // When we search for a callflow by a prefix
        List<CallFlow> foundCallflows = callFlowService.findAllByNamePrefix(Constants.CALLFLOW_MAIN_PREFIX);

        // Then
        assertThat(foundCallflows.size(), equalTo(searchedFlows.size()));
        verify(callFlowDataService, times(1)).findAllByName(Constants.CALLFLOW_MAIN_PREFIX);
    }

    @Test
    public void shouldReturnEmptyListOfCallFlowsIfInvalidSearchTermIsUsed() {
        // Given
        given(callFlowDataService.findAllByName(Constants.CALLFLOW_MAIN_PREFIX)).willReturn(searchedFlows);

        // When we search for a callflow by a invalid prefix
        List<CallFlow> foundCallflows = callFlowService.findAllByNamePrefix(Constants.CALLFLOW_INVALID_PREFIX);

        // Then
        assertThat(foundCallflows.size(), equalTo(0));
        verify(callFlowDataService, times(1)).findAllByName(Constants.CALLFLOW_INVALID_PREFIX);
    }

    @Test
    public void shouldFindCallFlowByValidName() {
        // Given
        given(callFlowDataService.findByName(Constants.CALLFLOW_MAIN)).willReturn(mainFlow);

        // When we search for that flow
        CallFlow returnedFlow = callFlowService.findByName(Constants.CALLFLOW_MAIN);

        // Then
        verify(callFlowDataService, times(1)).findByName(Constants.CALLFLOW_MAIN);
        assertNotNull(returnedFlow);
        assertThat(returnedFlow.getName(), equalTo(mainFlow.getName()));
    }

    @Test
    public void shouldThrowIllegalArgumentIfAttemptedToFindCallFlowByInvalidName() {
        expectException(IllegalArgumentException.class);
        // Given
        given(callFlowDataService.findByName(Constants.CALLFLOW_MAIN)).willReturn(mainFlow);

        try {
            // When we search for a non existent flow
            CallFlow returnedFlow = callFlowService.findByName(Constants.CALLFLOW_MAIN2);
        } finally {
            // Then
            verify(callFlowDataService, times(1)).findByName(Constants.CALLFLOW_MAIN2);
        }
    }

    @Test
    public void shouldDeleteCallFlow() {
        // Given
        given(callFlowDataService.findById(1L)).willReturn(mainFlow);

        // When
        callFlowService.delete(1L);

        // Then
        verify(callFlowDataService, times(1)).findById(1L);
        verify(callFlowDataService, times(1)).delete(mainFlow);
    }

    @Test
    public void shouldThrowIllegalArgumentIfAttemptedToDeleteCallFlowWithInvalidId() {
        expectException(IllegalArgumentException.class);
        // Given
        given(callFlowDataService.findById(1L)).willReturn(mainFlow);

        try {
            // When
            callFlowService.delete(-1L);
        } finally {
            // Then
            verify(callFlowDataService, times(1)).findById(-1L);
            verify(callFlowDataService, never()).delete(mainFlow);
        }
    }
}
