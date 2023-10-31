/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.outboundsync;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.integrationservices.LocalizationTestTemplate;
import de.hybris.platform.outboundsync.constants.OutboundsyncConstants;

@IntegrationTest
public class LocalizationIntegrationTest extends LocalizationTestTemplate
{
	@Override
	protected String getExtension()
	{
		return OutboundsyncConstants.EXTENSIONNAME;
	}
}
