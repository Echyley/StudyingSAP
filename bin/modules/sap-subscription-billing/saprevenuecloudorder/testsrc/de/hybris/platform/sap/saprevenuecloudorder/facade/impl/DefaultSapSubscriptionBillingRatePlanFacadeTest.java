/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saprevenuecloudorder.facade.impl;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import de.hybris.platform.sap.saprevenuecloudorder.pojo.rateplan.v2.RatePlanViewBatchRequest;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.rateplanResponse.v2.RatePlanViewBatchResponse;
import de.hybris.platform.sap.saprevenuecloudorder.service.SapSubscriptionBillingRatePlanService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.subscriptionfacades.data.ExternalObjectReferenceData;
import de.hybris.platform.subscriptionfacades.data.PricingData;
import de.hybris.platform.subscriptionfacades.data.PricingParameterData;
import de.hybris.platform.subscriptionfacades.data.SubscriptionData;
import de.hybris.platform.subscriptionfacades.data.SubscriptionPricePlanData;
import de.hybris.platform.subscriptionservices.exception.SubscriptionServiceException;

@RunWith(MockitoJUnitRunner.class)
public class DefaultSapSubscriptionBillingRatePlanFacadeTest {


    @Mock
    private SapSubscriptionBillingRatePlanService sapSubscriptionBillingRatePlanService;
    @Mock
    private Converter<RatePlanViewBatchResponse, SubscriptionData> subscriptionRateplanConverter;

    @InjectMocks
    DefaultSapSubscriptionBillingRatePlanFacade defaultSapSubscriptionBillingRatePlanFacade;

    private static List<ExternalObjectReferenceData> externalObjectReferencesList = new ArrayList<>();
    private static List<PricingParameterData> pricingParametersList = new ArrayList<>();


    private static  RatePlanViewBatchRequest ratePlanViewBatchRequest = new RatePlanViewBatchRequest();
    private static RatePlanViewBatchResponse ratePlanViewBatchResponse = new RatePlanViewBatchResponse();
    private static SubscriptionData response = new SubscriptionData();
    private static SubscriptionData subscriptionData = new SubscriptionData();
    private static PricingParameterData pricingParameter = new PricingParameterData();
    private SubscriptionData responseSubscriptionData;
    private static ExternalObjectReferenceData externalObjectReferenceData = new ExternalObjectReferenceData();
    private static PricingData pricingData = new PricingData();
    private static SubscriptionPricePlanData subscriptionPricePlanData = new SubscriptionPricePlanData();

    private static final String SUBSCRIPTIONDATA_ID = "subscriptionData_id";
    private static final String SUBSCRIPTIONDATA_NAME= " subscriptionData_name";
    private static final String SUBSCRIPTIONPPDATA_NAME = " subscriptionPPData_name";
    private static final String SUBSCRIPTIONDATA_DESCRIPTION = "for testing purpose";
    private static final String SUBSCRIPTIONDATA_PRODUCTCODE = "Prod_12345";
    private static final String SUBSCRIPTIONDATA_ORDERNUMBER = "P12345";
    private static final Integer ORDERENTRYNUMBER = 25;
    private static final String BILLINGFREQUENCY = "quarterly";
    private static final String EXTERNAL_ID = "ext_1234";
    private static final String EXTERNAL_SYS_ID = "ext_sys_1234";
    private static final String PP_CODE = "pp_123";
    private static final String PP_VALUE = "pp_val_123";

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private static Date SUBSCRIPTIONDATA_STARTDATE;
    private static Date SUBSCRIPTIONDATA_EndDATE;
    private static Calendar calendar = Calendar.getInstance();


    @Before
    public void setUp() throws Exception
    {
        try {
            SUBSCRIPTIONDATA_STARTDATE = sdf.parse(sdf.format(new Date()));
        } catch (ParseException e) {
            fail("Error : Unable to parse date ");
        }

        subscriptionPricePlanData.setName(SUBSCRIPTIONPPDATA_NAME);
        calendar.setTime(SUBSCRIPTIONDATA_STARTDATE);
        calendar.add(Calendar.YEAR, 1);
        SUBSCRIPTIONDATA_EndDATE = calendar.getTime();
        externalObjectReferencesList.add(externalObjectReferenceData);
        externalObjectReferenceData.setExternalId(EXTERNAL_ID);
        externalObjectReferenceData.setExternalSystemId(EXTERNAL_SYS_ID);

        pricingParameter.setCode(PP_CODE);
        pricingParameter.setValue(PP_VALUE);
        pricingParametersList.add(pricingParameter);
        pricingData.setPricingParameters(pricingParametersList);


        response.setBillingFrequency(BILLINGFREQUENCY);
        response.setId(SUBSCRIPTIONDATA_ID);
        response.setStartDate(SUBSCRIPTIONDATA_STARTDATE);
        response.setEndDate(SUBSCRIPTIONDATA_EndDATE);
        response.setName(SUBSCRIPTIONDATA_NAME);
        response.setDescription(SUBSCRIPTIONDATA_DESCRIPTION);
        response.setProductCode(SUBSCRIPTIONDATA_PRODUCTCODE);
        response.setOrderNumber(SUBSCRIPTIONDATA_ORDERNUMBER);
        response.setOrderEntryNumber(ORDERENTRYNUMBER);
        response.setPricePlan(subscriptionPricePlanData);
        response.setExternalObjectReferences(externalObjectReferencesList);
        response.setPricing(pricingData);
        

        //Mock
        Mockito.lenient().when(sapSubscriptionBillingRatePlanService.getRatePlanViewBatchRequest(ratePlanViewBatchRequest)).thenReturn(ratePlanViewBatchResponse);
        Mockito.lenient().when(subscriptionRateplanConverter.convert(Mockito.any())).thenReturn(response);
    }



    @Test
    public void testGetRatePlanForSubscription() {
    	
    	
        //Execute
        try {
            responseSubscriptionData = defaultSapSubscriptionBillingRatePlanFacade.getRatePlanForSubscription(response);
        } catch (SubscriptionServiceException e) {

            fail("Exception occured");
        }

        //Verify

        Assert.assertEquals(SUBSCRIPTIONDATA_ID,responseSubscriptionData.getId());
        Assert.assertEquals(SUBSCRIPTIONDATA_NAME,responseSubscriptionData.getName());
        Assert.assertEquals(SUBSCRIPTIONDATA_DESCRIPTION,responseSubscriptionData.getDescription());
        Assert.assertEquals(SUBSCRIPTIONDATA_PRODUCTCODE,responseSubscriptionData.getProductCode());
        Assert.assertEquals(SUBSCRIPTIONDATA_ORDERNUMBER,responseSubscriptionData.getOrderNumber());
        Assert.assertEquals(ORDERENTRYNUMBER,responseSubscriptionData.getOrderEntryNumber());
        Assert.assertEquals(BILLINGFREQUENCY,responseSubscriptionData.getBillingFrequency());
        Assert.assertEquals(SUBSCRIPTIONDATA_STARTDATE,responseSubscriptionData.getStartDate());
        Assert.assertEquals(SUBSCRIPTIONDATA_EndDATE,responseSubscriptionData.getEndDate());
        Assert.assertEquals(SUBSCRIPTIONPPDATA_NAME,responseSubscriptionData.getPricePlan().getName());


    }

}
