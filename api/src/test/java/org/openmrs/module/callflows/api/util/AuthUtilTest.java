package org.openmrs.module.callflows.api.util;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.openmrs.module.callflows.api.util.AuthUtil.APPLICATION_ID_CLAIM;
import static org.openmrs.module.callflows.api.util.AuthUtil.JTI_CLAIM;
import static org.openmrs.module.callflows.api.util.AuthUtil.TYPE;
import static org.openmrs.module.callflows.api.util.AuthUtil.TYPE_HEADER;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;
import java.util.Properties;
import java.util.UUID;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openmrs.module.callflows.api.service.SettingsManagerService;

@RunWith(MockitoJUnitRunner.class)
public class AuthUtilTest {

    private static final String TEST_PRIVATE_KEY = "private_test.key";
    private static final String TEST_IVR_PROPS = "ivr_test.properties";
    private static final String RS_256_ENCRYPTION_ALGORITHM = "RS256";
    private static final String ALG_PARAM = "alg";

    @Mock
    private SettingsManagerService settingsManagerService;

    @InjectMocks
    private AuthUtil authUtil = new AuthUtil();

    private String applicationId;

    private Key key;

    @Before
    public void setUp() throws IOException, NoSuchAlgorithmException, InvalidKeyException,
        InvalidKeySpecException, IllegalAccessException {
        when(settingsManagerService.getRawConfig(AuthUtil.PRIVATE_KEY_FILE_NAME))
            .thenReturn(loadConfigFile(TEST_PRIVATE_KEY));
        authUtil.loadPrivateKey();

        when(settingsManagerService.getRawConfig(AuthUtil.IVR_PROPERTIES_FILE_NAME))
            .thenReturn(loadConfigFile(TEST_IVR_PROPS));

        authUtil.loadProperties();
        Properties properties = new Properties();
        properties.load(loadConfigFile(TEST_IVR_PROPS));
        applicationId = properties.getProperty(AuthUtil.APPLICATION_ID_PROP);

        key = (Key) FieldUtils.readField(authUtil, "key", true);
    }

    @Test
    public void shouldGenerateTokenProperly() {
        // The required claims and headers listed here:
        // https://developer.nexmo.com/concepts/guides/authentication#json-web-tokens-jwt

        String token = authUtil.generateToken();
        String[] splitToken = token.split("\\.");
        Jwt<Header,Claims> parsedToken = Jwts.parser().parse(splitToken[0] + "." + splitToken[1] + ".");
        Header header = parsedToken.getHeader();
        Claims body = parsedToken.getBody();

        assertThat(header.getType(), equalTo(TYPE));
        assertThat(header.get(ALG_PARAM), equalTo(RS_256_ENCRYPTION_ALGORITHM));
        assertThat(body.get(APPLICATION_ID_CLAIM), equalTo(applicationId));

        assertNotNull(body.getExpiration());
        assertNotNull(body.getIssuedAt());
    }

    @Test
    public void shouldReturnInvalidTokenStatus() {
        Date now = new Date();
        String token = Jwts.builder()
            .setHeaderParam(TYPE_HEADER, TYPE)
            .setIssuedAt(now)
            .setExpiration(DateUtils.addDays(now, -1))
            .claim(APPLICATION_ID_CLAIM, applicationId)
            .claim(JTI_CLAIM, UUID.randomUUID().toString())
            .signWith(SignatureAlgorithm.RS256, key)
            .compact();

        boolean tokenValid = authUtil.isTokenValid(token);
        assertThat(tokenValid, equalTo(false));
    }

    @Test
    public void shouldReturnValidTokenStatus() {
        Date now = new Date();
        String token = Jwts.builder()
            .setHeaderParam(TYPE_HEADER, TYPE)
            .setIssuedAt(now)
            .setExpiration(DateUtils.addDays(now, 1))
            .claim(APPLICATION_ID_CLAIM, applicationId)
            .claim(JTI_CLAIM, UUID.randomUUID().toString())
            .signWith(SignatureAlgorithm.RS256, key)
            .compact();

        boolean tokenValid = authUtil.isTokenValid(token);
        assertThat(tokenValid, equalTo(true));
    }

    private InputStream loadConfigFile(String filename) throws IOException {
        return new ByteArrayInputStream(TestUtil.loadFile(filename).getBytes());
    }
}
