/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.callflows.api.service.impl;

import com.google.common.collect.Sets;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.UserDAO;
import org.openmrs.module.callflows.api.dao.CallDao;
import org.openmrs.module.callflows.api.domain.Call;
import org.openmrs.module.callflows.api.domain.CallFlow;
import org.openmrs.module.callflows.api.domain.Config;
import org.openmrs.module.callflows.api.domain.Constants;
import org.openmrs.module.callflows.api.domain.flow.Flow;
import org.openmrs.module.callflows.api.domain.types.CallDirection;
import org.openmrs.module.callflows.api.domain.types.CallStatus;
import org.openmrs.module.callflows.api.service.CallFlowService;
import org.openmrs.module.callflows.api.service.CallService;
import org.openmrs.module.callflows.api.service.ConfigService;
import org.openmrs.module.callflows.api.service.FlowService;
import org.openmrs.module.callflows.api.util.CallUtil;
import org.openmrs.module.callflows.api.util.DateUtil;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.OperationNotSupportedException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Call Service Implementation
 *
 * @author bramak09
 */
public class CallServiceImpl implements CallService {

    private static final Log LOGGER = LogFactory.getLog(CallServiceImpl.class);

    private static final String FAILURE = "failure";

    private static final String FAILED = "failed";

    private static final Set<Integer> ACCEPTABLE_IVR_RESPONSE_STATUSES = Sets
            .newHashSet(HttpURLConnection.HTTP_OK, HttpURLConnection.HTTP_ACCEPTED, HttpURLConnection.HTTP_CREATED);

    private static final String ADMIN_USER = "admin";

    private CallDao callDao;
    private CallFlowService callFlowService;
    private FlowService flowService;
    private ConfigService configService;
    private CallUtil callUtil;

    private static final String USER_DAO_BEAN_NAME = "userDAO";

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
    @Override
    @Transactional
    @SuppressWarnings("PMD.ExcessiveParameterList")
    public Call create(String config, CallFlow start, String startNode, CallDirection direction, String actorId,
                       String actorType, String externalId, String externalType, String playedMessages, String refKey,
                       Map<String, Object> params) {

        // Create a new call
        Call call = new Call();

        // using this configuration
        call.setConfig(config);

        // with these start properties
        call.setStartFlow(start);
        call.setStartNode(startNode);

        // and since we are creating a new call, our start and end properties are very similar
        call.setEndFlow(start);
        call.setEndNode(startNode);

        // The call direction
        call.setDirection(direction);

        // The call is identified uniquely by a UUID
        call.setCallId(UUID.randomUUID().toString());

        // The call's actor
        call.setActorId(actorId);
        call.setActorType(actorType);

        // External-provider related information and Message files played in the call
        call.setExternalId(externalId);
        call.setExternalType(externalType);
        call.setPlayedMessages(playedMessages);

        //External integrated system reference information
        call.setRefKey(refKey);

        // Parameters we were passed
        call.setContext(params == null ? new HashMap<String, Object>() : params);

        // No of steps that have happened so far, none to start with
        call.setSteps(0L);

        // and finally the call status
        call.setStatus(determineStatus(direction));

        if (Context.isSessionOpen() && !Context.isAuthenticated()) {
            call.setCreator(Context.getRegisteredComponent(USER_DAO_BEAN_NAME, UserDAO.class)
                    .getUserByUsername(ADMIN_USER));
        }

        return callDao.create(call);
    }

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
    @Override
    @Transactional
    public Call create(String config, CallFlow start, String startNode, CallDirection direction,
                       Map<String, Object> params) {
        return create(config, start, startNode, direction, null, null, null, null, null, null, params);
    }

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
    @Override
    public Call update(Call call) {
        Call currentCall = callDao.findById(call.getId());

        if (null == currentCall) {
            throw new IllegalArgumentException("Invalid call {} " + call);
        }
        // We can't allow updation of the start properties AND the callID AND the config
        // as those are write-once

        // We can update the end properties
        currentCall.setEndFlow(call.getEndFlow());
        currentCall.setEndNode(call.getEndNode());


        // AND parameters we were passed
        currentCall.setContext(call.getContext());

        // AND no of steps that have happened so far, incremented duly by the caller
        // We don't increment it here, as an update might be called multiple times in a single request by the caller
        currentCall.setSteps(call.getSteps());

        // The status
        currentCall.setStatus(call.getStatus());
        // Update status every time, we update status
        currentCall.setStatusText(call.getStatusText());

        // The provider data, cause we didn't have it at the time of creation
        currentCall.setProviderData(call.getProviderData());
        currentCall.setProviderCallId(call.getProviderCallId());
        currentCall.setProviderTime(call.getProviderTime());

        // We can update the actor data, if it wasn't set earlier
        if (null == currentCall.getActorId()) {
            currentCall.setActorId(call.getActorId());
            currentCall.setActorType(call.getActorType());
        }

        //We can update external reference information, if it wasn't set earlier
        if (null == currentCall.getRefKey()) {
            currentCall.setRefKey(call.getRefKey());
        }

        //Update the start time when the call is picked/answered
        if (CallStatus.IN_PROGRESS == call.getStatus() && call.getSteps() == 1) {
            // at this time
            currentCall.setStartTime(DateUtil.now());
        }
        //Update end time only once the call is answered.
        if (call.getSteps() >= 1) {
            currentCall.setEndTime(DateUtil.now());
        }

        //update the external provider information and messages played
        currentCall.setExternalId(call.getExternalId());
        currentCall.setExternalType(call.getExternalType());

        //update played messages
        currentCall.setPlayedMessages(call.getPlayedMessages());

        // update in the database
        return callDao.update(currentCall);
    }

