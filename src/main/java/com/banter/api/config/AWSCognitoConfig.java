package com.banter.api.config;

import com.nimbusds.jose.JWSAlgorithm;
import org.hibernate.validator.constraints.pl.REGON;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AWSCognitoConfig {

    private final String COGNITO_IDENTITY_POOL_URL = "https://cognito-idp.%s.amazonaws.com/%s";
    private final String JSON_WEB_TOKEN_SET_URL_SUFFIX = "/.well-known/jwks.json";

    private final String USER_POOL_ID = "us-east-1_M0GwiV1g7";

    private final String USER_POOL_REGION = "us-east-1";
    private final String AUTH_HEADER = "Authorization";

    private final int CONNECTION_TIMEOUT = 2000;
    private final int READ_TIMEOUT = 2000;

    private final JWSAlgorithm TOKEN_ALGORITHM = JWSAlgorithm.RS256;


    public String getJWKURL() {
        return String.format(COGNITO_IDENTITY_POOL_URL + JSON_WEB_TOKEN_SET_URL_SUFFIX, USER_POOL_REGION, USER_POOL_ID);
    }

    public String getCognitoIdentityPoolURL() {
        return String.format(COGNITO_IDENTITY_POOL_URL, USER_POOL_REGION ,USER_POOL_ID);
    }

    public int getConnectionTimeout() {
        return this.CONNECTION_TIMEOUT;
    }

    public int getReadTimeout() {
        return this.READ_TIMEOUT;
    }

    public JWSAlgorithm getTokenAlgorithm() {
        return this.TOKEN_ALGORITHM;
    }
}
