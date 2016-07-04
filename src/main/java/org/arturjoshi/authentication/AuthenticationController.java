package org.arturjoshi.authentication;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.arturjoshi.authentication.token.TokenHandler;
import org.arturjoshi.authentication.dto.TokenHandlerDto;
import org.arturjoshi.users.controller.dto.UserAuthenticationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * Created by ajoshi on 20-May-16.
 */
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AuthenticationController {

    private final @NonNull
    AuthenticationManager authenticationManager;

    private final @NonNull
    TokenHandler tokenHandler;

    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public TokenHandlerDto authenticate(@RequestBody UserAuthenticationDto dto)
            throws BadCredentialsException{

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPass());
        Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return new TokenHandlerDto(tokenHandler.createTokenForUser(userDetails), userDetails);
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String handleBadCredentials(Exception e){
        return e.getMessage();
    }

}
