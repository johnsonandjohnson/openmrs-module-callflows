package org.openmrs.module.callflows.api.event;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.event.Event;

public final class CallFlowEventListenerFactory {

	private static final Log LOGGER = LogFactory.getLog(CallFlowEventListenerFactory.class);


	public static void registerEventListeners() {
		subscribeListener(new CallFlowInitiateCallEventListener());
	}

	public static void unRegisterEventListeners() {
		unSubscribeListener(new CallFlowInitiateCallEventListener());
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
