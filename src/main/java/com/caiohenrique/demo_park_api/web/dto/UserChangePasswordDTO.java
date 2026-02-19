package com.caiohenrique.demo_park_api.web.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserChangePasswordDTO {

    private String currentPassword;
    private String newPassword;
    private String confirmPassword;

}
