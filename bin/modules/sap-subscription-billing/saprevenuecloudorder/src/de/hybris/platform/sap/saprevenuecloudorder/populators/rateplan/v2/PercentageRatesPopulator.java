/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saprevenuecloudorder.populators.rateplan.v2;

import com.sap.hybris.saprevenuecloudproduct.service.SapRevenueCloudProductService;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.rateplanResponse.v2.BlockRate;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.rateplanResponse.v2.PercentageRate;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.subscriptionfacades.data.PerUnitUsageChargeData;
import de.hybris.platform.subscriptionfacades.data.UsageChargeEntryData;
import de.hybris.platform.subscriptionfacades.data.UsageChargeTypeData;
import de.hybris.platform.subscriptionfacades.data.UsageUnitData;
import de.hybris.platform.subscriptionservices.model.UsageUnitModel;

import java.math.BigDecimal;
import java.util.List;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

public class PercentageRatesPopulator  <SOURCE extends PercentageRate, TARGET extends PerUnitUsageChargeData>  implements Populator<SOURCE, TARGET> {
    private PriceDataFactory priceDataFactory;
    private SapRevenueCloudProductService sapRevenueCloudProductService;

    @Override
    public void populate(SOURCE source, TARGET target) throws ConversionException {
        validateParameterNotNullStandardMessage("source", source);
        validateParameterNotNullStandardMessage("target", target);

        target.setSubscriptionBillingId(source.getId());

        //Setting Usage charge Type
        UsageChargeTypeData usageChargeType = new UsageChargeTypeData();
        usageChargeType.setCode("percentage_usage_charge");
        target.setUsageChargeType(usageChargeType);

        if (source.getMetricId() != null) {
            UsageUnitData usageUnitData = new UsageUnitData();
            usageUnitData.setId(source.getMetricId());

            UsageUnitModel unitModel = sapRevenueCloudProductService.getUsageUnitfromId(source.getMetricId());
            usageUnitData.setName(unitModel.getName());
            usageUnitData.setNamePlural(unitModel.getNamePlural());

            target.setUsageUnit(usageUnitData);
        }

        target.setRatio(source.getRatio());

        UsageChargeEntryData usageChargeEntryData = new UsageChargeEntryData();
        usageChargeEntryData.setPrice(populatePrice(source.getPricePerUnit().getAmount(),source.getPricePerUnit().getCurrency()));
        target.setUsageChargeEntries(List.of(usageChargeEntryData));
    }
    PriceData populatePrice(String amount, String currency){
        double price = 0d;
        if (amount != null)
        {
            price = Double.parseDouble(amount);
        }


        if(currency != null) {
            final PriceData priceData = getPriceDataFactory().create(PriceDataType.BUY, BigDecimal.valueOf(price), currency);
            return priceData;
        }
        return  null;
    }

    public PriceDataFactory getPriceDataFactory() {
        return priceDataFactory;
    }

    public void setPriceDataFactory(PriceDataFactory priceDataFactory) {
        this.priceDataFactory = priceDataFactory;
    }

    public SapRevenueCloudProductService getSapRevenueCloudProductService() {
        return sapRevenueCloudProductService;
    }

    public void setSapRevenueCloudProductService(SapRevenueCloudProductService sapRevenueCloudProductService) {
        this.sapRevenueCloudProductService = sapRevenueCloudProductService;
    }
}
