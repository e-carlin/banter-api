package com.banter.api.security;

import com.banter.api.config.AWSCognitoConfig;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.JWTClaimsSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@Service
public class AWSCognitoAccessTokenProcessor {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String EMPTY_PWD = "";
    private static final List<GrantedAuthority> NO_AUTHORITIES = new ArrayList<GrantedAuthority>();
//    @Autowired private JwtIdTokenCredentialsHolder jwtIdTokenCredentialsHolder; //TODO: change to AccessToken...
    @Autowired private ConfigurableJWTProcessor configurableJWTProcessor;
    @Autowired private AWSCognitoConfig awsCognitoConfig;

    public AWSCognitoAccessTokenProcessor() {}

    public Authentication getAuthentication(HttpServletRequest request) throws Exception {
        logger.debug("In AWSCogntioAccessTokenPROCESSOR doing getAuthentication()");

        String accessToken = request.getHeader("Authorization");
        logger.debug("AccessToken from header is: "+accessToken);
        if(accessToken != null) {
            logger.debug("AccessToken is not null processing....");

            //This will throw exception if token isn't a valid JWT, can't be read using our keys, or is expired
            JWTClaimsSet jwtClaimsSet = getClaimsSet(accessToken);

            //Fail if token isn't from our UserPool
            if(!tokenIssuedFromOurUserPool(jwtClaimsSet)) {
                logger.warn(String.format("Supplied accessToken doesn't match ours. Issuer: %s our cognito idp %s", jwtClaimsSet.getIssuer(), awsCognitoConfig.getCognitoIdentityPoolURL()));
                throw new Exception(String.format("Issuer %s in JWT doesn't match cognito idp %s", jwtClaimsSet.getIssuer(), awsCognitoConfig.getCognitoIdentityPoolURL()));
            }
            if(!isAccessToken(jwtClaimsSet)) {
                logger.warn("Suppliced accessToken wasn't actually an access token");
                throw new Exception(String.format("JWT is not an access token"));
            }
            logger.debug("accessToken verfication has succeeded. Building Authentication object");

            String sub = getSub(jwtClaimsSet);
            logger.debug(String.format("sub is: %s", sub));
            User user = new User(sub, EMPTY_PWD, NO_AUTHORITIES);
            return new AWSCognitoAccessTokenAuthentication(user, jwtClaimsSet, NO_AUTHORITIES);
        }
        return null; //TODO, don't return null
    }

    private JWTClaimsSet getClaimsSet(String accessToken) throws ParseException, JOSEException, BadJOSEException {
        return configurableJWTProcessor.process(accessToken, null);
    }

    private boolean tokenIssuedFromOurUserPool(JWTClaimsSet claimsSet) {
        return claimsSet.getIssuer().equals(awsCognitoConfig.getCognitoIdentityPoolURL());
    }

    private boolean isAccessToken(JWTClaimsSet jwtClaimsSet) {
        return jwtClaimsSet.getClaim("token_use").equals("access");
    }

    private String getSub(JWTClaimsSet claimsSet) throws Exception{
        String sub = claimsSet.getClaim("sub").toString();
        if(sub == null || sub.isEmpty()) {
            throw new Exception("The sub field could not be parsed in the accessToken");
        }
        return sub;
    }
}
