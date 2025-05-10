package com.resumereviewer.webapp.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class LoginDTO {

    private String username;

    private String password;

    @Default
    private Boolean adminLogin = false;


}
