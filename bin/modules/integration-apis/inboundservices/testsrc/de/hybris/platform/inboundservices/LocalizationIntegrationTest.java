/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.inboundservices;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.inboundservices.constants.InboundservicesConstants;
import de.hybris.platform.integrationservices.LocalizationTestTemplate;

@IntegrationTest
public class LocalizationIntegrationTest extends LocalizationTestTemplate
{
	@Override
	protected String getExtension()
	{
		return InboundservicesConstants.EXTENSIONNAME;
	}
}
