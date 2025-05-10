package com.resumereviewer.webapp.dto.response;

import lombok.Builder;

@Builder
public class Status {

    private String status;

    private String message;

    private String timestamp;

    private String code;

    private String path;

    public Status() {}

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Status(String status, String message, String timestamp, String code, String path) {
        this.status = status;
        this.message = message;
        this.timestamp = timestamp;
        this.code = code;
        this.path = path;
    }

    @Override
    public String toString() {
        return String.format("Status: %s\n"
                + "Message: %s\n"
                + "Timestamp: %s\n"
                + "Code: %s\n"
                + "Path: %s\n",
                this.status,this.message,this.timestamp,this.code,this.path);
    }
}
