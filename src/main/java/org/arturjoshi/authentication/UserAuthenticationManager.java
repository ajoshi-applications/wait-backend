package org.arturjoshi.authentication;

import org.arturjoshi.users.domain.User;
import org.arturjoshi.users.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class UserAuthenticationManager {

    @Autowired
    private UserRepository userRepository;

    public boolean isLegal(User user) {
        UserDetails currentUser = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(user.getUsername().equals(currentUser.getUsername())) {
            return true;
        }
        return false;
    }
}
