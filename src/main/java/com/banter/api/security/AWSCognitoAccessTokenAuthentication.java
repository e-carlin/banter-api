package com.banter.api.security;

import com.nimbusds.jwt.JWTClaimsSet;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 *
 * Value object holding the principal, the JWT clailmset and the granted authorities.
 * This is the authentication object that will be made available in the security context.
 *
 */
public class AWSCognitoAccessTokenAuthentication extends AbstractAuthenticationToken {

    private Object principal;
    private JWTClaimsSet claimsSet;

    public AWSCognitoAccessTokenAuthentication(Object principal, JWTClaimsSet jwtClaimsSet, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.claimsSet = jwtClaimsSet;
        super.setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        throw new UnsupportedOperationException("The AWSCognitoAccessTokenAuthentication object has no credentials");
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    public JWTClaimsSet getClaimsSet() {
        return this.claimsSet;
    }
}
