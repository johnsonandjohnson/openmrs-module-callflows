package org.openmrs.module.callflows.api.service.it;

import org.hibernate.HibernateException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.callflows.Constants;
import org.openmrs.module.callflows.api.dao.CallFlowDao;
import org.openmrs.module.callflows.api.domain.CallFlow;
import org.openmrs.module.callflows.api.domain.types.CallFlowStatus;
import org.openmrs.module.callflows.api.exception.CallFlowAlreadyExistsException;
import org.openmrs.module.callflows.api.exception.ValidationException;
import org.openmrs.module.callflows.api.helper.CallFlowHelper;
import org.openmrs.module.callflows.api.service.CallFlowService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static junit.framework.Assert.assertNull;
import static junit.framework.TestCase.assertNotNull;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.openmrs.module.callflows.Constants.CALLFLOW_MAIN_RAW;
import static org.openmrs.module.callflows.Constants.SUPER_USER_ADMIN_DISPLAY_STRING;

/**
 * Call Flow Service Integration Tests
 *
 * @author bramak09
 */
public class CallFlowServiceITTest extends BaseModuleContextSensitiveTest {

    private CallFlow mainFlow;

    private CallFlow mainFlow2;

    private CallFlow mainFlow3;

    private CallFlow nonMainFlow;

    private CallFlow badFlow;

    @Autowired
    private CallFlowService callFlowService;

    @Autowired
    private CallFlowDao callFlowDao;

    @Before
    public void setUp() {
        mainFlow = CallFlowHelper.createMainFlow();
        badFlow = CallFlowHelper.createBadFlow();

        mainFlow2 = CallFlowHelper.createMainFlow();
        mainFlow2.setName("MainFlow2");

        mainFlow3 = CallFlowHelper.createMainFlow();
        mainFlow3.setName("MainFlow3");

        nonMainFlow = CallFlowHelper.createMainFlow();
        nonMainFlow.setName("NonMainFlow");
    }

    @After
    public void tearDown() {
        Context.clearSession();
        callFlowDao.deleteAll();
    }

    @Test
    public void shouldReturnOSGIService() {
        assertNotNull(callFlowService);
    }

    @Test
    public void shouldReturnNewlyCreatedCallFlow() throws CallFlowAlreadyExistsException {
        // When
        CallFlow callFlow = callFlowService.create(mainFlow);

        // Then
        assertNotNull(callFlow);
        assertNotNull(callFlow.getId());
        assertThat(callFlow.getName(), equalTo(mainFlow.getName()));
        assertThat(callFlow.getDescription(), equalTo(mainFlow.getDescription()));
        assertThat(callFlow.getStatus(), equalTo(CallFlowStatus.DRAFT));
        assertThat(callFlow.getRaw(), equalTo(mainFlow.getRaw()));
    }

    @Test(expected = javax.validation.ValidationException.class)
    public void shouldThrowCallFlowAlreadyExistsIfDuplicateCallFlowIsCreated() throws CallFlowAlreadyExistsException {
        // Given
        CallFlow callFlow = callFlowService.create(mainFlow);
        // When a flow with the same name is created again
        mainFlow.setId(null);
        callFlowService.create(mainFlow);

    }

    @Test(expected = ValidationException.class)
    public void shouldThrowIllegalArgumentIfCallFlowWithNonAlphanumericCharactersIsCreated()
            throws CallFlowAlreadyExistsException {
        // Given, When And Then
        CallFlow callFlow = callFlowService.create(badFlow);
    }

    @Test
    public void showSetDefaultCreator()
            throws CallFlowAlreadyExistsException {
        // Given And When
        CallFlow callFlow = callFlowService.create(mainFlow);

        // Then
        assertThat(callFlow.getCreator().getDisplayString(), equalTo(SUPER_USER_ADMIN_DISPLAY_STRING));
    }

    @Test
    public void showSaveLargeRawField()
            throws CallFlowAlreadyExistsException {
        // Given
        int emptySpacesSize = 10000000;
        StringBuilder sb = new StringBuilder();
        sb.append(CALLFLOW_MAIN_RAW);
        sb.append(new String(new char[emptySpacesSize]));
        String raw = sb.toString();
        mainFlow3.setRaw(raw);

        // When
        CallFlow callFlow = callFlowService.create(mainFlow3);

        // Then
        assertThat(callFlow.getRaw().length(), equalTo(raw.length()));
    }

    @Test
    public void shouldReturnNewlyUpdatedCallFlow() throws CallFlowAlreadyExistsException {
        // Given
        CallFlow existingFlow = callFlowService.create(mainFlow);

        // When
        existingFlow.setDescription(Constants.CALLFLOW_MAIN_DESCRIPTION + Constants.UPDATED);
        CallFlow callFlow = callFlowService.update(existingFlow);

        // Then
        assertNotNull(callFlow);
        assertThat(callFlow.getId(), equalTo(existingFlow.getId()));
        assertThat(callFlow.getName(), equalTo(existingFlow.getName()));
        assertThat(callFlow.getDescription(), equalTo(existingFlow.getDescription()));
        assertThat(callFlow.getStatus(), equalTo(existingFlow.getStatus()));
        assertThat(callFlow.getRaw(), equalTo(existingFlow.getRaw()));
    }

