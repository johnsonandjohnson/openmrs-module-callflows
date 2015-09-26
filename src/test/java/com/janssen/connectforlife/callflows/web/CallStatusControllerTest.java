package com.janssen.connectforlife.callflows.web;

import com.janssen.connectforlife.callflows.BaseTest;
import com.janssen.connectforlife.callflows.Constants;
import com.janssen.connectforlife.callflows.domain.Call;
import com.janssen.connectforlife.callflows.helper.CallHelper;
import com.janssen.connectforlife.callflows.service.CallService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.server.MockMvc;
import org.springframework.test.web.server.setup.MockMvcBuilders;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;

/**
 * Call Status Controller Test
 *
 * @author nanakapa
 */

@RunWith(MockitoJUnitRunner.class)
public class CallStatusControllerTest extends BaseTest {
    private MockMvc mockMvc;

    private Call call;

    @InjectMocks
    private CallStatusController callStatusController = new CallStatusController();

    @Mock
    private CallService callService;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(callStatusController).build();
        call = CallHelper.createOutboundCall();
    }

    @Test
    public void shouldReturnXMLOkIfTheCallStatusUpdateIsSuccessful() throws Exception {
        //Given
        given(callService.findByCallId(Constants.CALL_ID)).willReturn(call);

        //When && Then
        mockMvc.perform(get("/status/" + Constants.CALL_ID + "?status = ANSWERED & reason = call answered"))

               .andExpect(status().is(HttpStatus.OK.value())).andExpect(content().string(Constants.XML_OK_RESPONSE));
    }

    @Test
    public void shouldReturnXMLErrorIfTheCallIsNotFoundInDatabase() throws Exception {

        //Given
        given(callService.findByCallId(Constants.CALL_ID)).willReturn(null);

        //When && Then
        mockMvc.perform(get("/status/" + Constants.CALL_ID + "?status = ANSWERED & reason = call answered"))

               .andExpect(status().is(HttpStatus.OK.value())).andExpect(content().string(Constants.XML_ERROR_RESPONSE));

    }
}