    /**
     * Find a call by it's unique call ID, i.e the generated call ID
     *
     * @param callId to lookup by
     * @return the associated call
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Call findByCallId(String callId) {
        return callDao.findByCallId(callId);
    }

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
    @Override
    // Transactional approach is disabled here because a call entity is created in this method and
    // it's id is passed via HTTP request to the external provider, so it shouldn't be in one
    // transaction (otherwise the entity won't be saved yet during POST request)
    public Call makeCall(String configName, String flowName, Map<String, Object> params) {
        Call call = null;
        CallFlow callFlow = null;
        Config config = null;
        String phone = null;

        try {
            phone = (String) params.get(Constants.PARAM_PHONE);

            if (StringUtils.isEmpty(phone)) {
                throw new IllegalArgumentException(
                        "Empty Phone no while initiating a outbound call for flow " + flowName);
            }

            callFlow = callFlowService.findByName(flowName);
            config = configService.getConfig(configName);
            // Load Flow
            Flow flow = flowService.load(flowName);
            // Prepare Call
            call = prepareCall(phone, callFlow, flow, config, params);

            // Pre Request Hook, will throw OperationNotAllowed which will pass through to the exception handler
            callUtil.checkCallCanBePlaced(call, config, params);

            HttpUriRequest request = callUtil.buildOutboundRequest(phone, call, config, params);
            makeOutboundRequest(request, call, params);

        } catch (OperationNotSupportedException ose) {
            LOGGER.error(String.format(
                    "Outbound call not made for flow: %s, config: %s, phone: %s as the call queuing limit has been exceeded at this point in time",
                    flowName, configName, phone), ose);

            if (call != null) {
                // Since this is an error, the status is always FAILED
                call.setStatus(CallStatus.FAILED);
                call.setStatusText(ose.getMessage());
                callDao.update(call);
            }
        } catch (Exception e) {
            LOGGER.error(String.format("Outbound call not made for flow: %s, config: %s, phone: %s", flowName, configName, phone), e);
            handleError(call, e.getMessage(), params);
        }
        return call;
    }

    /**
     * Fetch calls based on startingRecord and recordsAmount params utilizing 'retrieveAll' function
     * Hibernate uses number of record from which we want to start fetching data (first record has index 0) and
     * amount of records we want to fetch
     *
     * @param startingRecord indicating number of record from which we want to start fetching data
     * @param recordsAmount  indicating amount of records which we want to fetch
     * @return list of calls
     */
    @Override
    public List<Call> findAll(int startingRecord, int recordsAmount) {
        return callDao.retrieveAll((startingRecord - 1) * recordsAmount, recordsAmount);
    }

    /**
     * Fetch the count of call records present in db
     *
     * @return total number of call records present in db
     */
    @Override
    public long retrieveCount() {
        return callDao.count();
    }

