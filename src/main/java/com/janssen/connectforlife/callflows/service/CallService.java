package com.janssen.connectforlife.callflows.service;

import com.janssen.connectforlife.callflows.domain.Call;
import com.janssen.connectforlife.callflows.domain.CallFlow;
import com.janssen.connectforlife.callflows.domain.types.CallDirection;

import java.util.Map;

/**
 * Call Service
 *
 * @author bramak09
 */
public interface CallService {

    /**
     * Create a new call in the system by generating a unique call ID for a specific actor
     *
     * @param config    to use when creating a call in the system
     * @param start     the start flow to use
     * @param startNode the start node to use
     * @param direction the call direction either of outbound or inbound
     * @param actorId   the actor associated with this call
     * @param actorType the type of actor, eg: patient, doctor, etc
     * @param params    the initial params for the flow to function
     * @return a new call object with a generated call ID
     */
    Call create(String config,
                CallFlow start,
                String startNode,
                CallDirection direction,
                String actorId,
                String actorType,
                Map<String, Object> params);

    /**
     * Create a new call in the system by generating a unique call ID for a actor not known at the time of creating the call
     * This is essentially a wrapper for the other create method with actor set as null
     *
     * @param config    to use when creating a call in the system
     * @param start     the start flow to use
     * @param startNode the start node to use
     * @param direction the call direction either of outbound or inbound
     * @param params    the initial params for the flow to function
     * @return a new call object with a generated call ID
     */
    Call create(String config, CallFlow start, String startNode, CallDirection direction, Map<String, Object> params);

    /**
     * Update a call in the system with new properties set
     * The service will not update the three start properties, the config or the call ID as those are write once only
     * The actor properties (actorId and actorType) can be set if they have not already been set
     * All other properties can be updated
     *
     * @param call to update
     * @return the updated call object
     * @throws IllegalArgumentException if the call could not be retrieved in the system
     */
    Call update(Call call);

    /**
     * Find a call by it's unique call ID, i.e the generated call ID
     *
     * @param callId to lookup by
     * @return the associated call
     */
    Call findByCallId(String callId);
}
