package org.openmrs.module.callflows.filter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.FilterConfig;
import java.lang.reflect.Field;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AuthorizationFilterTest {

    @Mock
    private FilterConfig filterConfig;

    @InjectMocks
    private AuthorizationFilter authorizationFilter = new AuthorizationFilter();

    @Test
    public void shouldFilterIgnoredUrls() throws Exception {
        Field field =  AuthorizationFilter.class.getDeclaredField("ignoredUrls");
        field.setAccessible(true);

        when(filterConfig.getInitParameter("ignored-urls")).thenReturn(
                "/ws/callflows/in/{conf}/flows/{flowName}.{extension}\n" +
                        "                /ws/callflows/calls/{callId}.{extension}"
        );

        authorizationFilter.init(filterConfig);

        List<String> ignoredUrls = (List<String>) field.get(authorizationFilter);

        assertThat(ignoredUrls.size(), equalTo(2));
        assertThat(ignoredUrls.get(0), equalTo("/ws/callflows/in/{conf}/flows/{flowName}.{extension}"));
        assertThat(ignoredUrls.get(1), equalTo("/ws/callflows/calls/{callId}.{extension}"));
    }

}
