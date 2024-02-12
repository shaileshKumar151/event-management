package com.adobe.assignment.core.servlets;

import com.google.common.collect.ImmutableMap;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.ServletException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * JUnit test for the EventRegistrationServlet class
 */

@ExtendWith({MockitoExtension.class, AemContextExtension.class})
class EventRegistrationServletTest {

    private final AemContext context = new AemContext();
    private EventRegistrationServlet eventRegistrationServlet = new EventRegistrationServlet();

    private MockSlingHttpServletRequest request;
    private MockSlingHttpServletResponse response;


    @BeforeEach
    public void setUp() throws IOException {
        request = context.request();
        response = context.response();

    }

    @Test
    void testDoGet() throws ServletException, IOException {
        eventRegistrationServlet.doGet(request, response);
        assertEquals(SlingHttpServletResponse.SC_BAD_REQUEST, response.getStatus());
    }

    @Test
    void testEmptyFirstName() throws ServletException, IOException {
        context.request().setParameterMap(ImmutableMap.<String, Object>builder()
                .put(EventRegistrationServlet.PARAMETER_LAST_NAME, "lastNameTest")
                .put(EventRegistrationServlet.PARAMETER_EMAIL, "emailTest")
                .put(EventRegistrationServlet.PARAMETER_EVENT_ID, "eventIdTest")
                .build());

        eventRegistrationServlet.doPost(request, response);
        assertEquals(SlingHttpServletResponse.SC_BAD_REQUEST, response.getStatus());
    }

    @Test
    void testEmptyLastName() throws ServletException, IOException {
        context.request().setParameterMap(ImmutableMap.<String, Object>builder()
                .put(EventRegistrationServlet.PARAMETER_FIRST_NAME, "firstNameTest")
                .put(EventRegistrationServlet.PARAMETER_EMAIL, "emailTest")
                .put(EventRegistrationServlet.PARAMETER_EVENT_ID, "eventIdTest")
                .build());

        eventRegistrationServlet.doPost(request, response);
        assertEquals(SlingHttpServletResponse.SC_BAD_REQUEST, response.getStatus());
    }

    @Test
    void testEmptyEmail() throws ServletException, IOException {
        context.request().setParameterMap(ImmutableMap.<String, Object>builder()
                .put(EventRegistrationServlet.PARAMETER_FIRST_NAME, "firstNameTest")
                .put(EventRegistrationServlet.PARAMETER_LAST_NAME, "lastNameTest")
                .put(EventRegistrationServlet.PARAMETER_EVENT_ID, "eventIdTest")
                .build());

        eventRegistrationServlet.doPost(request, response);
        assertEquals(SlingHttpServletResponse.SC_BAD_REQUEST, response.getStatus());
    }
}
