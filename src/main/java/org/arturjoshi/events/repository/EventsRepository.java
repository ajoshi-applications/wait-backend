package org.arturjoshi.events.repository;

import org.arturjoshi.events.domain.Event;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

@RepositoryRestResource
public interface EventsRepository extends CrudRepository<Event, Long> {

    @Override
    @RestResource(exported = false)
    Event save(Event e);

    @Override
    @RestResource(exported = false)
    void delete(Long id);

    @Override
    @RestResource(exported = false)
    void delete(Event event);

    @Override
    @RestResource(exported = false)
    void deleteAll();

    @Override
    @RestResource(exported = false)
    Iterable<Event> findAll();
}
