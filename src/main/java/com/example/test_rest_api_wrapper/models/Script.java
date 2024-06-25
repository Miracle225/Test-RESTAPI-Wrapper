package com.example.test_rest_api_wrapper.models;

import java.time.LocalDateTime;

/**
 * @author Ishchenko Danylo
 * @version 0.0.1
 * Represents a JavaScript script entity, including its unique identifier,
 * script code, current status, output, and error messages.
 */
public class Script {
    private String id;
    private String code;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private String output;
    private String error;

    public Script() {
    }

    public Script(String id, String code) {
        this.id = id;
        this.code = code;
        this.status = "queued";
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "Script{" +
                "id='" + id + '\'' +
                ", code='" + code + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", status='" + status + '\'' +
                ", output='" + output + '\'' +
                ", error='" + error + '\'' +
                '}';
    }
}
