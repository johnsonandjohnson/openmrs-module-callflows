package org.openmrs.module.callflows.filter;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.ContextAuthenticationException;
import org.springframework.util.AntPathMatcher;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Filter intended for all /ws/callflows calls that allows the user to authenticate via Basic
 * authentication. (It will not fail on invalid or missing credentials. We count on the API to throw
 * exceptions if an unauthenticated user tries to do something they are not allowed to do.) <br/>
 */
public class AuthorizationFilter implements Filter {

    private static final Log LOGGER = LogFactory.getLog(AuthorizationFilter.class);

    private static final String BASIC_KEYWORD = "Basic ";

    private FilterConfig config;

    private List<String> ignoredUrls = new ArrayList<>();

    /**
     * @see Filter#init(FilterConfig)
     */
    @Override
    public void init(FilterConfig arg0) throws ServletException {
        LOGGER.debug("Initializing CallFlow Authorization filter");
        this.config = arg0;
        List<String> ignoredUrls =
                Arrays.asList(arg0.getInitParameter("ignored-urls").split("[\\t\\n]+")
                );
        this.ignoredUrls = filterOutBlankUrls(ignoredUrls);
    }

    /**
     * @see Filter#destroy()
     */
    @Override
    public void destroy() {
        LOGGER.debug("Destroying CallFlow Authorization filter");
    }

    /**
     * @see Filter#doFilter(ServletRequest,
     * ServletResponse, FilterChain)
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {

        // skip if the session has timed out, we're already authenticated, or it's not an HTTP request
        if (request instanceof HttpServletRequest) {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            String pathWithoutContext = httpRequest.getRequestURI().split(httpRequest.getContextPath())[1];
            if (httpRequest.getRequestedSessionId() != null
                    && !httpRequest.isRequestedSessionIdValid()
                    && !isUrlIgnored(pathWithoutContext)) {
                HttpServletResponse httpResponse = (HttpServletResponse) response;
                httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Session timed out");
            }
            String authorization = httpRequest.getHeader("Authorization");
            if (authorization != null
                    && authorization.contains(BASIC_KEYWORD)
                    && !isUrlIgnored(pathWithoutContext)) {
                performBasicAuth(authorization);
            }
        }
        chain.doFilter(request, response);
    }

    private boolean isUrlIgnored(String requestURI) {
        for (String u : ignoredUrls) {
            if (new AntPathMatcher().match(u, requestURI)) {
                return true;
            }
        }
        return false;
    }

    private void performBasicAuth(String authorization) {
        // this is "Basic ${base64encode(username + ":" + password)}"
        try {
            authorization = authorization.replace(BASIC_KEYWORD, "");
            String decoded = new String(Base64.decodeBase64(authorization), Charset.forName("UTF-8"));
            String[] userAndPass = decoded.split(":");
            Context.authenticate(userAndPass[0], userAndPass[1]);
            if (LOGGER.isDebugEnabled())
                LOGGER.debug("authenticated " + userAndPass[0]);
        } catch (ContextAuthenticationException ex) {
            Context.logout();
        } catch (Exception ex) {
            // This filter never stops execution. If the user failed to
            // authenticate, that will be caught later.
        }
    }

    private List<String> filterOutBlankUrls(List<String> ignoredUrls) {
        List<String> list = new ArrayList<>();
        for (String ignoredUrl : ignoredUrls) {
            if (StringUtils.isNotBlank(ignoredUrl)) {
                list.add(ignoredUrl);
            }
        }
        return list;
    }
}
