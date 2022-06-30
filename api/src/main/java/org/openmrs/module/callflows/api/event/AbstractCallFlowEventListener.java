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

import org.openmrs.api.context.Context;
import org.openmrs.api.context.Daemon;
import org.openmrs.event.EventListener;
import org.openmrs.module.DaemonToken;
import org.openmrs.module.callflows.api.exception.CallFlowRuntimeException;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Abstract CallFlow Event Listener
 */
public abstract class AbstractCallFlowEventListener implements EventListener {

    private DaemonToken daemonToken;

    /**
     * Runs the event
     *
     * @param message to get from properties
     * @throws CallFlowRuntimeException while handling the flow event.
     */
    @Override
    public void onMessage(Message message) {
        try {
            Map<String, Object> properties = getProperties(message);
            Daemon.runInDaemonThread(new Runnable() {
                @Override
                public void run() {
                    handleEvent(properties);
                }
            }, daemonToken);
        } catch (JMSException ex) {
            throw new CallFlowRuntimeException("Error during handling Call Flow event", ex);
        }
    }

    /**
     * Get the Subject
     *
     * @return Returns a string
     */
    public abstract String getSubject();

    /**
     * Set the Daemon token
     *
     * @param daemonToken Daemon token
     */
    public void setDaemonToken(DaemonToken daemonToken) {
        this.daemonToken = daemonToken;
    }

    /**
     * Handle the event
     *
     * @param properties Map of properties
     */
    protected abstract void handleEvent(Map<String, Object> properties);

    /**
     * Get the component
     *
     * @param beanName of type String
     * @param type
     * @return Return of type <T>
     */
    protected <T> T getComponent(String beanName, Class<T> type) {
        return Context.getRegisteredComponent(beanName, type);
    }

    private Map<String, Object> getProperties(Message message) throws JMSException {
        Map<String, Object> properties = new HashMap<>();

        // OpenMRS event module uses underneath MapMessage to construct Message. For some reason retrieving properties
        // from Message interface doesn't work and we have to map object to MapMessage.
        MapMessage mapMessage = (MapMessage) message;
        Enumeration<String> propertiesKey = (Enumeration<String>) mapMessage.getMapNames();

        while (propertiesKey.hasMoreElements()) {
            String key = propertiesKey.nextElement();
            properties.put(key, mapMessage.getObject(key));
        }
        return properties;
    }

}
