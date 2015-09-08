package com.janssen.connectforlife.callflows.web.it;

import com.janssen.connectforlife.callflows.contract.CallFlowCreationRequest;
import com.janssen.connectforlife.callflows.exception.CallFlowAlreadyExistsException;
import com.janssen.connectforlife.callflows.helper.CallFlowContractHelper;
import com.janssen.connectforlife.callflows.helper.CallFlowHelper;
import com.janssen.connectforlife.callflows.repository.CallFlowDataService;
import com.janssen.connectforlife.callflows.service.CallFlowService;

import org.motechproject.testing.osgi.container.MotechNativeTestContainerFactory;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
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

    private CallFlowCreationRequest mainFlowRequest;
    private CallFlowCreationRequest badFlowRequest;

    @Inject
    private CallFlowDataService callFlowDataService;

    @Inject
    private CallFlowService callFlowService;

    @Before
    public void setUp() {
        mainFlowRequest = CallFlowContractHelper.createMainFlowCreationRequest();
        badFlowRequest = CallFlowContractHelper.createBadFlowCreationRequest();
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
    public void shouldReturnStatusConflictOnCreateDuplicateCallFlow() throws CallFlowAlreadyExistsException, IOException, URISyntaxException, InterruptedException {

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
    public void shouldReturnStatusInternalServerErrorOnCreationOfBadCallFlow() throws CallFlowAlreadyExistsException, IOException, URISyntaxException, InterruptedException {

        // Given
        HttpPost httpPost = buildPostRequest("/callflows/flows", json(badFlowRequest));

        // When
        HttpResponse response = getHttpClient().execute(httpPost);

        // Then
        assertNotNull(response);
        assertThat(response.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_BAD_REQUEST));
    }
}
