package org.openmrs.module.callflows.api.builder;

import org.junit.Test;
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
public class CallFlowResponseBuilderTest extends BaseTest {

    private CallFlowResponse callFlowResponse;

    private CallFlow callFlow;

    @Test
    public void shouldBuildCallFlowResponseFromCallFlow() {
        // Given
        callFlow = CallFlowHelper.createMainFlow();
        callFlow.setId(1);
        // When
        callFlowResponse = CallFlowResponseBuilder.createFrom(callFlow);
        // Then
        assertThat(callFlowResponse.getId(), equalTo(callFlow.getId()));
        assertThat(callFlowResponse.getName(), equalTo(callFlow.getName()));
        assertThat(callFlowResponse.getDescription(), equalTo(callFlow.getDescription()));
        assertThat(callFlowResponse.getStatus(), equalTo(callFlow.getStatus().name()));
        assertThat(callFlowResponse.getRaw(), equalTo(callFlow.getRaw()));
    }

}
