/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.oaa.commerce.services.sourcing.strategy.impl;

import org.apache.log4j.Logger;

import com.sap.retail.oaa.commerce.services.rest.util.exception.BackendDownException;
import com.sap.retail.oaa.commerce.services.sourcing.SourcingService;
import com.sap.retail.oaa.commerce.services.sourcing.exception.SourcingException;
import com.sap.retail.oaa.commerce.services.sourcing.strategy.SourcingStrategy;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;

import java.util.concurrent.atomic.AtomicReference;


/**
 * Default Implementation for SourcingStrategy
 */
public class DefaultSourcingStrategy implements SourcingStrategy
{

	private static final Logger LOG = Logger.getLogger(DefaultSourcingStrategy.class);

	private SourcingService sourcingService;

	private AtomicReference<SourcingService> atomicRefSourcingService = new AtomicReference<>(sourcingService);

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.sap.retail.oaa.commerce.services.sourcing.strategy.OaaSourcingStrategy#doSourcing(de.hybris.platform.core.
	 * model.order.AbstractOrderModel)
	 */
	@Override
	public boolean doSourcing(final AbstractOrderModel abstractOrderModel)
	{
		LOG.info("Source Order: " + abstractOrderModel.getCode());

		try
		{
			getSourcingService().callRestServiceAndPersistResult(abstractOrderModel);
		}
		catch (final SourcingException e)
		{
			LOG.error(e);
			return false;
		}
		catch (final BackendDownException e)
		{
			LOG.error(e.getMessage(), e);

			//When Cart has Pick Up Items sourcing is not successful in offline scenario
			if (hasPickUpItems(abstractOrderModel))
			{
				return false;
			}

		}
		return true;
	}

	protected boolean hasPickUpItems(final AbstractOrderModel abstractOrderModel)
	{
		for (final AbstractOrderEntryModel entry : abstractOrderModel.getEntries())
		{
			if (entry.getDeliveryPointOfService() != null)
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * @return the sourcingService
	 */
	protected SourcingService getSourcingService()
	{
		return this.atomicRefSourcingService.get();
	}

	/**
	 * @param sourcingService
	 *           the sourcingService to set
	 */
	public void setSourcingService(final SourcingService sourcingService)
	{
		this.atomicRefSourcingService.set(sourcingService);
	}

}
