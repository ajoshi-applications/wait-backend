package org.arturjoshi.users.controller;

import org.arturjoshi.users.controller.dto.UserInfoDto;
import org.arturjoshi.users.domain.User;
import org.arturjoshi.users.pagination.Page;
import org.arturjoshi.users.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RepositoryRestController
public class SearchController {

    @Autowired
    private UserProvider userProvider;

    @Autowired
    private UserRepository userRepository;

    @RequestMapping(value = "/people/searchByUsername", method = RequestMethod.GET)
    @ResponseBody
    public Page<User> searchByUsername(@RequestParam("username") String username,
                                       @RequestParam("page") Integer page){
        return userProvider.searchByUsername(username, page);
    }

    @RequestMapping(value = "/people/searchByPhonenumber", method = RequestMethod.GET)
    @ResponseBody
    public Page<User> searchByPhonenumber(@RequestParam("phonenumber") String phonenumber,
                                          @RequestParam("page") Integer page){
        return userProvider.searchByPhonenumber(phonenumber, page);
    }

    @RequestMapping(value = "/people/searchByPhonenumbersList", method = RequestMethod.POST)
    @ResponseBody
    public List<UserInfoDto> searchByPhonenumbersList(@RequestBody List<String> phonenumbers) {
        List<UserInfoDto> searchResults = new ArrayList<>();
        for (String phonenumber : phonenumbers) {
            List<User> currentSearchResults = userRepository.findByPhonenumberContaining(phonenumber);
            if(!currentSearchResults.isEmpty()) {
                User searchResult = currentSearchResults.get(0);
                searchResults.add(searchResult.toUserInfoDto());
            }
        }
        return searchResults;
    }
}
