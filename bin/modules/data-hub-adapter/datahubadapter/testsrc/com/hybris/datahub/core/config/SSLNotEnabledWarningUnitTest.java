/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.hybris.datahub.core.config;

import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.dataimportcommons.log.LogProbe;

import org.junit.Rule;
import org.junit.Test;

@UnitTest
public class SSLNotEnabledWarningUnitTest
{
	@Rule
	public LogProbe logProbe = LogProbe.create(SSLNotEnabledWarning.class);

	private final SSLNotEnabledWarning warning = new SSLNotEnabledWarning();

	@Test
	public void testWarningIsNotLoggedWhenSslEnabled()
	{
		warning.setSslEnabled(true);

		warning.afterPropertiesSet();

		assertThat(logProbe.getAllLoggedRecords()).isEmpty();
	}

	@Test
	public void testWarningIsLoggedWhenSslDisabled()
	{
		warning.setSslEnabled(false);

		warning.afterPropertiesSet();

		assertThat(logProbe.getMessagesLoggedAtWarn()).contains("Data Hub Adapter is running in HTTP mode.");
	}
}
