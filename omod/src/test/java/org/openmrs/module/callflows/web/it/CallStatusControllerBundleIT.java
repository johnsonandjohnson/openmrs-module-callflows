//package org.openmrs.module.callflows.web.it;
//
//import org.openmrs.module.callflows.api.domain.Call;
//import org.openmrs.module.callflows.api.helper.CallHelper;
//import org.openmrs.module.callflows.api.dao.CallDao;
//import org.openmrs.module.callflows.api.dao.CallFlowDao;
//
//import org.motechproject.testing.osgi.container.MotechNativeTestContainerFactory;
//
//import org.apache.commons.httpclient.HttpStatus;
//import org.apache.http.HttpResponse;
//import org.apache.http.client.methods.HttpGet;
//import org.junit.After;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.ops4j.pax.exam.ExamFactory;
//import org.ops4j.pax.exam.junit.PaxExam;
//import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
//import org.ops4j.pax.exam.spi.reactors.PerSuite;
//import javax.inject.Inject;
//
//import static junit.framework.TestCase.assertNotNull;
//import static org.hamcrest.CoreMatchers.equalTo;
//import static org.junit.Assert.assertThat;
//
///**
// * Call Status Controller Integration Tests
// */
//
//@RunWith(PaxExam.class)
//@ExamReactorStrategy(PerSuite.class)
//@ExamFactory(MotechNativeTestContainerFactory.class)
//public class CallStatusControllerBundleIT extends RESTControllerPaxIT {
//
//    @Inject
//    private CallDao callDao;
//
//    @Inject
//    private CallFlowDao callFlowDao;
//
//    @Test
//    public void shouldReturnStatusOkIfTheCallStatusUpdateIsSuccessful() throws Exception {
//        //Given
//        Call call = CallHelper.createOutboundCall();
//        callDao.create(call);
//        //When
//        HttpGet httpGet = buildGetRequest("/callflows/status/" + call.getId().toString(), "status", "ANSWERED", "reason",
//                                          "call answered");
//        HttpResponse response = getHttpClient().execute(httpGet);
//
//        // Then
//        assertNotNull(response);
//        assertThat(response.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_OK));
//    }
//
//    @Test
//    public void shouldReturnStatusOkIfTheCallIDIsNotFoundInDatabase() throws Exception {
//
//        //When
//        HttpGet httpGet = buildGetRequest("/callflows/status/22", "status", "ANSWERED", "reason", "call answered");
//        HttpResponse response = getHttpClient().execute(httpGet);
//
//        // Then
//        assertNotNull(response);
//        assertThat(response.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_OK));
//    }
//
//    @After
//    public void tearDown() {
//        super.tearDown();
//        callDao.deleteAll();
//        callFlowDao.deleteAll();
//    }
//}
