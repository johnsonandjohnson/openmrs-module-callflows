package com.janssen.connectforlife.callflows.builder;

import com.janssen.connectforlife.callflows.BaseTest;
import com.janssen.connectforlife.callflows.contract.CallFlowResponse;
import com.janssen.connectforlife.callflows.domain.CallFlow;
import com.janssen.connectforlife.callflows.helper.CallFlowHelper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Call Flow Response Builder Test
 *
 * @author bramak09
 */
@RunWith(MockitoJUnitRunner.class)
public class CallFlowResponseBuilderTest extends BaseTest {

    private CallFlowResponse callFlowResponse;

    private CallFlow callFlow;

    @InjectMocks
    private CallFlowResponseBuilder callFlowResponseBuilder = new CallFlowResponseBuilder();

    @Test
    public void shouldBuildCallFlowResponseFromCallFlow() {
        // Given
        callFlow = CallFlowHelper.createMainFlow();
        callFlow.setId(1L);
        // When
        callFlowResponse = callFlowResponseBuilder.createFrom(callFlow);
        // Then
        assertThat(callFlowResponse.getId(), equalTo(callFlow.getId()));
        assertThat(callFlowResponse.getName(), equalTo(callFlow.getName()));
        assertThat(callFlowResponse.getDescription(), equalTo(callFlow.getDescription()));
        assertThat(callFlowResponse.getStatus(), equalTo(callFlow.getStatus().name()));
        assertThat(callFlowResponse.getRaw(), equalTo(callFlow.getRaw()));
    }

}
