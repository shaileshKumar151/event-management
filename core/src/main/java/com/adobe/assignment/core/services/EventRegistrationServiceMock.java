/*
package com.adobe.assignment.core.services;

import com.day.crx.JcrConstants;
import com.drew.lang.annotations.NotNull;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Stopwatch;
import com.velocityfrequentflyer.aem.core.VffCoreSiteException;
import com.velocityfrequentflyer.aem.core.beans.FaultJsonResponseError;
import com.velocityfrequentflyer.aem.core.beans.OfferRequest;
import com.velocityfrequentflyer.aem.core.beans.OfferResponse;
import com.velocityfrequentflyer.aem.core.enums.Verb;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Binary;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

*/
/**
 * Implementation class for retrieving the promotion
 * It's used for mocking the data based on the JSON file before the promotion API ready
 *
 * @author johnnyyang
 *//*

@Component(service = EventRegistrationServiceMock.class)
@Designate(ocd = EventRegistrationServiceMock.Configuration.class)
public class EventRegistrationServiceMock {

    private static final Logger LOG = LoggerFactory.getLogger(EventRegistrationServiceMock.class);

    */
/**
     * Configuration service
     *//*

    @ObjectClassDefinition(name = "VFF Core Promotion Retriever Mock Impl Configuration Service")
    @interface Configuration {

        @AttributeDefinition(
                name = "Get data path",
                description = "The GET data path of the JSON binary file"
        )
        String data_path() default "/content/dam/vff/data.json/jcr:content/renditions/original/jcr:content";

    }

    private String dataPath;


    @Activate
    @Modified
    protected void activate(PromotionRetrieverMock.Configuration configuration) {
        dataPath = configuration.data_path();
    }

    public OfferResponse getPromotion(@NotNull OfferRequest request, ResourceResolver resolver) {
        try {
            return executeRequest(request, resolver);
        } catch (RepositoryException | IOException e) {
            VffCoreSiteException coreSiteException = new VffCoreSiteException(e);
            String responseBody = "";
            coreSiteException.handle();
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                FaultJsonResponseError faultJsonResponseError = new FaultJsonResponseError("An unknown error has occurred in one " +
                        "of Virgin Australia's systems.", "Unknown Error");
                responseBody = objectMapper.writeValueAsString(faultJsonResponseError);
            } catch (JsonProcessingException jsonException) {
                VffCoreSiteException jsonError = new VffCoreSiteException(jsonException);
                jsonError.handle();
            }
            OfferResponse response = new OfferResponse();
            response.setCode(500);
            response.setBody(responseBody);
            return response;
        }
    }

    private Map<String, Object> getDataResource(ResourceResolver resolver) throws RepositoryException, IOException {
        Map<String, Object> dataMap = null;
        Resource resource = resolver.getResource(dataPath);
        if (resource != null) {
            Node node = resource.adaptTo(Node.class);
            if (node != null && node.hasProperty(JcrConstants.JCR_DATA)) {
                Binary data = node.getProperty(JcrConstants.JCR_DATA).getBinary();
                if (data != null) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    dataMap = objectMapper.readValue(data.getStream(), Map.class);
                }
            }

        }
        return dataMap;
    }

    private OfferResponse executeRequest(OfferRequest request, ResourceResolver resolver) throws RepositoryException, IOException {
        Stopwatch timer = Stopwatch.createStarted();
        LOG.info("PromotionRetrieverImpl executeRequest {} starts", request.getVerb().getValue());
        OfferResponse response = new OfferResponse();
        Verb verb = request.getVerb();
        Map<String, Object> dataMap = getDataResource(resolver);
        if (Verb.POST == verb) {
            executePostRequest(request, response, dataMap);
        } else if (Verb.GET == verb) {
            executeGetRequest(request, response, dataMap);
        }
        LOG.info("PromotionRetrieverImpl executeRequest end: {}", timer);
        return response;
    }

    private void executeGetRequest(OfferRequest request, OfferResponse response, Map<String, Object> dataMap) {
        List<Map<String, String>> promotionsResponse = new ArrayList<>();
        String responseBody = "";
        int responseCode = 200;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            if (dataMap != null && dataMap.containsKey("promotions")) {
                String promoCode = request.getPromoCode();
                List<Map<String, String>> promotions = (List<Map<String, String>>) dataMap.get("promotions");
                if (promotions != null) {
                    Map<String, String> promotionMap;
                    Optional<Map<String, String>> itemOptional = promotions.stream()
                            .filter(promotion -> (promotion.containsKey("promotionCode")
                                    && promotion.get("promotionCode").equals(promoCode))).findFirst();
                    if (itemOptional.isPresent()) {
                        promotionMap = itemOptional.get();
                        promotionsResponse.add(promotionMap);
                        responseBody = objectMapper.writeValueAsString(promotionsResponse);
                        responseCode = 200;
                    }
                }
            }

            if (promotionsResponse.isEmpty()) {
                responseCode = 404;
                FaultJsonResponseError faultJsonResponseError = new FaultJsonResponseError("Promotion Not Found", "Promotion Not Found");
                responseBody = objectMapper.writeValueAsString(faultJsonResponseError);
            }
        } catch (JsonProcessingException e) {
            VffCoreSiteException coreSiteException = new VffCoreSiteException(e);
            responseCode = 500;
            coreSiteException.handle();
            try {
                FaultJsonResponseError faultJsonResponseError = new FaultJsonResponseError("An unknown error has occurred in one " +
                        "of Virgin Australia's systems.", "Unknown Error");
                responseBody = objectMapper.writeValueAsString(faultJsonResponseError);
            } catch (JsonProcessingException jsonException) {
                VffCoreSiteException jsonError = new VffCoreSiteException(jsonException);
                jsonError.handle();
            }
        }

        response.setCode(responseCode);
        response.setBody(responseBody);

    }

    private void executePostRequest(OfferRequest request, OfferResponse response, Map<String, Object> dataMap) throws IOException {
        List<Map<String, Object>> responseList = new ArrayList<>();
        String responseBody = "";
        int responseCode = 200;
        ObjectMapper objectMapper = new ObjectMapper();
        if (dataMap != null && dataMap.containsKey("activation")) {
            String customerId = request.getCustomerId();
            Map<String, Map<String, Object>> activationMap = (Map<String, Map<String, Object>>) dataMap.get("activation");
            Map<String, Object> resultMap;
            if (customerId != null) {
                boolean isSuccess = false;
                switch (customerId) {
                    case "1181508556":
                        resultMap = activationMap.get("Not Eligible");
                        break;
                    case "1012503435":
                        resultMap = activationMap.get("SUCCESS");
                        isSuccess = true;
                        break;
                    case "1125758984":
                        resultMap = activationMap.get("Already Activated");
                        break;
                    default:
                        resultMap = activationMap.get("Membership Not Found");
                        break;
                }
                if (resultMap != null) {
                    if (isSuccess) {
                        responseList.add(resultMap);
                        responseBody = objectMapper.writeValueAsString(responseList);
                        responseCode = 200;
                    } else {
                        responseBody = objectMapper.writeValueAsString(resultMap);
                        responseCode = 400;
                    }

                }

            }
        }

        if (StringUtils.isBlank(responseBody)) {
            responseCode = 404;
            FaultJsonResponseError faultJsonResponseError = new FaultJsonResponseError("Promotion Not Found", "Promotion Not Found");
            responseBody = objectMapper.writeValueAsString(faultJsonResponseError);
        }

        response.setCode(responseCode);
        response.setBody(responseBody);

    }

}
*/
