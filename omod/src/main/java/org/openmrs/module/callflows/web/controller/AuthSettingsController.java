package org.openmrs.module.callflows.web.controller;

import static org.openmrs.module.callflows.api.util.AuthUtil.IVR_PROPERTIES_FILE_NAME;
import static org.openmrs.module.callflows.api.util.AuthUtil.PRIVATE_KEY_FILE_NAME;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.callflows.api.service.SettingsManagerService;
import org.openmrs.module.callflows.api.util.AuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping(value = "/callflows/auth")
public class AuthSettingsController extends RestController {

    private static final Log LOGGER = LogFactory.getLog(AuthSettingsController.class);

    @Autowired
    private SettingsManagerService settingsManagerService;

    @Autowired
    private AuthUtil authUtil;

    /**
     * Loads private key from the uploaded file.
     *
     * @param file the file containing the private key file
     * @throws IOException if there was a problem reading the file
     */
    @RequestMapping(value = "upload/private-key", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void uploadPrivateKey(@RequestParam("file") MultipartFile file) throws IOException,
        InvalidKeyException, InvalidKeySpecException, NoSuchAlgorithmException {
        authUtil.validatePrivateKey(file.getBytes());
        settingsManagerService.saveRawConfig(PRIVATE_KEY_FILE_NAME,
            new ByteArrayResource(file.getBytes()));
        authUtil.loadPrivateKey();
        LOGGER.info("Private key file has been uploaded");
    }

    /**
     * Loads IVR properties from the uploaded file.
     *
     * @param file the file containing the IVR properties file
     * @throws IOException if there was a problem reading the file
     */
    @RequestMapping(value = "upload/ivr-properties", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void uploadIvrProperties(@RequestParam("file") MultipartFile file) throws IOException,
        NoSuchAlgorithmException, InvalidKeyException, InvalidKeySpecException {
        settingsManagerService.saveRawConfig(IVR_PROPERTIES_FILE_NAME,
            new ByteArrayResource(file.getBytes()));
        authUtil.loadProperties();
        LOGGER.info("IVR properties file has been uploaded");
    }

}

