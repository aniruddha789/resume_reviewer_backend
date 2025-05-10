package com.resumereviewer.webapp.util;

import com.resumereviewer.webapp.dto.response.Status;

import java.time.Instant;

public class StatusBuilder {

    private String status;

    private String message;

    private String timestamp;

    private String code;

    private String path;

    public StatusBuilder status(String status){
        this.status = status;
        return this;
    }

    public StatusBuilder message(String message){
        this.message = message;
        return this;
    }

   public StatusBuilder code(String code){
       this.code = code;
       return this;
   }

   public StatusBuilder path(String path){
       this.path = path;
       return this;
   }

    public Status build(){
        Status statusObj = new Status();
        statusObj.setStatus(status);
        statusObj.setMessage(message);
        statusObj.setTimestamp(Instant.now().toString());
        statusObj.setCode(code);
        statusObj.setPath(path);
        return statusObj;
    }
}
