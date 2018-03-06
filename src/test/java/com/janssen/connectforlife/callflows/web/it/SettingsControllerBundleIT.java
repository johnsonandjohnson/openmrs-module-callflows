package com.janssen.connectforlife.callflows.web.it;

import com.janssen.connectforlife.callflows.contract.ConfigContract;
import com.janssen.connectforlife.callflows.contract.RendererContract;
import com.janssen.connectforlife.callflows.domain.Config;
import com.janssen.connectforlife.callflows.domain.Renderer;
import com.janssen.connectforlife.callflows.helper.ConfigHelper;
import com.janssen.connectforlife.callflows.helper.RendererHelper;
import com.janssen.connectforlife.callflows.service.SettingsService;

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
public class SettingsControllerBundleIT extends RESTControllerPaxIT {

    @Inject
    private SettingsService settingsService;

    private List<Config> configs;

    private List<Renderer> renderers;

    private List<ConfigContract> configContracts;

    private List<RendererContract> rendererContracts;

    @Before
    public void setUp() {
        configs = ConfigHelper.createConfigs();
        configContracts = ConfigHelper.createConfigContracts();
        settingsService.updateConfigs(configs);

        renderers = RendererHelper.createRenderers();
        rendererContracts = RendererHelper.createRendererContracts();
        settingsService.updateRenderers(renderers);
    }

    @After
    public void tearDown() {
        super.tearDown();
        configs = new ArrayList<>();
        settingsService.updateConfigs(configs);

        renderers = new ArrayList<>();
        settingsService.updateRenderers(renderers);
    }

    @Test
    public void shouldReturnService() {
        assertNotNull(settingsService);
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

    @Test
    public void shouldReturnStatusOKForGetAllRenderers() throws IOException, URISyntaxException, InterruptedException {

        // Given
        HttpGet httpGet = buildGetRequest("/callflows/renderers");

        // When we try to get all configs
        HttpResponse response = getHttpClient().execute(httpGet);

        // Then
        assertNotNull(response);
        assertThat(response.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_OK));
    }

    @Test
    public void shouldReturnStatusOKForUpdateRenderers() throws IOException, URISyntaxException, InterruptedException {

        // Given
        HttpPost httpPost = buildPostRequest("/callflows/renderers", json(rendererContracts));

        // When
        HttpResponse response = getHttpClient().execute(httpPost);

        // Then
        assertNotNull(response);
        assertThat(response.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_OK));
    }
}
