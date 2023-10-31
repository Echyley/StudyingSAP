/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.jco.sappassport;

import java.util.Collections;
import java.util.List;

import de.hybris.platform.sap.core.configuration.model.SAPRFCDestinationModel;
import de.hybris.platform.sap.core.configuration.rfc.dao.SAPRFCDestinationDao;


public class MockSAPRFCDestinationDao implements SAPRFCDestinationDao
{

	@Override
	public List<SAPRFCDestinationModel> findRfcDestinations()
	{
		return Collections.emptyList();
	}

	@Override
	public List<SAPRFCDestinationModel> findRfcDestinationByName(String rfcDestinationName)
	{
		return Collections.emptyList();
	}

}
