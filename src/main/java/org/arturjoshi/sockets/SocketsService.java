package org.arturjoshi.sockets;

import org.arturjoshi.users.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

/**
 * Created by ajoshi on 22-Jun-16.
 */
@Component
public class SocketsService {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    public void newFriendRequest(Long inviteeId, User user) {
        simpMessagingTemplate.convertAndSend("/update/" + inviteeId + "/newFriendRequest", user);
    }

    public void newFriend(Long userId, User newFriend) {
        simpMessagingTemplate.convertAndSend("/update/" + userId + "/newFriend", newFriend);
    }

    public void deleteFriend(Long userId, User deletedFriend) {
        simpMessagingTemplate.convertAndSend("/update/" + userId + "/friendDeleted", deletedFriend);
    }
}
