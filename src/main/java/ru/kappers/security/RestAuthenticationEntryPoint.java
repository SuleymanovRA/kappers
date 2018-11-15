package ru.kappers.security;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

@Component
public final class RestAuthenticationEntryPoint extends BasicAuthenticationEntryPoint
        implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException {
        setUnauthorizedResponse(response);
    }

    public boolean checkRequest(HttpServletRequest request) {
        Objects.requireNonNull(request, "request is required not null");
        return false;
    }

    public void setUnauthorizedResponse(HttpServletResponse response) throws IOException {
        Objects.requireNonNull(response, "response is required not null");
        response.addHeader("WWW-Authenticate", "Basic realm=" + getRealmName());
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
    }
}
