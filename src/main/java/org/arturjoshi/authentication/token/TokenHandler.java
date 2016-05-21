package org.arturjoshi.authentication.token;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.arturjoshi.authentication.AccountAuthDetails;
import org.arturjoshi.users.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * Created by ajoshi on 20-May-16.
 */
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TokenHandler {

    private String secretKey = "secretKey";

    private String expirationTime = "604800000";

    private @NonNull
    UserDetailsService userDetailsService;

    public AccountAuthDetails getUserFromToken(String token) throws BadCredentialsException {

        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            String decoded = new String(Base64.decode(token.getBytes()));
            String[] strings = decoded.split(":");
            String username = strings[0];
            long timestamp = Long.parseLong(strings[1]);
            if (timestamp < System.currentTimeMillis()) {
                System.out.println("TokenHandler: token has expired");
                return null;
            }
            byte[] digest = Base64.decode(strings[2].getBytes());

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            StringBuilder builder = new StringBuilder();
            User user = (User) userDetails;
            builder.append(username).append(':').append(timestamp).append(':').append(user.getPass()).append(":")
                    .append(secretKey);
            byte[] expectedDigest = md5.digest(builder.toString().getBytes());
            if (Arrays.equals(expectedDigest, digest)) {
                return (AccountAuthDetails)userDetails;
            } else {
                throw new BadCredentialsException("Bad credentials");
            }
        } catch(Exception  e){
            System.out.println("TokenHandler: can`t getUserFromToken");
            e.printStackTrace();
        }
        return null;
    }

    public String createTokenForUser(UserDetails user){
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            StringBuilder builder = new StringBuilder();
            long timestamp = System.currentTimeMillis() + Long.parseLong(expirationTime);
            builder.append(user.getUsername()).append(':').append(timestamp).append(':').append(user.getPassword())
                    .append(':').append(secretKey);
            String digest = new String(Base64.encode(md5.digest(builder.toString().getBytes())));
            builder = new StringBuilder();
            builder.append(user.getUsername()).append(':').append(timestamp).append(':').append(digest);
            return new String(Base64.encode(builder.toString().getBytes()));
        } catch(NoSuchAlgorithmException e) {
            System.out.println("TokenHandler:createTokenForUser exception");
            e.printStackTrace();
            return null;
        }
    }

}
