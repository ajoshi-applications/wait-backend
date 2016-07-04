package org.arturjoshi.authentication.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.arturjoshi.users.domain.User;
import org.springframework.security.core.userdetails.UserDetails;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class TokenHandlerDto {
    private String token;
    private UserDetails userDetails;
}
