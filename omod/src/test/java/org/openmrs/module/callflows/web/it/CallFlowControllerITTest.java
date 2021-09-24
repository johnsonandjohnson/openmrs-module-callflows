package org.openmrs.module.callflows.web.it;

import org.apache.http.HttpStatus;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.callflows.Constants;
import org.openmrs.module.callflows.api.contract.CallFlowRequest;
import org.openmrs.module.callflows.api.dao.CallFlowDao;
import org.openmrs.module.callflows.api.domain.CallFlow;
import org.openmrs.module.callflows.api.helper.CallFlowContractHelper;
import org.openmrs.module.callflows.api.helper.CallFlowHelper;
import org.openmrs.module.callflows.api.service.CallFlowService;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * CallFlow Controller Integration Tests
 *
 * @author bramak09
 */
@WebAppConfiguration
public class CallFlowControllerITTest extends BaseModuleWebContextSensitiveTest {

    private CallFlow mainFlow;

    private CallFlow mainFlow2;

    private CallFlow nonMainFlow;

    private CallFlowRequest mainFlowRequest;

    private CallFlowRequest badFlowRequest;

    private CallFlowRequest badFlowRequestWithoutNodes;

    private CallFlow existingFlow;

    @Autowired
    private CallFlowDao callFlowDao;

