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
