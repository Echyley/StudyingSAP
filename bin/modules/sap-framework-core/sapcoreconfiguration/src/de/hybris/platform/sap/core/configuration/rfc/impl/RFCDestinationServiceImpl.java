/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.configuration.rfc.impl;

import de.hybris.platform.sap.core.configuration.constants.SapcoreconfigurationConstants;
import de.hybris.platform.sap.core.configuration.model.SAPRFCDestinationModel;
import de.hybris.platform.sap.core.configuration.rfc.RFCDestination;
import de.hybris.platform.sap.core.configuration.rfc.RFCDestinationService;
import de.hybris.platform.sap.core.configuration.rfc.dao.SAPRFCDestinationDao;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import java.util.ArrayList;
import java.util.List;


/**
 * 
 */
public class RFCDestinationServiceImpl implements RFCDestinationService
{

	private SAPRFCDestinationDao rfcDestinationDao;

	@Override
	public RFCDestination getRFCDestination(final String destinationName)
	{
		final SAPRFCDestinationModel destinationModel = readRFCDestination(destinationName);
		if (destinationModel != null)
		{
			return new RFCDestinationImpl(destinationModel);
		}
		return null;
	}

	/**
	 * Read RFC destination model for the given name.
	 * 
	 * @param destinationName
	 *           RFC destination name
	 * @return destination model
	 */
	private SAPRFCDestinationModel readRFCDestination(final String destinationName)
	{
		final List<SAPRFCDestinationModel> rfcDestinations = rfcDestinationDao.findRfcDestinationByName(destinationName);
		if (rfcDestinations.isEmpty())
		{
			throw new UnknownIdentifierException(String.format("No SAPRFCDestination found for JCoDestinationName {%s}",
					destinationName));
		}
		else if (rfcDestinations.size() > 1)
		{
			throw new AmbiguousIdentifierException(String.format(
					"More than one SAPRFCDestination found for JCoDestinationName {%s} ", destinationName));
		}
		else
		{
			return rfcDestinations.get(0);
		}
	}

	/**
	 * @param rfcDestinationDao
	 *           the rfcDestinationDao to set
	 */
	public void setRfcDestinationDao(final SAPRFCDestinationDao rfcDestinationDao)
	{
		this.rfcDestinationDao = rfcDestinationDao;
	}

	@Override
	public String toString()
	{
		final List<RFCDestinationImpl> rfcDestinations = new ArrayList<RFCDestinationImpl>();
		final List<SAPRFCDestinationModel> rfcDestinationModels = rfcDestinationDao.findRfcDestinations();
		for (final SAPRFCDestinationModel sapRFCDestinationModel : rfcDestinationModels)
		{
			rfcDestinations.add(new RFCDestinationImpl(sapRFCDestinationModel));
		}
		return super.toString() + SapcoreconfigurationConstants.CRLF + "- RFC Destinations: " + rfcDestinations;
	}

}
