package com.janssen.connectforlife.callflows.builder;

import com.janssen.connectforlife.callflows.BaseTest;
import com.janssen.connectforlife.callflows.Constants;
import com.janssen.connectforlife.callflows.contract.ConfigContract;
import com.janssen.connectforlife.callflows.contract.RendererContract;
import com.janssen.connectforlife.callflows.domain.Config;
import com.janssen.connectforlife.callflows.domain.Renderer;
import com.janssen.connectforlife.callflows.helper.ConfigContractHelper;
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
 * Config Builder Test
 *
 * @author bramak09
 */
@RunWith(MockitoJUnitRunner.class)
public class ConfigBuilderTest extends BaseTest {

    private ConfigContract voxeoContract;

    private RendererContract vxmlRendererContract;

    private RendererContract txtRendererContract;

    private Renderer vxmlRenderer;

    private Renderer txtRenderer;

    @Mock
    private RendererBuilder rendererBuilder = new RendererBuilder();

    @InjectMocks
    private ConfigBuilder configBuilder = new ConfigBuilder();

    @Before
    public void setUp() {
        voxeoContract = ConfigContractHelper.createConfigContracts().get(0);

        vxmlRendererContract = voxeoContract.getRenderersMap().get(Constants.CONFIG_RENDERER_VXML);
        txtRendererContract = voxeoContract.getRenderersMap().get(Constants.CONFIG_RENDERER_TXT);

        Map<String, Renderer> renderers = GenericHelper.buildRenderersMap();
        vxmlRenderer = renderers.get(Constants.CONFIG_RENDERER_VXML);
        txtRenderer = renderers.get(Constants.CONFIG_RENDERER_TXT);
    }

    @Test
    public void shouldBuildConfigFromContract() throws IOException {
        // Given
        given(rendererBuilder.createFrom(vxmlRendererContract)).willReturn(vxmlRenderer);
        given(rendererBuilder.createFrom(txtRendererContract)).willReturn(txtRenderer);

        // When
        Config voxeo = configBuilder.createFrom(voxeoContract);

        // Then
        verify(rendererBuilder, times(1)).createFrom(vxmlRendererContract);
        verify(rendererBuilder, times(1)).createFrom(txtRendererContract);

        assertThat(voxeo.getName(), equalTo(voxeoContract.getName()));
        assertThat(voxeo.getOutgoingCallMethod(), equalTo(voxeoContract.getOutgoingCallMethod()));
        assertThat(voxeo.getOutgoingCallUriTemplate(), equalTo(voxeoContract.getOutgoingCallUriTemplate()));

        assertThat(voxeo.getTestUsersMap().size(), equalTo(voxeoContract.getTestUsersMap().size()));
        assertThat(voxeo.getTestUsersMap(), equalTo(voxeoContract.getTestUsersMap()));

        assertThat(voxeo.getServicesMap().size(), equalTo(voxeoContract.getServicesMap().size()));
        assertThat(voxeo.getServicesMap(), equalTo(voxeoContract.getServicesMap()));

        assertThat(voxeo.getRenderersMap().size(), equalTo(voxeoContract.getRenderersMap().size()));
        // The signature's different on either side, so we compare content as a json string
        assertThat(json(voxeo.getRenderersMap()), equalTo(json(voxeoContract.getRenderersMap())));

    }

}

