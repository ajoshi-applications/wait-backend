package org.arturjoshi.users.processor;

import org.arturjoshi.users.controller.exceptions.IllegalFriendRequestException;
import org.arturjoshi.users.controller.exceptions.NoSuchEventException;
import org.arturjoshi.users.controller.exceptions.NoSuchFriendException;
import org.arturjoshi.users.controller.exceptions.NoSuchUserException;
import org.arturjoshi.users.controller.UserController;
import org.arturjoshi.events.domain.Event;
import org.arturjoshi.users.domain.User;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
public class UserProcessor implements ResourceProcessor<Resource<User>> {

    @Override
    public Resource<User> process(Resource<User> userResource) {
        User user = userResource.getContent();
        //"Invite friend" method
        try {
            userResource.add(linkTo(methodOn(UserController.class).
                    inviteUser(user.getId(), (long) 0, null)).withRel("inviteFriend"));
        } catch (NoSuchUserException e) {
            e.printStackTrace();
        } catch (IllegalFriendRequestException e) {
            e.printStackTrace();
        }
        //"Remove friend" method
        if(!user.getFriends().isEmpty()) {
            for (User friend : user.getFriends()) {
                try {
                    userResource.add(linkTo(methodOn(UserController.class).
                            removeFriend(user.getId(), friend.getId(), null)).withRel("removeFriend"));
                } catch (NoSuchFriendException e) {
                    e.printStackTrace();
                }
            }
        }

        //"Confirm/Decline" methods
        if (!user.getFriendsRequests().isEmpty()) {
            for (User inviter : user.getFriendsRequests()) {
                try {
                    userResource.add(linkTo(methodOn(UserController.class).
                            confirm(user.getId(), inviter.getId(), null)).withRel("confirmFriendRequest"));
                    userResource.add(linkTo(methodOn(UserController.class).
                            decline(user.getId(), inviter.getId(), null)).withRel("declineFriendRequest"));
                } catch (NoSuchUserException e) {
                    e.printStackTrace();
                } catch (IllegalFriendRequestException e) {
                    e.printStackTrace();
                }
            }
        }
        //"Create event" method
        userResource.add(linkTo(methodOn(UserController.class).
                createEvent(user.getId(), null, null)).withRel("createEvent"));
        //"Invite friends to event" method
        if(!user.getFriends().isEmpty() && !user.getEventsOrganized().isEmpty()) {
            for (Event event : user.getEventsOrganized()) {
                for (User friend : user.getFriends()) {
                    try {
                        userResource.add(linkTo(methodOn(UserController.class).
                            inviteToEvent(user.getId(), event.getId(), friend.getId(), null)).withRel("inviteToEvent"));
                    } catch (NoSuchFriendException e) {
                        e.printStackTrace();
                    } catch (NoSuchEventException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        //"Confirm event invitation" method
        if(!user.getEventInvitations().isEmpty()) {
            for (Event event : user.getEventInvitations()) {
                try {
                    userResource.add(linkTo(methodOn(UserController.class).
                        confirmEventInvitation(user.getId(), event.getId(), null)).withRel("confirmEvent"));
                } catch (NoSuchEventException e) {
                    e.printStackTrace();
                }
            }
        }
        //"Decline event invitation" method
        if(!user.getEventInvitations().isEmpty()) {
            for (Event event : user.getEventInvitations()) {
                try {
                    userResource.add(linkTo(methodOn(UserController.class).
                            declineEventInvitation(user.getId(), event.getId(), null)).withRel("declineEvent"));
                } catch (NoSuchEventException e) {
                    e.printStackTrace();
                }
            }
        }
        //"Kick friend from event" method
        if(!user.getEventsOrganized().isEmpty()) {
            for (Event event : user.getEventsOrganized()) {
                for (User friend : event.getGuests()) {
                    try {
                        userResource.add(linkTo(methodOn(UserController.class).
                                kickFriendEvent(user.getId(), event.getId(), friend.getId(), null)).withRel("kickFriendFromEvent"));
                    } catch (NoSuchFriendException e) {
                        e.printStackTrace();
                    } catch (NoSuchEventException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return userResource;
    }
}
