/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.callflows;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.BaseModuleActivator;
import org.openmrs.module.callflows.api.event.CallFlowEventListenerFactory;

/**
 * This class contains the logic that is run every time this module is either started or shutdown
 */
public class CallflowsActivator extends BaseModuleActivator {
	
	private static final Log LOGGER = LogFactory.getLog(CallflowsActivator.class);
	
	/**
	 * @see #started()
	 */
	@Override
	public void started() {
		LOGGER.info("Started Callflows");
		CallFlowEventListenerFactory.registerEventListeners();
	}
	
	/**
	 * @see #shutdown()
	 */
	public void shutdown() {
		LOGGER.info("Shutdown Callflows");
		CallFlowEventListenerFactory.unRegisterEventListeners();
	}

	/**
	 +	 * @see #stopped()
	 +	 */
	@Override
	public void stopped() {
		LOGGER.info("Stopped Sms");
		CallFlowEventListenerFactory.unRegisterEventListeners();
	}
}
