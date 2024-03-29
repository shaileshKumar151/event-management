package com.adobe.assignment.core.services;

import com.adobe.assignment.core.beans.EventRegistrationRequest;
import com.adobe.assignment.core.beans.EventRegistrationResponse;
import com.drew.lang.annotations.NotNull;
import com.google.common.base.Stopwatch;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Service class to make the API call to Event Registration API.
 *
 */
@Component(service = EventRegistrationService.class)
@Designate(ocd = EventRegistrationService.Configuration.class)
public class EventRegistrationService {

    private static final Logger LOG = LoggerFactory.getLogger(EventRegistrationService.class);

    private static final String DEFAULT_RESPONSE = "{\"status\": \"success\"}";

    /**
     * Configuration service
     */
    @ObjectClassDefinition(name = "Event Registration Service")
    @interface Configuration {

        @AttributeDefinition(
                name = "Time out second",
                description = "Setting the time out second"
        )
        int timeOutSecond() default 15;

        @AttributeDefinition(
                name = "API endpoint",
                description = "Endpoint for registration API"
        )
        String apiEndpoint() default "http://localhost:4503/";
    }

    private int timeOutSecond;

    private String apiEndpoint;

    private HttpClient httpClient;

    @Activate
    @Modified
    protected void activate(EventRegistrationService.Configuration configuration) {
        timeOutSecond = configuration.timeOutSecond();
        apiEndpoint = configuration.apiEndpoint();
        httpClient = getClient();
    }

    @Deactivate
    protected void deactivate() {
        if (httpClient != null) {
            HttpClientUtils.closeQuietly(httpClient);
            httpClient = null;
        }
    }

    public EventRegistrationResponse register(@NotNull EventRegistrationRequest request) throws IOException {
        return executeRequest(request);
    }

    private EventRegistrationResponse executeRequest(EventRegistrationRequest request) throws IOException {
        Stopwatch timer = Stopwatch.createStarted();
        LOG.info("EventRegistrationService executeRequest {} starts");
        EventRegistrationResponse response = new EventRegistrationResponse();
        //the below lines should be executed when we have the actual API available.
        //executePostRequest(httpClient, apiEndpoint, request, response);
        response.setCode(HttpStatus.SC_OK);
        response.setBody(DEFAULT_RESPONSE);
        LOG.info("EventRegistrationService executeRequest end: {}", timer);
        return response;
    }

    private void executePostRequest(HttpClient client, String requestUri,
                                    EventRegistrationRequest request, EventRegistrationResponse response) throws IOException {
        HttpPost method = new HttpPost(requestUri);
        int timeout = timeOutSecond * 1000;
        RequestConfig config = RequestConfig.custom()
                .setConnectionRequestTimeout(timeout)
                .setSocketTimeout(timeout)
                .setConnectTimeout(timeout).build();
        method.setConfig(config);
        try {
            method.setEntity(new StringEntity(request.getBody(), ContentType.APPLICATION_JSON));
            internalExecuteRequest(client, method, request, response);
        } finally {
            method.releaseConnection();
        }
    }

    private void internalExecuteRequest(HttpClient client, HttpUriRequest method,
                                        EventRegistrationRequest request, EventRegistrationResponse response)
            throws IOException {
        HttpResponse httpResponse = null;
        try {
            httpResponse = client.execute(method);
            response.setCode(httpResponse.getStatusLine().getStatusCode());
            HttpEntity httpEntity = httpResponse.getEntity();
            if (httpEntity != null) {
                String body = EntityUtils.toString(httpResponse.getEntity());
                response.setBody(body);
            }
        } finally {
            if (httpResponse != null) {
                HttpClientUtils.closeQuietly(httpResponse);
            }
        }
    }

    private HttpClient getClient() {
        if (this.httpClient == null) {
            this.httpClient = HttpClients.createDefault();
        }
        return this.httpClient;
    }
}
