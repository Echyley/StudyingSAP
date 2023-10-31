/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.rules.rao.provider;

import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.customersupport.CommerceCustomerSupportService;
import de.hybris.platform.ruleengineservices.rao.CustomerSupportRAO;
import de.hybris.platform.ruleengineservices.rao.providers.RAOProvider;

import java.util.Collections;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;


/**
 * Provider for the {@link CustomerSupportRAO}
 */
public class ProductConfigCustomerSupportRAOProvider implements RAOProvider
{
	private CommerceCustomerSupportService commerceCustomerSupportService;

	@Override
	public Set<CustomerSupportRAO> expandFactModel(final Object modelFact)
	{
		final CustomerSupportRAO customerSupportRAO = new CustomerSupportRAO();
		customerSupportRAO.setCustomerSupportAgentActive(Boolean.valueOf(getCommerceCustomerSupportService()
				.isCustomerSupportAgentActive()));
		if (getCommerceCustomerSupportService().isCustomerSupportAgentActive())
		{
			final UserModel emulatedCustomer = getCommerceCustomerSupportService().getEmulatedCustomer();
			customerSupportRAO.setCustomerEmulationActive(Boolean.valueOf(emulatedCustomer != null));
		}
		else
		{
			customerSupportRAO.setCustomerEmulationActive(Boolean.FALSE);
		}
		return Collections.singleton(customerSupportRAO);
	}

	protected CommerceCustomerSupportService getCommerceCustomerSupportService()
	{
		return this.commerceCustomerSupportService;
	}

	/**
	 * @param commerceCustomerSupportService
	 */
	@Required
	public void setCommerceCustomerSupportService(final CommerceCustomerSupportService commerceCustomerSupportService)
	{
		this.commerceCustomerSupportService = commerceCustomerSupportService;
	}
}
