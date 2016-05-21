package org.arturjoshi.tracking.repository;

import org.arturjoshi.tracking.dto.UserCoordinatesDto;

import java.util.List;

public interface UserCoordinatesMongoRepository {
    UserCoordinatesDto save(UserCoordinatesDto userCoordinatesDto);
    List<UserCoordinatesDto> getById(String id);
}
