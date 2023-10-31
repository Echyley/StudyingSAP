/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.customerticketingc4cintegration.converters.populators;

import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.user.CustomerModel;


/**
 *
 * Populator for populating the customer Id value
 *
 */
public class C4CCustomerPopulator implements Populator<CustomerModel, CustomerData>
{

	@Override
	public void populate(final CustomerModel source, final CustomerData target)
	{
		target.setCustomerId(source.getCustomerID());
	}
}