    @Autowired
    private CallFlowService callFlowService;

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setUp() {
        // for create
        mainFlowRequest = CallFlowContractHelper.createMainFlowRequest();
        badFlowRequest = CallFlowContractHelper.createBadFlowRequest();
        badFlowRequestWithoutNodes = CallFlowContractHelper.createBadFlowRequestWithoutNodes();
        mainFlow = CallFlowHelper.createMainFlow();
        // for searches
        mainFlow2 = CallFlowHelper.createMainFlow();
        mainFlow2.setName("MainFlow2");

        nonMainFlow = CallFlowHelper.createMainFlow();
        nonMainFlow.setName("NonMainFlow");

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @After
    public void tearDown() {
        callFlowDao.deleteAll();
    }

    @Test
    public void shouldReturnStatusOkOnCreateCallFlow() throws Exception {
        mockMvc.perform(post("/callflows/flows").contentType(MediaType.APPLICATION_JSON)
                .content(json(mainFlowRequest)))
                .andExpect(status().is(HttpStatus.SC_OK))
                .andExpect(content().contentType(Constants.APPLICATION_JSON_UTF8));
    }

    @Test
    public void shouldReturnBadRequestOnCreateFlowWithBadRaw() throws Exception {
        mockMvc.perform(post("/callflows/flows").contentType(MediaType.APPLICATION_JSON)
                .content(json(badFlowRequest)))
                .andExpect(status().is(HttpStatus.SC_BAD_REQUEST))
                .andExpect(content().contentType(Constants.APPLICATION_JSON_UTF8));
    }

    @Test
    public void shouldReturnBadRequestOnCreateFlowWithBadRawNoNodes() throws Exception {
        mockMvc.perform(post("/callflows/flows").contentType(MediaType.APPLICATION_JSON)
                .content(json(badFlowRequestWithoutNodes)))
                .andExpect(status().is(HttpStatus.SC_BAD_REQUEST))
                .andExpect(content().contentType(Constants.APPLICATION_JSON_UTF8));
    }

    @Test
    public void shouldReturnStatusConflictOnCreateDuplicateCallFlow() throws Exception {
        // Given
        callFlowService.create(CallFlowHelper.createMainFlow());
        mockMvc.perform(post("/callflows/flows").contentType(MediaType.APPLICATION_JSON)
                .content(json(mainFlowRequest)))
                .andExpect(status().is(HttpStatus.SC_BAD_REQUEST))
                .andExpect(content().contentType(Constants.APPLICATION_JSON_UTF8));
    }

    @Test
    public void shouldReturnStatusBadRequestOnCreationOfBadCallFlow() throws Exception {
        mockMvc.perform(post("/callflows/flows").contentType(MediaType.APPLICATION_JSON)
                .content(json(badFlowRequest)))
                .andExpect(status().is(HttpStatus.SC_BAD_REQUEST))
                .andExpect(content().contentType(Constants.APPLICATION_JSON_UTF8));
    }

    @Test
    public void shouldReturnStatusOkOnUpdateCallFlow() throws Exception {
        // Given
        existingFlow = callFlowService.create(mainFlow);
        mockMvc.perform(put("/callflows/flows/" + existingFlow.getId()).contentType(MediaType.APPLICATION_JSON)
                .content(json(mainFlowRequest)))
                .andExpect(status().is(HttpStatus.SC_OK))
                .andExpect(content().contentType(Constants.APPLICATION_JSON_UTF8));
    }

    @Test
    public void shouldReturnStatusConflictOnUpdateThatLeadsToDuplicateCallFlow() throws Exception {
        // Given Two flows with name MainFlow and NewMainFlow
        CallFlow flow1 = CallFlowHelper.createMainFlow();
        CallFlow flow2 = CallFlowHelper.createMainFlow();
        flow2.setName("NewMainFlow");
        callFlowService.create(flow1);
        CallFlow flowToUpdate = callFlowService.create(flow2);

        // When we try to update flow2 to have the same name as flow1
        mainFlowRequest.setName(flow1.getName());

        mockMvc.perform(put("/callflows/flows/" + flow2.getId()).contentType(MediaType.APPLICATION_JSON)
                .content(json(mainFlowRequest)))
                .andExpect(status().is(HttpStatus.SC_BAD_REQUEST))
                .andExpect(content().contentType(Constants.APPLICATION_JSON_UTF8));
    }

    @Test
    public void shouldReturnStatusBadRequestOnUpdateOfBadCallFlow() throws Exception {
        // Given
        mainFlowRequest.setName("BadFlow.");
        mockMvc.perform(put("/callflows/flows/1").contentType(MediaType.APPLICATION_JSON)
                .content(json(mainFlowRequest)))
                .andExpect(status().is(HttpStatus.SC_BAD_REQUEST))
                .andExpect(content().contentType(Constants.APPLICATION_JSON_UTF8));
    }

    @Test
    public void shouldReturnStatusOKForSuccessfulCallFlowSearches() throws Exception {
        // Given three call flows that have names MainFlow and MainFlow2 and NonMainFlow
        callFlowService.create(mainFlow);
        callFlowService.create(mainFlow2);
        callFlowService.create(nonMainFlow);

        // When we search for callflows by the name prefix "Ma"
        mockMvc.perform(get("/callflows/flows")
                .param("lookup", "By Name")
                .param("term", "Ma"))
                .andExpect(status().is(HttpStatus.SC_OK))
                .andExpect(content().contentType(Constants.APPLICATION_JSON_UTF8));
    }

    @Test
    public void shouldReturnStatusOKForUnSuccessfulCallFlowSearches() throws Exception {
        // Given three call flows that have names MainFlow and MainFlow2 and NonMainFlow
        callFlowService.create(mainFlow);
        callFlowService.create(mainFlow2);
        callFlowService.create(nonMainFlow);

        // When we search for callflows by the name prefix "Xu" (invalid)
        mockMvc.perform(get("/callflows/flows")
                .param("lookup", "By Name")
                .param("term", "Xu"))
                .andExpect(status().is(HttpStatus.SC_OK))
                .andExpect(content().contentType(Constants.APPLICATION_JSON_UTF8));
    }

    @Test
    public void shouldReturnStatusOKForSuccessfulDelete() throws Exception {
        // Given a callflow by name MainFlow
        mainFlow = callFlowService.create(mainFlow);
        // When we try to delete this callflow by passing it's ID
        mockMvc.perform(delete("/callflows/flows/" + mainFlow.getId()))
                .andExpect(status().is(HttpStatus.SC_OK));
    }

    @Test
    public void shouldReturnStatusBadRequestForUnsuccessfulDelete() throws Exception {
        // Given a callflow by name MainFlow
        mainFlow = callFlowService.create(mainFlow);
        // When we invoke a deletion operation with a invalid ID
        mockMvc.perform(delete("/callflows/flows/-1"))
                .andExpect(status().is(HttpStatus.SC_BAD_REQUEST));
    }

    private String json(Object obj) throws IOException {
        return new ObjectMapper().writeValueAsString(obj);
    }
}
