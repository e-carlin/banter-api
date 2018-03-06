package com.banter.api.security;

import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.JWTClaimsSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class AWSCognitoAccessTokenProcessor {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String ROLE_PREFIX = "ROLE_";
    private static final String EMPTY_PWD = "";

//    @Autowired private JwtConfiguration jwtConfiguration;
//    @Autowired private JwtIdTokenCredentialsHolder jwtIdTokenCredentialsHolder; //TODO: change to AccessToken...
    @Autowired private ConfigurableJWTProcessor configurableJWTProcessor;

    public AWSCognitoAccessTokenProcessor() {}

    public Authentication getAuthentication(HttpServletRequest request) throws Exception {

        String accessToken = request.getHeader("Authorization");
        if(accessToken != null) {
            JWTClaimsSet jwtClaimsSet = null;

            jwtClaimsSet = configurableJWTProcessor.process(accessToken, null);
            logger.debug("^^^^^^^^^^ GOT CLAIMS SET: "+jwtClaimsSet.toString());
        }
        return null; //TODO, don't return null
    }
}
