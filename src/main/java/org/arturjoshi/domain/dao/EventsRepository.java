package org.arturjoshi.domain.dao;

import org.arturjoshi.domain.Event;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface EventsRepository extends CrudRepository<Event, Long> {
}
