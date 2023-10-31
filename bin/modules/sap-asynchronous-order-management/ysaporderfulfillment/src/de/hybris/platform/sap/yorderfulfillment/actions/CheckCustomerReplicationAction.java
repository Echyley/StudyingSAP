/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.yorderfulfillment.actions;

import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction;
import de.hybris.platform.sap.core.configuration.global.SAPGlobalConfigurationService;
import de.hybris.platform.sap.orderexchange.outbound.B2CCustomerHelper;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;


/**
 * 
 */
public class CheckCustomerReplicationAction extends AbstractSimpleDecisionAction<OrderProcessModel>
{

	private FlexibleSearchService flexibleSearchService;
	private SAPGlobalConfigurationService sAPGlobalConfigurationService;
	private B2CCustomerHelper b2CCustomerHelper;

	
	public B2CCustomerHelper getB2CCustomerHelper()
	{
		return b2CCustomerHelper;
	}

	
	public void setB2CCustomerHelper(final B2CCustomerHelper b2cCustomerHelper)
	{
		b2CCustomerHelper = b2cCustomerHelper;
	}

	
	public SAPGlobalConfigurationService getsAPGlobalConfigurationService()
	{
		return sAPGlobalConfigurationService;
	}

	
	public void setsAPGlobalConfigurationService(final SAPGlobalConfigurationService sAPGlobalConfigurationService)
	{
		this.sAPGlobalConfigurationService = sAPGlobalConfigurationService;
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
	public Transition executeAction(final OrderProcessModel process)
	{
		final OrderModel order = process.getOrder();


		if (sAPGlobalConfigurationService.getProperty("replicateregistereduser").equals(Boolean.TRUE))
		{
			final CustomerModel customerModel = ((CustomerModel) order.getUser());
			final boolean isCustomerExported = customerModel.getSapIsReplicated().booleanValue();
			final boolean isGuestUser = isGuestUser(customerModel);
			final boolean isB2B = isB2BCase(order);

			if (isCustomerExported || isGuestUser || isB2B)
			{
				return Transition.OK;
			}
			else
			{
				return Transition.NOK;
			}
		}
		return Transition.OK;
	}

	protected boolean isB2BCase(final OrderModel orderModel)
	{
		if (orderModel.getSite() != null)
		{
			return SiteChannel.B2B.equals(orderModel.getSite().getChannel());
		}
		else
		{
			return false;
		}
	}

	protected boolean isGuestUser(final CustomerModel customerModel)
	{
		return CustomerType.GUEST.equals(customerModel.getType());
	}

}
