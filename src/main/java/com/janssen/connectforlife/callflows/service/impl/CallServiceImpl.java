package com.janssen.connectforlife.callflows.service.impl;

import com.janssen.connectforlife.callflows.domain.Call;
import com.janssen.connectforlife.callflows.domain.CallFlow;
import com.janssen.connectforlife.callflows.domain.types.CallDirection;
import com.janssen.connectforlife.callflows.domain.types.CallStatus;
import com.janssen.connectforlife.callflows.repository.CallDataService;
import com.janssen.connectforlife.callflows.service.CallService;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map;
import java.util.UUID;

/**
 * Call Service Implementation
 *
 * @author bramak09
 */
@Service("callService")
public class CallServiceImpl implements CallService {

    @Autowired
    private CallDataService callDataService;

    @Override
    @Transactional
    public Call create(String config,
                       CallFlow start,
                       String startNode,
                       CallDirection direction,
                       String actorId,
                       String actorType,
                       Map<String, Object> params) {

        // Create a new call
        Call call = new Call();

        // using this configuration
        call.setConfig(config);

        // with these start properties
        call.setStartFlow(start);
        call.setStartNode(startNode);
        // at this time
        DateTime now = DateTime.now();
        call.setStartTime(now);

        // and since we are creating a new call, our start and end properties are very similar
        call.setEndFlow(start);
        call.setEndNode(startNode);
        call.setEndTime(now);

        // The call direction
        call.setDirection(direction);

        // The call is identified uniquely by a UUID
        call.setCallId(UUID.randomUUID().toString());

        // The call's actor
        call.setActorId(actorId);
        call.setActorType(actorType);

        // Parameters we were passed
        call.setContext(params);

        // No of steps that have happened so far, none to start with
        call.setSteps(0L);

        // and finally the call status
        call.setStatus(determineStatus(direction));
        return callDataService.create(call);
    }

    @Override
    @Transactional
    public Call create(String config,
                       CallFlow start,
                       String startNode,
                       CallDirection direction,
                       Map<String, Object> params) {
        return create(config, start, startNode, direction, null, null, params);
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
        currentCall.setEndTime(DateTime.now());

        // AND parameters we were passed
        currentCall.setContext(call.getContext());

        // AND no of steps that have happened so far, incremented duly by the caller
        // We don't increment it here, as an update might be called multiple times in a single request by the caller
        currentCall.setSteps(call.getSteps());

        // The status
        currentCall.setStatus(call.getStatus());

        // The provider data, cause we didn't have it at the time of creation
        currentCall.setProviderData(call.getProviderData());
        currentCall.setProviderCallId(call.getProviderCallId());
        currentCall.setProviderTime(call.getProviderTime());

        // We can update the actor data, if it wasn't set earlier
        if (null == currentCall.getActorId()) {
            currentCall.setActorId(call.getActorId());
            currentCall.setActorType(call.getActorType());
        }

        // update in the database
        return callDataService.update(currentCall);
    }

    @Override
    @Transactional
    public Call findByCallId(String callId) {
        return callDataService.findByCallId(callId);
    }

    private CallStatus determineStatus(CallDirection direction) {
        if (direction == CallDirection.OUTGOING) {
            return CallStatus.MOTECH_INITIATED;
        } else {
            return CallStatus.INITIATED;
        }
    }
}
