package com.janssen.connectforlife.callflows.util;

import com.janssen.connectforlife.callflows.BaseTest;
import com.janssen.connectforlife.callflows.Constants;
import com.janssen.connectforlife.callflows.domain.Call;
import com.janssen.connectforlife.callflows.domain.CallFlow;
import com.janssen.connectforlife.callflows.domain.Config;
import com.janssen.connectforlife.callflows.domain.flow.Flow;
import com.janssen.connectforlife.callflows.helper.CallFlowHelper;
import com.janssen.connectforlife.callflows.helper.CallHelper;
import com.janssen.connectforlife.callflows.helper.ConfigHelper;
import com.janssen.connectforlife.callflows.helper.FlowHelper;

import org.apache.http.client.methods.HttpUriRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.assertNotNull;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;

/**
 * Call Helper Test Cases
 *
 * @author bramak09
 */
@RunWith(MockitoJUnitRunner.class)
public class CallUtilTest extends BaseTest {

    private CallUtil callUtil = new CallUtil();

    @Mock
    private HttpServletRequest request;

    private CallFlow mainFlow;

    private Flow flow;

    private Call inboundCall;

    private Config voxeo;

    private Map<String, Object> callParams;

    @Before
    public void setUp() throws IOException {
        mainFlow = CallFlowHelper.createMainFlow();

        String raw = TestUtil.loadFile("main_flow.json");
        mainFlow.setRaw(raw);
        flow = FlowHelper.createFlow(raw);

        inboundCall = CallHelper.createInboundCall();
        voxeo = ConfigHelper.createConfigs().get(0);

        callParams = new HashMap<>();
        callParams.put(Constants.TEST_PARAM, Constants.TEST_VALUE);
    }

    @Test
    public void shouldBuildFullNodePath() {
        // When
        String out = callUtil.buildFullNodePath(mainFlow, flow.getNodes().get(0));

        // Then
        assertNotNull(out);
        assertThat(out, equalTo("MainFlow.entry"));
    }

    @Test
    public void shouldBuildCallContinuationUrl() {
        // Given a call
        given(request.getScheme()).willReturn("http");
        given(request.getHeader("Host")).willReturn("localhost");
        given(request.getContextPath()).willReturn("/motech-platform-server/modules");

        // When
        String url = callUtil.buildContinuationUrl(request, inboundCall, Constants.CONFIG_RENDERER_VXML);

        // Then
        assertNotNull(url);
        assertThat(url, equalTo("http://localhost/motech-platform-server/modules/callflows/calls/" +
                                        inboundCall.getCallId() + ".vxml"));

    }

    @Test
    public void shouldReplaceParamsInString() {
        // Given
        String url = "http://localhost/context-path?phone=[phone]&callId=[internal.callId]&noreplace=ok";
        Map<String, Object> params = new HashMap<>();
        params.put("phone", "1234567890");
        params.put("internal.callId", "myCallId");

        // When
        String result = callUtil.mergeUriAndRemoveParams(url, params);

        // Then
        assertThat(result, equalTo("http://localhost/context-path?phone=1234567890&callId=myCallId&noreplace=ok"));
    }

    @Test
    public void shouldBuildOutboundRequestUrlForGetRequest() throws URISyntaxException {
        // Given
        voxeo.setOutgoingCallUriTemplate("http://to-outer-space?callId=[internal.callId]&myParam=[testParam]");

        // When
        HttpUriRequest uriRequest = callUtil.buildOutboundRequest("1234567890", inboundCall, voxeo, callParams);

        // Then
        assertNotNull(uriRequest);
        assertThat(uriRequest.getMethod(), equalTo("GET"));
        assertThat(uriRequest.getURI(),
                   equalTo(new URI("http://to-outer-space?callId=" + inboundCall.getCallId() + "&myParam=testValue")));
    }

    @Test
    public void shouldBuildOutboundRequestUrlForPostRequest() throws URISyntaxException {
        // Given
        voxeo.setOutgoingCallUriTemplate("http://to-outer-space?callId=[internal.callId]&myParam=[testParam]");
        voxeo.setOutgoingCallMethod("POST");

        // When
        HttpUriRequest uriRequest = callUtil.buildOutboundRequest("1234567890", inboundCall, voxeo, callParams);

        // Then
        assertNotNull(uriRequest);
        assertThat(uriRequest.getMethod(), equalTo("POST"));
        assertThat(uriRequest.getURI(),
                   equalTo(new URI("http://to-outer-space?callId=" + inboundCall.getCallId() + "&myParam=testValue")));

    }

    @Test
    public void shouldBuildOutboundRequestUrlForTestUserCorrectly() throws URISyntaxException {
        // Given
        voxeo.setOutgoingCallUriTemplate("http://to-outer-space?callId=[internal.callId]&myParam=[testParam]");
        voxeo.getTestUsersMap()
             .put("1234567890", "http://i-should-override-everything?callId=[internal.callId]&myParam=[testParam]");
        voxeo.setOutgoingCallMethod("POST");

        // When
        HttpUriRequest uriRequest = callUtil.buildOutboundRequest("1234567890", inboundCall, voxeo, callParams);

        // Then
        assertNotNull(uriRequest);
        assertThat(uriRequest.getMethod(), equalTo("POST"));
        assertThat(uriRequest.getURI(),
                   equalTo(new URI("http://i-should-override-everything?callId=" + inboundCall.getCallId() +
                                           "&myParam=testValue")));

    }

    @Test
    public void shouldThrowIllegalArgumentIfBadUrlWasProvidedInBuildOutboundRequest() throws URISyntaxException {
        expectException(IllegalArgumentException.class);

        // Given
        voxeo.setOutgoingCallUriTemplate("http://bad-url?% ");

        // When
        callUtil.buildOutboundRequest("1234567890", inboundCall, voxeo, callParams);

    }

    @Test
    public void shouldThrowIllegalArgumentIfBadUrlWasProvidedForTestUserInBuildOutboundRequest() throws URISyntaxException {
        expectException(IllegalArgumentException.class);

        // Given
        voxeo.setOutgoingCallUriTemplate("http://bad-url?% ");
        voxeo.getTestUsersMap()
             .put("1234567890", "http://i-should-override-everything?callId=[internal.callId]&myParam=[testParam]&%");
        voxeo.setOutgoingCallMethod("POST");

        // When
        callUtil.buildOutboundRequest("1234567890", inboundCall, voxeo, callParams);

    }
}
