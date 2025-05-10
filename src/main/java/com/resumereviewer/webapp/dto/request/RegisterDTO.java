package com.resumereviewer.webapp.dto.request;

import com.resumereviewer.webapp.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterDTO {

    private String firstname;
    private String lastname;
    private String email;
    private String username;
    private String password;
    private String firebaseUid;
    private String phone;

    private ArrayList<Role> roles;


}
