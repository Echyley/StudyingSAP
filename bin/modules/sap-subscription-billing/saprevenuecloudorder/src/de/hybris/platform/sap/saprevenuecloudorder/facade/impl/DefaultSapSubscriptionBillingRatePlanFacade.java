/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saprevenuecloudorder.facade.impl;

import de.hybris.platform.converters.Converters;
import de.hybris.platform.sap.saprevenuecloudorder.facade.SapSubscriptionBillingRatePlanFacade;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.rateplan.v2.PricingParameter;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.rateplan.v2.RatePlanViewBatchRequest;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.rateplan.v2.RatePlanViewBatchRequestBody;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.rateplan.v2.RatePlanViewBatchRequestElement;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.rateplanResponse.v2.RatePlanViewBatchResponse;
import de.hybris.platform.sap.saprevenuecloudorder.service.SapSubscriptionBillingRatePlanService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.subscriptionfacades.data.PricingData;
import de.hybris.platform.subscriptionfacades.data.SubscriptionData;
import de.hybris.platform.subscriptionfacades.impl.DefaultSubscriptionFacade;
import de.hybris.platform.subscriptionservices.exception.SubscriptionServiceException;
import org.apache.commons.collections.CollectionUtils;

import java.util.LinkedList;
import java.util.List;


public class DefaultSapSubscriptionBillingRatePlanFacade extends DefaultSubscriptionFacade implements SapSubscriptionBillingRatePlanFacade {

    private SapSubscriptionBillingRatePlanService sapSubscriptionBillingRatePlanService;
    private Converter<RatePlanViewBatchResponse, SubscriptionData> subscriptionRateplanConverter;

    @Override
    public SubscriptionData getRatePlanForSubscription(SubscriptionData subscriptionData) throws SubscriptionServiceException {

        RatePlanViewBatchRequest ratePlanViewBatchRequest = new RatePlanViewBatchRequest();
        final PricingData pricing = subscriptionData.getPricing();


        if (CollectionUtils.isNotEmpty(subscriptionData.getExternalObjectReferences()) &&
                CollectionUtils.isNotEmpty(pricing.getPricingParameters())) {

            RatePlanViewBatchRequestBody ratePlanViewBatchRequestBody = new RatePlanViewBatchRequestBody();

            //Set pricing parameter list
            final List<PricingParameter> pricingParameterList = new LinkedList<>();
            pricing.getPricingParameters().forEach(entry -> {
                PricingParameter pricingParameter = new PricingParameter();
                pricingParameter.setCode(entry.getCode());
                pricingParameter.setValue(entry.getValue());
                pricingParameterList.add(pricingParameter);
            });
            ratePlanViewBatchRequestBody.setPricingParameters(pricingParameterList);

            // build filter query
            String externalSystemId = subscriptionData.getExternalObjectReferences().get(0).getExternalSystemId();
            String externalId = subscriptionData.getExternalObjectReferences().get(0).getExternalId();
            String pricingDate = pricing.getPricingDate();
            String filterQuery = String.format("?$filter=externalObjectReference.externalSystemId eq '%s' and " +
                            "externalObjectReference.externalId eq '%s' and " +
                            "effectiveAt eq '%s'",
                    externalSystemId, externalId, pricingDate
            );

            // Set rate plan element
            // Assumption: Only one element is needed for each subscription
            RatePlanViewBatchRequestElement ratePlanViewBatchRequestElement = new RatePlanViewBatchRequestElement();
            ratePlanViewBatchRequestElement.setId(subscriptionData.getId());
            ratePlanViewBatchRequestElement.setMethod("POST");
            ratePlanViewBatchRequestElement.setUrl(filterQuery);
            ratePlanViewBatchRequestElement.setBody(ratePlanViewBatchRequestBody);

            // Set requests
            ratePlanViewBatchRequest.setRequests(List.of(ratePlanViewBatchRequestElement));
        }
        RatePlanViewBatchResponse ratePlanViewBatchResponse = sapSubscriptionBillingRatePlanService.getRatePlanViewBatchRequest(ratePlanViewBatchRequest);

        SubscriptionData response = subscriptionRateplanConverter.convert(ratePlanViewBatchResponse);

        subscriptionData.setPricePlan(response.getPricePlan());
        subscriptionData.setBillingFrequency(response.getBillingFrequency());
        return subscriptionData;
    }

    public void setSapSubscriptionBillingRatePlanService(SapSubscriptionBillingRatePlanService sapSubscriptionBillingRatePlanService) {
        this.sapSubscriptionBillingRatePlanService = sapSubscriptionBillingRatePlanService;
    }

    public void setSubscriptionRateplanConverter(Converter<RatePlanViewBatchResponse, SubscriptionData> subscriptionRateplanConverter) {
        this.subscriptionRateplanConverter = subscriptionRateplanConverter;
    }
}
