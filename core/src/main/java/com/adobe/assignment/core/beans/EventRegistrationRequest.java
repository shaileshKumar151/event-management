package com.adobe.assignment.core.beans;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * The bean class to create the request object for the API call.
 */
public class EventRegistrationRequest {

    private static final Logger LOG = LoggerFactory.getLogger(EventRegistrationRequest.class);

    private String firstName;

    private String lastName;

    private String email;

    private String eventId;

    private String body;

    private Map<String, String> bodyData;

    private String contentType;

    private Map<String, String> headerMap;

    public EventRegistrationRequest(String firstName, String lastName, String email, String eventId) {
        LOG.debug("Init EventRegistrationRequest, firstName: {0}, lastName: {1}, eventId: {2}", firstName, lastName, eventId);
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.eventId = eventId;
        setBody();
    }

    /**
     * Set the body of the request.
     */
    private void setBody() {
        bodyData = new HashMap<>();
        bodyData.put("firstName", firstName);
        bodyData.put("lastName", lastName);
        bodyData.put("email", email);
        bodyData.put("eventId", eventId);

            setContentType(ContentType.APPLICATION_JSON.getMimeType());
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                this.body = objectMapper.writeValueAsString(bodyData);
            } catch (JsonProcessingException e) {
                LOG.error("Error while creating Request object: {} - {}", e.getMessage());
            }
        }

    public String getBody() {
        return body;
    }

    public String getContentType() {
        return contentType;
    }

    private void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Map<String, String> getBodyData() {
        return bodyData;
    }
}
