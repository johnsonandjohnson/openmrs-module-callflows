package org.openmrs.module.callflows.api.builder;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import org.openmrs.module.callflows.BaseTest;
import org.openmrs.module.callflows.api.contract.CallFlowResponse;
import org.openmrs.module.callflows.api.domain.CallFlow;
import org.openmrs.module.callflows.api.helper.CallFlowHelper;

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
        callFlow.setId(1);
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
