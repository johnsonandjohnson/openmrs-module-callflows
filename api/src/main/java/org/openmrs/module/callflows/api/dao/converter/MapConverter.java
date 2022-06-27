/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.callflows.api.dao.converter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;

@Converter()
public class MapConverter implements AttributeConverter<Map, byte[]> {

    private static final Log LOGGER = LogFactory.getLog(MapConverter.class);

    @Override
    public byte[] convertToDatabaseColumn(Map attribute) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(bos)) {
            out.writeObject(attribute);
        } catch (IOException e) {
            LOGGER.error("The error occurred during conversion Map into byte[].", e);
        }
        return bos.toByteArray();
    }

    @Override
    public Map convertToEntityAttribute(byte[] dbData) {
        Map<String, Object> result = null;
        ByteArrayInputStream bis = new ByteArrayInputStream(dbData);
        try (ObjectInputStream out = new ObjectInputStream(bis)) {
            result = (Map<String, Object>) out.readObject();
        } catch (IOException | ClassNotFoundException e) {
            LOGGER.error("The error occurred during conversion byte[] into Map.", e);
        }
        return result;
    }
}
