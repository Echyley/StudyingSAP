/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commerceservices.customer;

import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.user.CustomerModel;

import java.util.Optional;

import org.apache.commons.lang.NotImplementedException;


/**
 * Provides methods for retrieving customers
 */
public interface CustomerService
{
	/**
	 * Verifies if given id is in UUID format
	 *
	 * @param id the id
	 * @return <code>true<code/> if UUID format
	 */
	boolean isUUID(final String id);

	/**
	 * Gets the customer by customerId
	 *
	 * @param customerId the customerID in UUID format of the customer
	 * @return the found customer
	 */
	CustomerModel getCustomerByCustomerId(final String customerId);

	/**
	 * Gets the first customer who registered to this BaseSite
	 *
	 * @param baseSitePK BaseSite PK
	 * @return return Optional.empty() if no one customer found and Optional.of(customer) is customer found
	 */
	default Optional<CustomerModel> getFirstCustomerByBaseSitePK(final PK baseSitePK)
	{
		throw new NotImplementedException();
	}
}
