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
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.subscriptionfacades.data.PerUnitUsageChargeData;
import de.hybris.platform.subscriptionfacades.data.UsageChargeEntryData;
import de.hybris.platform.subscriptionfacades.data.UsageChargeTypeData;
import de.hybris.platform.subscriptionfacades.data.UsageUnitData;
import de.hybris.platform.subscriptionservices.model.UsageUnitModel;
import org.apache.commons.collections.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

public class BlockRatesPopulator<SOURCE extends BlockRate, TARGET extends PerUnitUsageChargeData>  implements Populator<SOURCE, TARGET> {
    private PriceDataFactory priceDataFactory;
    private SapRevenueCloudProductService sapRevenueCloudProductService;

    @Override
    public void populate(SOURCE source, TARGET target) throws ConversionException {
        validateParameterNotNullStandardMessage("source", source);
        validateParameterNotNullStandardMessage("target", target);

        target.setSubscriptionBillingId(source.getId());

        if (source.getMetricId() != null) {
            UsageUnitData usageUnitData = new UsageUnitData();
            usageUnitData.setId(source.getMetricId());

            UsageUnitModel unitModel = sapRevenueCloudProductService.getUsageUnitfromId(source.getMetricId());
            usageUnitData.setName(unitModel.getName());
            usageUnitData.setNamePlural(unitModel.getNamePlural());
            target.setUsageUnit(usageUnitData);
        }

        target.setBlockSize(Integer.parseInt(source.getBlockSize()));
        target.setIncludedQty(Integer.parseInt(source.getIncludedQuantity()));

        //Setting Usage charge Type
        UsageChargeTypeData usageChargeType = new UsageChargeTypeData();
        usageChargeType.setCode("block_usage_charge");
        target.setUsageChargeType(usageChargeType);

        //Setting Usage Units
        UsageChargeEntryData usageChargeEntryData = new UsageChargeEntryData();
        usageChargeEntryData.setPrice(populatePrice(source.getPricePerBlock().getAmount(),source.getPricePerBlock().getCurrency()));
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
