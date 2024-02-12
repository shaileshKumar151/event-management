package com.adobe.assignment.core.beans;

/**
 * Bean for setting response for Event Registration API
 */

public class EventRegistrationResponse {

    private String body;

    private Integer code;

    public EventRegistrationResponse() {
    }

    public EventRegistrationResponse(Integer code, String body) {
        this.body = body;
        this.code = code;
    }

    public String getBody() {
        return this.body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Integer getCode() {
        return this.code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

}
