package org.openmrs.module.callflows.api.util;


/**
 * Collection of utility methods to authenticate IVR provider
 *
 * @author nanakapa
 */

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.InvalidClaimException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.callflows.api.service.SettingsManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Date;
import java.util.Properties;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component("authUtil")
public class AuthUtil {

    public static final String PRIVATE_KEY_FILE_NAME = "private.key";
    public static final String IVR_PROPERTIES_FILE_NAME = "ivr.properties";
    public static final String APPLICATION_ID_CLAIM = "application_id";
    public static final String APPLICATION_ID_PROP = "applicationId";
    public static final String TYPE = "JWT";
    public static final String DEFAULT_EXPIRY_TIME = "1";
    public static final String CHARSET_NAME = StandardCharsets.UTF_8.name();
    public static final String JTI_CLAIM = "jti";
    public static final String TYPE_HEADER = "typ";
    public static final String EXP_TIME_IN_HRS_PROP = "expTimeInHrs";

    private static final Log LOGGER = LogFactory.getLog(CallUtil.class);
    private static final Pattern PEM_PATTERN = Pattern.compile("-----BEGIN PRIVATE KEY-----" +
                    // File header
                    "(.*\\n)" +                     // Key data
                    "-----END PRIVATE KEY-----" +   // File footer
                    "\\n?",
            // Optional trailing line break
            Pattern.MULTILINE | Pattern.DOTALL);

    private PrivateKey key;
    private String applicationId;
    private int expTimeInHrs;

    @Autowired
    private SettingsManagerService settingsManagerService;

    @PostConstruct
    public void initialize() {
        try {
            boolean isConfigLoaded = settingsManagerService.configurationExist(PRIVATE_KEY_FILE_NAME)
                    && settingsManagerService.configurationExist(IVR_PROPERTIES_FILE_NAME);
            if (isConfigLoaded) {
                loadProperties();
                loadPrivateKey();
            }
        } catch (Exception ex) {
            LOGGER.error("Authentication configuration couldn't be loaded", ex);
            ex.printStackTrace();
        }
    }

    public void validatePrivateKey(byte[] data) throws InvalidKeyException {
        boolean isValid = false;
        try {
            isValid = PEM_PATTERN.matcher(new String(data, CHARSET_NAME)).matches();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (!isValid) {
            throw new InvalidKeyException("Private key should be provided in PEM format!");
        }
    }

    public void loadPrivateKey()
            throws IOException, InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException {
        InputStream privateKeyStream = settingsManagerService.getRawConfig(PRIVATE_KEY_FILE_NAME);
        final byte[] privateKey = IOUtils.toByteArray(privateKeyStream);

        byte[] decodedPrivateKey = privateKey;
        if (privateKey[0] == '-') {
            decodedPrivateKey = decodePrivateKey(privateKey);
        }
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decodedPrivateKey);
        KeyFactory kf = KeyFactory.getInstance("RSA");

        key = kf.generatePrivate(spec);
    }

    public void loadProperties() throws IOException {
        Properties properties = getProperties(settingsManagerService.getRawConfig(IVR_PROPERTIES_FILE_NAME));
        applicationId = properties.getProperty(APPLICATION_ID_PROP);
        expTimeInHrs = Integer.parseInt(properties.getProperty(EXP_TIME_IN_HRS_PROP, DEFAULT_EXPIRY_TIME));
    }

    public String generateToken() {
        LOGGER.info("Generating JWT");
        Date now = new Date();
        return Jwts.builder()
                .setHeaderParam(TYPE_HEADER, TYPE)
                .setIssuedAt(now)
                .setExpiration(DateUtils.addHours(now, expTimeInHrs))
                .claim(APPLICATION_ID_CLAIM, applicationId)
                .claim(JTI_CLAIM, constructJTI())
                .signWith(SignatureAlgorithm.RS256, key)
                .compact();
    }

    public boolean isTokenValid(String jwt) {
        boolean isTokenValid = false;

        if (jwt != null) {
            try {
                Jws<Claims> claimsJws = Jwts.parser()
                        .setSigningKey(key)
                        .require(APPLICATION_ID_CLAIM, applicationId)
                        .parseClaimsJws(jwt);
                if (claimsJws.getBody().getExpiration().before(new Date())) {
                    LOGGER.debug("Token has expired");
                } else {
                    isTokenValid = true;
                }
            } catch (InvalidClaimException ice) {
                LOGGER.error(String.format("Token is invalid for the following parameter (claim): %s",
                        ice.getClaimName()));
            } catch (ExpiredJwtException ex) {
                LOGGER.debug("Token has expired.");
            }
        }
        return isTokenValid;
    }

    private static String constructJTI() {
        return UUID.randomUUID().toString();
    }

    private byte[] decodePrivateKey(byte[] data) throws UnsupportedEncodingException, InvalidKeyException {
        validatePrivateKey(data);
        String s = new String(data, CHARSET_NAME);
        Matcher extractor = PEM_PATTERN.matcher(s);
        if (extractor.find()) {
            String pemBody = extractor.group(1);
            return DatatypeConverter.parseBase64Binary(pemBody);
        } else {
            throw new InvalidKeyException("Cannot decode private key");
        }
    }

    private Properties getProperties(InputStream propertiesStream) throws IOException {
        Properties properties = new Properties();
        properties.load(propertiesStream);
        return properties;
    }
}
