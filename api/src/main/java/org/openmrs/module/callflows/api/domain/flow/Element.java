package org.openmrs.module.callflows.api.domain.flow;

import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonTypeInfo;

/**
 * Element class
 *
 * @author bramak09
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({ @JsonSubTypes.Type(value = TextElement.class, name = "txt"),
        @JsonSubTypes.Type(value = FieldElement.class, name = "field"),
        @JsonSubTypes.Type(value = MenuElement.class, name = "menu"), })
public class Element {

    /**
     * The name of the element
     */
    private String name;

    /**
     * element type, is one of txt, field or menu
     * These types are derived from the VoiceXML standard as they were found to be generic enough for most IVR formats
     */
    private String type;

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
}
