package com.janssen.connectforlife.callflows.service.impl;

import com.janssen.connectforlife.callflows.domain.Call;
import com.janssen.connectforlife.callflows.domain.CallFlow;
import com.janssen.connectforlife.callflows.domain.Config;
import com.janssen.connectforlife.callflows.domain.Constants;
import com.janssen.connectforlife.callflows.domain.flow.Flow;
import com.janssen.connectforlife.callflows.domain.types.CallDirection;
import com.janssen.connectforlife.callflows.domain.types.CallStatus;
import com.janssen.connectforlife.callflows.repository.CallDataService;
import com.janssen.connectforlife.callflows.service.CallFlowService;
import com.janssen.connectforlife.callflows.service.CallService;
import com.janssen.connectforlife.callflows.service.FlowService;
import com.janssen.connectforlife.callflows.service.SettingsService;
import com.janssen.connectforlife.callflows.util.CallUtil;

import org.motechproject.mds.query.QueryParams;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.naming.OperationNotSupportedException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.google.common.collect.Sets;

/**
 * Call Service Implementation
 *
 * @author bramak09
 */
@Service("callService")
public class CallServiceImpl implements CallService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CallServiceImpl.class);

    private static final String FAILURE = "failure";

    private static final Set<Integer> ACCEPTABLE_IVR_RESPONSE_STATUSES = Sets
            .newHashSet(HttpStatus.SC_OK, HttpStatus.SC_ACCEPTED, HttpStatus.SC_CREATED);

    @Autowired
    private CallDataService callDataService;

    @Autowired
    private CallFlowService callFlowService;

    @Autowired
    private FlowService flowService;

    @Autowired
    private SettingsService settingsService;

    @Autowired
    private CallUtil callUtil;

    @Override
    @Transactional
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
        return callDataService.create(call);
    }

    @Override
    @Transactional
    public Call create(String config, CallFlow start, String startNode, CallDirection direction,
                       Map<String, Object> params) {
        return create(config, start, startNode, direction, null, null, null, null, null, null, params);
    }

    @Override
    @Transactional
    public Call update(Call call) {
        Call currentCall = callDataService.findById(call.getId());

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
            currentCall.setStartTime(DateTime.now());
        }
        //Update end time only once the call is answered.
        if (call.getSteps() >= 1) {
            currentCall.setEndTime(DateTime.now());
        }

        //update the external provider information and messages played
        currentCall.setExternalId(call.getExternalId());
        currentCall.setExternalType(call.getExternalType());

        //update played messages
        currentCall.setPlayedMessages(call.getPlayedMessages());

        // update in the database
        return callDataService.update(currentCall);
    }

    @Override
    @Transactional
    public Call findByCallId(String callId) {
        return callDataService.findByCallId(callId);
    }

    @Override
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
            config = settingsService.getConfig(configName);
            // Load Flow
            Flow flow = flowService.load(flowName);
            // Prepare Call
            call = prepareCall(phone, callFlow, flow, config, params);

            // Pre Request Hook, will throw OperationNotAllowed which will pass through to the exception handler
            callUtil.checkCallCanBePlaced(call, config, params);

            HttpUriRequest request = callUtil.buildOutboundRequest(phone, call, config, params);
            makeOutboundRequest(request, call, params);

        } catch (OperationNotSupportedException ose) {
            LOGGER.error(
                    "Outbound call not made for flow: {}, config: {}, phone: {} as the call queuing limit has been exceeded at this point in time",
                    flowName, configName, phone, ose);

            if (call != null) {
                // Since this is an error, the status is always FAILED
                call.setStatus(CallStatus.FAILED);
                call.setStatusText(ose.getMessage());
                callDataService.update(call);
            }
        } catch (Exception e) {
            LOGGER.error("Outbound call not made for flow: {}, config: {}, phone: {}", flowName, configName, phone, e);
            handleError(call, e.getMessage(), params);
        }
        return call;
    }

    @Override
    public List<Call> findAll(QueryParams queryParams) {
        return callDataService.retrieveAll(queryParams);
    }

    @Override
    public long retrieveCount() {
        return callDataService.count();
    }

    private void handleError(Call call, String reason, Map<String, Object> params) {
        LOGGER.error("call {} failed with reason {}", call, reason);
        // update call failed status
        if (call != null) {
            // Since this is an error, the status is always FAILED
            call.setStatus(CallStatus.FAILED);
            call.setStatusText(reason);
            callDataService.update(call);
            // send a motech event with all params as received, so that the module that called this
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

        HttpResponse response = new DefaultHttpClient().execute(request);

        LOGGER.debug("Response for call {} -> {}  headers : {}  ", call.getCallId(),
                     response.getStatusLine().toString(), response.getAllHeaders());


        // check status code for any possible issues
        if (!ACCEPTABLE_IVR_RESPONSE_STATUSES.contains(response.getStatusLine().getStatusCode())) {
            handleError(call, "Unacceptable status line: " + response.getStatusLine().toString(), params);
        } else {
            // check content for possible issues, cause some IVR providers might return 200 and return a error body
            try (InputStream is = response.getEntity().getContent()) {
                String content = IOUtils.toString(is);

                LOGGER.debug("response : {} ", content);

                if (content.indexOf(FAILURE) != -1) {
                    handleError(call, "Unacceptable body: " + content, params);
                }
            } catch (IOException ioe) {
                LOGGER.error("Error retrieving content response for call {} ", call.getCallId(), ioe);
                handleError(call, "Unreadable content: " + response.getStatusLine().toString(), params);
            }
        }
    }

    private CallStatus determineStatus(CallDirection direction) {
        if (direction == CallDirection.OUTGOING) {
            return CallStatus.MOTECH_INITIATED;
        } else {
            return CallStatus.INITIATED;
        }
    }
}
