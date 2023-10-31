/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.customerticketingc4cintegration.facade;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.customerticketingfacades.customerticket.CustomerTicketingFacadeIntegrationTest;

import org.junit.Test;

import de.hybris.platform.customerticketingc4cintegration.facade.utils.HttpHeaderUtil;
import org.mockito.Mockito;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.junit.runner.RunWith;

@RunWith(MockitoJUnitRunner.class)
@IntegrationTest(replaces = CustomerTicketingFacadeIntegrationTest.class)
public class C4CTicketFacadeImplIntegrationTest
{
	private static final String SITE_ID = "siteId";
    
    @Mock
	HttpHeaderUtil httpHeaderUtil;

	@Test
	public void testGetTicketsIncludesTicketCategory()
	{
        httpHeaderUtil.getDefaultHeaders(SITE_ID);
		Mockito.verify(httpHeaderUtil).getDefaultHeaders(SITE_ID);
	}
}

