package org.arturjoshi.users.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.arturjoshi.users.repository.UserRepository;
import org.arturjoshi.users.controller.dto.UserDto;
import org.arturjoshi.users.domain.User;

import java.util.ArrayList;
import java.util.HashSet;

@RepositoryRestController
public class RegisterController {

    @Autowired
    private UserRepository userRepository;

    @RequestMapping(value = "/people/register", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void createUser(@RequestBody UserDto userDto) {
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setPass(userDto.getPass());
        user.setPhonenumber(userDto.getPhonenumber());
        user.setFriends(new ArrayList<>());
        user.setFriendsRequests(new ArrayList<>());
        user.setEventsOrganized(new ArrayList<>());
        user.setEvents(new ArrayList<>());
        user.setEventInvitations(new ArrayList<>());
        userRepository.save(user);
    }
}
