/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saprevenuecloudorder.populators.rateplan.v2;


import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.rateplanResponse.v2.SetupFee;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.subscriptionfacades.data.BillingTimeData;
import de.hybris.platform.subscriptionfacades.data.OneTimeChargeEntryData;
import de.hybris.platform.subscriptionfacades.product.converters.populator.AbstractChargeEntryPopulator;
import de.hybris.platform.subscriptionservices.jalo.BillingTime;
import org.springframework.beans.factory.annotation.Required;

import java.math.BigDecimal;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;


public class SetupFeePopulator<SOURCE extends SetupFee, TARGET extends OneTimeChargeEntryData>  implements Populator<SOURCE, TARGET> {

    private PriceDataFactory priceDataFactory;

    @Override
    public void populate(SOURCE source, TARGET target) throws ConversionException {

        validateParameterNotNullStandardMessage("source", source);
        validateParameterNotNullStandardMessage("target", target);

        target.setSubscriptionBillingId(source.getId());

        double price = 0d;
        if (source.getPrice() != null)
        {
            price = Double.parseDouble(source.getPrice().getAmount())
            ;
        }

        String currency = source.getPrice().getCurrency();

        if(currency != null) {
            final PriceData priceData = getPriceDataFactory().create(PriceDataType.BUY, BigDecimal.valueOf(price), currency);
            target.setPrice(priceData);
        }


        BillingTimeData billingTimeData = new BillingTimeData();
        billingTimeData.setCode(source.getMetricId());
        target.setBillingTime(billingTimeData);

    }

    protected PriceDataFactory getPriceDataFactory()
    {
        return priceDataFactory;
    }

    @Required
    public void setPriceDataFactory(final PriceDataFactory priceDataFactory)
    {
        this.priceDataFactory = priceDataFactory;
    }

}
