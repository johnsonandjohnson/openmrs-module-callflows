package org.openmrs.module.callflows.api.service.it;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Call Flow Integration Test Suite
 *
 * @author bramak09
 */

@RunWith(Suite.class)
@Suite.SuiteClasses({ CallFlowServiceBundleIT.class, SettingsServiceBundleIT.class, CallServiceBundleIT.class,
        FlowServiceBundleIT.class })
public class CallFlowIntegrationTests {
}
