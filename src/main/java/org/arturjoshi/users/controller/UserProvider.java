package org.arturjoshi.users.controller;

import org.arturjoshi.users.domain.User;
import org.arturjoshi.users.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Set;

@Service
public class UserProvider {

    private static final Integer PAGE_SIZE = 5;

    @Autowired
    private UserRepository userRepository;

    @Transactional(readOnly = true)
    public Page<User> getFriends(Long id, Integer page){
        Pageable pageRequest = createOrdersPageRequest(page);
        Set<User> usersSet = userRepository.findOne(id).getFriends();
        Page<User> users = new PageImpl<User>(new ArrayList<>(usersSet), pageRequest, usersSet.size());
        return users;
    }

    @Transactional(readOnly = true)
    public Page<User> getFriendsRequests(Long id, Integer page){
        Pageable pageRequest = createOrdersPageRequest(page);
        Set<User> usersSet = userRepository.findOne(id).getFriendsRequests();
        Page<User> users = new PageImpl<User>(new ArrayList<>(usersSet), pageRequest, usersSet.size());
        return users;
    }

    private Pageable createOrdersPageRequest(Integer pageNumber) {
        return new PageRequest(pageNumber - 1, PAGE_SIZE);
    }

}
