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
import org.arturjoshi.users.pagination.Page;
import org.arturjoshi.users.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private UserProvider userProvider;

    @RequestMapping(value = "/people/{id}/friends/{page}", method = RequestMethod.GET)
    @ResponseBody
    public Page<User> getFriends(@PathVariable("id") Long id, @PathVariable("page") Integer page){
        return userProvider.getFriends(id, page);
    }

    @RequestMapping(value = "/people/{id}/friendsRequests/{page}", method = RequestMethod.GET)
    @ResponseBody
    public Page<User> getFriendsRequests(@PathVariable("id") Long id, @PathVariable("page") Integer page){
        return userProvider.getFriendsRequests(id, page);
    }

    @RequestMapping(value = "/people/{id}/searchByUsername", method = RequestMethod.GET)
    @ResponseBody
    public Page<User> searchByUsername(@PathVariable("id") Long id,
                                         @RequestParam("username") String username,
                                         @RequestParam("page") Integer page){
        return userProvider.searchByUsername(username, page);
    }

    @RequestMapping(value = "/people/{id}/searchByPhonenumber", method = RequestMethod.GET)
    @ResponseBody
    public Page<User> searchByPhonenumber(@PathVariable("id") Long id,
                                         @RequestParam("phonenumber") String phonenumber,
                                         @RequestParam("page") Integer page){
        return userProvider.searchByPhonenumber(phonenumber, page);
    }

    @RequestMapping(value = "/people/{id}/invite/{invitee_id}", method = RequestMethod.POST)
    @ResponseBody
    public User inviteUser(@PathVariable("id") Long id, @PathVariable("invitee_id") Long invitee_id)
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
            return userRepository.save(user);
        }

        invitee.getFriendsRequests().add(user);
        socketsService.newFriendRequest(invitee_id, user);
        return userRepository.save(invitee);
    }

    @RequestMapping(value = "/people/{id}/removeFriend/{friend_id}", method = RequestMethod.DELETE)
    @ResponseBody
    public User removeFriend(@PathVariable("id") Long id, @PathVariable("friend_id") Long friendId)
            throws NoSuchFriendException {
        User user = userRepository.findOne(id);
        User friend = userRepository.findOne(friendId);
        if(!user.getFriends().contains(friend)) {
            throw new NoSuchFriendException();
        }
        user.getFriends().remove(friend);
        friend.getFriends().remove(user);
        userRepository.save(friend);
        socketsService.deleteFriend(friendId, user);
        return userRepository.save(user);
    }

    @RequestMapping(value = "/people/{id}/confirm/{inviter_id}", method = RequestMethod.POST)
    @ResponseBody
    public User confirm(@PathVariable("id") Long id, @PathVariable("inviter_id") Long inviter_id)
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
        return userRepository.save(inviter);
    }

    @RequestMapping(value = "/people/{id}/decline/{inviter_id}", method = RequestMethod.POST)
    @ResponseBody
    public User decline(@PathVariable("id") Long id, @PathVariable("inviter_id") Long invitee_id)
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
        return userRepository.save(user);
    }

    @RequestMapping(value = "/people/{id}/createEvent", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public Event createEvent(@PathVariable("id") Long id, @RequestBody Event event) {
        User user = userRepository.findOne(id);
        event.setOwner(user);
        return eventsRepository.save(event);
    }

    @RequestMapping(value = "people/{id}/inviteEvent/{event_id}/{friend_id}", method = RequestMethod.POST)
    @ResponseBody
    public Event inviteToEvent(@PathVariable("id") Long id, @PathVariable("event_id") Long eventId,
                                                  @PathVariable("friend_id") Long friendId)
            throws NoSuchFriendException, NoSuchEventException {
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
        return eventsRepository.save(event);
    }

    @RequestMapping(value = "people/{id}/confirmEvent/{event_id}", method = RequestMethod.POST)
    @ResponseBody
    public Event confirmEventInvitation(@PathVariable("id") Long id, @PathVariable("event_id") Long eventId)
            throws NoSuchEventException {
        User user = userRepository.findOne(id);
        Event event = eventsRepository.findOne(eventId);
        if(!user.getEventInvitations().contains(event)) {
            throw new NoSuchEventException();
        }
        event.getInvitations().remove(user);
        event.getGuests().add(user);
        return eventsRepository.save(event);
    }

    @RequestMapping(value = "people/{id}/declineEvent/{event_id}", method = RequestMethod.POST)
    @ResponseBody
    public Event declineEventInvitation(@PathVariable("id") Long id, @PathVariable("event_id") Long eventId)
            throws NoSuchEventException {
        User user = userRepository.findOne(id);
        Event event = eventsRepository.findOne(eventId);
        if(!user.getEventInvitations().contains(event)) {
            throw new NoSuchEventException();
        }
        event.getInvitations().remove(user);
        return eventsRepository.save(event);
    }

    @RequestMapping(value = "people/{id}/kickFriendEvent/{event_id}/{friend_id}", method = RequestMethod.DELETE)
    @ResponseBody
    public Event kickFriendEvent(@PathVariable("id") Long id, @PathVariable("event_id") Long eventId,
                                                    @PathVariable("friend_id") Long friendId)
            throws NoSuchFriendException, NoSuchEventException {
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
        return eventsRepository.save(event);
    }

    @RequestMapping(value = "people/update_username/{id}", method = RequestMethod.POST)
    @ResponseBody
    public User updateUsername(@PathVariable("id") Long id, @RequestParam("username") String username) {
        User user = userRepository.findOne(id);
        user.setUsername(username);
        return userRepository.save(user);
    }

    @RequestMapping(value = "people/update_phonenumber/{id}", method = RequestMethod.POST)
    @ResponseBody
    public User updatePhonenumber(@PathVariable("id") Long id,
                                                      @RequestParam("phonenumber") String phonenumber) {
        User user = userRepository.findOne(id);
        user.setPhonenumber(phonenumber);
        return userRepository.save(user);
    }

    @RequestMapping(value = "people/update_password/{id}", method = RequestMethod.POST)
    @ResponseBody
    public User updatePassword(@PathVariable("id") Long id,
                                                   @RequestParam("pass") String pass) {
        User user = userRepository.findOne(id);
        user.setPass(pass);
        return userRepository.save(user);
    }

    @RequestMapping(value = "people/deleteProfile/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    public void deleteUser(@PathVariable("id") Long id) {
        User user = userRepository.findOne(id);
        userRepository.delete(user);
    }

    @RequestMapping(value = "people/{id}/updateEvent/{event_id}", method = RequestMethod.PATCH)
    @ResponseBody
    public Event updateEvent(@PathVariable("id") Long id, @PathVariable("event_id") Long eventId,
                                                @RequestBody Event event) throws NoSuchEventException {
        User user = userRepository.findOne(id);
        Event eventUpdated = eventsRepository.findOne(eventId);
        if(!user.getEventsOrganized().contains(eventUpdated)) {
            throw new NoSuchEventException();
        }
        eventUpdated.setTitle(event.getTitle());
        eventUpdated.setLat(event.getLat());
        eventUpdated.setLon(event.getLon());
        eventUpdated.setDate(event.getDate());
        return eventsRepository.save(eventUpdated);
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