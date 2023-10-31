/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.facades.impl;

import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.cpq.productconfig.facades.OrderIntegrationFacade;
import de.hybris.platform.cpq.productconfig.facades.data.ProductConfigData;
import de.hybris.platform.cpq.productconfig.services.AbstractOrderIntegrationService;
import de.hybris.platform.order.OrderService;
import de.hybris.platform.store.services.BaseStoreService;


/**
 * Default implementation of CPQ facade for integration with order
 */
public class DefaultOrderIntegrationFacade implements OrderIntegrationFacade
{

	private final AbstractOrderIntegrationService abstractOrderIntegrationService;
	private final OrderService orderService;
	private final BaseStoreService baseStoreService;
	private final CustomerAccountService customerAccountService;

	/**
	 * Default constructor for dependency injection.
	 *
	 * @param abstractOrderIntegrationService
	 *           for retrieving the configuration id from the abstract order entry
	 * @param orderService
	 *           for reading the order entry model
	 * @param baseStoreService
	 *           required to read the order model
	 * @param customerAccountService
	 *           for reading the order model
	 */
	public DefaultOrderIntegrationFacade(final AbstractOrderIntegrationService abstractOrderIntegrationService,
			final OrderService orderService, final BaseStoreService baseStoreService,
			final CustomerAccountService customerAccountService)
	{
		super();
		this.abstractOrderIntegrationService = abstractOrderIntegrationService;
		this.orderService = orderService;
		this.baseStoreService = baseStoreService;
		this.customerAccountService = customerAccountService;
	}


	@Override
	public ProductConfigData getConfigurationId(final String orderCode, final int entryNumber)
	{
		final OrderEntryModel orderEntry = findOrderEntry(orderCode, entryNumber);
		final String configId = getAbstractOrderIntegrationService().getConfigIdForAbstractOrderEntry(orderEntry);
		final ProductConfigData data = new ProductConfigData();
		data.setConfigId(configId);
		return data;
	}

	@Override
	public String getProductCode(final String orderCode, final int entryNumber)
	{
		final OrderEntryModel orderEntry = findOrderEntry(orderCode, entryNumber);
		return orderEntry.getProduct().getCode();
	}

	protected OrderEntryModel findOrderEntry(final String orderCode, final int entryNumber)
	{
		final OrderModel orderModel = getCustomerAccountService().getOrderForCode(orderCode,
				getBaseStoreService().getCurrentBaseStore());

		return getOrderService().getEntryForNumber(orderModel, entryNumber);
	}

	protected AbstractOrderIntegrationService getAbstractOrderIntegrationService()
	{
		return abstractOrderIntegrationService;
	}

	protected OrderService getOrderService()
	{
		return orderService;
	}

	protected BaseStoreService getBaseStoreService()
	{
		return baseStoreService;
	}

	protected CustomerAccountService getCustomerAccountService()
	{
		return customerAccountService;
	}

}
