package org.openmrs.module.callflows.api.evaluation;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.velocity.VelocityContext;
import org.openmrs.module.callflows.api.domain.flow.Flow;
import org.openmrs.module.callflows.api.domain.flow.Node;

public class EvaluationContext {

    private Flow flow;

    private Node node;

    private VelocityContext context;

    private String template;

    public EvaluationContext() {
    }

    public EvaluationContext(Flow flow, Node node, VelocityContext context, String template) {
        this.flow = flow;
        this.node = node;
        this.context = context;
        this.template = template;
    }

    public Flow getFlow() {
        return flow;
    }

    public EvaluationContext setFlow(Flow flow) {
        this.flow = flow;
        return this;
    }

    public Node getNode() {
        return node;
    }

    public EvaluationContext setNode(Node node) {
        this.node = node;
        return this;
    }

    public VelocityContext getContext() {
        return context;
    }

    public EvaluationContext setContext(VelocityContext context) {
        this.context = context;
        return this;
    }

    public String getTemplate() {
        return template;
    }

    public EvaluationContext setTemplate(String template) {
        this.template = template;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
