package com.banter.api.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class FirebaseIdTokenAuthenticationFilter extends GenericFilterBean {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private FirebaseIdTokenProcessor firebaseIdTokenProcessor;

    public FirebaseIdTokenAuthenticationFilter(FirebaseIdTokenProcessor firebaseIdTokenProcessor) {
        this.firebaseIdTokenProcessor = firebaseIdTokenProcessor;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        logger.debug("In FirebaseIdTokenAuthenticationFilter.doFilter()");
        Authentication authentication = null;
        try {
            authentication = firebaseIdTokenProcessor.getAuthentication((HttpServletRequest)request);

            if(authentication != null) {
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        catch (Exception e) {
            logger.error("Error occurred while processing Firbease Id token: "+e);
            SecurityContextHolder.clearContext();
        }
        chain.doFilter(request, response);
    }
}
