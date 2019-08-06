package org.openmrs.module.callflows.api.builder;

import org.openmrs.module.callflows.api.BaseTest;
import org.openmrs.module.callflows.api.contract.CallFlowRequest;
import org.openmrs.module.callflows.api.domain.CallFlow;
import org.openmrs.module.callflows.api.helper.CallFlowContractHelper;

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

    private CallFlowRequest callFlowRequest;

    private CallFlow callFlow;

    @InjectMocks
    private CallFlowBuilder callFlowBuilder = new CallFlowBuilder();

    @Test
    public void shouldBuildCallFlowFromCallFlowRequest() {
        // Given
        callFlowRequest = CallFlowContractHelper.createMainFlowRequest();

        // When
        callFlow = callFlowBuilder.createFrom(callFlowRequest);

        // Then
        assertThat(callFlow.getId(), equalTo(null));
        assertThat(callFlow.getName(), equalTo(callFlowRequest.getName()));
        assertThat(callFlow.getDescription(), equalTo(callFlowRequest.getDescription()));
        assertThat(callFlow.getStatus().name(), equalTo(callFlowRequest.getStatus()));
        assertThat(callFlow.getRaw(), equalTo(callFlowRequest.getRaw()));
    }

}
