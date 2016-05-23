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

    @RequestMapping(value = "/sendCoordinates/{user_id}")
    @ResponseStatus(HttpStatus.OK)
    public UserCoordinatesDto sendCoordinates(@PathVariable("user_id") String userId, @RequestBody UserCoordinatesDto userCoordinatesDto) {
        User user = userRepository.findOne(Long.parseLong(userId));
        if(!userAuthenticationManager.isLegal(user)) {
            throw new AccessDeniedException("Access is denied");
        }
        userCoordinatesDto.setUser_id(userId);
        return userCoordinatesMongoRepository.save(userCoordinatesDto);
    }

    @RequestMapping(value = "getTrackingData/{user_id}")
    @ResponseBody
    public List<UserCoordinatesDto> getTrackingData(@PathVariable("user_id") String userId) {
        User user = userRepository.findOne(Long.parseLong(userId));
        if(!userAuthenticationManager.isLegal(user)) {
            throw new AccessDeniedException("Access is denied");
        }
        return userCoordinatesMongoRepository.getById(userId);
    }
}
