/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.datahubbackoffice.service.datahub;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import de.hybris.platform.datahubbackoffice.exception.NoDataHubInstanceAvailableException;

import org.junit.Test;

public class InaccessibleDataHubServerTest
{
	private final InaccessibleDataHubServer server = new InaccessibleDataHubServer();

	@Test
	public void testGetLocationThrowsException()
	{
		assertThatThrownBy(server::getLocation).isInstanceOf(NoDataHubInstanceAvailableException.class);
	}

	@Test
	public void testServerNameIsAlwaysNoAccess()
	{
		assertThat(server.getName()).isEqualTo("No Access");
	}

	@Test
	public void testServerIsAlwaysInaccessible()
	{
		assertThat(server.isAccessibleWithTimeout()).isFalse();
	}
}
