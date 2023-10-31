/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package ydocumentcartpackage.test;

import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.directpersistence.annotation.RetryConcurrentModification;
import de.hybris.platform.servicelayer.model.ItemModelContext;
import de.hybris.platform.servicelayer.model.ModelContextUtils;
import de.hybris.platform.servicelayer.model.ModelService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConcurrentModificationTestService
{
	private static final Logger LOG = LoggerFactory.getLogger(ConcurrentModificationTestService.class);
	private final ModelService modelService;

	public ConcurrentModificationTestService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	@RetryConcurrentModification(retries = 120, sleepIntervalInMillis = 10)
	public void updateCart(final PK modelPk)
	{
		logStart("updateCart", modelPk);
		final CartModel model = modelService.get(modelPk);

		modelService.refresh(model);
		model.getEntries().forEach(modelService::refresh);

		final long version = getVersion(model);

		final long paymentCost = Math.round(model.getPaymentCost());
		model.setPaymentCost(paymentCost + 1.0);
		modelService.save(model);

		logEnd(model, version, paymentCost, "updateCart");
	}

	@RetryConcurrentModification(retries = 120, sleepIntervalInMillis = 10)
	public void updateCartEntry(final PK modelPk)
	{
		logStart("updateCartEntry", modelPk);
		final CartEntryModel model = modelService.get(modelPk);

		modelService.refresh(model);

		final long version = getVersion(model);
		final Long quantity = model.getQuantity();

		model.setQuantity(quantity + 1);

		modelService.save(model);

		logEnd(model, version, quantity, "updateCartEntry");
	}

	private void logEnd(final ItemModel model, final long version, final long counter, final String methodName)
	{
		LOG.debug("{}: {} - pk: {}, ver: {}, counter: {}", methodName, Thread.currentThread(), model.getPk().getLong(), version,
				counter);

		LOG.debug("{}: Exiting business logic => {}", methodName, Thread.currentThread());
	}

	private void logStart(final String methodName, final PK modelPk)
	{
		LOG.debug("{}: Entering business logic => {} for {}", methodName, Thread.currentThread(), modelPk.getLong());
	}

	private long getVersion(final ItemModel model)
	{
		final ItemModelContext itemModelContext = ModelContextUtils.getItemModelContext(model);
		return itemModelContext.getPersistenceVersion();
	}

}
