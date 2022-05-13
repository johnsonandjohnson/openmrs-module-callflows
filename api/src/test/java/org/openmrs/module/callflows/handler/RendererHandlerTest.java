/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.callflows.handler;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.openmrs.module.callflows.api.domain.Renderer;
import org.openmrs.module.callflows.api.service.ConfigService;
import org.openmrs.module.callflows.handler.metadatasharing.RendererHandler;
import org.openmrs.test.BaseContextMockTest;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RendererHandlerTest extends BaseContextMockTest {

    @Mock
    private ConfigService configService;

    @Before
    public void setupConfigServiceMock() {
        contextMockHelper.setService(ConfigService.class, configService);
    }

    @Test
    public void shouldReturnCorrectRendererId() {
        // given
        final String rendererName = "rendererName";
        final Integer expectedRendererId = rendererName.hashCode();
        final Renderer rendererWithId = new Renderer();
        rendererWithId.setName(rendererName);

        final RendererHandler handler = new RendererHandler();

        // when
        final Integer rendererId = handler.getId(rendererWithId);

        // then
        assertEquals(rendererId, expectedRendererId);
    }

    @Test
    public void shouldReturnCorrectRendererUUID() {
        // given
        final String rendererName = "rendererName";
        final Renderer rendererWithUUID = new Renderer();
        rendererWithUUID.setName(rendererName);

        final RendererHandler handler = new RendererHandler();

        // when
        final String rendererUUID = handler.getUuid(rendererWithUUID);

        // then
        assertEquals(rendererUUID, rendererName);
    }

    @Test
    public void shouldReturnCorrectName() {
        // given
        final String rendererName = "rendererName";
        final Renderer rendererWithName = new Renderer();
        rendererWithName.setName(rendererName);

        final RendererHandler handler = new RendererHandler();

        // when
        final String testedRendererName = handler.getName(rendererWithName);

        // then
        assertEquals(testedRendererName, rendererName);
    }

    @Test
    public void shouldReturnOnlyMimeTypeProperty() {
        // given
        final String mimeType = "mimeType";
        final Renderer rendererWithMimeType = new Renderer();
        rendererWithMimeType.setMimeType(mimeType);

        final RendererHandler handler = new RendererHandler();

        // when
        final Map<String, Object> properties = handler.getProperties(rendererWithMimeType);

        // then
        assertEquals(properties.size(), 1);
        assertEquals(properties.entrySet().iterator().next().getValue(), mimeType);
    }

    @Test
    public void shouldSaveNewRenderer() {
        // given
        final Renderer newRenderer = new Renderer();
        newRenderer.setName("name");
        newRenderer.setMimeType("mimeType");
        newRenderer.setTemplate("template");

        final RendererHandler handler = new RendererHandler();

        // when
        handler.saveItem(newRenderer);

        // then
        final ArgumentCaptor<List> argumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(configService).updateRenderers(argumentCaptor.capture());
        final List<Renderer> capturedList = argumentCaptor.getValue();

        assertEquals(capturedList.size(), 1);
        assertEquals(capturedList.get(0).getName(), newRenderer.getName());
        assertEquals(capturedList.get(0).getMimeType(), newRenderer.getMimeType());
        assertEquals(capturedList.get(0).getTemplate(), newRenderer.getTemplate());
    }

    @Test
    public void shouldUpdateExistingRenderer() {
        // given
        final Renderer existingRenderer = new Renderer();
        existingRenderer.setName("name");
        existingRenderer.setMimeType("mimeType");
        existingRenderer.setTemplate("template");

        final Renderer updatedRenderer = new Renderer();
        updatedRenderer.setName("name");
        updatedRenderer.setMimeType("mimeType");
        updatedRenderer.setTemplate("updated");

        final RendererHandler handler = new RendererHandler();

        when(configService.hasRenderer(existingRenderer.getName())).thenReturn(Boolean.TRUE);
        when(configService.getRenderer(existingRenderer.getName())).thenReturn(existingRenderer);
        when(configService.allRenderers()).thenReturn(singletonList(existingRenderer));

        // when
        handler.saveItem(updatedRenderer);

        // then
        final ArgumentCaptor<List> argumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(configService).updateRenderers(argumentCaptor.capture());
        final List<Renderer> capturedList = argumentCaptor.getValue();

        assertEquals(capturedList.size(), 1);
        assertEquals(capturedList.get(0).getName(), updatedRenderer.getName());
        assertEquals(capturedList.get(0).getMimeType(), updatedRenderer.getMimeType());
        assertEquals(capturedList.get(0).getTemplate(), updatedRenderer.getTemplate());
    }

    @Test
    public void shouldReturnCorrectCountOfRenderers() {
        // given
        final List<Renderer> allRenderers = Arrays.asList(new Renderer(), new Renderer());
        when(configService.allRenderers()).thenReturn(allRenderers);

        final RendererHandler handler = new RendererHandler();

        // when
        final int count = handler.getItemsCount(Renderer.class, false, null);

        // then
        assertEquals(count, allRenderers.size());
    }

    @Test
    public void shouldReturnAllRenderers() {
        // given
        final Renderer[] allRenderersArray = new Renderer[]{new Renderer(), new Renderer()};
        final List<Renderer> allRenderers = Arrays.asList(allRenderersArray);

        when(configService.allRenderers()).thenReturn(allRenderers);

        final RendererHandler handler = new RendererHandler();

        // when
        final List<Renderer> returnedRenderers = handler.getItems(Renderer.class, false, null, null, null);

        // then
        assertEquals(returnedRenderers.size(), allRenderers.size());
        assertThat(returnedRenderers, CoreMatchers.hasItems(allRenderersArray));
    }

    @Test
    public void shouldReturnPaginatedRenderers() {
        // given
        final Renderer[] allRenderersArray = new Renderer[]{new Renderer(), new Renderer()};
        final List<Renderer> allRenderers = Arrays.asList(allRenderersArray);

        when(configService.allRenderers()).thenReturn(allRenderers);

        final RendererHandler handler = new RendererHandler();

        // when
        final List<Renderer> returnedRenderers = handler.getItems(Renderer.class, false, null, 1, 10);

        // then
        assertEquals(returnedRenderers.size(), 1);
        assertThat(returnedRenderers, CoreMatchers.hasItem(allRenderersArray[1]));
    }

    @Test
    public void shouldReturnRendererById() {
        // given
        final Renderer correctRenderer = new Renderer();
        correctRenderer.setName("correctRenderer");
        final Renderer incorrectRenderer = new Renderer();
        incorrectRenderer.setName("incorrectRenderer");

        when(configService.allRenderers()).thenReturn(Arrays.asList(correctRenderer, incorrectRenderer));

        final RendererHandler handler = new RendererHandler();

        // when
        final Renderer returned = handler.getItemById(Renderer.class, correctRenderer.getName().hashCode());

        // then
        assertEquals(returned, correctRenderer);
    }

    @Test
    public void shouldReturnRendererByUUID() {
        // given
        final Renderer correctRenderer = new Renderer();
        correctRenderer.setName("correctRenderer");
        final Renderer incorrectRenderer = new Renderer();
        incorrectRenderer.setName("incorrectRenderer");

        when(configService.allRenderers()).thenReturn(Arrays.asList(incorrectRenderer, correctRenderer));

        final RendererHandler handler = new RendererHandler();

        // when
        final Renderer returned = handler.getItemByUuid(Renderer.class, correctRenderer.getName());

        // then
        assertEquals(returned, correctRenderer);
    }
}
