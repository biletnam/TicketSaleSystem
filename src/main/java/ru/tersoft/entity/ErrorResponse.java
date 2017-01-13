package ru.tersoft.entity;

import java.util.Calendar;

public class ErrorResponse {
    private Long time;
    private Long code;
    private String desc;

    public ErrorResponse(Long code, String desc) {
        this.time = Calendar.getInstance().getTimeInMillis();
        this.code = code;
        this.desc = desc;
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

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
