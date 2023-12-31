/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.yorderfulfillment.jobs;

import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.sap.orderexchange.constants.SapOrderExchangeActionConstants;
import de.hybris.platform.sap.orderexchange.outbound.OrderExchangeRepair;
import de.hybris.platform.sap.yorderfulfillment.constants.ActionIds;
import de.hybris.platform.sap.yorderfulfillment.constants.YsaporderfulfillmentConstants;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import java.util.List;


/**
 * Resend the order creation IDoc to SAP backend in case that the original sending (incl. retry) was not successful
 */
public class OrderExchangeRepairJob extends AbstractJobPerformable<CronJobModel>
{

	private static final Logger LOG = Logger.getLogger(OrderExchangeRepairJob.class);

	private OrderExchangeRepair orderExchangeRepair;
	private BusinessProcessService businessProcessService;


	@Override
	public PerformResult perform(final CronJobModel job)
	{
		List<OrderProcessModel> objectsToRepair;
		objectsToRepair = orderExchangeRepair.findAllProcessModelsToRepair(YsaporderfulfillmentConstants.ORDER_PROCESS_NAME,
				SapOrderExchangeActionConstants.ERROR_END_MESSAGE);

		final Integer objectsToRepairCount = Integer.valueOf(objectsToRepair.size());
		LOG.info("Number of objects that are being repaired: " + objectsToRepairCount);

		for (final OrderProcessModel businessProcessModel : objectsToRepair)
		{
			businessProcessService.restartProcess(businessProcessModel, ActionIds.SEND_ORDER_AS_IDOC);
		}

		return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
	}


	
	public OrderExchangeRepair getOrderExchangeRepair()
	{
		return orderExchangeRepair;
	}


	
	@Required
	public void setOrderExchangeRepair(final OrderExchangeRepair orderExchangeRepair)
	{
		this.orderExchangeRepair = orderExchangeRepair;
	}


	
	public BusinessProcessService getBusinessProcessService()
	{
		return businessProcessService;
	}


	
	@Required
	public void setBusinessProcessService(final BusinessProcessService businessProcessService)
	{
		this.businessProcessService = businessProcessService;
	}

}
