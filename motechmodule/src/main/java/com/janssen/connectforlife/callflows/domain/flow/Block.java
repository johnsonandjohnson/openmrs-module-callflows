package com.janssen.connectforlife.callflows.domain.flow;

import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import java.util.List;

/**
 * A node consists of multiple blocks, this being the base class for those
 * Blocks are top level containers
 *
 * @author bramak09
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        // we have a form level block
        @JsonSubTypes.Type(value = FormBlock.class, name = "form"),
        // and a menu level block
        @JsonSubTypes.Type(value = MenuBlock.class, name = "menu") })
public class Block {

    /**
     * The block name
     */
    private String name;

    /**
     * The type of a block, typically one of form or menu
     */
    private String type;

    /**
     * A list of elements in the block
     */
    private List<Element> elements;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Element> getElements() {
        return elements;
    }

    public void setElements(List<Element> elements) {
        this.elements = elements;
    }
}
