/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.callflows.api.evaluation;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.velocity.VelocityContext;
import org.openmrs.module.callflows.api.domain.flow.Flow;
import org.openmrs.module.callflows.api.domain.flow.Node;

/**
 * Evaluation context
 */
public class EvaluationContext {

    private Flow flow;

    private Node node;

    private VelocityContext context;

    private String template;

    /**
     * Empty Constructor for Evaluation context
     */
    public EvaluationContext() {
    }

    /**
     * Constructors setting the initial values on load
     *
     * @param flow flow set
     * @param node node set
     * @param context context set
     * @param template template is set
     */
    public EvaluationContext(Flow flow, Node node, VelocityContext context, String template) {
        this.flow = flow;
        this.node = node;
        this.context = context;
        this.template = template;
    }

    /**
     * Get the Flow
     *
     * @return Flow is returned
     */
    public Flow getFlow() {
        return flow;
    }

    /**
     * Set the flow
     *
     * @param flow is set
     */
    public EvaluationContext setFlow(Flow flow) {
        this.flow = flow;
        return this;
    }

    /**
     * Get the Node
     *
     * @return Node is returned
     */
    public Node getNode() {
        return node;
    }

    /**
     * Set the Node
     *
     * @param node is set
     */
    public EvaluationContext setNode(Node node) {
        this.node = node;
        return this;
    }

    /**
     * Get the context
     *
     * @return Velocity context is returned
     */
    public VelocityContext getContext() {
        return context;
    }

    /**
     * Set the Context
     *
     * @param context Velocity context is set
     */
    public EvaluationContext setContext(VelocityContext context) {
        this.context = context;
        return this;
    }

    /**
     * Get the template
     *
     * @return Template string is returned
     */
    public String getTemplate() {
        return template;
    }

    /**
     * Set the template
     *
     * @param template is set
     */
    public EvaluationContext setTemplate(String template) {
        this.template = template;
        return this;
    }

    /**
     * Checks for Equality
     *
     * @param o check the object against this
     * @return returns boolean result
     */
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

    /**
     * Gets the HashCode
     *
     * @return return the HashCode
     */
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
