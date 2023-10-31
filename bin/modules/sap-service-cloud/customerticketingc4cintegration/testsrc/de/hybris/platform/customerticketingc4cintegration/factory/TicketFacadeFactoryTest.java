/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.customerticketingc4cintegration.factory;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.customerticketingc4cintegration.facade.C4CTicketFacadeImpl;
import de.hybris.platform.customerticketingc4cintegration.facade.C4CTicketFacadeMock;
import de.hybris.platform.customerticketingfacades.TicketFacade;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.util.Config;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * Test cases for TicketFacadeFactory class.
 */
@IntegrationTest
public class TicketFacadeFactoryTest extends ServicelayerTest
{
	@Mock
	private C4CTicketFacadeImpl c4cTicketFacadeImpl;
	@Mock
	private C4CTicketFacadeMock c4cTicketFacadeMock;
	@InjectMocks
	private TicketFacadeFactory facadeFactory;
	private static final String C4C_INTEGRATION_FACADE_MOCK_FLAG = "customerticketingc4cintegration.facade.mock";
	private static final String C4C_URL = "customerticketingc4cintegration.c4c-url";

	/**
	 * Setup.
	 */
	@Before
	public void setup()
	{
		facadeFactory = new TicketFacadeFactory();
		MockitoAnnotations.initMocks(this);
	}

	/**
	 * Should return non-mock facade implantation if mock flag set to false.
	 *
	 */
	@Test
	public void shouldReturnFacadeImplWhenMockIsFalse() {
		Config.setParameter(C4C_INTEGRATION_FACADE_MOCK_FLAG, "false");
		Config.setParameter(C4C_URL, "https://dummy-c4c-url.com");

		TicketFacade ticketFacade = facadeFactory.getTicketFacade();

		Assert.assertEquals(c4cTicketFacadeImpl, ticketFacade);
	}

	/**
	 * Should return a mock facade implantation if mock flag set to true.
	 *
	 */
	@Test
	public void shouldReturnMockFacadeWhenMockIsTrue() {
		Config.setParameter(C4C_INTEGRATION_FACADE_MOCK_FLAG, "true");
		Config.setParameter(C4C_URL, "https://dummy-c4c-url.com");
		TicketFacade ticketFacade = facadeFactory.getTicketFacade();

		Assert.assertEquals(c4cTicketFacadeMock, ticketFacade);
	}

	/**
	 * Should return a mock facade implantation if mock flag is not set.
	 *
	 */
	@Test
	public void shouldReturnMockFacadeWhenNoParameterSet() {
		Config.setParameter(C4C_INTEGRATION_FACADE_MOCK_FLAG, null);
		Config.setParameter(C4C_URL, "https://dummy-c4c-url.com");
		TicketFacade ticketFacade = facadeFactory.getTicketFacade();

		Assert.assertEquals(c4cTicketFacadeMock, ticketFacade);
	}
}
