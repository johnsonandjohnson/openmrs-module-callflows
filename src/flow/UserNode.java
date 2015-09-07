package com.janssen.connectforlife.callflows.domain.flow;

import java.util.List;

/**
 * A user node
 * This class captures the properties that are specific in a user interaction node
 *
 * @author bramak09
 * @see com.janssen.connectforlife.callflows.domain.flow.SystemNode
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
                "blocks=" + blocks +
                '}';
    }
}
