package org.arturjoshi.domain.dao;

import org.arturjoshi.domain.User;
import org.arturjoshi.domain.projections.NoPasswordProjection;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Set;

@RepositoryRestResource(excerptProjection = NoPasswordProjection.class, path = "people")
public interface UserRepository extends CrudRepository<User, Long> {
    Set<User> findByUsernameStartsWith(@Param("username") String username);
}
