package org.openmrs.module.callflows.api.domain;

import org.openmrs.module.callflows.api.domain.flow.Flow;

/**
 * A representation of a step in a flow that we will jump to from one of the templates
 *
 * @author bramak09
 */
public class FlowStep {

    /**
     * The flow object in this instance.
     */
    private Flow flow;

    /**
     * The step name
     */
    private String step;

    public Flow getFlow() {
        return flow;
    }

    public FlowStep setFlow(Flow flow) {
        this.flow = flow;
        return this;
    }

    public String getStep() {
        return step;
    }

    public FlowStep setStep(String step) {
        this.step = step;
        return this;
    }
}


