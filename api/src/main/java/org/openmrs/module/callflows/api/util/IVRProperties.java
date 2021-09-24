package org.openmrs.module.callflows.api.util;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * The immutable IVRProperties Class contains the IVR properties.
 */
final class IVRProperties {
    static final String APPLICATION_ID_PROP_NAME = "applicationId";
    static final String KEY_ALGORITHM_PROP_NAME = "keyAlgorithm";
    static final String EXP_TIME_IN_HRS_PROP_NAME = "expTimeInHrs";

    private final String applicationId;
    private final String keyAlgorithm;
    private final int expirationTimeInHours;

    /**
     * @param ivrPropertiesFileStream the stream with IVR properties file (the caller must ensure the closing of this
     *                                stream), not null
     * @throws IOException              if it was not possible read the {@code ivrPropertiesFileStream}
     * @throws IllegalArgumentException if the file in {@code ivrPropertiesFileStream} contains invalid values or missing
     *                                  IVR properties
     */
    IVRProperties(final InputStream ivrPropertiesFileStream) throws IOException {
        final Properties properties = new Properties();
        properties.load(ivrPropertiesFileStream);

        this.applicationId = getRequiredProperty(properties, APPLICATION_ID_PROP_NAME);
        this.keyAlgorithm = getRequiredProperty(properties, KEY_ALGORITHM_PROP_NAME);

        try {
            this.expirationTimeInHours = Integer.parseInt(properties.getProperty(EXP_TIME_IN_HRS_PROP_NAME));
        } catch (NumberFormatException nfe) {
            throw new IllegalArgumentException("The IVR property 'expTimeInHrs' is required and must be a number!");
        }
    }

    private String getRequiredProperty(final Properties properties, final String propertyName) {
        final String property = properties.getProperty(propertyName);

        if (StringUtils.isEmpty(property)) {
            throw new IllegalArgumentException(new StringBuilder("The IVR property '")
                    .append(propertyName)
                    .append("' is required!")
                    .toString());
        }

        return property;
    }

    String getApplicationId() {
        return applicationId;
    }

    String getKeyAlgorithm() {
        return keyAlgorithm;
    }

    int getExpirationTimeInHours() {
        return expirationTimeInHours;
    }
}
