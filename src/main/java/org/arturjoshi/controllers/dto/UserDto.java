package org.arturjoshi.controllers.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserDto {
    private String username;
    private String pass;
    private String phonenumber;
    private String email;
}
