/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationbackoffice.localization.impl;


import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.UnitTest;

import org.junit.Test;

@UnitTest
public class DefaultIntegrationBackofficeLocalizationServiceUnitTest
{
	private final DefaultIntegrationBackofficeLocalizationService service = new DefaultIntegrationBackofficeLocalizationService();

	@Test
	public void testReturnsKeyWhenTheKeyIsNotFoundInTheResourceBundle()
	{
		final String key = "non.existent.key";
		final Object[] params = { "Some message paraemter" };

		assertThat(service.getLocalizedString(key)).isEqualTo(key);
		assertThat(service.getLocalizedString(key, params)).isEqualTo(key);
	}
}