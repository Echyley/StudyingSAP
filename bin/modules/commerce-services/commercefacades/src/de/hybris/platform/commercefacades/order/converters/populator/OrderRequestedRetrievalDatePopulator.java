/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commercefacades.order.converters.populator;

import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.exceptions.AttributeNotSupportedException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.store.BaseStoreModel;

import org.apache.log4j.Logger;


/**
 * Implementation for populating retrieval dates for order.
 */
public class OrderRequestedRetrievalDatePopulator implements Populator<OrderModel, OrderData>
{

	private static final Logger LOG = Logger.getLogger(OrderRequestedRetrievalDatePopulator.class);
	private ModelService modelService;

	@Override
	public void populate(final OrderModel source, final OrderData target) throws ConversionException
	{
		if (isRequestedRetrievalDateFeatureEnabled(source.getStore()))
		{
			target.setRequestedRetrievalAt(source.getRequestedRetrievalDate());
		}
	}

	protected boolean isRequestedRetrievalDateFeatureEnabled(final BaseStoreModel baseStore)
	{
		if (baseStore != null)
		{
			try
			{
				final Boolean featureEnabled = getModelService().getAttributeValue(baseStore,
						BaseStoreModel.REQUESTEDRETRIEVALDATEENABLED);
				return featureEnabled != null && featureEnabled.booleanValue();
			}
			catch (final AttributeNotSupportedException e)
			{
				LOG.debug("Requested retrieval date feature is not enabled, please update the system", e);
			}
		}
		return false;
	}

	public ModelService getModelService()
	{
		return modelService;
	}

	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

}
