/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.orderexchange.outbound.impl;


import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.sap.core.configuration.global.SAPGlobalConfigurationService;
import de.hybris.platform.sap.orderexchange.outbound.B2CCustomerHelper;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.task.TaskConditionModel;


/**
 * Helper class for b2c customer processes within order
 */
public class DefaultB2CCustomerHelper implements B2CCustomerHelper

{
	private static final String SAP_ORDER_CUSTOMER_EVENT = "ERPCustomerReplicationEvent_";
	private FlexibleSearchService flexibleSearchService;
	private SAPGlobalConfigurationService sAPGlobalConfigurationService;
	private BusinessProcessService businessProcessService;

	
	public BusinessProcessService getBusinessProcessService()
	{
		return businessProcessService;
	}

	
	public void setBusinessProcessService(final BusinessProcessService businessProcessService)
	{
		this.businessProcessService = businessProcessService;
	}

	
	public SAPGlobalConfigurationService getsAPGlobalConfigurationService()
	{
		return sAPGlobalConfigurationService;
	}

	
	public void setsAPGlobalConfigurationService(final SAPGlobalConfigurationService sAPGlobalConfigurationService)
	{
		this.sAPGlobalConfigurationService = sAPGlobalConfigurationService;
	}

	public String determineB2CCustomer(final OrderModel order)
	{
		return Boolean.TRUE.equals(sAPGlobalConfigurationService.getProperty("replicateregistereduser")) ? ((CustomerModel) order
				.getUser()).getSapConsumerID() : null;
	}

	
	public FlexibleSearchService getFlexibleSearchService()
	{
		return flexibleSearchService;
	}

	
	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;
	}

	@Override
	public void processWaitingCustomerOrders(final String customerID)
	{
		final String orderCustomerSearchExpression = SAP_ORDER_CUSTOMER_EVENT + customerID + "%";
		final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(
				"SELECT {o:pk} FROM {TaskCondition AS o} WHERE  {o.uniqueID} LIKE ?uniqueID");
		flexibleSearchQuery.addQueryParameter("uniqueID", orderCustomerSearchExpression);

		final SearchResult<TaskConditionModel> taskConditions = this.flexibleSearchService.search(flexibleSearchQuery);

		for (final TaskConditionModel taskConditionModel : taskConditions.getResult())
		{
			final String trigger = taskConditionModel.getUniqueID();
			businessProcessService.triggerEvent(trigger);
		}

	}
}
