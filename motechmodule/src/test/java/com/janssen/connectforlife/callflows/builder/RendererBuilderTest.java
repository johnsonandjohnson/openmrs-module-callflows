package com.janssen.connectforlife.callflows.builder;

import com.janssen.connectforlife.callflows.BaseTest;
import com.janssen.connectforlife.callflows.Constants;
import com.janssen.connectforlife.callflows.contract.RendererContract;
import com.janssen.connectforlife.callflows.domain.Renderer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Renderer Builder Test
 *
 * @author bramak09
 */
@RunWith(MockitoJUnitRunner.class)
public class RendererBuilderTest extends BaseTest {

    @InjectMocks
    private RendererBuilder rendererBuilder = new RendererBuilder();

    private RendererContract rendererContract;

    @Before
    public void setUp() {
        // Given
        rendererContract = new RendererContract();
        rendererContract.setName(Constants.CONFIG_RENDERER_VXML);
        rendererContract.setMimeType(Constants.CONFIG_RENDERER_VXML_MIME);
        rendererContract.setTemplate(Constants.CONFIG_RENDERER_VXML_TPL);
    }

    @Test
    public void shouldBuildRenderer() {
        // When
        Renderer renderer = rendererBuilder.createFrom(rendererContract);

        // Then
        assertThat(renderer.getName(), equalTo(rendererContract.getName()));
        assertThat(renderer.getMimeType(), equalTo(rendererContract.getMimeType()));
        assertThat(renderer.getTemplate(), equalTo(rendererContract.getTemplate()));
    }
}

