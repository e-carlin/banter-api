package com.banter.api.security;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
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
public class FirebaseIdTokenProcessor {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String EMPTY_PWD = "";
    private static final List<GrantedAuthority> NO_AUTHORITIES = new ArrayList<GrantedAuthority>();

    public FirebaseIdTokenProcessor() {}

    public Authentication getAuthentication(HttpServletRequest request) throws Exception {
        logger.debug("In FirebaseIdTokenPROCESSOR doing getAuthentication()");

        String idToken = request.getHeader("Authorization");
        logger.debug(String.format("Authorization header is: %s", idToken));
        if(idToken != null) {
            logger.debug("Decoding firebase auth token");
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdTokenAsync(idToken).get();
            String uid = decodedToken.getUid();
            logger.debug(String.format("Id token uid is %s",uid));

            logger.debug("idToken verfication has succeeded. Building Authentication object");
            User user = new User(uid, EMPTY_PWD, NO_AUTHORITIES);
            return new FirebaseIdTokenAuthentication(user, NO_AUTHORITIES);
        }
        else {
            throw new Exception("The ID token could not be found in the Authorization header");
        }
    }

}
