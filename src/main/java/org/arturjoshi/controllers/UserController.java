package org.arturjoshi.controllers;

import org.arturjoshi.domain.Event;
import org.arturjoshi.domain.User;
import org.arturjoshi.domain.dao.EventsRepository;
import org.arturjoshi.domain.dao.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.PersistentEntityResource;
import org.springframework.data.rest.webmvc.PersistentEntityResourceAssembler;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.web.bind.annotation.*;

@RepositoryRestController
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventsRepository eventsRepository;

    @RequestMapping(value = "/people/{id}/invite/{invitee_id}", method = RequestMethod.POST)
    @ResponseBody
    public PersistentEntityResource inviteUser(@PathVariable("id") Long id, @PathVariable("invitee_id") Long invitee_id,
                                               PersistentEntityResourceAssembler asm)
            throws NoSuchUserException, IllegalFriendRequestException {
        User user = userRepository.findOne(id);
        User invitee = userRepository.findOne(invitee_id);
        if(user == null || invitee == null) {
            throw new NoSuchUserException();
        }
        if(user.getFriends().contains(invitee)) {
            throw new IllegalFriendRequestException();
        }

        invitee.getFriendsRequests().add(user);
        return asm.toFullResource(userRepository.save(invitee));
    }

    @RequestMapping(value = "/people/{id}/confirm/{inviter_id}", method = RequestMethod.POST)
    @ResponseBody
    public PersistentEntityResource confirm(@PathVariable("id") Long id, @PathVariable("inviter_id") Long invitee_id,
                                               PersistentEntityResourceAssembler asm)
            throws NoSuchUserException, IllegalFriendRequestException {
        User user = userRepository.findOne(id);
        User inviter = userRepository.findOne(invitee_id);
        if(user == null || inviter == null) {
            throw new NoSuchUserException();
        }
        if(user.getFriends().contains(inviter)) {
            throw new IllegalFriendRequestException();
        }

        user.getFriendsRequests().remove(inviter);
        user.getFriends().add(inviter);
        inviter.getFriends().add(user);
        userRepository.save(inviter);
        return asm.toFullResource(userRepository.save(user));
    }

    @RequestMapping(value = "/people/{id}/decline/{inviter_id}", method = RequestMethod.POST)
    @ResponseBody
    public PersistentEntityResource decline(@PathVariable("id") Long id, @PathVariable("inviter_id") Long invitee_id,
                                            PersistentEntityResourceAssembler asm)
            throws NoSuchUserException, IllegalFriendRequestException {
        User user = userRepository.findOne(id);
        User inviter = userRepository.findOne(invitee_id);
        if(user == null || inviter == null) {
            throw new NoSuchUserException();
        }
        if(user.getFriends().contains(inviter)) {
            throw new IllegalFriendRequestException();
        }

        user.getFriendsRequests().remove(inviter);
        return asm.toFullResource(userRepository.save(user));
    }

    @RequestMapping(value = "/people/{id}/createEvent", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public PersistentEntityResource createEvent(@PathVariable("id") Long id, @RequestBody Event event,
                                                PersistentEntityResourceAssembler asm) {
        event.setOwner(userRepository.findOne(id));
        return asm.toFullResource(eventsRepository.save(event));
    }
}
