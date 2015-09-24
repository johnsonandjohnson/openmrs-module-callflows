package com.janssen.connectforlife.callflows.web.it;

import org.motechproject.security.repository.MotechUsersDataService;
import org.motechproject.security.service.MotechRoleService;
import org.motechproject.security.service.MotechUserService;
import org.motechproject.testing.osgi.BasePaxIT;
import org.motechproject.testing.osgi.TestContext;
import org.motechproject.testing.osgi.container.MotechNativeTestContainerFactory;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.ExamFactory;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;
import org.springframework.http.MediaType;
import javax.inject.Inject;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Locale;

import static junit.framework.TestCase.assertNotNull;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * The base class for REST Controller integration tests running with Pax Exam.
 * All PaxIT tests for REST Controllers have to extend this class.
 *
 * @author nanakapa
 */

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public class RESTControllerPaxIT extends BasePaxIT {

    public static final String HEADER_CONTENT_TYPE = "Content-type";
    public static final String MIME_JSON = "application/json";

    public static final String CFL_TEST_USER_NAME = "motech";
    public static final String CFL_TEST_USER_PASSWORD = "motech";

    @Inject
    private MotechRoleService motechRoleService;

    @Inject
    private MotechUserService motechUserService;

    @Inject
    private MotechUsersDataService motechUsersDataService;

    /**
     * This method creates the test user for PaxIT tests for REST Controllers
     *
     * @throws Exception
     */

    @Before
    public void setup() throws Exception {

        //Create a User using Motech User Service and assign roles to the user
        motechUserService.register(CFL_TEST_USER_NAME,
                                   CFL_TEST_USER_PASSWORD,
                                   "test@test.com",
                                   "",
                                   new ArrayList<String>(),
                                   Locale.ENGLISH);
        login();
    }

    /**
     * This method deletes the test user created for PaxIT tests for REST Controllers
     */
    @After
    public void tearDown() {
        motechUsersDataService.delete(motechUsersDataService.findByUserName(CFL_TEST_USER_NAME));
    }

    /**
     * Dummy method to run this test class
     */
    @Test
    public void testControllerHelper() {
        getLogger().debug("Dummy test method");
    }

    protected void addAuthHeader(HttpGet httpGet, String userName, String password) {
        httpGet.addHeader("Authorization",
                          "Basic " + new String(Base64.encodeBase64((userName + ":" + password).getBytes())));
    }

    protected void addAuthHeader(HttpPost httpPost, String userName, String password) {
        httpPost.addHeader("Authorization",
                           "Basic " + new String(Base64.encodeBase64((userName + ":" + password).getBytes())));

    }

    protected void addAuthHeader(HttpPut httpPut, String userName, String password) {
        httpPut.addHeader("Authorization",
                          "Basic " + new String(Base64.encodeBase64((userName + ":" + password).getBytes())));
    }

    protected void addAuthHeader(HttpDelete httpDelete, String userName, String password) {
        httpDelete.addHeader("Authorization",
                             "Basic " + new String(Base64.encodeBase64((userName + ":" + password).getBytes())));
    }

    /**
     * Converts the provided object into a JSON string
     * This is used in controller unit tests, but is generic to be used elsewhere as well
     *
     * @param obj to convert
     * @return a json string representation of the object
     * @throws IOException if not able to convert
     */
    protected String json(Object obj) throws IOException {
        return new ObjectMapper().writeValueAsString(obj);
    }

    /**
     * Handy wrapper to build a POST request
     *
     * @param path to invoke
     * @param requestBody as json typically
     * @return HttpPost
     * @throws URISyntaxException
     * @throws UnsupportedEncodingException
     */
    protected HttpPost buildPostRequest(String path, String requestBody)
            throws URISyntaxException, UnsupportedEncodingException {
        URIBuilder builder = new URIBuilder();
        builder.setScheme("http").setHost("localhost").setPort(TestContext.getJettyPort())
               .setPath(path);
        URI uri = builder.build();
        HttpPost httpPost = new HttpPost(uri);
        httpPost.setHeader(HEADER_CONTENT_TYPE, MIME_JSON);
        httpPost.setEntity(new StringEntity(requestBody));
        addAuthHeader(httpPost, CFL_TEST_USER_NAME, CFL_TEST_USER_PASSWORD);
        return httpPost;
    }

    /**
     * Handy wrapper to build a PUT request
     *
     * @param path to invoke
     * @param requestBody as json typically
     * @return HttpPut
     * @throws URISyntaxException
     * @throws UnsupportedEncodingException
     */
    protected HttpPut buildPutRequest(String path, String requestBody)
            throws URISyntaxException, UnsupportedEncodingException {
        URIBuilder builder = new URIBuilder();
        builder.setScheme("http").setHost("localhost").setPort(TestContext.getJettyPort())
               .setPath(path);
        URI uri = builder.build();
        HttpPut httpPut = new HttpPut(uri);
        httpPut.setHeader(HEADER_CONTENT_TYPE, MIME_JSON);
        httpPut.setEntity(new StringEntity(requestBody));
        addAuthHeader(httpPut, CFL_TEST_USER_NAME, CFL_TEST_USER_PASSWORD);
        return httpPut;
    }

    /**
     * Handy wrapper to build a GET request
     *
     * @param path to invoke
     * @return HttpGet
     * @throws URISyntaxException
     * @throws UnsupportedEncodingException
     */
    protected HttpGet buildGetRequest(String path, String... params)
            throws URISyntaxException, UnsupportedEncodingException {
        URIBuilder builder = new URIBuilder();
        builder.setScheme("http").setHost("localhost").setPort(TestContext.getJettyPort()).setPath(path);
        for (int i = 0; i < params.length; i += 2) {
            builder.addParameter(params[i], params[i + 1]);
        }
        URI uri = builder.build();
        HttpGet httpGet = new HttpGet(uri);
        addAuthHeader(httpGet, CFL_TEST_USER_NAME, CFL_TEST_USER_PASSWORD);
        return httpGet;
    }

    /**
     * Wrapper to build a DELETE request
     *
     * @param path to build
     * @return HttpDelete
     * @throws URISyntaxException
     * @throws UnsupportedEncodingException
     */
    protected HttpDelete buildDeleteRequest(String path)
            throws URISyntaxException, UnsupportedEncodingException {
        URIBuilder builder = new URIBuilder();
        builder.setScheme("http").setHost("localhost").setPort(TestContext.getJettyPort()).setPath(path);
        URI uri = builder.build();
        HttpDelete httpDelete = new HttpDelete(uri);
        addAuthHeader(httpDelete, CFL_TEST_USER_NAME, CFL_TEST_USER_PASSWORD);
        return httpDelete;
    }


    /**
     * Handy assert for checking that the response is not null, the status is a certain kind and of a certain MediaType
     *
     * @param response  got
     * @param status    to assert
     * @param mediaType to assert
     */
    protected void assertResponseToBe(HttpResponse response, int status, MediaType mediaType) {
        assertNotNull(response);
        assertThat(response.getStatusLine().getStatusCode(), equalTo(status));
        assertThat(response.getEntity().getContentType().getValue(), equalTo(mediaType.toString()));
    }
}
