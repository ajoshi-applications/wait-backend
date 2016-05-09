package org.arturjoshi.domain.projections;

import org.arturjoshi.domain.User;
import org.springframework.data.rest.core.config.Projection;

@Projection(name = "noPassword", types = {User.class})
public interface NoPasswordProjection {
    String getUsername();
    String getPhonenumber();
    String getEmail();
}