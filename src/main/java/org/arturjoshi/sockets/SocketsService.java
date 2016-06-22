package org.arturjoshi.sockets;

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

    public void newFriendRequest(Long userId) {
        simpMessagingTemplate.convertAndSend("/update/" + userId, "NEW FRIEND REQUEST");
    }
}
