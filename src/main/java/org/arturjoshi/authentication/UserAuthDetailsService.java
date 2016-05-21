package org.arturjoshi.authentication;

import org.arturjoshi.users.domain.User;
import org.arturjoshi.users.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Created by ajoshi on 20-May-16.
 */
@Service
public class UserAuthDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = (User) userRepository.findByUsernameContainingIgnoreCase(username).toArray()[0];
        if(user == null) {
            throw new UsernameNotFoundException("Could not find account " + username);
        }
        return new AccountAuthDetails(user);
    }
}
