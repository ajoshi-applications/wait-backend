package org.arturjoshi.tracking.controller;

import org.arturjoshi.authentication.UserAuthenticationManager;
import org.arturjoshi.tracking.repository.UserCoordinatesMongoRepository;
import org.arturjoshi.tracking.dto.UserCoordinatesDto;
import org.arturjoshi.users.domain.User;
import org.arturjoshi.users.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TrackingController {
    @Autowired
    private UserCoordinatesMongoRepository userCoordinatesMongoRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserAuthenticationManager userAuthenticationManager;

    @RequestMapping(value = "/sendCoordinates/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserCoordinatesDto sendCoordinates(@PathVariable("id") String id, @RequestBody UserCoordinatesDto userCoordinatesDto) {
        User user = userRepository.findOne(Long.parseLong(id));
        userCoordinatesDto.setUser_id(id);
        return userCoordinatesMongoRepository.save(userCoordinatesDto);
    }

    @RequestMapping(value = "getTrackingData/{id}")
    @ResponseBody
    public List<UserCoordinatesDto> getTrackingData(@PathVariable("id") String id) {
        User user = userRepository.findOne(Long.parseLong(id));
        return userCoordinatesMongoRepository.getById(id);
    }
}
