package org.openmrs.module.callflows.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openmrs.module.callflows.BaseTest;
import org.openmrs.module.callflows.Constants;
import org.openmrs.module.callflows.api.domain.Call;
import org.openmrs.module.callflows.api.helper.CallHelper;
import org.openmrs.module.callflows.api.service.CallService;
import org.openmrs.module.callflows.api.util.CallUtil;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    @Mock
    private CallUtil callUtil;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(callStatusController).build();
        call = CallHelper.createOutboundCall();
    }

    @Test
    public void shouldReturnOkIfTheCallStatusUpdateIsSuccessful() throws Exception {
        //Given
        given(callService.findByCallId(Constants.INBOUND_CALL_ID.toString())).willReturn(call);

        //When && Then
        mockMvc.perform(
                get("/status/" + Constants.INBOUND_CALL_ID.toString() + "?status=ANSWERED&reason=call answered"))

               .andExpect(status().is(HttpStatus.OK.value())).andExpect(content().string(Constants.OK_RESPONSE));

        verify(callService, times(1)).findByCallId(Constants.INBOUND_CALL_ID.toString());
        verify(callService, times(1)).update(call);
        verify(callUtil, times(1)).sendStatusEvent(call);
    }

    @Test
    public void shouldReturnErrorIfTheCallIsNotFoundInDatabase() throws Exception {

        //Given
        given(callService.findByCallId(Constants.INBOUND_CALL_ID.toString())).willReturn(null);

        //When && Then
        mockMvc.perform(
                get("/status/" + Constants.INBOUND_CALL_ID.toString() + "?status = ANSWERED & reason = call answered"))

               .andExpect(status().is(HttpStatus.OK.value())).andExpect(content().string(Constants.ERROR_RESPONSE));

        verify(callService, times(1)).findByCallId(Constants.INBOUND_CALL_ID.toString());
        verify(callService, never()).update(call);
        verify(callUtil, never()).sendStatusEvent(any(Call.class));
    }

    @Test
    public void shouldReturnOkIfTheCallStatusUpdateIsSuccessfulWhenExternalProviderDetailsArePassed() throws Exception {
        //Given
        call.setExternalId(null);
        given(callService.findByCallId(Constants.INBOUND_CALL_ID.toString())).willReturn(call);

        //When
        mockMvc.perform(
                get("/status/" + Constants.INBOUND_CALL_ID.toString() + "?status=ANSWERED&reason=call answered&externalId=1234&externalType=extType"))

               .andExpect(status().is(HttpStatus.OK.value())).andExpect(content().string(Constants.OK_RESPONSE));

        //Then
        verify(callService, times(1)).findByCallId(Constants.INBOUND_CALL_ID.toString());
        verify(callService, times(1)).update(call);
        verify(callUtil, times(1)).sendStatusEvent(call);
    }

}
