package com.janssen.connectforlife.callflows.service.impl;

import com.janssen.connectforlife.callflows.domain.CallFlow;
import com.janssen.connectforlife.callflows.domain.FlowStep;
import com.janssen.connectforlife.callflows.domain.flow.Flow;
import com.janssen.connectforlife.callflows.domain.flow.Node;
import com.janssen.connectforlife.callflows.repository.CallFlowDataService;
import com.janssen.connectforlife.callflows.service.FlowService;
import com.janssen.connectforlife.callflows.util.FlowUtil;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;

/**
 * Flow Service Implementation
 *
 * @author bramak09
 */
@Service("flowService")
public class FlowServiceImpl implements FlowService {

    @Autowired
    private CallFlowDataService callFlowDataService;

    @Autowired
    private FlowUtil flowUtil;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public FlowStep parse(String input, Flow currentFlow) {

        String currentFlowName = currentFlow == null ? null : currentFlow.getName();
        String[] parsed = flowUtil.parse(input, currentFlowName);

        Flow flow = load(parsed[0]);
        String step = parsed[1] == null ? flow.getNodes().get(0).getStep() : parsed[1];

        Node node = flowUtil.getNodeByStep(flow, step);
        if (null == node) {
            String message = String.format("Unable to load step %s in flow %s ", step, flow.getName());
            throw new IllegalArgumentException(message);
        }

        FlowStep flowStep = new FlowStep();
        flowStep.setFlow(flow).setStep(step);
        return flowStep;
    }

    @Override
    public Flow load(String name) {
        try {
            CallFlow callFlow = callFlowDataService.findByName(name);
            if (callFlow != null) {
                return objectMapper.readValue(callFlow.getRaw(), Flow.class);
            } else {
                throw new IllegalArgumentException("Unable to load Flow : " + name);
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("JSON parse issue. Unable to load flow : " + name, e);
        }
    }

}


