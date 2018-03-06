package com.banter.api.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public class AWSCognitoAccessTokenAuthenticationProvider implements AuthenticationProvider
{
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public AWSCognitoAccessTokenAuthenticationProvider() {}

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        logger.debug("&&&&& Trying to authenticate!!!!");
        //TODO: Actually authenticate with AWS cognito

        return null;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return true; // TODO: https://stackoverflow.com/questions/8162698/no-authenticationprovider-found-for-usernamepasswordauthenticationtoken
    }
}
