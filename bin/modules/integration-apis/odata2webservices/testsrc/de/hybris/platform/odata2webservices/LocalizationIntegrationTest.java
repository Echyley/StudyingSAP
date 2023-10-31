/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.odata2webservices;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.integrationservices.LocalizationTestTemplate;
import de.hybris.platform.odata2webservices.constants.Odata2webservicesConstants;

@IntegrationTest
public class LocalizationIntegrationTest extends LocalizationTestTemplate
{
	@Override
	protected String getExtension()
	{
		return Odata2webservicesConstants.EXTENSIONNAME;
	}
}
