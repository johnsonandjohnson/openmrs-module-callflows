package com.janssen.connectforlife.callflows.web.it;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * CallFlow Web Integration Test Suite
 *
 * @author bramak09
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({ CallFlowControllerBundleIT.class, ConfigControllerBundleIT.class, CallControllerBundleIT.class,
        CallStatusControllerBundleIT.class })
public class CallFlowWebIntegrationTests {
}
