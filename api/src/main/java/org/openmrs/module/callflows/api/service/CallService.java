package org.openmrs.module.callflows.api.service;

import org.openmrs.module.callflows.api.domain.Call;
import org.openmrs.module.callflows.api.domain.CallFlow;
import org.openmrs.module.callflows.api.domain.types.CallDirection;

import java.util.List;
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
     * @param config         to use when creating a call in the system
     * @param start          the start flow to use
     * @param startNode      the start node to use
     * @param direction      the call direction either of outbound or inbound
     * @param actorId        the actor associated with this call
     * @param actorType      the type of actor, eg: patient, doctor, etc
     * @param externalId     the externalId of the provider, if any, associated with this call
     * @param externalType   type of the provider id, if any, associated with this call
     * @param playedMessages information of any message being played
     * @param refKey         reference information to link with different integrated systems
     * @param params         the initial params for the flow to function
     * @return a new call object with a generated call ID
     */
    Call create(String config, CallFlow start, String startNode, CallDirection direction, String actorId,
                String actorType, String externalId, String externalType, String playedMessages, String refKey,
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

    /**
     * Makes an outbound call using the IVR configuration to use, the flow name to kick start the call with
     * and a map of parameters
     * <p></p>
     * In the params map, three keys have special meanings
     * phone - indicating the phone number to call [REQUIRED]
     * actorId - indicating a unique ID that you can connect with your application user [OPTIONAL]
     * actorType - indicating a type of a user, say a doctor or a patient [OPTIONAL]
     * <p></p>
     * The flowName will be stored in the created call's context as a variable named jumpTo
     * Typically the IVR providers will be configured to always hit a specific main flow and while making outbound calls,
     * when you want to invoke a different flow, this parameter will be passed to the IVR provider as internal.jumpTo,
     * which one will then get in the request back.
     * The callflow designer can make use of this information to jump to this requested from from a appropriate point
     * in their main flow.
     * Eg: One can use the main flow for any authentication like entering a PIN and then jump to this flow
     * or you could jump even before the authentication, if so desired.
     * Typically only these two parameters (phone and jumpTo) along with a callId would be passed to the IVR provider's API
     * to initiate a call.
     * No other params *need to be* passed, but this is configurable in the relevant IVR's configuration section
     * These params will be passed to the IVR provider under the names internal.phone, internal.jumpId and internal.callId
     * <p></p>
     * All params passed to this method will also be persisted in the call's context,
     * so that they are available for use in the callflow.
     * Eg: If making a outbound call for a visit reminder, use
     * <code>flowName</code> for your entry flow,
     * <code>jumpTo</code> as your visit reminder flow,
     * <code>visitId</code> indicating for which visit this reminder is for,
     * that you can then use from within your callflow to retrieve the visit object and do something with it
     * <p></p>
     * Event: Sends a failed call event passing all parameters into the event in case the call could not be made for any reason
     * <p></p>
     * Note: This method was inspired and adapted from the MOTECH IVR module
     *
     * @param configName indicating the IVR configuration to use to connect to the caller
     * @param flowName   indicating the flow to execute first for the callee
     * @param params     a map of parameters containing information needed for the call to be placed
     * @return the call created
     */
    Call makeCall(String configName, String flowName, Map<String, Object> params);


    /**
     * Fetch calls based on startingRecord and recordsAmount params utilizing 'retrieveAll' function
     * Hibernate uses number of record from which we want to start fetching data (first record has index 0) and
     * amount of records we want to fetch
     *
     * @param startingRecord indicating number of record from which we want to start fetching data
     * @param recordsAmount  indicating amount of records which we want to fetch
     * @return list of calls
     */
    List<Call> findAll(int startingRecord, int recordsAmount);

    /**
     * Fetch the count of call records present in db
     *
     * @return total number of call records present in db
     */
    long retrieveCount();
}
