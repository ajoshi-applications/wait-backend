package org.arturjoshi.tracking.controller;

import com.mongodb.BasicDBObject;
import org.arturjoshi.tracking.dto.UserCoordinatesDto;

public class UserCoordinatesDBObject extends BasicDBObject {
    public UserCoordinatesDBObject() {
        super();
    }

    public UserCoordinatesDBObject(UserCoordinatesDto userCoordinatesDto) {
        this.put("user_id", userCoordinatesDto.getUser_id());
        this.put("lat", userCoordinatesDto.getLat());
        this.put("lon", userCoordinatesDto.getLon());
        this.put("date", userCoordinatesDto.getDate());
    }
}
