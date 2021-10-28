package org.openmrs.module.callflows.api.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.InvalidClaimException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.bouncycastle.util.io.pem.PemWriter;
import org.openmrs.module.callflows.api.service.SettingsManagerService;
import org.springframework.core.io.ByteArrayResource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Date;
import java.util.UUID;

/**
 * Collection of utility methods to authenticate IVR provider
 *
 * @author nanakapa
 */
public class AuthUtil {

    public static final String PRIVATE_KEY_FILE_NAME = "private.key";
    public static final String IVR_PROPERTIES_FILE_NAME = "ivr.properties";

    static final String APPLICATION_ID_CLAIM_NAME = "application_id";
    static final String JTI_CLAIM_NAME = "jti";
    static final String TYPE_HEADER_NAME = "typ";
    static final String TYPE_HEADER_VALUE = "JWT";

    private static final Log LOGGER = LogFactory.getLog(CallUtil.class);

    private SettingsManagerService settingsManagerService;

    private static String constructJTI() {
        return UUID.randomUUID().toString();
    }

    /**
     * Generate new JWT token.
     *
     * @return the String with new JWT token, never null
     * @throws IOException if the method failed to read configuration file or private key file
     */
    public String generateToken() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        LOGGER.info("Generating JWT");

        try (InputStream ivrPropertiesFileStream = settingsManagerService.getRawConfig(IVR_PROPERTIES_FILE_NAME);
             InputStream privateKeyFileStream = settingsManagerService.getRawConfig(PRIVATE_KEY_FILE_NAME)) {

            final IVRProperties ivrProperties = new IVRProperties(ivrPropertiesFileStream);
            final PrivateKey key = loadPrivateKey(privateKeyFileStream, ivrProperties);
            final Date issuedAt = DateUtil.now();

            return Jwts
                    .builder()
                    .setHeaderParam(TYPE_HEADER_NAME, TYPE_HEADER_VALUE)
                    .setIssuedAt(issuedAt)
                    .setExpiration(DateUtils.addHours(issuedAt, ivrProperties.getExpirationTimeInHours()))
                    .claim(APPLICATION_ID_CLAIM_NAME, ivrProperties.getApplicationId())
                    .claim(JTI_CLAIM_NAME, constructJTI())
                    .signWith(SignatureAlgorithm.RS256, key)
                    .compact();
        }
    }

    private PrivateKey loadPrivateKey(final InputStream pemFileStream, final IVRProperties ivrProperties)
            throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        final PemObject pemObject = loadPemFile(pemFileStream);
        final KeyFactory keyFactory = KeyFactory.getInstance(ivrProperties.getKeyAlgorithm());
        final PKCS8EncodedKeySpec privateKeySpecification = new PKCS8EncodedKeySpec(pemObject.getContent());

        return keyFactory.generatePrivate(privateKeySpecification);
    }

    private PemObject loadPemFile(final InputStream pemFileStream) throws IOException {
        final PemReader pemReader = new PemReader(new InputStreamReader(pemFileStream));
        return pemReader.readPemObject();
    }

    /**
     * Save new private key.
     * <p>
     * The method overrides current private key.
     * </p>
     *
     * @param newPrivateKeyFile teh stream with new private key file in PEM format, the caller is responsible for closing
     *                          this stream, not null
     * @throws IOException if the new key could not be read from the {@code newPrivateKeyFile} or it was not possible to
     *                     write the new key
     */
    public void saveNewPrivateKey(final InputStream newPrivateKeyFile) throws IOException {
        final PemObject pemObject = loadPemFile(newPrivateKeyFile);

        final ByteArrayOutputStream newPrivateKeyFileBytesStream = new ByteArrayOutputStream();

        try (PemWriter pemWriter = new PemWriter(new OutputStreamWriter(newPrivateKeyFileBytesStream))) {
            pemWriter.writeObject(pemObject);
        }

        settingsManagerService.saveRawConfig(PRIVATE_KEY_FILE_NAME,
                new ByteArrayResource(newPrivateKeyFileBytesStream.toByteArray()));
    }

    /**
     * Checks if the JWT Token {@code jwt} is valid.
     *
     * @param jwt the String with JWT Token to check, not null
     * @return true if the {@code jwt} is a valid token, false otherwise
     */
    public boolean isTokenValid(String jwt) {
        if (StringUtils.isEmpty(jwt)) {
            return false;
        }

        boolean isTokenValid = false;

        try (InputStream ivrPropertiesFileStream = settingsManagerService.getRawConfig(IVR_PROPERTIES_FILE_NAME);
             InputStream privateKeyFileStream = settingsManagerService.getRawConfig(PRIVATE_KEY_FILE_NAME)) {

            final IVRProperties ivrProperties = new IVRProperties(ivrPropertiesFileStream);
            final PrivateKey key = loadPrivateKey(privateKeyFileStream, ivrProperties);

            final Jws<Claims> claimsJws = Jwts
                    .parser()
                    .setSigningKey(key)
                    .require(APPLICATION_ID_CLAIM_NAME, ivrProperties.getApplicationId())
                    .parseClaimsJws(jwt);

            if (claimsJws.getBody().getExpiration().before(DateUtil.now())) {
                LOGGER.warn("Token has expired");
                isTokenValid = false;
            } else {
                isTokenValid = true;
            }
        } catch (InvalidClaimException ice) {
            LOGGER.error(String.format("Token is invalid for the following parameter (claim): %s", ice.getClaimName()));
        } catch (ExpiredJwtException ex) {
            LOGGER.warn("Token has expired.");
        } catch (Exception ex) {
            LOGGER.error("Failed to verify if JWT token is valid.", ex);
        }

        return isTokenValid;
    }

    public void setSettingsManagerService(SettingsManagerService settingsManagerService) {
        this.settingsManagerService = settingsManagerService;
    }
}
