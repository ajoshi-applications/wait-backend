package org.arturjoshi.users.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserInfoDto {
    private Long id;
    private String username;
    private String phonenumber;

}
