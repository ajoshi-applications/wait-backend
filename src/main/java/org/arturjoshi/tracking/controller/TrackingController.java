package org.arturjoshi.tracking.controller;

import org.arturjoshi.tracking.repository.UserCoordinatesMongoRepository;
import org.arturjoshi.tracking.dto.UserCoordinatesDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TrackingController {
    @Autowired
    private UserCoordinatesMongoRepository userCoordinatesMongoRepository;

    @RequestMapping(value = "/sendCoordinates/{user_id}")
    @ResponseStatus(HttpStatus.OK)
    public UserCoordinatesDto sendCoordinates(@PathVariable("user_id") String userId, @RequestBody UserCoordinatesDto userCoordinatesDto) {
        userCoordinatesDto.setUser_id(userId);
        return userCoordinatesMongoRepository.save(userCoordinatesDto);
    }

    @RequestMapping(value = "getTrackingData/{user_id}")
    @ResponseBody
    public List<UserCoordinatesDto> getTrackingData(@PathVariable("user_id") String userId) {
        return userCoordinatesMongoRepository.getById(userId);
    }
}
