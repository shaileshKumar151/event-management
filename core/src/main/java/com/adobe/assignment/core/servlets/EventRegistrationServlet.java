package com.adobe.assignment.core.servlets;

import com.adobe.assignment.core.beans.EventRegistrationRequest;
import com.adobe.assignment.core.beans.EventRegistrationResponse;
import com.adobe.assignment.core.services.EventRegistrationService;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.ContentType;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;

import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import static org.apache.sling.api.SlingHttpServletResponse.SC_BAD_REQUEST;
import static org.apache.sling.api.SlingHttpServletResponse.SC_OK;

/**
 * Custom servlet for the Front end post the data from event registration page
 */
@Component(
        service = {Servlet.class},
        immediate = true,
        property = {
                Constants.SERVICE_DESCRIPTION + "= Sling servlet for events registration",
                ServletResolverConstants.SLING_SERVLET_PATHS + "=/bin/event-management/events/register",
                ServletResolverConstants.SLING_SERVLET_METHODS + "=GET",
                ServletResolverConstants.SLING_SERVLET_EXTENSIONS + "=json"
        }
)
public class EventRegistrationServlet extends SlingAllMethodsServlet {

    private static final Logger LOG = LoggerFactory.getLogger(EventRegistrationServlet.class);

    private static final long serialVersionUID = -4251977534692231553L;
    public static final String PARAMETER_FIRST_NAME = "firstName";
    public static final String PARAMETER_LAST_NAME = "lastName";
    public static final String PARAMETER_EMAIL = "email";
    public static final String PARAMETER_EVENT_ID = "eventId";

    /**
     * Configuration service
     */
    @ObjectClassDefinition(name = "VFF Core Offer Servlet Configuration Service")
    @interface Configuration {
        @AttributeDefinition(
                name = "Enable mock response",
                description = "Enable it to use mock response for Event Registration service"
        )
        boolean isMockServiceEnabled() default true;
    }

    @Reference
    private transient EventRegistrationService eventRegistrationService;

    @Override
    protected final void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws
            ServletException, IOException {
        response.setStatus(SC_BAD_REQUEST);
        response.getWriter().write("Method not supported");
    }

    @Override
    protected final void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws
            ServletException, IOException {
        doRequest(request, response);
    }

    private void doRequest(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        response.setContentType(ContentType.APPLICATION_JSON.getMimeType());
        String firstName = request.getParameter(PARAMETER_FIRST_NAME);
        String lastName = request.getParameter(PARAMETER_LAST_NAME);
        String email = request.getParameter(PARAMETER_EMAIL);
        String eventId = request.getParameter(PARAMETER_EVENT_ID);
        String responseBody = "";

        boolean isRequiredElementMet = isRequiredElementMet(firstName, lastName, email, response);

        if (!isRequiredElementMet) {
            return;
        }
        EventRegistrationRequest registrationRequest = new EventRegistrationRequest(firstName, lastName, email, eventId);
        try {
            EventRegistrationResponse registrationResponse;
            registrationResponse = eventRegistrationService.register(registrationRequest);

            if (SC_OK == registrationResponse.getCode()) {
                response.setStatus(registrationResponse.getCode());
            } else {
                response.setStatus(SC_BAD_REQUEST);
            }

            responseBody = registrationResponse.getBody();
        } catch (IOException e) {
            LOG.error("A error occurred during the API call {}, {}", e.getMessage());
            e.printStackTrace();
            response.setStatus(SC_INTERNAL_SERVER_ERROR);
            responseBody = "{\"Error\":\"An error occurred while processing your request\"}";
        }
        response.getWriter().write(responseBody);
    }

    public static boolean isRequiredElementMet(String firstName, String lastName, String email,
                                              SlingHttpServletResponse response)
            throws IOException {
        //Response bad request if any of the parameter was not provided in the request
        if (StringUtils.isBlank(firstName)) {
            response.setStatus(SC_BAD_REQUEST);
            response.getWriter().write("{\"Error\":\"Please ensure the first name have been provided\"}");
            return false;
        }

        if (StringUtils.isBlank(lastName)) {
            response.setStatus(SC_BAD_REQUEST);
            response.getWriter().write("{\"Error\":\"Please ensure the last name have been provided\"}");
            return false;
        }

        if (StringUtils.isBlank(email)) {
            response.setStatus(SC_BAD_REQUEST);
            response.getWriter().write("{\"Error\":\"Please ensure the email have been provided\"}");
            return false;
        }
        return true;
    }

}
