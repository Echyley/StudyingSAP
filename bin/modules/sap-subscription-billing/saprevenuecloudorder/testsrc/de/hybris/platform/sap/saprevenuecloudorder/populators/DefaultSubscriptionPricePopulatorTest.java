/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saprevenuecloudorder.populators;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.sap.saprevenuecloudorder.populators.subscription.DefaultSubscriptionPricePopulator;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.subscriptionfacades.data.SubscriptionPricePlanData;
import de.hybris.platform.subscriptionservices.model.SubscriptionPricePlanModel;
import de.hybris.platform.subscriptionservices.price.SubscriptionCommercePriceService;

@RunWith(MockitoJUnitRunner.class)
public class DefaultSubscriptionPricePopulatorTest {

    @Mock
    private Populator<SubscriptionPricePlanModel, SubscriptionPricePlanData> pricePlanOneTimeChargePopulator;
    @Mock
    private Populator<SubscriptionPricePlanModel, SubscriptionPricePlanData> pricePlanRecurringChargePopulator;
    @Mock
    private Converter<SubscriptionPricePlanModel, SubscriptionPricePlanData> pricePlanUsageChargeConverter;
    @Mock
    private SubscriptionCommercePriceService commercePriceService;
    @Mock
    private SubscriptionPricePlanModel source;

    private static ProductData target = new ProductData();
    private static SubscriptionPricePlanData pricePlanData = new SubscriptionPricePlanData();
    private static PriceData priceData = new PriceData();

    private static final String MODEL_NAME = "subs_model_name";
    private static final String ISO_CODE ="iso_code";
    private static final Long MIN_QUANTITY_VAL = 2000l;
    private static final Long MAX_QUANTITY_VAL = 8000l;


    @InjectMocks
    DefaultSubscriptionPricePopulator <SubscriptionPricePlanModel, ProductData> defaultSubscriptionPricePopulator;

    @Before
    public void setUp(){

        priceData.setCurrencyIso(ISO_CODE);
        priceData.setMaxQuantity(MAX_QUANTITY_VAL);
        priceData.setMinQuantity(MIN_QUANTITY_VAL);
        target.setPrice(priceData);

        //mock
        when(pricePlanUsageChargeConverter.convert(any(SubscriptionPricePlanModel.class))).thenReturn(pricePlanData);
        when(source.getName()).thenReturn(MODEL_NAME);
    }

    @Test
    public void test() {

        //execute
        defaultSubscriptionPricePopulator.populate(source, target);

        //Verify
        Assert.assertEquals(ISO_CODE,target.getPrice().getCurrencyIso());
        Assert.assertEquals(MIN_QUANTITY_VAL,target.getPrice().getMinQuantity());
        Assert.assertEquals(MAX_QUANTITY_VAL,target.getPrice().getMaxQuantity());

    }

}
