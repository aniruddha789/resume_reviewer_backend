package com.resumereviewer.webapp.dto.request;
// DTO for Google auth request

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoogleAuthRequest {
    private String firebaseUid;
    private String email;
    private String displayName;
    private String photoURL;

    // Getters and setters
}
