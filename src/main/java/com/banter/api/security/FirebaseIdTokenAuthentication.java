package com.banter.api.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 *
 * Value object holding the principal, the JWT clailmset and the granted authorities.
 * This is the authentication object that will be made available in the security context.
 *
 */
public class FirebaseIdTokenAuthentication extends AbstractAuthenticationToken {

    private Object principal;

    public FirebaseIdTokenAuthentication(Object principal, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        super.setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        throw new UnsupportedOperationException("The FireBaseIdTokenAuthentication object has no credentials");
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }
}
