/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapcpiorderexchange.inbound.events;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.inboundservices.persistence.PersistenceContext;
import de.hybris.platform.inboundservices.persistence.hook.PrePersistHook;
import de.hybris.platform.sap.orderexchange.datahub.inbound.DataHubInboundOrderHelper;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;


public class SapCpiOmmOrderCancellationPersistenceHook
		implements PrePersistHook, de.hybris.platform.odata2services.odata.persistence.hook.PrePersistHook
{
	private static final Logger LOG = LoggerFactory.getLogger(SapCpiOmmOrderCancellationPersistenceHook.class);

	private DataHubInboundOrderHelper sapDataHubInboundOrderHelper;

	@Override
	public Optional<ItemModel> execute(final ItemModel item)
	{
		return execute(item, null);
	}

	@Override
	public Optional<ItemModel> execute(final ItemModel item, final PersistenceContext context)
	{
		if (item instanceof OrderModel)
		{
			LOG.info("The persistence hook sapCpiOmmOrderCancellationPersistenceHook is called!");
			final OrderModel orderModel = (OrderModel) item;
			try
			{
				getSapDataHubInboundOrderHelper().cancelOrder(orderModel.getSapRejectionReason(), orderModel.getCode());
			}
			catch (final ImpExException e)
			{
				LOG.error("Cancelling order [{}] failed! {}", orderModel.getCode(), e.getMessage());
			}
			return Optional.empty();
		}
		return Optional.of(item);
	}

	public DataHubInboundOrderHelper getSapDataHubInboundOrderHelper()
	{
		return sapDataHubInboundOrderHelper;
	}

	@Required
	public void setSapDataHubInboundOrderHelper(DataHubInboundOrderHelper sapDataHubInboundOrderHelper)
	{
		this.sapDataHubInboundOrderHelper = sapDataHubInboundOrderHelper;
	}

}
