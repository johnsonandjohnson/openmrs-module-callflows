package org.openmrs.module.callflows.api.event;

import org.openmrs.api.context.Context;
import org.openmrs.event.EventListener;
import org.openmrs.module.callflows.api.exception.CallFlowRuntimeException;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;

public abstract class AbstractCallFlowEventListener implements EventListener {

	@Override
	public void onMessage(Message message) {
		try {
			Map<String, Object> properties = getProperties(message);
			handleEvent(properties);
		} catch(JMSException ex) {
			throw new CallFlowRuntimeException("Error during handling Call Flow event", ex);
		}
	}

	public abstract String getSubject();

	protected abstract void handleEvent(Map<String, Object> properties);

	protected <T> T getComponent(String beanName, Class<T> type) {
		return Context.getRegisteredComponent(beanName, type);
	}

	private Map<String, Object> getProperties(Message message) throws JMSException {
		Map<String, Object> properties = new HashMap<>();

		// OpenMRS event module uses underneath MapMessage to construct Message. For some reason retrieving properties
		// from Message interface doesn't work and we have to map object to MapMessage.
		MapMessage mapMessage = (MapMessage) message;
		Enumeration<String> propertiesKey = (Enumeration<String>) mapMessage.getMapNames();

		while(propertiesKey.hasMoreElements()) {
			String key = propertiesKey.nextElement();
			properties.put(key, mapMessage.getObject(key));
		}
		return properties;
	}

}
