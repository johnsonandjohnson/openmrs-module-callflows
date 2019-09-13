package org.openmrs.module.callflows.web.it;

import org.apache.http.HttpStatus;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.callflows.Constants;
import org.openmrs.module.callflows.api.contract.ConfigContract;
import org.openmrs.module.callflows.api.contract.RendererContract;
import org.openmrs.module.callflows.api.domain.Config;
import org.openmrs.module.callflows.api.domain.Renderer;
import org.openmrs.module.callflows.api.helper.ConfigHelper;
import org.openmrs.module.callflows.api.helper.RendererHelper;
import org.openmrs.module.callflows.api.service.ConfigService;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Config Controller Integration Tests
 *
 * @author bramak09
 */
@WebAppConfiguration
public class SettingsControllerITTest extends BaseModuleWebContextSensitiveTest {

	@Autowired
	private ConfigService configService;

	@Autowired
	private WebApplicationContext webApplicationContext;

	private MockMvc mockMvc;

	private List<Config> configs;

	private List<Renderer> renderers;

	private List<ConfigContract> configContracts;

	private List<RendererContract> rendererContracts;

	@Before
	public void setUp() {
		configs = ConfigHelper.createConfigs();
		configContracts = ConfigHelper.createConfigContracts();
		configService.updateConfigs(configs);

		renderers = RendererHelper.createRenderers();
		rendererContracts = RendererHelper.createRendererContracts();
		configService.updateRenderers(renderers);

		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}

	@After
	public void tearDown() {
		configs = new ArrayList<>();
		configService.updateConfigs(configs);

		renderers = new ArrayList<>();
		configService.updateRenderers(renderers);
	}

	@Test
	public void shouldReturnService() {
		assertNotNull(configService);
	}

	@Test
	public void shouldReturnStatusOKForGetAllConfigs() throws Exception {
		mockMvc.perform(get("/callflows/configs"))
				.andExpect(status().is(HttpStatus.SC_OK))
				.andExpect(content().contentType(Constants.APPLICATION_JSON_UTF8));
	}

	@Test
	public void shouldReturnStatusOKForUpdateConfigs() throws Exception {
		mockMvc.perform(post("/callflows/configs").contentType(MediaType.APPLICATION_JSON)
				.content(json(configContracts)))
				.andExpect(status().is(HttpStatus.SC_OK))
				.andExpect(content().contentType(Constants.APPLICATION_JSON_UTF8));
	}

	@Test
	public void shouldReturnStatusOKForGetAllRenderers() throws Exception {
		mockMvc.perform(get("/callflows/renderers"))
				.andExpect(status().is(HttpStatus.SC_OK))
				.andExpect(content().contentType(Constants.APPLICATION_JSON_UTF8));
	}

	@Test
	public void shouldReturnStatusOKForUpdateRenderers() throws Exception {
		mockMvc.perform(post("/callflows/renderers").contentType(MediaType.APPLICATION_JSON)
				.content(json(rendererContracts)))
				.andExpect(status().is(HttpStatus.SC_OK))
				.andExpect(content().contentType(Constants.APPLICATION_JSON_UTF8));
	}

	private String json(Object obj) throws IOException {
		return new ObjectMapper().writeValueAsString(obj);
	}
}
