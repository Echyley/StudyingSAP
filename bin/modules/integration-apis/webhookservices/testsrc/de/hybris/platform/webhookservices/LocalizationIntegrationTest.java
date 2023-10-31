/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.webhookservices;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.integrationservices.LocalizationTestTemplate;
import de.hybris.platform.webhookservices.constants.WebhookservicesConstants;

@IntegrationTest
public class LocalizationIntegrationTest extends LocalizationTestTemplate
{
	@Override
	protected String getExtension()
	{
		return WebhookservicesConstants.EXTENSIONNAME;
	}
}
