/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.callflows.api.domain.flow;

import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonTypeInfo;

import java.util.Map;
import java.util.Objects;

/**
 * Any flow consists of a series of nodes, this being the base class of all nodes
 * Nodes are of two types, those that capture user interaction (user nodes) and those that capture server processing (server nodes).
 *
 * @author bramak09
 */

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "nodeType")
@JsonSubTypes({
        // we have a user node
        @JsonSubTypes.Type(value = UserNode.class, name = "user"),
        // and a system node
        @JsonSubTypes.Type(value = SystemNode.class, name = "system") })
public class Node {

    /**
     * Each node has a step that's unique
     */
    private String step;

    /**
     * The nodeType of a node - either of user for something that happens on the user-side
     * and system for something that happens on the system side
     */
    private String nodeType;

    /**
     * Each node comprises of rendered templates
     * The key is the extension of the template, namely vxml, ccxml, etc.
     * The value is a Template class containing the actual content of the template plus any user specific edits
     */
    private Map<String, Template> templates;

    /**
     * The current block that is active in this node
     */
    private Block currentBlock;

    /**
     * An index to the current block, useful when tracking where we are currently trying to perform audio mappings
     */
    private int currentBlockId;

    /**
     * The current element in the current block that is active in this node
     */
    private Element currentElement;

    /**
     * An index to the current element, useful when tracking where we are currently trying to perform audio mappings
     */
    private int currentElementId;

    /**
     * The X position of this node in a visualization of this node
     */
    private int xpos;

    /**
     * The Y position of this node in a visualization of this node
     */
    private int ypos;

    public String getStep() {
        return step;
    }

    public void setStep(String step) {
        this.step = step;
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public Map<String, Template> getTemplates() {
        return templates;
    }

    public void setTemplates(Map<String, Template> templates) {
        this.templates = templates;
    }

    public Block getCurrentBlock() {
        return currentBlock;
    }

    public void setCurrentBlock(Block currentBlock) {
        this.currentBlock = currentBlock;
    }

    public Element getCurrentElement() {
        return currentElement;
    }

    public void setCurrentElement(Element currentElement) {
        this.currentElement = currentElement;
    }

    public int getCurrentBlockId() {
        return currentBlockId;
    }

    public void setCurrentBlockId(int currentBlockId) {
        this.currentBlockId = currentBlockId;
    }

    public int getCurrentElementId() {
        return currentElementId;
    }

    public void setCurrentElementId(int currentElementId) {
        this.currentElementId = currentElementId;
    }

    public int getXpos() {
        return xpos;
    }

    public void setXpos(int xpos) {
        this.xpos = xpos;
    }

    public int getYpos() {
        return ypos;
    }

    public void setYpos(int ypos) {
        this.ypos = ypos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Node)) {
            return false;
        }
        final Node other = (Node) o;
        return Objects.equals(this.step, other.step);
    }

    @Override
    public int hashCode() {
        return Objects.hash(step);
    }

    @Override
    public String toString() {
        return "Node{" +
                "step='" + step +
                '}';
    }


}
