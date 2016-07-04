package org.arturjoshi.users.controller;

import org.arturjoshi.users.domain.User;
import org.arturjoshi.users.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@RepositoryRestController
public class SearchController {

    @Autowired
    private UserProvider userProvider;

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
}
