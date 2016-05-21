package org.arturjoshi.tracking.controller;

import org.arturjoshi.tracking.UserCoordinatesMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TrackingController {
    @Autowired
    private UserCoordinatesMongoRepository userCoordinatesMongoRepository;
}
