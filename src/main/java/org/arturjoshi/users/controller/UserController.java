package org.arturjoshi.users.controller;

import org.arturjoshi.authentication.UserAuthenticationManager;
import org.arturjoshi.sockets.SocketsService;
import org.arturjoshi.users.controller.exceptions.IllegalFriendRequestException;
import org.arturjoshi.users.controller.exceptions.NoSuchEventException;
import org.arturjoshi.users.controller.exceptions.NoSuchFriendException;
import org.arturjoshi.users.controller.exceptions.NoSuchUserException;
import org.arturjoshi.events.domain.Event;
import org.arturjoshi.users.domain.User;
import org.arturjoshi.events.repository.EventsRepository;
import org.arturjoshi.users.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.PersistentEntityResource;
import org.springframework.data.rest.webmvc.PersistentEntityResourceAssembler;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RepositoryRestController
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventsRepository eventsRepository;

    @Autowired
    private UserAuthenticationManager userAuthenticationManager;

    @Autowired
    private SocketsService socketsService;

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
        if(invitee.getFriendsRequests().contains(user)) {
            throw new IllegalFriendRequestException();
        }
        // If we invite user, who invites us
        if(user.getFriendsRequests().contains(invitee)) {
            user.getFriendsRequests().remove(invitee);
            invitee.getFriends().add(user);
            user.getFriends().add(invitee);
            userRepository.save(invitee);
            socketsService.newFriend(id, invitee);
            socketsService.newFriend(invitee_id, user);
            return asm.toFullResource(userRepository.save(user));
        }

        invitee.getFriendsRequests().add(user);
        socketsService.newFriendRequest(invitee_id, user);
        return asm.toFullResource(userRepository.save(invitee));
    }

    @RequestMapping(value = "/people/{id}/removeFriend/{friend_id}", method = RequestMethod.DELETE)
    @ResponseBody
    public PersistentEntityResource removeFriend(@PathVariable("id") Long id, @PathVariable("friend_id") Long friendId,
                                                 PersistentEntityResourceAssembler asm) throws NoSuchFriendException {
        User user = userRepository.findOne(id);
        User friend = userRepository.findOne(friendId);
        if(!user.getFriends().contains(friend)) {
            throw new NoSuchFriendException();
        }
        user.getFriends().remove(friend);
        friend.getFriends().remove(user);
        userRepository.save(friend);
        socketsService.deleteFriend(friendId, user);
        return asm.toFullResource(userRepository.save(user));
    }

    @RequestMapping(value = "/people/{id}/confirm/{inviter_id}", method = RequestMethod.POST)
    @ResponseBody
    public PersistentEntityResource confirm(@PathVariable("id") Long id, @PathVariable("inviter_id") Long inviter_id,
                                               PersistentEntityResourceAssembler asm)
            throws NoSuchUserException, IllegalFriendRequestException {
        User user = userRepository.findOne(id);
        User inviter = userRepository.findOne(inviter_id);
        if(user == null || inviter == null) {
            throw new NoSuchUserException();
        }
        if(user.getFriends().contains(inviter)) {
            throw new IllegalFriendRequestException();
        }

        user.getFriendsRequests().remove(inviter);
        user.getFriends().add(inviter);
        inviter.getFriends().add(user);
        userRepository.save(user);
        socketsService.newFriend(inviter_id, user);
        return asm.toFullResource(userRepository.save(inviter));
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
        User user = userRepository.findOne(id);
        event.setOwner(user);
        return asm.toFullResource(eventsRepository.save(event));
    }

    @RequestMapping(value = "people/{id}/inviteEvent/{event_id}/{friend_id}", method = RequestMethod.POST)
    @ResponseBody
    public PersistentEntityResource inviteToEvent(@PathVariable("id") Long id, @PathVariable("event_id") Long eventId,
                                                  @PathVariable("friend_id") Long friendId,
                                                  PersistentEntityResourceAssembler asm) throws NoSuchFriendException, NoSuchEventException {
        User user = userRepository.findOne(id);
        User friend = userRepository.findOne(friendId);
        Event event = eventsRepository.findOne(eventId);

        if(!user.getFriends().contains(friend)) {
            throw new NoSuchFriendException();
        }
        if(!user.getEventsOrganized().contains(event)) {
            throw new NoSuchEventException();
        }
        event.getInvitations().add(friend);
        return asm.toFullResource(eventsRepository.save(event));
    }

    @RequestMapping(value = "people/{id}/confirmEvent/{event_id}", method = RequestMethod.POST)
    @ResponseBody
    public PersistentEntityResource confirmEventInvitation(@PathVariable("id") Long id, @PathVariable("event_id") Long eventId,
                                                           PersistentEntityResourceAssembler asm) throws NoSuchEventException {
        User user = userRepository.findOne(id);
        Event event = eventsRepository.findOne(eventId);
        if(!user.getEventInvitations().contains(event)) {
            throw new NoSuchEventException();
        }
        event.getInvitations().remove(user);
        event.getGuests().add(user);
        return asm.toFullResource(eventsRepository.save(event));
    }

    @RequestMapping(value = "people/{id}/declineEvent/{event_id}", method = RequestMethod.POST)
    @ResponseBody
    public PersistentEntityResource declineEventInvitation(@PathVariable("id") Long id, @PathVariable("event_id") Long eventId,
                                                           PersistentEntityResourceAssembler asm) throws NoSuchEventException {
        User user = userRepository.findOne(id);
        Event event = eventsRepository.findOne(eventId);
        if(!user.getEventInvitations().contains(event)) {
            throw new NoSuchEventException();
        }
        event.getInvitations().remove(user);
        return asm.toFullResource(eventsRepository.save(event));
    }

    @RequestMapping(value = "people/{id}/kickFriendEvent/{event_id}/{friend_id}", method = RequestMethod.DELETE)
    @ResponseBody
    public PersistentEntityResource kickFriendEvent(@PathVariable("id") Long id, @PathVariable("event_id") Long eventId,
                                                    @PathVariable("friend_id") Long friendId,
                                                    PersistentEntityResourceAssembler asm) throws NoSuchFriendException, NoSuchEventException {
        User user = userRepository.findOne(id);
        User friend = userRepository.findOne(friendId);
        Event event = eventsRepository.findOne(eventId);
        if(!user.getFriends().contains(friend)) {
            throw new NoSuchFriendException();
        }
        if(!user.getEventsOrganized().contains(event)) {
            throw new NoSuchEventException();
        }
        event.getGuests().remove(friend);
        return asm.toFullResource(eventsRepository.save(event));
    }

    @RequestMapping(value = "people/update_username/{id}", method = RequestMethod.POST)
    @ResponseBody
    public PersistentEntityResource updateUsername(@PathVariable("id") Long id, @RequestParam("username") String username,
                                               PersistentEntityResourceAssembler asm) {
        User user = userRepository.findOne(id);
        user.setUsername(username);
        return asm.toFullResource(userRepository.save(user));
    }

    @RequestMapping(value = "people/update_phonenumber/{id}", method = RequestMethod.POST)
    @ResponseBody
    public PersistentEntityResource updatePhonenumber(@PathVariable("id") Long id,
                                                      @RequestParam("phonenumber") String phonenumber,
                                               PersistentEntityResourceAssembler asm) {
        User user = userRepository.findOne(id);
        user.setPhonenumber(phonenumber);
        return asm.toFullResource(userRepository.save(user));
    }

    @RequestMapping(value = "people/update_password/{id}", method = RequestMethod.POST)
    @ResponseBody
    public PersistentEntityResource updatePassword(@PathVariable("id") Long id,
                                                   @RequestParam("pass") String pass,
                                               PersistentEntityResourceAssembler asm) {
        User user = userRepository.findOne(id);
        user.setPass(pass);
        return asm.toFullResource(userRepository.save(user));
    }

    @RequestMapping(value = "people/deleteProfile/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    public void deleteUser(@PathVariable("id") Long id) {
        User user = userRepository.findOne(id);
        userRepository.delete(user);
    }

    @RequestMapping(value = "people/{id}/updateEvent/{event_id}", method = RequestMethod.PATCH)
    @ResponseBody
    public PersistentEntityResource updateEvent(@PathVariable("id") Long id, @PathVariable("event_id") Long eventId,
                                                @RequestBody Event event, PersistentEntityResourceAssembler asm) throws NoSuchEventException {
        User user = userRepository.findOne(id);
        Event eventUpdated = eventsRepository.findOne(eventId);
        if(!user.getEventsOrganized().contains(eventUpdated)) {
            throw new NoSuchEventException();
        }
        eventUpdated.setTitle(event.getTitle());
        eventUpdated.setLat(event.getLat());
        eventUpdated.setLon(event.getLon());
        eventUpdated.setDate(event.getDate());
        return asm.toFullResource(eventsRepository.save(eventUpdated));
    }

    @RequestMapping(value = "people/{id}/deleteEvent/{event_id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    public void deleteEvent(@PathVariable("id") Long id, @PathVariable("event_id") Long eventId) throws NoSuchEventException {
        User user = userRepository.findOne(id);
        Event event = eventsRepository.findOne(eventId);
        if(!user.getEventsOrganized().contains(event)) {
            throw new NoSuchEventException();
        }
        eventsRepository.delete(event);
    }
}