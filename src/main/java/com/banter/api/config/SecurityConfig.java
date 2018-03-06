package com.banter.api.config;

import com.banter.api.security.AWSCognitoAccessTokenProcessor;
import com.banter.api.security.AWSCognitoJWTAuthenticationFilter;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.util.DefaultResourceRetriever;
import com.nimbusds.jose.util.ResourceRetriever;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import static com.nimbusds.jose.JWSAlgorithm.RS256;

import javax.servlet.http.HttpServletResponse;
import java.net.MalformedURLException;
import java.net.URL;

@Configuration
@EnableWebSecurity
// @EnableGlobalMethodSecurity //TODO: Understand what this does, I think it enables AOP and annotations like @PreAuthorize. I don't need any of that
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    AWSCognitoAccessTokenProcessor awsCognitoAccessTokenProcessor;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.
                csrf().disable(). //TODO: What is CSRF? should we add CORS too?
                sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS). //Makes every session stateless. Good for REST
                and().
                authorizeRequests().anyRequest().authenticated(). //All requests need to be authenticated
                and().
                anonymous().disable(). //No "anonymous" authentication. AKA everyone must be authenticated
                addFilterBefore(new AWSCognitoJWTAuthenticationFilter(awsCognitoAccessTokenProcessor), BasicAuthenticationFilter.class); //This adds our AWSCognitoJWTAuthenticationFilter before the BasicAuthenticationFilter
        //Don't worry about the BasicAuthenticationFilter, we aren't using basicAuthentication. This method just needs a filter to put ours before and that is one of the filters we can put ours before.
    }

    /**
     * For any unauthorized request just return a message saying they are unauthorized. Our clients
     * should know where to go to get authorized.
     *
     * @return
     */
    @Bean
    public AuthenticationEntryPoint unauthorizedEntryPoint() {
        return (request, response, authException) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
    }


    @Bean
    public ConfigurableJWTProcessor configurableJWTProcessor() throws MalformedURLException {

        int CONNECTION_TIMEOUT = 5000;

        String COGNITO_IDENTITY_POOL_URL = "https://cognito-idp.%s.amazonaws.com/%s";
        String JSON_WEB_TOKEN_SET_URL_SUFFIX = "/.well-known/jwks.json";
        String region = "us-east-1";
        String userPoolId = "us-east-1_M0GwiV1g7";
        String JWK_SET_URL = String.format(COGNITO_IDENTITY_POOL_URL + JSON_WEB_TOKEN_SET_URL_SUFFIX, region, userPoolId);


        ResourceRetriever resourceRetriever = new DefaultResourceRetriever(CONNECTION_TIMEOUT, CONNECTION_TIMEOUT);
        URL jwkSetURL = new URL(JWK_SET_URL);
        JWKSource keySource = new RemoteJWKSet(jwkSetURL, resourceRetriever);
        ConfigurableJWTProcessor jwtProcessor = new DefaultJWTProcessor();
        JWSKeySelector keySelector = new JWSVerificationKeySelector(RS256, keySource);
        jwtProcessor.setJWSKeySelector(keySelector);
        return jwtProcessor;
//        ResourceRetriever resourceRetriever = new DefaultResourceRetriever(jwtConfiguration.getConnectionTimeout(), jwtConfiguration.getReadTimeout());
//        URL jwkSetURL = new URL(jwtConfiguration.getJwkUrl());
//        JWKSource keySource = new RemoteJWKSet(jwkSetURL, resourceRetriever);
//        ConfigurableJWTProcessor jwtProcessor = new DefaultJWTProcessor();
//        JWSKeySelector keySelector = new JWSVerificationKeySelector(RS256, keySource);
//        jwtProcessor.setJWSKeySelector(keySelector);
//        return jwtProcessor;

    }
}
