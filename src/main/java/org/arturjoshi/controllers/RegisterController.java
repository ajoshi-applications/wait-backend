package org.arturjoshi.controllers;

import org.arturjoshi.controllers.dto.UserDto;
import org.arturjoshi.domain.User;
import org.arturjoshi.domain.dao.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.PersistentEntityResource;
import org.springframework.data.rest.webmvc.PersistentEntityResourceAssembler;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashSet;

@RepositoryRestController
public class RegisterController {

    @Autowired
    private UserRepository userRepository;

    @RequestMapping(value = "/people/register", method = RequestMethod.POST)
    @ResponseBody
    public PersistentEntityResource createUser(@RequestBody UserDto userDto, PersistentEntityResourceAssembler asm) {
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setPass(userDto.getPass());
        user.setEmail(userDto.getEmail());
        user.setPhonenumber(userDto.getPhonenumber());
        user.setFriends(new HashSet<>());
        user.setFriendsRequests(new HashSet<>());
        user.setEventsOrganized(new HashSet<>());
        user.setEvents(new HashSet<>());
        user.setEventInvitations(new HashSet<>());
        return asm.toFullResource(userRepository.save(user));
    }
}
