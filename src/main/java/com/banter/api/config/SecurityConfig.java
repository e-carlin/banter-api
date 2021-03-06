package com.banter.api.config;

import com.banter.api.security.FirebaseIdTokenProcessor;
import com.banter.api.security.FirebaseIdTokenAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
// @EnableGlobalMethodSecurity //TODO: Understand what this does, I think it enables AOP and annotations like @PreAuthorize. I don't need any of that
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.
                csrf().disable(). //TODO: What is CSRF? should we add CORS too?
                sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS). //Makes every session stateless. Good for REST
                and().
                authorizeRequests().
                antMatchers("/plaid/webhook", "/dialogflow/webhook").
                permitAll(). //TODO: Remove hard coding
                anyRequest().authenticated().
                and().
                addFilterBefore(new FirebaseIdTokenAuthenticationFilter(firebaseIdTokenProcessor()), BasicAuthenticationFilter.class). //This adds our FirebaseIdTokenAuthenticationFilter before the BasicAuthenticationFilter
                //Don't worry about the BasicAuthenticationFilter, we aren't using basicAuthentication. This method just needs a filter to put ours before and that is one of the filters we can put ours before.
                // this disables session creation on Spring Security
                        sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Bean
    public FirebaseIdTokenProcessor firebaseIdTokenProcessor() {
        return new FirebaseIdTokenProcessor();
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
}
