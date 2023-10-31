/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saprevenuecloudorder.populators.rateplan.v2;


import de.hybris.platform.converters.Converters;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.rateplanResponse.v2.*;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.subscriptionfacades.data.*;
import org.apache.commons.collections.CollectionUtils;

import java.util.LinkedList;
import java.util.List;


public class DefaultSapSubscriptionRateplanPopulator<S extends RatePlanViewBatchResponse, T extends SubscriptionData>
        implements Populator<S, T> {


    private Converter<SetupFee,OneTimeChargeEntryData> setupFeeConverter;
    private Converter<FixedRate, RecurringChargeEntryData> fixedRatesConverter;;
    private Converter<AllUnitVolumeRate, VolumeUsageChargeData> allUnitVolumeRatesConverter;
    private Converter<TierRate,PerUnitUsageChargeData> tierRateConverter;
    private Converter<PercentageRate,PerUnitUsageChargeData> percentageRateConverter;
    private Converter<BlockRate,PerUnitUsageChargeData> blockRateConverter;

    @Override
    public void populate(S s, T t) {
        final SubscriptionPricePlanData rateplanSubscriptionPricePlanData = new SubscriptionPricePlanData();

        if (CollectionUtils.isNotEmpty(s.getResponses())) {
            s.getResponses().get(0).getBody().getSnapshots().forEach(entry -> {
            

                //OneTime Charge Populator
                List<OneTimeChargeEntryData> oneTimeChargeEntryDataList = Converters.convertAll(entry.getSetupFees(), getSetupFeeConverter());
                rateplanSubscriptionPricePlanData.setOneTimeChargeEntries(oneTimeChargeEntryDataList);

                //Recurring Charge Populator
                List<RecurringChargeEntryData> recurringChargeEntryDataList = Converters.convertAll(entry.getFixedRates(), getFixedRatesConverter());
                rateplanSubscriptionPricePlanData.setRecurringChargeEntries(recurringChargeEntryDataList);

                //Usage Charge populator
                final List < UsageChargeData > usageCharges = new LinkedList<>();
                //Volume Usage Charge populator
                List<VolumeUsageChargeData> volumeUsageChargeData = Converters.convertAll(entry.getAllUnitVolumeRates(), getAllUnitVolumeRatesConverter());
                usageCharges.addAll(volumeUsageChargeData);


                //Tier Usage Charge populator
                List<PerUnitUsageChargeData> tierUsageChargeData = Converters.convertAll(entry.getTierRates(),getTierRateConverter());
                usageCharges.addAll(tierUsageChargeData);

                //Block Usage Charge
                List<PerUnitUsageChargeData> blockUsageChargeData = Converters.convertAll(entry.getBlockRates(),getBlockRateConverter());
                usageCharges.addAll(blockUsageChargeData);

                //Percentage Usage Charge
                List<PerUnitUsageChargeData> percentageUsageChargeData = Converters.convertAll(entry.getPercentageRates(),getPercentageRateConverter());
                usageCharges.addAll(percentageUsageChargeData);

                rateplanSubscriptionPricePlanData.setUsageCharges(usageCharges);
            });

            t.setPricePlan(rateplanSubscriptionPricePlanData);
            t.setBillingFrequency(s.getResponses().get(0).getBody().getPeriodicity());
        }
    }

    public Converter<FixedRate, RecurringChargeEntryData> getFixedRatesConverter() {
        return fixedRatesConverter;
    }

    public void setFixedRatesConverter(Converter<FixedRate, RecurringChargeEntryData> fixedRatesConverter) {
        this.fixedRatesConverter = fixedRatesConverter;
    }


    public Converter<SetupFee, OneTimeChargeEntryData> getSetupFeeConverter() {
        return setupFeeConverter;
    }

    public void setSetupFeeConverter(Converter<SetupFee, OneTimeChargeEntryData> setupFeeConverter) {
        this.setupFeeConverter = setupFeeConverter;
    }
    public Converter<AllUnitVolumeRate, VolumeUsageChargeData> getAllUnitVolumeRatesConverter() {
        return allUnitVolumeRatesConverter;
    }

    public void setAllUnitVolumeRatesConverter(Converter<AllUnitVolumeRate, VolumeUsageChargeData> allUnitVolumeRatesConverter) {
        this.allUnitVolumeRatesConverter = allUnitVolumeRatesConverter;
    }

    public Converter<TierRate, PerUnitUsageChargeData> getTierRateConverter() {
        return tierRateConverter;
    }

    public void setTierRateConverter(Converter<TierRate, PerUnitUsageChargeData> tierRateConverter) {
        this.tierRateConverter = tierRateConverter;
    }

    public Converter<PercentageRate, PerUnitUsageChargeData> getPercentageRateConverter() {
        return percentageRateConverter;
    }

    public void setPercentageRateConverter(Converter<PercentageRate, PerUnitUsageChargeData> percentageRateConverter) {
        this.percentageRateConverter = percentageRateConverter;
    }

    public Converter<BlockRate, PerUnitUsageChargeData> getBlockRateConverter() {
        return blockRateConverter;
    }

    public void setBlockRateConverter(Converter<BlockRate, PerUnitUsageChargeData> blockRateConverter) {
        this.blockRateConverter = blockRateConverter;
    }





}
