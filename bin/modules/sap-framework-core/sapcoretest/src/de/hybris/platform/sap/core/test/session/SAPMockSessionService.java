/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.test.session;

import de.hybris.platform.servicelayer.session.MockSessionService;
import de.hybris.platform.servicelayer.session.Session;


/**
 * SAP Mock Session Service which returns the SAP Mock Session.
 */
public class SAPMockSessionService extends MockSessionService
{

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.servicelayer.session.MockSessionService#createSession()
	 */
	@Override
	public Session createSession()
	{
		return new SAPMockSession();
	}

}
