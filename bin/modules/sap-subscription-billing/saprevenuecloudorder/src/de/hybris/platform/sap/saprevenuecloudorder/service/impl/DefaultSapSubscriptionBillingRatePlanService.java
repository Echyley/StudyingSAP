/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saprevenuecloudorder.service.impl;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.hybris.platform.sap.saprevenuecloudorder.clients.SubscriptionBillingApiClient;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.rateplan.v2.RatePlanViewBatchRequest;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.rateplanResponse.v2.RatePlanViewBatchResponse;
import de.hybris.platform.sap.saprevenuecloudorder.service.SapSubscriptionBillingRatePlanService;
import de.hybris.platform.subscriptionservices.exception.SubscriptionServiceException;
import org.apache.log4j.Logger;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;


/**
 * Default Subscription Rate Plan Service to communicate with Sap Subscription Billing system
 */
public class DefaultSapSubscriptionBillingRatePlanService implements SapSubscriptionBillingRatePlanService {

    private static final Logger LOG = Logger.getLogger(DefaultSapSubscriptionBillingRatePlanService.class);
    private SubscriptionBillingApiClient sbApiClient;
    private static final JsonParser JSON_PARSER = new JsonParser();

    @Override
    public RatePlanViewBatchResponse getRatePlanViewBatchRequest(RatePlanViewBatchRequest ratePlanViewBatchRequest) throws SubscriptionServiceException {

        //Prepare Url
        String uriString = "/rate-plan/v2/rate-plans/view/$batch";

        UriComponents uriComponents = UriComponentsBuilder.fromUriString(uriString)
                .build();

        //Call API
        try {
            return sbApiClient.postEntity(uriComponents, ratePlanViewBatchRequest, RatePlanViewBatchResponse.class);
        } catch (HttpClientErrorException clientError) {
            LOG.error("Error while cancelling subscription: ", clientError);
            String actualError = extractErrorMessage(clientError.getResponseBodyAsString());
            throw new SubscriptionServiceException(actualError);
        }

    }


    //<editor-fold desc="Private Methods">
    private String extractErrorMessage(final String errorJson){
        try {
            JsonObject main = JSON_PARSER.parse(errorJson).getAsJsonObject();
            return main.get("message").getAsString();
        }
        catch (Exception e){
            LOG.error(String.format("Unable to parse json: %s", errorJson), e);
            return "Unknown subscription error. Check server logs for details";
        }
    }
    //</editor-fold>

    //<editor-fold desc="Getters and Setters">

    public void setSbApiClient(SubscriptionBillingApiClient sbApiClient) {
        this.sbApiClient = sbApiClient;
    }
}
