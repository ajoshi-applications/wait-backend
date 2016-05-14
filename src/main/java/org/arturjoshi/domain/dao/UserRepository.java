package org.arturjoshi.domain.dao;

import org.arturjoshi.domain.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.Set;

@RepositoryRestResource(path = "people")
public interface UserRepository extends CrudRepository<User, Long> {
    @RestResource(path = "username", rel = "username")
    Set<User> findByUsernameStartsWithIgnoreCase(@Param("username") String username);

    @Override
    @RestResource(exported = false)
    public User save(User user);
}