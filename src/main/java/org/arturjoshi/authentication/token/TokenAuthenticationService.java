package org.arturjoshi.authentication.token;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.arturjoshi.authentication.AccountAuthDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by ajoshi on 20-May-16.
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TokenAuthenticationService {
    private static final String AUTH_HEADER_NAME = "X-AUTH-TOKEN";

    private final @NonNull
    AuthenticationManager authenticationManager;

    private final @NonNull
    TokenHandler tokenHandler;

    public Authentication getAuthentication(HttpServletRequest request) throws BadCredentialsException {

        String token = request.getHeader(AUTH_HEADER_NAME);
        if(token != null){
            AccountAuthDetails userDetails = tokenHandler.getUserFromToken(token);
            if (userDetails != null) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, userDetails.getPassword(), userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                return authentication;
            }
        }
        return null;
    }
}
