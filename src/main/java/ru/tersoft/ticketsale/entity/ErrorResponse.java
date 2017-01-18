package ru.tersoft.ticketsale.entity;

import java.util.Calendar;

public class ErrorResponse {
    private Long time;
    private Long code;
    private String error_description;

    public ErrorResponse(Long code, String error_description) {
        this.time = Calendar.getInstance().getTimeInMillis();
        this.code = code;
        this.error_description = error_description;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Long getCode() {
        return code;
    }

    public void setCode(Long code) {
        this.code = code;
    }

    public String getDescription() {
        return error_description;
    }

    public void setDescription(String error_description) {
        this.error_description = error_description;
    }
}
