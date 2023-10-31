/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationservices;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.integrationservices.constants.IntegrationservicesConstants;

@IntegrationTest
public class LocalizationIntegrationTest extends LocalizationTestTemplate
{
	protected final String getExtension()
	{
		return IntegrationservicesConstants.EXTENSIONNAME;
	}
}
