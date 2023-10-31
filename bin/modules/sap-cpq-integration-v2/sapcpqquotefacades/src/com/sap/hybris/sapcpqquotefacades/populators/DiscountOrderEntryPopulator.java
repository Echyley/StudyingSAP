/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcpqquotefacades.populators;

/**
 * Converter for converting order / cart entries. It sets the total and base prices for entries that have multiple
 * billing frequencies (as they are subscription products)
 */

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.QuoteEntryModel;
import de.hybris.platform.util.DiscountValue;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.StringUtils;

import com.sap.hybris.sapcpqquotefacades.data.quote.DiscountValueData;

public class DiscountOrderEntryPopulator implements Populator<AbstractOrderEntryModel, OrderEntryData> {
    @Override
    public void populate(@Nonnull final AbstractOrderEntryModel source, @Nonnull final OrderEntryData target) {
    	List<DiscountValueData> discountValueDataList = new ArrayList<>();
        DiscountValueData discountValueData = new DiscountValueData();

        if (source instanceof QuoteEntryModel) {
            QuoteEntryModel quoteEntry = (QuoteEntryModel) source;
            validateParameterNotNullStandardMessage("source", source);
            validateParameterNotNullStandardMessage("target", target);
            target.setDiscountPercent("0.00");
            if(StringUtils.isNotEmpty(quoteEntry.getDiscountPercent())) {
            	 String discountPercent  = String.format("%.4s",quoteEntry.getDiscountPercent());
              	 target.setDiscountPercent(discountPercent);
            }
            
       }
        List<DiscountValue> discountValues = source.getDiscountValues();
        if(discountValues != null) {
        for (Iterator<DiscountValue> iterator = discountValues.iterator(); iterator.hasNext();) {
			DiscountValue discountValue = (DiscountValue) iterator.next();
			
			discountValueData.setValue(discountValue.getValue());
	    	discountValueData.setAppliedValue(discountValue.getAppliedValue());
	    	discountValueData.setIsoCode(discountValue.getCurrencyIsoCode());
	    	discountValueDataList.add(discountValueData);
			
		}
        }
        
        target.setCpqDiscounts(discountValueDataList);
    }
}
