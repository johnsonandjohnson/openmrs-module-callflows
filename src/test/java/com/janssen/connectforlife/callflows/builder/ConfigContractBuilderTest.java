package com.janssen.connectforlife.callflows.builder;

import com.janssen.connectforlife.callflows.BaseTest;
import com.janssen.connectforlife.callflows.Constants;
import com.janssen.connectforlife.callflows.contract.ConfigContract;
import com.janssen.connectforlife.callflows.contract.RendererContract;
import com.janssen.connectforlife.callflows.domain.Config;
import com.janssen.connectforlife.callflows.domain.Renderer;
import com.janssen.connectforlife.callflows.helper.ConfigHelper;
import com.janssen.connectforlife.callflows.helper.GenericHelper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import java.io.IOException;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Config Contract Builder Test
 *
 * @author bramak09
 */
@RunWith(MockitoJUnitRunner.class)
public class ConfigContractBuilderTest extends BaseTest {

    private Config voxeo;

    private RendererContract vxmlRendererContract;

    private RendererContract txtRendererContract;

    private Renderer vxmlRenderer;

    private Renderer txtRenderer;

    @Mock
    private RendererContractBuilder rendererContractBuilder = new RendererContractBuilder();

    @InjectMocks
    private ConfigContractBuilder configContractBuilder = new ConfigContractBuilder();

    @Before
    public void setUp() {
        voxeo = ConfigHelper.createConfigs().get(0);

        vxmlRenderer = voxeo.getRenderersMap().get(Constants.CONFIG_RENDERER_VXML);
        txtRenderer = voxeo.getRenderersMap().get(Constants.CONFIG_RENDERER_TXT);

        Map<String, RendererContract> renderers = GenericHelper.buildRenderersContractMap();
        vxmlRendererContract = renderers.get(Constants.CONFIG_RENDERER_VXML);
        txtRendererContract = renderers.get(Constants.CONFIG_RENDERER_TXT);
    }

    @Test
    public void shouldBuildConfigContractFromConfig() throws IOException {
        // Given
        given(rendererContractBuilder.createFrom(vxmlRenderer)).willReturn(vxmlRendererContract);
        given(rendererContractBuilder.createFrom(txtRenderer)).willReturn(txtRendererContract);

        // When
        ConfigContract voxeoContract = configContractBuilder.createFrom(voxeo);

        // Then
        verify(rendererContractBuilder, times(1)).createFrom(vxmlRenderer);
        verify(rendererContractBuilder, times(1)).createFrom(txtRenderer);

        assertThat(voxeoContract.getName(), equalTo(voxeo.getName()));
        assertThat(voxeoContract.getOutgoingCallMethod(), equalTo(voxeo.getOutgoingCallMethod()));
        assertThat(voxeoContract.getOutgoingCallUriTemplate(), equalTo(voxeo.getOutgoingCallUriTemplate()));

        assertThat(voxeoContract.getTestUsersMap().size(), equalTo(voxeo.getTestUsersMap().size()));
        assertThat(voxeoContract.getTestUsersMap(), equalTo(voxeo.getTestUsersMap()));

        assertThat(voxeoContract.getServicesMap().size(), equalTo(voxeo.getServicesMap().size()));
        assertThat(voxeoContract.getServicesMap(), equalTo(voxeo.getServicesMap()));

        assertThat(voxeoContract.getRenderersMap().size(), equalTo(voxeo.getRenderersMap().size()));
        // Signature's different on either side, so we compare content as a json string
        assertThat(json(voxeoContract.getRenderersMap()), equalTo(json(voxeo.getRenderersMap())));

    }
}

