/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapproductavailability.businessobject.impl;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.core.bol.businessobject.BackendInterface;
import de.hybris.platform.sap.core.bol.businessobject.BusinessObjectBase;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.sapproductavailability.backend.SapProductAvailabilityBackend;
import de.hybris.platform.sap.sapproductavailability.businessobject.SapProductAvailability;
import de.hybris.platform.sap.sapproductavailability.businessobject.SapProductAvailabilityBO;
import de.hybris.platform.sap.sapproductavailability.exceptions.SapProductAvailabilityException;


/**
 * 
 */
@BackendInterface(SapProductAvailabilityBackend.class)
public class SapProductAvailabilityBOImpl extends BusinessObjectBase implements SapProductAvailabilityBO
{

	/**
	 * @return the sapProductAvailabilityBackend
	 * @throws BackendException
	 */
	public SapProductAvailabilityBackend getSapProductAvailabilityBackend() throws BackendException
	{
			return (SapProductAvailabilityBackend) getBackendBusinessObject();
	}

	@Override
	public SapProductAvailability readProductAvailability(final ProductModel product, final String customerId, final String plant,
			final Long requestedQuantity)
	{

		SapProductAvailability availability = null;

		try
		{
			availability = getSapProductAvailabilityBackend().readProductAvailability(product, customerId, plant, requestedQuantity);
		}
		catch (final BackendException e)
		{
			throw new SapProductAvailabilityException("readProductAvailability", e); 
		}


		return availability;

	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapproductavailability.businessobject.SapProductAvailabilityBO#readPlantForCustomerMaterial
	 * (java.lang.String, java.lang.String)
	 */
	@Override
	public String readPlantForCustomerMaterial(final String material, final String customerId)
	{
		try
		{
			return getSapProductAvailabilityBackend().readPlantForCustomerMaterial(material, customerId);
		}
		catch (final BackendException e)
		{
			throw new SapProductAvailabilityException("readPlantForCustomerMaterial", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapproductavailability.businessobject.SapProductAvailabilityBO#readPlant(de.hybris.platform
	 * .core.model.product.ProductModel, java.lang.String)
	 */
	@Override
	public String readPlant(final ProductModel product, final String customerId)
	{
		try
		{
			return getSapProductAvailabilityBackend().readPlant(product, customerId);
		}
		catch (final BackendException e)
		{
			throw new SapProductAvailabilityException("readPlantForCustomerMaterial", e);
		}
	}



}
