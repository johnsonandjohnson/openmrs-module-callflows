package com.janssen.connectforlife.callflows.web.it;

import com.janssen.connectforlife.callflows.domain.Call;
import com.janssen.connectforlife.callflows.helper.CallHelper;
import com.janssen.connectforlife.callflows.repository.CallDataService;
import com.janssen.connectforlife.callflows.repository.CallFlowDataService;

import org.motechproject.testing.osgi.container.MotechNativeTestContainerFactory;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.ExamFactory;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;
import javax.inject.Inject;

import static junit.framework.TestCase.assertNotNull;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Call Status Controller Integration Tests
 */

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public class CallStatusControllerBundleIT extends RESTControllerPaxIT {

    @Inject
    private CallDataService callDataService;

    @Inject
    private CallFlowDataService callFlowDataService;

    @Test
    public void shouldReturnStatusOkIfTheCallStatusUpdateIsSuccessful() throws Exception {
        //Given
        Call call = CallHelper.createOutboundCall();
        callDataService.create(call);
        //When
        HttpGet httpGet = buildGetRequest("/callflows/status/" + call.getId().toString(), "status", "ANSWERED", "reason",
                                          "call answered");
        HttpResponse response = getHttpClient().execute(httpGet);

        // Then
        assertNotNull(response);
        assertThat(response.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_OK));
    }

    @Test
    public void shouldReturnStatusOkIfTheCallIDIsNotFoundInDatabase() throws Exception {

        //When
        HttpGet httpGet = buildGetRequest("/callflows/status/22", "status", "ANSWERED", "reason", "call answered");
        HttpResponse response = getHttpClient().execute(httpGet);

        // Then
        assertNotNull(response);
        assertThat(response.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_OK));
    }

    @After
    public void tearDown() {
        callDataService.deleteAll();
        callFlowDataService.deleteAll();
    }
}
