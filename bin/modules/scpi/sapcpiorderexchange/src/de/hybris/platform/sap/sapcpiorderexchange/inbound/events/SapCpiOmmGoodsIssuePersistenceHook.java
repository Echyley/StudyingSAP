/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapcpiorderexchange.inbound.events;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.inboundservices.persistence.PersistenceContext;
import de.hybris.platform.inboundservices.persistence.hook.PrePersistHook;
import de.hybris.platform.sap.orderexchange.constants.DataHubInboundConstants;
import de.hybris.platform.sap.orderexchange.datahub.inbound.DataHubInboundDeliveryHelper;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;


public class SapCpiOmmGoodsIssuePersistenceHook
		implements PrePersistHook, de.hybris.platform.odata2services.odata.persistence.hook.PrePersistHook
{
	private static final Logger LOG = LoggerFactory.getLogger(SapCpiOmmGoodsIssuePersistenceHook.class);
	private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern(DataHubInboundConstants.IDOC_DATE_FORMAT);

	private DataHubInboundDeliveryHelper sapDataHubInboundDeliveryHelper;

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
			LOG.info("The persistence hook sapCpiOmmGoodsIssuePersistenceHook is called!");
			final OrderModel orderModel = (OrderModel) item;
			getSapDataHubInboundDeliveryHelper().processDeliveryAndGoodsIssue(orderModel.getCode(), orderModel.getSapPlantCode(),
					orderModel.getSapGoodsIssueDate().toInstant().atOffset(ZoneOffset.UTC).toLocalDate().format(DTF));
			return Optional.empty();
		}
		return Optional.of(item);
	}

	public DataHubInboundDeliveryHelper getSapDataHubInboundDeliveryHelper()
	{
		return sapDataHubInboundDeliveryHelper;
	}

	@Required
	public void setSapDataHubInboundDeliveryHelper(DataHubInboundDeliveryHelper sapDataHubInboundDeliveryHelper)
	{
		this.sapDataHubInboundDeliveryHelper = sapDataHubInboundDeliveryHelper;
	}

}