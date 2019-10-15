package org.openmrs.module.callflows.api.util;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Properties;
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

    @Before
    public void setUp() throws IOException, NoSuchAlgorithmException, InvalidKeyException,
        InvalidKeySpecException {
        when(settingsManagerService.getRawConfig(AuthUtil.PRIVATE_KEY_FILE_NAME))
            .thenReturn(loadConfigFile(TEST_PRIVATE_KEY));
        authUtil.loadPrivateKey();

        when(settingsManagerService.getRawConfig(AuthUtil.IVR_PROPERTIES_FILE_NAME))
            .thenReturn(loadConfigFile(TEST_IVR_PROPS));

        authUtil.loadProperties();
        Properties properties = new Properties();
        properties.load(loadConfigFile(TEST_IVR_PROPS));
        applicationId = properties.getProperty(AuthUtil.APPLICATION_ID_PROP);
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

        assertThat(header.getType(), equalTo(AuthUtil.TYPE));
        assertThat(header.get(ALG_PARAM), equalTo(RS_256_ENCRYPTION_ALGORITHM));
        assertThat(body.get(AuthUtil.APPLICATION_ID_CLAIM), equalTo(applicationId));

        assertNotNull(body.getExpiration());
        assertNotNull(body.getIssuedAt());
    }

    private InputStream loadConfigFile(String filename) throws IOException {
        return new ByteArrayInputStream(TestUtil.loadFile(filename).getBytes());
    }
}
