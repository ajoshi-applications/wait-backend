package org.arturjoshi.processors;

import org.arturjoshi.controllers.IllegalFriendRequestException;
import org.arturjoshi.controllers.NoSuchUserException;
import org.arturjoshi.controllers.UserController;
import org.arturjoshi.domain.User;
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
        // Adding "Invite friend" method
        try {
            userResource.add(linkTo(methodOn(UserController.class).
                    inviteUser(user.getId(), (long) 0, null)).withRel("invite"));
        } catch (NoSuchUserException e) {
            e.printStackTrace();
        } catch (IllegalFriendRequestException e) {
            e.printStackTrace();
        }
        // Adding "confirm/decline" methods
        if (!user.getFriendsRequests().isEmpty()) {
            for (User inviter : user.getFriendsRequests()) {
                try {
                    userResource.add(linkTo(methodOn(UserController.class).
                            confirm(user.getId(), inviter.getId(), null)).withRel("confirm"));
                    userResource.add(linkTo(methodOn(UserController.class).
                            decline(user.getId(), inviter.getId(), null)).withRel("decline"));
                } catch (NoSuchUserException e) {
                    e.printStackTrace();
                } catch (IllegalFriendRequestException e) {
                    e.printStackTrace();
                }
            }
        }
        //Adding "Create event" method
        userResource.add(linkTo(methodOn(UserController.class).
                createEvent(user.getId(), null, null)).withRel("createEvent"));
        return userResource;
    }
}
