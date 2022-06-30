/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.callflows.api.event;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.event.Event;

import java.util.List;

/**
 * CallFlow Event Listener Factory
 */
public final class CallFlowEventListenerFactory {

    private static final Log LOGGER = LogFactory.getLog(CallFlowEventListenerFactory.class);

    /**
     * Register the event listeners
     */
    public static void registerEventListeners() {
        List<AbstractCallFlowEventListener> eventComponents = Context.getRegisteredComponents(AbstractCallFlowEventListener.class);
        for (AbstractCallFlowEventListener eventListener : eventComponents) {
            subscribeListener(eventListener);
        }
    }

    /**
     * Unregister the event listeners
     */
    public static void unRegisterEventListeners() {
        List<AbstractCallFlowEventListener> eventComponents = Context.getRegisteredComponents(AbstractCallFlowEventListener.class);
        for (AbstractCallFlowEventListener eventListener : eventComponents) {
            unSubscribeListener(eventListener);
        }
    }

    private static void subscribeListener(AbstractCallFlowEventListener callFlowEventListener) {
        LOGGER.debug(String.format("The Call Flow module subscribe %s listener on the %s subject.",
                callFlowEventListener.getClass().toString(), callFlowEventListener.getSubject()));
        Event.subscribe(callFlowEventListener.getSubject(), callFlowEventListener);
    }

    private static void unSubscribeListener(AbstractCallFlowEventListener callFlowEventListener) {
        LOGGER.debug(String.format("The Call Flow module unsubscribe %s listener on the %s subject.",
                callFlowEventListener.getClass().toString(), callFlowEventListener.getSubject()));
        Event.unsubscribe(callFlowEventListener.getSubject(), callFlowEventListener);
    }

    private CallFlowEventListenerFactory() {
    }
}
