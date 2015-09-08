package com.janssen.connectforlife.callflows.builder;

import com.janssen.connectforlife.callflows.BaseTest;
import com.janssen.connectforlife.callflows.contract.CallFlowCreationRequest;
import com.janssen.connectforlife.callflows.domain.CallFlow;
import com.janssen.connectforlife.callflows.helper.CallFlowContractHelper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * CallFlow builder Test Class
 *
 * @author bramak09
 */
@RunWith(MockitoJUnitRunner.class)
public class CallFlowBuilderTest extends BaseTest {

    private CallFlowCreationRequest callFlowCreationRequest;

    private CallFlow callFlow;

    @InjectMocks
    private CallFlowBuilder callFlowBuilder = new CallFlowBuilder();

    @Test
    public void shouldBuildCallFlowFromCallFlowCreationRequest() {
        // Given
        callFlowCreationRequest = CallFlowContractHelper.createMainFlowCreationRequest();

        // When
        callFlow = callFlowBuilder.createFrom(callFlowCreationRequest);

        // Then
        assertThat(callFlow.getId(), equalTo(null));
        assertThat(callFlow.getName(), equalTo(callFlowCreationRequest.getName()));
        assertThat(callFlow.getDescription(), equalTo(callFlowCreationRequest.getDescription()));
        assertThat(callFlow.getStatus().name(), equalTo(callFlowCreationRequest.getStatus()));
        assertThat(callFlow.getRaw(), equalTo(callFlowCreationRequest.getRaw()));
    }

}
