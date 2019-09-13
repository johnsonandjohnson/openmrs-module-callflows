package org.openmrs.module.callflows.web.it;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * CallFlow Web Integration Test Suite
 *
 * @author bramak09
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({ CallFlowControllerBundleIT.class, SettingsControllerBundleIT.class, CallControllerBundleIT.class,
		CallStatusControllerBundleIT.class })
public class CallFlowWebIntegrationTest {

}
