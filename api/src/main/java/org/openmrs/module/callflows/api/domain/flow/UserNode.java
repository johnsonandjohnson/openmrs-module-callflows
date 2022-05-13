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

import java.util.List;

/**
 * A user node
 * This class captures the properties that are specific in a user interaction node
 *
 * @author bramak09
 * @see org.openmrs.module.callflows.api.domain.flow.SystemNode
 */
public class UserNode extends Node {

    /**
     * A user node contains a list of blocks
     */
    private List<Block> blocks;

    /**
     * Sometimes we run into cases, where we continue with a flow and submit to a server from a user node, even if no direct
     * input is captured from a user. This field captures those cases
     */
    private Boolean continueNode;

    public List<Block> getBlocks() {
        return blocks;
    }

    public void setBlocks(List<Block> blocks) {
        this.blocks = blocks;
    }

    public Boolean isContinueNode() {
        return continueNode;
    }

    public void setContinueNode(Boolean continueNode) {
        this.continueNode = continueNode;
    }

    @Override
    public String toString() {
        return "UserNode{" +
                "name=" + getStep() +
                ", blocks=" + blocks +
                '}';
    }
}
