package org.openmrs.module.callflows.api.builder;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import org.openmrs.module.callflows.BaseTest;
import org.openmrs.module.callflows.Constants;
import org.openmrs.module.callflows.api.contract.RendererContract;
import org.openmrs.module.callflows.api.domain.Renderer;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Renderer Contract Builder Test
 *
 * @author bramak09
 */
@RunWith(MockitoJUnitRunner.class)
public class RendererContractBuilderTest extends BaseTest {

    @InjectMocks
    private RendererContractBuilder rendererContractBuilder = new RendererContractBuilder();

    private Renderer renderer;

    @Before
    public void setUp() {
        // Given
        renderer = new Renderer();
        renderer.setName(Constants.CONFIG_RENDERER_VXML);
        renderer.setMimeType(Constants.CONFIG_RENDERER_VXML_MIME);
        renderer.setTemplate(Constants.CONFIG_RENDERER_VXML_TPL);
    }

    @Test
    public void shouldBuildRendererContract() {
        // When
        RendererContract rendererContract = rendererContractBuilder.createFrom(renderer);

        // Then
        assertThat(rendererContract.getName(), equalTo(renderer.getName()));
        assertThat(rendererContract.getMimeType(), equalTo(renderer.getMimeType()));
        assertThat(rendererContract.getTemplate(), equalTo(renderer.getTemplate()));
    }
}