    @Test(expected = ValidationException.class)
    public void shouldThrowCallFlowAlreadyExistsIfCallFlowExistsDuringUpdate() throws CallFlowAlreadyExistsException {
        // Given Two flows with name MainFlow and NewMainFlow
        CallFlow flow1 = CallFlowHelper.createMainFlow();
        CallFlow flow2 = CallFlowHelper.createMainFlow();
        flow2.setName("NewMainFlow");
        callFlowService.create(flow1);
        CallFlow flowToUpdate = callFlowService.create(flow2);
        // Used to detach object from the session cache
        Context.evictFromSession(flowToUpdate);
        // When we try to update name of NewMainFlow to MainFlow and update
        flowToUpdate.setName(flow1.getName());
        callFlowService.update(flowToUpdate);

        // Then we expect a exception
    }

    @Test(expected = javax.validation.ValidationException.class)
    public void shouldThrowHibernateExceptionIfNameIsNewButIdIsInvalidDuringUpdate()
            throws HibernateException, CallFlowAlreadyExistsException {
        // Given  flow named MainFlow
        CallFlow flow1 = CallFlowHelper.createMainFlow();
        callFlowService.create(flow1);

        // When we try to update with a brand new name, but a invalid ID
        flow1.setName(Constants.CALLFLOW_MAIN2);
        flow1.setId(-1);
        CallFlow callFlow = callFlowService.update(flow1);

        // Then expect a exception
    }

    @Test(expected = ValidationException.class)
    public void shouldThrowIllegalArgumentIfCallFlowWithNonAlphanumericCharactersIsUsedDuringUpdate()
            throws CallFlowAlreadyExistsException {
        // Given, When And Then
        CallFlow callFlow = callFlowService.update(badFlow);
    }

    @Test
    public void shouldFindCallFlowsForValidSearchTerm() throws CallFlowAlreadyExistsException {
        // Given three call flows that have names MainFlow and MainFlow2 and NonMainFlow
        callFlowService.create(mainFlow);
        callFlowService.create(mainFlow2);
        callFlowService.create(nonMainFlow);

        // When we search for a callflow by the Ma prefix
        List<CallFlow> foundCallflows = callFlowService.findAllByNamePrefix(Constants.CALLFLOW_MAIN_PREFIX);

        // Then we should find the two flows that start with Ma, but not the other one
        assertNotNull(foundCallflows);
        assertThat(foundCallflows.size(), equalTo(2));
        assertThat(foundCallflows.get(0).getName(), equalTo(mainFlow.getName()));
        assertThat(foundCallflows.get(1).getName(), equalTo(mainFlow2.getName()));
    }

    @Test
    public void shouldReturnEmptyListOfCallFlowsIfInvalidSearchTermIsUsed() throws CallFlowAlreadyExistsException {
        // Given three call flows that have names MainFlow and MainFlow2 and NonMainFlow
        callFlowService.create(mainFlow);
        callFlowService.create(mainFlow2);
        callFlowService.create(nonMainFlow);

        // When we search for a callflow by the Xu (invalid) prefix
        List<CallFlow> foundCallflows = callFlowService.findAllByNamePrefix(Constants.CALLFLOW_INVALID_PREFIX);

        // Then we should find no flows
        assertNotNull(foundCallflows);
        assertThat(foundCallflows.size(), equalTo(0));
    }

    @Test
    public void shouldFindCallFlowByValidName() throws CallFlowAlreadyExistsException {
        // Given
        callFlowService.create(mainFlow);

        // When we search for that flow
        CallFlow returnedFlow = callFlowService.findByName(Constants.CALLFLOW_MAIN);

        // Then
        assertNotNull(returnedFlow);
        assertThat(returnedFlow.getName(), equalTo(mainFlow.getName()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentIfAttemptedToFindCallFlowByInvalidName()
            throws CallFlowAlreadyExistsException {
        // Given
        callFlowService.create(mainFlow);

        // When we search for a non existent flow
        CallFlow returnedFlow = callFlowService.findByName(Constants.CALLFLOW_MAIN2);

        // Then
    }

    @Test
    public void shouldDeleteCallFlow() throws CallFlowAlreadyExistsException {
        // Given
        mainFlow = callFlowService.create(mainFlow);

        // When
        callFlowService.delete(mainFlow.getId());

        // Then
        assertNull(callFlowDao.findById(mainFlow.getId()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentIfAttemptedToDeleteCallFlowByInvalidId()
            throws CallFlowAlreadyExistsException {
        // Given
        mainFlow = callFlowService.create(mainFlow);

        // When
        callFlowService.delete(-1);
    }
}
