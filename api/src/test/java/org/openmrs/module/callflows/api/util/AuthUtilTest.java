/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.callflows.api.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.openmrs.module.callflows.api.service.SettingsManagerService;
import org.springframework.core.io.ByteArrayResource;

import javax.xml.bind.DatatypeConverter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Date;
import java.util.Properties;
import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.openmrs.module.callflows.api.util.AuthUtil.APPLICATION_ID_CLAIM_NAME;
import static org.openmrs.module.callflows.api.util.AuthUtil.JTI_CLAIM_NAME;
import static org.openmrs.module.callflows.api.util.AuthUtil.TYPE_HEADER_NAME;
import static org.openmrs.module.callflows.api.util.AuthUtil.TYPE_HEADER_VALUE;

@RunWith(MockitoJUnitRunner.class)
public class AuthUtilTest {

    private static final String TEST_PRIVATE_KEY = "private_test.key";
    private static final String TEST_PRIVATE_EXPECTED_RAW_KEY = "private_test_expected_raw.key";
    private static final String TEST_IVR_PROPS = "ivr_test.properties";
    private static final String RS_256_ENCRYPTION_ALGORITHM = "RS256";
    private static final String ALG_PARAM = "alg";

    @Mock
    private SettingsManagerService settingsManagerService;

    @InjectMocks
    private AuthUtil authUtil = new AuthUtil();

    private String testApplicationId;
    private String testKeyAlgorithm;
    private Key testKey;

    @Before
    public void setUp() throws Exception {
        when(settingsManagerService.getRawConfig(AuthUtil.PRIVATE_KEY_FILE_NAME)).thenReturn(
                loadConfigFile(TEST_PRIVATE_KEY));
        when(settingsManagerService.getRawConfig(AuthUtil.IVR_PROPERTIES_FILE_NAME)).thenReturn(
                loadConfigFile(TEST_IVR_PROPS));

        Properties properties = new Properties();
        properties.load(loadConfigFile(TEST_IVR_PROPS));
        testApplicationId = properties.getProperty(IVRProperties.APPLICATION_ID_PROP_NAME);
        testKeyAlgorithm = properties.getProperty(IVRProperties.KEY_ALGORITHM_PROP_NAME);

        testKey = loadTestKey();
    }

    private Key loadTestKey() throws Exception {
        try (final InputStream privateKeyStream = loadConfigFile(TEST_PRIVATE_EXPECTED_RAW_KEY)) {
            final String base64EncodedKey = IOUtils.toString(privateKeyStream, "UTF-8");
            final byte[] privateKey = DatatypeConverter.parseBase64Binary(base64EncodedKey);
            final PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(privateKey);
            final KeyFactory kf = KeyFactory.getInstance(testKeyAlgorithm);

            return kf.generatePrivate(spec);
        }
    }

    @Test
    public void shouldGenerateTokenProperly() throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
        // The required claims and headers listed here:
        // https://developer.nexmo.com/concepts/guides/authentication#json-web-tokens-jwt

        String token = authUtil.generateToken();
        String[] splitToken = token.split("\\.");
        Jwt<Header, Claims> parsedToken = Jwts
                .parser()
                .parseClaimsJwt(splitToken[0] + "." + splitToken[1] + ".");
        Header header = parsedToken.getHeader();
        Claims body = parsedToken.getBody();

        assertThat(header.getType(), equalTo(TYPE_HEADER_VALUE));
        assertThat(header.get(ALG_PARAM), equalTo(RS_256_ENCRYPTION_ALGORITHM));
        assertThat(body.get(APPLICATION_ID_CLAIM_NAME), equalTo(testApplicationId));

        assertNotNull(body.getExpiration());
        assertNotNull(body.getIssuedAt());
    }

    @Test
    public void shouldReturnInvalidTokenStatus() {
        Date now = new Date();

        String token = Jwts
                .builder()
                .setHeaderParam(TYPE_HEADER_NAME, TYPE_HEADER_VALUE)
                .setIssuedAt(now)
                .setExpiration(DateUtils.addDays(now, -1))
                .claim(APPLICATION_ID_CLAIM_NAME, testApplicationId)
                .claim(JTI_CLAIM_NAME, UUID
                        .randomUUID()
                        .toString())
                .signWith(SignatureAlgorithm.RS256, testKey)
                .compact();

        boolean tokenValid = authUtil.isTokenValid(token);
        assertThat(tokenValid, equalTo(false));
    }

    @Test
    public void shouldReturnValidTokenStatus() {
        Date now = new Date();
        String token = Jwts
                .builder()
                .setHeaderParam(TYPE_HEADER_NAME, TYPE_HEADER_VALUE)
                .setIssuedAt(now)
                .setExpiration(DateUtils.addDays(now, 1))
                .claim(APPLICATION_ID_CLAIM_NAME, testApplicationId)
                .claim(JTI_CLAIM_NAME, UUID
                        .randomUUID()
                        .toString())
                .signWith(SignatureAlgorithm.RS256, testKey)
                .compact();

        boolean tokenValid = authUtil.isTokenValid(token);
        assertThat(tokenValid, equalTo(true));
    }

    @Test
    public void shouldSaveNewPrivateKeyInProperFormat() throws IOException {
        final byte[] expectedBytes = TestUtil
                .loadFile(TEST_PRIVATE_KEY)
                .getBytes();
        final String expectedFileContent = new String(expectedBytes);

        try (final InputStream newPrivateKeyFile = new ByteArrayInputStream(expectedBytes)) {
            authUtil.saveNewPrivateKey(newPrivateKeyFile);
        }

        final ArgumentCaptor<ByteArrayResource> argumentCaptor = ArgumentCaptor.forClass(ByteArrayResource.class);
        Mockito
                .verify(settingsManagerService)
                .saveRawConfig(eq(AuthUtil.PRIVATE_KEY_FILE_NAME), argumentCaptor.capture());

        final ByteArrayResource actualSavedBytes = argumentCaptor.getValue();
        final String actualFileContent = new String(actualSavedBytes.getByteArray());

        Assert.assertEquals(normalizeKeyFileContent(actualFileContent), normalizeKeyFileContent(expectedFileContent));
    }

    private InputStream loadConfigFile(String filename) throws IOException {
        return new ByteArrayInputStream(TestUtil
                .loadFile(filename)
                .getBytes());
    }

    /**
     * Removes carriage return characters (optional part of new line characters) and removes optional new line character(-s)
     * at the end of the file.
     *
     * @param fileContent the string to normalize, not null
     * @return normalized result, never null
     */
    private String normalizeKeyFileContent(final String fileContent) {
        return StringUtils.remove(fileContent, '\r').trim();
    }
}
