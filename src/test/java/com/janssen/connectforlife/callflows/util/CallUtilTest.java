package com.janssen.connectforlife.callflows.util;

import com.janssen.connectforlife.callflows.BaseTest;
import com.janssen.connectforlife.callflows.Constants;
import com.janssen.connectforlife.callflows.domain.Call;
import com.janssen.connectforlife.callflows.domain.CallFlow;
import com.janssen.connectforlife.callflows.domain.flow.Flow;
import com.janssen.connectforlife.callflows.helper.CallFlowHelper;
import com.janssen.connectforlife.callflows.helper.CallHelper;
import com.janssen.connectforlife.callflows.helper.FlowHelper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

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

    @Inject
    private CallUtil callUtil = new CallUtil();

    @Mock
    private HttpServletRequest request;

    private CallFlow mainFlow;

    private Flow flow;

    private Call inboundCall;

    @Before
    public void setUp() throws IOException {
        mainFlow = CallFlowHelper.createMainFlow();

        String raw = TestUtil.loadFile("main_flow.json");
        mainFlow.setRaw(raw);
        flow = FlowHelper.createFlow(raw);

        inboundCall = CallHelper.createInboundCall();
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

}
