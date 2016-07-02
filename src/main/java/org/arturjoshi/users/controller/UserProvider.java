package org.arturjoshi.users.controller;

import org.arturjoshi.users.domain.User;
import org.arturjoshi.users.pagination.Page;
import org.arturjoshi.users.pagination.PaginationService;
import org.arturjoshi.users.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class UserProvider {

    private static final Integer PAGE_SIZE = 25;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PaginationService paginationService;

    @Transactional(readOnly = true)
    public Page<User> getFriends(Long id, Integer page){
        return paginationService.getPage(userRepository.findOne(id).getFriends(), page, PAGE_SIZE);
    }

    @Transactional(readOnly = true)
    public Page<User> getFriendsRequests(Long id, Integer page){
        return paginationService.getPage(userRepository.findOne(id).getFriendsRequests(), page, PAGE_SIZE);
    }

    @Transactional(readOnly = true)
    public Page<User> searchByUsername(String username, Integer page){
        return paginationService.getPage(userRepository.findByUsernameContainingIgnoreCase(username), page, PAGE_SIZE);
    }

    @Transactional(readOnly = true)
    public Page<User> searchByPhonenumber(String phonenumber, Integer page){
        return paginationService.getPage(userRepository.findByPhonenumberContaining(phonenumber), page, PAGE_SIZE);
    }

}
