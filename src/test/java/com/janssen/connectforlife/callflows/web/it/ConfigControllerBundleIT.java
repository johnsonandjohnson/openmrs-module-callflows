package com.janssen.connectforlife.callflows.web.it;

import com.janssen.connectforlife.callflows.contract.ConfigContract;
import com.janssen.connectforlife.callflows.domain.Config;
import com.janssen.connectforlife.callflows.helper.ConfigContractHelper;
import com.janssen.connectforlife.callflows.helper.ConfigHelper;
import com.janssen.connectforlife.callflows.service.ConfigService;

import org.motechproject.testing.osgi.container.MotechNativeTestContainerFactory;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.ExamFactory;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;
import javax.inject.Inject;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertNotNull;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Config Controller Integration Tests
 *
 * @author bramak09
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public class ConfigControllerBundleIT extends RESTControllerPaxIT {

    @Inject
    private ConfigService configService;

    private List<Config> configs;

    private List<ConfigContract> configContracts;

    @Before
    public void setUp() {
        configs = ConfigHelper.createConfigs();
        configContracts = ConfigContractHelper.createConfigContracts();
        configService.updateConfigs(configs);
    }

    @After
    public void tearDown() {
        configs = new ArrayList<>();
        configService.updateConfigs(configs);
    }

    @Test
    public void shouldReturnService() {
        assertNotNull(configService);
    }

    @Test
    public void shouldReturnStatusOKForGetAllConfigs() throws IOException, URISyntaxException, InterruptedException {

        // Given
        HttpGet httpGet = buildGetRequest("/callflows/configs");

        // When we try to get all configs
        HttpResponse response = getHttpClient().execute(httpGet);

        // Then
        assertNotNull(response);
        assertThat(response.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_OK));
    }

    @Test
    public void shouldReturnStatusOKForUpdateConfigs() throws IOException, URISyntaxException, InterruptedException {

        // Given
        HttpPost httpPost = buildPostRequest("/callflows/configs", json(configContracts));

        // When
        HttpResponse response = getHttpClient().execute(httpPost);

        // Then
        assertNotNull(response);
        assertThat(response.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_OK));
    }
}
