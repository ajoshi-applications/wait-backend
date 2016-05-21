package org.arturjoshi.tracking.repository;

import com.mongodb.*;
import org.arturjoshi.tracking.controller.UserCoordinatesDBObject;
import org.arturjoshi.tracking.dto.UserCoordinatesDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class UserCoordinatesMongoRepositoryImpl implements UserCoordinatesMongoRepository {

    private final String USERS_COORDINATES = "users_coordinates";
    @Autowired
    private DB db;

    @Override
    public UserCoordinatesDto save(UserCoordinatesDto userCoordinatesDto) {
        DBCollection table = db.getCollection(USERS_COORDINATES);
        UserCoordinatesDBObject document = new UserCoordinatesDBObject(userCoordinatesDto);
        table.insert(document);
        return userCoordinatesDto;
    }

    @Override
    public List<UserCoordinatesDto> getById(String id) {
        List<UserCoordinatesDto> result = new ArrayList<>();
        DBCollection collection = db.getCollection(USERS_COORDINATES);
        BasicDBObject searchQuery = new BasicDBObject();
        searchQuery.put("user_id", id);
        DBCursor cursor = collection.find(searchQuery);
        while(cursor.hasNext()) {
            BasicDBObject user = (BasicDBObject) cursor.next();
            result.add(new UserCoordinatesDto(user.getString("user_id"),
                    user.getDouble("lat"), user.getDouble("lon"), user.getString("date")));
        }
        return result;
    }
}