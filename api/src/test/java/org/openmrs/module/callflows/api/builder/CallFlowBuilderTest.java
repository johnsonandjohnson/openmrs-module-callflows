package org.openmrs.module.callflows.api.builder;

import org.junit.Test;
import org.openmrs.module.callflows.BaseTest;
import org.openmrs.module.callflows.api.contract.CallFlowRequest;
import org.openmrs.module.callflows.api.domain.CallFlow;
import org.openmrs.module.callflows.api.helper.CallFlowContractHelper;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * CallFlow builder Test Class
 *
 * @author bramak09
 */
public class CallFlowBuilderTest extends BaseTest {

    private CallFlowRequest callFlowRequest;

    private CallFlow callFlow;

    @Test
    public void shouldBuildCallFlowFromCallFlowRequest() {
        // Given
        callFlowRequest = CallFlowContractHelper.createMainFlowRequest();

        // When
        callFlow = CallFlowBuilder.createFrom(callFlowRequest);

        // Then
        assertThat(callFlow.getId(), equalTo(null));
        assertThat(callFlow.getName(), equalTo(callFlowRequest.getName()));
        assertThat(callFlow.getDescription(), equalTo(callFlowRequest.getDescription()));
        assertThat(callFlow.getStatus().name(), equalTo(callFlowRequest.getStatus()));
        assertThat(callFlow.getRaw(), equalTo(callFlowRequest.getRaw()));
    }

}
