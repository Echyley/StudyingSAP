/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapcpiorderexchangeoms.inbound.events;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.inboundservices.persistence.PersistenceContext;
import de.hybris.platform.inboundservices.persistence.hook.PrePersistHook;
import de.hybris.platform.sap.sapmodel.model.SAPOrderModel;
import de.hybris.platform.sap.saporderexchangeoms.datahub.inbound.SapDataHubInboundHelper;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;


public class SapCpiOmsDeliveryPersistenceHook
		implements PrePersistHook, de.hybris.platform.odata2services.odata.persistence.hook.PrePersistHook
{

	private static final Logger LOG = LoggerFactory.getLogger(SapCpiOmsDeliveryPersistenceHook.class);
	private SapDataHubInboundHelper sapDataHubInboundHelper;

	@Override
	public Optional<ItemModel> execute(final ItemModel item)
	{
		return execute(item, null);
	}

	@Override
	public Optional<ItemModel> execute(final ItemModel item, final PersistenceContext context)
	{
		if (item instanceof SAPOrderModel)
		{
			LOG.info("The persistence hook sapCpiOmsDeliveryPersistenceHook is called!");
			final SAPOrderModel sapOrderModel = (SAPOrderModel) item;
			if (sapOrderModel.getQuantity() > 0)
			{
				getSapDataHubInboundHelper().processDeliveryNotification(sapOrderModel.getCode(),
						sapOrderModel.getOrderEntryNumber().toString());
			}
			else
			{
				LOG.error("Delivery notification for order [{}] and entry number [{}] failed because of the missing quantity!",
						sapOrderModel.getCode(), sapOrderModel.getOrderEntryNumber());
			}
			return Optional.empty();
		}
		return Optional.of(item);
	}

	protected SapDataHubInboundHelper getSapDataHubInboundHelper()
	{
		return sapDataHubInboundHelper;
	}

	@Required
	public void setSapDataHubInboundHelper(SapDataHubInboundHelper sapDataHubInboundHelper)
	{
		this.sapDataHubInboundHelper = sapDataHubInboundHelper;
	}

}
