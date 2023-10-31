/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.c4ccpiquote.strategy.impl;

import static com.sap.hybris.c4ccpiquote.constants.C4ccpiquoteConstants.QUOTE_REPLICATION_DESTINATION_ID;
import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

import com.sap.hybris.c4ccpiquote.service.C4CConsumedDestinationService;

import de.hybris.platform.commerceservices.order.strategies.impl.DefaultQuoteCartValidationStrategy;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;


public class DefaultSapCpiQuoteCartValidationStrategy extends DefaultQuoteCartValidationStrategy
{
	private C4CConsumedDestinationService c4CConsumedDestinationService;

	@Override
	public boolean validate(final AbstractOrderModel source, final AbstractOrderModel target)
	{
		
		if (!getC4CConsumedDestinationService().checkIfDestinationExists(QUOTE_REPLICATION_DESTINATION_ID)){
        	return super.validate(source, target);
        }
        
		validateParameterNotNullStandardMessage("source", source);
		validateParameterNotNullStandardMessage("target", target);

		if (source.getSubtotal() == 0 && target.getSubtotal() == 0)
		{
			return false;
		}

		return compareEntries(source.getEntries(), target.getEntries());
	}

	@Override
	protected boolean compareEntries(final List<AbstractOrderEntryModel> sourceEntries,
			final List<AbstractOrderEntryModel> targetEntries)
	{
		if (!getC4CConsumedDestinationService().checkIfDestinationExists(QUOTE_REPLICATION_DESTINATION_ID)){
        	return super.compareEntries(sourceEntries, targetEntries);
        }
		
		if (CollectionUtils.size(sourceEntries) != CollectionUtils.size(targetEntries))
		{
			return false;
		}

		for (int i = 0; i < sourceEntries.size(); i++)
		{
			final AbstractOrderEntryModel sourceEntry = sourceEntries.get(i);
			final AbstractOrderEntryModel targetEntry = targetEntries.get(i);

			if (ObjectUtils.compare(sourceEntry.getEntryNumber(), targetEntry.getEntryNumber()) != 0
					|| !StringUtils.equals(sourceEntry.getProduct().getCode(), targetEntry.getProduct().getCode())
					|| ObjectUtils.compare(sourceEntry.getQuantity(), targetEntry.getQuantity()) != 0)
			{
				return false;
			}
		}

		return true;
	}

	public C4CConsumedDestinationService getC4CConsumedDestinationService() {
		return c4CConsumedDestinationService;
	}

	public void setC4CConsumedDestinationService(C4CConsumedDestinationService c4cConsumedDestinationService) {
		c4CConsumedDestinationService = c4cConsumedDestinationService;
	}
	

}
