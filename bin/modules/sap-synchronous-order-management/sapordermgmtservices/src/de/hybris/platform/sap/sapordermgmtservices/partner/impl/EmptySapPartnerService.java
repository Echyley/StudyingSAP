/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtservices.partner.impl;

import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.sap.sapordermgmtservices.partner.SapPartnerService;

import java.util.Collection;
import java.util.Collections;

import org.apache.log4j.Logger;


/**
 * Empty implementation for {@link SapPartnerService}. Used just to avoid spring issues. Use a real implementation,
 * instead.
 */
public class EmptySapPartnerService implements SapPartnerService
{
	private static final Logger LOG = Logger.getLogger(EmptySapPartnerService.class);

	@Override
	public String getCurrentSapCustomerId()
	{
		if (LOG.isInfoEnabled())
		{
			LOG.info("An empty implementation of get current SAP customer ID");
		}

		return null;
	}

	@Override
	public String getCurrentSapContactId()
	{
		if (LOG.isInfoEnabled())
		{
			LOG.info("An empty implementation of get current SAP contact ID");
		}

		return null;
	}

	@Override
	public AddressModel getHybrisAddressForSAPCustomerId(final String sapCustomerId)
	{
		if (LOG.isInfoEnabled())
		{
			LOG.info("An empty implementation of get Hybris address for SAP customer ID");
		}

		return null;
	}

	@Override
	public Collection<AddressModel> getAllowedDeliveryAddresses()
	{
		if (LOG.isInfoEnabled())
		{
			LOG.info("An empty implementation of get allowed delivery address");
		}

		return Collections.emptyList();
	}

	@Override
	public CustomerModel getB2BCustomerForSapContactId(final String sapContactId)
	{
		if (LOG.isInfoEnabled())
		{
			LOG.info("An empty implementation of get B2B customer for SAP contact ID.");
		}

		return null;
	}

}