    private void handleError(Call call, String reason, Map<String, Object> params) {
        LOGGER.error(String.format("call %s failed with reason %s", call, reason));
        // update call failed status
        if (call != null) {
            // Since this is an error, the status is always FAILED
            call.setStatus(CallStatus.FAILED);
            call.setStatusText(reason);
            callDao.update(call);
            // send a OpenMRS event with all params as received, so that the module that called this
            // could inspect the error and retry if so desired
            callUtil.sendStatusEvent(call);
        } else {
            // We don't have a valid call, but there was still some error making the call,
            // so we send out an event with whatever information we have
            callUtil.sendStatusEvent(CallStatus.FAILED, reason, params);
        }
    }

    private Call prepareCall(String phone, CallFlow callFlow, Flow flow, Config config, Map<String, Object> params) {
        Map<String, Object> context = new HashMap<>();
        context.putAll(params);

        // Maintain a internal context
        Map<String, String> internalContext = new HashMap<>();
        internalContext.put(Constants.PARAM_PHONE, phone);
        internalContext.put(Constants.PARAM_JUMP_TO, flow.getName());
        context.put(Constants.PARAM_INTERNAL, internalContext);

        // Set actors if available
        String actorId = (String) params.get(Constants.PARAM_ACTOR_ID);
        String actorType = (String) params.get(Constants.PARAM_ACTOR_TYPE);

        String startNode = flow.getNodes().get(0).getStep();

        // Set the external provider information and messages played, if any
        String externalId = (String) params.get(Constants.PARAM_EXTERNAL_ID);
        String externalType = (String) params.get(Constants.PARAM_EXTERNAL_TYPE);
        String playedMessages = (String) params.get(Constants.PARAM_PLAYED_MESSAGES);

        // Set external reference information, if any
        String refKey = (String) params.get(Constants.PARAM_REF_KEY);

        // create the call
        return create(config.getName(), callFlow, startNode, CallDirection.OUTGOING, actorId, actorType, externalId,
                externalType, playedMessages, refKey, context);
    }

    private void makeOutboundRequest(HttpUriRequest request, Call call, Map<String, Object> params) throws IOException {

        try(CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
            HttpResponse response = httpClient.execute(request);

            LOGGER.debug(String.format("Response for call %s -> %s  headers : %s  ", call.getCallId(),
                    response.getStatusLine().toString(), response.getAllHeaders()));


            // check status code for any possible issues
            if (!ACCEPTABLE_IVR_RESPONSE_STATUSES.contains(response.getStatusLine().getStatusCode())) {
                handleError(call, "Unacceptable status line: " + response.getStatusLine().toString(), params);
            } else {
                // check content for possible issues, cause some IVR providers might return 200 and return a error body
                try (InputStream is = response.getEntity().getContent()) {
                    String content = IOUtils.toString(is);

                    LOGGER.debug(String.format("response : %s ", content));

                    if (isFailedContent(content)) {
                        handleError(call, "Unacceptable body: " + content, params);
                    }
                } catch (IOException ioe) {
                    LOGGER.error(String.format("Error retrieving content response for call %s ", call.getCallId()), ioe);
                    handleError(call, "Unreadable content: " + response.getStatusLine().toString(), params);
                }
            }
        }
    }

    private boolean isFailedContent(String content) {
        return content != null && (content.toLowerCase().contains(FAILURE) || content.toLowerCase().contains(FAILED));
    }

    private CallStatus determineStatus(CallDirection direction) {
        if (direction == CallDirection.OUTGOING) {
            return CallStatus.OPENMRS_INITIATED;
        } else {
            return CallStatus.INITIATED;
        }
    }

    /**
     * Sets Call Dao
     *
     * @param callDao Call DAO
     */
    public void setCallDao(CallDao callDao) {
        this.callDao = callDao;
    }

    /**
     * Sets the CallFLow Service
     *
     * @param callFlowService CallFlow Service
     */
    public void setCallFlowService(CallFlowService callFlowService) {
        this.callFlowService = callFlowService;
    }

    /**
     * Sets the Flow Service
     *
     * @param flowService Flow Service
     */
    public void setFlowService(FlowService flowService) {
        this.flowService = flowService;
    }

    /**
     * Sets the configuration Service
     *
     * @param configService configuration Service
     */
    public void setConfigService(ConfigService configService) {
        this.configService = configService;
    }

    /**
     * Sets the Call Util
     *
     * @param callUtil Call Util
     */
    public void setCallUtil(CallUtil callUtil) {
        this.callUtil = callUtil;
    }
}
