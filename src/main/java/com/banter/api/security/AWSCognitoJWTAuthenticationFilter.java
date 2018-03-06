package com.banter.api.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

public class AWSCognitoJWTAuthenticationFilter extends GenericFilterBean {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final AuthenticationManager authenticationManager;

    public AWSCognitoJWTAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        logger.debug("*********************** Trying to authenticate a request");

        PreAuthenticatedAuthenticationToken requestAuthentication = new PreAuthenticatedAuthenticationToken("123", null);
        Authentication authentication = authenticationManager.authenticate(requestAuthentication);
        logger.debug("******** Result of authentication is: "+authentication.getDetails().toString());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        logger.debug("AWSCognitoJWTAuthenticationFilget is passing request down the filter chain");
        chain.doFilter(request, response);
    }
}
