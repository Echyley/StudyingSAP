/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtbol.transaction.util;

import de.hybris.platform.sap.sapmodel.services.SalesAreaService;


/**
 * Dummmy implementation of SalesAreaService for test purpose.
 */
public class DummySalesAreaService implements SalesAreaService
{
	@Override
	public String getSalesOrganization()
	{
		return null;
	}

	@Override
	public String getDistributionChannel()
	{
		return null;
	}

	@Override
	public String getDistributionChannelForConditions()
	{
		return null;

	}

	@Override
	public String getDistributionChannelForCustomerMaterial()
	{
		return null;
	}

	@Override
	public String getDivision()
	{
		return null;
	}

	@Override
	public String getDivisionForConditions()
	{
		return null;
	}

	@Override
	public String getDivisionForCustomerMaterial()
	{
		return null;
	}

}
