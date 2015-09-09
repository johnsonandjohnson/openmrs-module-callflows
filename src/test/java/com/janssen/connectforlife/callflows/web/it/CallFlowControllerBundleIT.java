package com.janssen.connectforlife.callflows.web.it;

import com.janssen.connectforlife.callflows.contract.CallFlowRequest;
import com.janssen.connectforlife.callflows.domain.CallFlow;
import com.janssen.connectforlife.callflows.exception.CallFlowAlreadyExistsException;
import com.janssen.connectforlife.callflows.helper.CallFlowContractHelper;
import com.janssen.connectforlife.callflows.helper.CallFlowHelper;
import com.janssen.connectforlife.callflows.repository.CallFlowDataService;
import com.janssen.connectforlife.callflows.service.CallFlowService;

import org.motechproject.testing.osgi.container.MotechNativeTestContainerFactory;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.ExamFactory;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;
import javax.inject.Inject;
import java.io.IOException;
import java.net.URISyntaxException;

import static junit.framework.TestCase.assertNotNull;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * CallFlow Controller Integration Tests
 *
 * @author bramak09
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public class CallFlowControllerBundleIT extends RESTControllerPaxIT {

    private CallFlow mainFlow;

    private CallFlowRequest mainFlowRequest;
    private CallFlowRequest badFlowRequest;

    private CallFlow existingFlow;

    @Inject
    private CallFlowDataService callFlowDataService;

    @Inject
    private CallFlowService callFlowService;

    @Before
    public void setUp() throws CallFlowAlreadyExistsException {
        // for create
        mainFlowRequest = CallFlowContractHelper.createMainFlowRequest();
        badFlowRequest = CallFlowContractHelper.createBadFlowRequest();
        mainFlow = CallFlowHelper.createMainFlow();
    }

    @After
    public void tearDown() {
        callFlowDataService.deleteAll();
    }

    @Test
    public void shouldReturnStatusOkOnCreateCallFlow() throws IOException, URISyntaxException, InterruptedException {

        // Given
        HttpPost httpPost = buildPostRequest("/callflows/flows", json(mainFlowRequest));

        // When
        HttpResponse response = getHttpClient().execute(httpPost);

        // Then
        assertNotNull(response);
        assertThat(response.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_OK));
    }

    @Test
    public void shouldReturnStatusConflictOnCreateDuplicateCallFlow()
            throws CallFlowAlreadyExistsException, IOException, URISyntaxException, InterruptedException {

        // Given
        callFlowService.create(CallFlowHelper.createMainFlow());
        HttpPost httpPost = buildPostRequest("/callflows/flows", json(mainFlowRequest));

        // When
        HttpResponse response = getHttpClient().execute(httpPost);

        // Then
        assertNotNull(response);
        assertThat(response.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_CONFLICT));
    }

    @Test
    public void shouldReturnStatusBadRequestOnCreationOfBadCallFlow()
            throws CallFlowAlreadyExistsException, IOException, URISyntaxException, InterruptedException {

        // Given
        HttpPost httpPost = buildPostRequest("/callflows/flows", json(badFlowRequest));

        // When
        HttpResponse response = getHttpClient().execute(httpPost);

        // Then
        assertNotNull(response);
        assertThat(response.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_BAD_REQUEST));
    }


    @Test
    public void shouldReturnStatusOkOnUpdateCallFlow()
            throws IOException, URISyntaxException, InterruptedException, CallFlowAlreadyExistsException {

        // Given
        existingFlow = callFlowService.create(mainFlow);
        HttpPut httpPut = buildPutRequest("/callflows/flows/" + existingFlow.getId(), json(mainFlowRequest));

        // When
        HttpResponse response = getHttpClient().execute(httpPut);

        // Then
        assertNotNull(response);
        assertThat(response.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_OK));
    }

    @Test
    public void shouldReturnStatusConflictOnUpdateThatLeadsToDuplicateCallFlow()
            throws CallFlowAlreadyExistsException, IOException, URISyntaxException, InterruptedException {

        // Given Two flows with name MainFlow and NewMainFlow
        CallFlow flow1 = CallFlowHelper.createMainFlow();
        CallFlow flow2 = CallFlowHelper.createMainFlow();
        flow2.setName("NewMainFlow");
        callFlowService.create(flow1);
        CallFlow flowToUpdate = callFlowService.create(flow2);

        // When we try to update flow2 to have the same name as flow1
        mainFlowRequest.setName(flow1.getName());
        HttpPut httpPut = buildPutRequest("/callflows/flows/" + flow2.getId(), json(mainFlowRequest));

        // And we execute the request
        HttpResponse response = getHttpClient().execute(httpPut);

        // Then we expect the below
        assertNotNull(response);
        assertThat(response.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_CONFLICT));
    }

    @Test
    public void shouldReturnStatusBadRequestOnUpdateOfBadCallFlow()
            throws CallFlowAlreadyExistsException, IOException, URISyntaxException, InterruptedException {

        // Given
        mainFlowRequest.setName("BadFlow.");
        HttpPut httpPut = buildPutRequest("/callflows/flows/1", json(mainFlowRequest));

        // When
        HttpResponse response = getHttpClient().execute(httpPut);

        // Then
        assertNotNull(response);
        assertThat(response.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_BAD_REQUEST));
    }
}
