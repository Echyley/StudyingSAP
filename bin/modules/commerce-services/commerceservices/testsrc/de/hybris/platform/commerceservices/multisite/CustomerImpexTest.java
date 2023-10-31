/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commerceservices.multisite;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.session.SessionExecutionBody;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;

import javax.annotation.Resource;

import java.nio.charset.StandardCharsets;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;


@IntegrationTest
public class CustomerImpexTest extends ServicelayerTransactionalTest
{
	private static final Logger LOG = Logger.getLogger(CustomerImpexTest.class);

	private static final String ISOLATED_SITE_UID = "isolatedSite";

	@Resource
	private UserService userService;
	@Resource
	private BaseSiteService baseSiteService;
	@Resource
	private SessionService sessionService;

	@Before
	public void setup() throws Exception
	{
		createCoreData();

		try
		{
			importData("/test/customer-data.impex", StandardCharsets.UTF_8.name());
		}
		catch (final ImpExException impexException)
		{
			LOG.info("expected impex exception during creation");
		}

	}

	@Test
	public void testUnIsolatedCustomerCreateSuccessful()
	{
		validateUnIsolatedCustomer("test1@sap.com", "test1@sap.com", "test1@sap.com");
		validateUnIsolatedCustomer("test2@sap.com", "Test2@SAP.com", "test2@sap.com");
		validateUnIsolatedCustomer("test3@sap.com", "test3@sap.com", "test3@sap.com");
		validateUnIsolatedCustomer("test4@sap.com", "test4@sap.com", "test4@sap.com");
		validateUnIsolatedCustomer("test5@sap.com", "Test5@SAP.com", "test5@sap.com");
		validateUnIsolatedCustomer("test6@sap.com", "test6@sap.com", "test6@sap.com");
		validateUnIsolatedCustomer("test7@sap.com", "Test7@SAP.com", "test7@sap.com");
	}

	@Test
	public void testIsolatedCustomerCreateSuccessful()
	{
		validateIsolatedCustomer("test11@sap.com", "test11@sap.com", "test11@sap.com", ISOLATED_SITE_UID);
		validateIsolatedCustomer("test12@sap.com", "Test12@SAP.com", "test12@sap.com", ISOLATED_SITE_UID);
		validateIsolatedCustomer("test13@sap.com", "test13@sap.com", "test13@sap.com", ISOLATED_SITE_UID);
		validateIsolatedCustomer("test14@sap.com", "test14@sap.com", "test14@sap.com", ISOLATED_SITE_UID);
		validateIsolatedCustomer("test15@sap.com", "Test15@sap.com", "test15@sap.com", ISOLATED_SITE_UID);
		validateIsolatedCustomer("test16@sap.com", "test16@sap.com", "test16@sap.com", ISOLATED_SITE_UID);
		validateIsolatedCustomer("test17@sap.com", "Test17@SAP.com", "test17@sap.com", ISOLATED_SITE_UID);
	}

	@Test
	public void testUpdateUnIsolatedCustomerOriginal()
	{
		validateUnIsolatedCustomer("test21@sap.com", "Test21@SAP.com", "test21@sap.com");
		validateUnIsolatedCustomer("test22@sap.com", "test22@sap.com", "test22@sap.com");
		validateUnIsolatedCustomer("test23@sap.com", "test23@sap.com", "test23@sap.com");
		validateUnIsolatedCustomer("test24@sap.com", "Test24@sap.com", "test24@sap.com");
	}

	@Test
	public void testUpdateIsolatedCustomerOriginal()
	{
		validateIsolatedCustomer("test31@sap.com", "Test31@SAP.com", "test31@sap.com", ISOLATED_SITE_UID);
		validateIsolatedCustomer("test32@sap.com", "test32@sap.com", "test32@sap.com", ISOLATED_SITE_UID);
		validateIsolatedCustomer("test33@sap.com", "test33@sap.com", "test33@sap.com", ISOLATED_SITE_UID);
		validateIsolatedCustomer("test34@sap.com", "Test34@sap.com", "test34@sap.com", ISOLATED_SITE_UID);
	}

	private void validateIsolatedCustomer(final String searchUid, final String expectedOriginalUid,
			final String expectedUndecoratedUid, final String expectedSite)
	{
		sessionService.executeInLocalView(new SessionExecutionBody()
		{
			@Override
			public void executeWithoutResult()
			{
				baseSiteService.setCurrentBaseSite(expectedSite, false);
				final CustomerModel customerInDB = userService.getUserForUID(searchUid, CustomerModel.class);

				assertNotNull(customerInDB);
				assertEquals(format("%s|%s", searchUid, expectedSite), customerInDB.getUid());
				assertEquals(expectedOriginalUid, customerInDB.getOriginalUid());
				assertEquals(expectedUndecoratedUid, customerInDB.getUndecoratedUid());
				assertEquals(expectedSite, customerInDB.getSite().getUid());
			}
		});
	}

	private void validateUnIsolatedCustomer(final String searchUid, final String expectedOriginalUid,
			final String expectedUndecoratedUid)
	{
		final CustomerModel customerInDB = userService.getUserForUID(searchUid, CustomerModel.class);
		assertNotNull(customerInDB);
		assertEquals(searchUid, customerInDB.getUid());
		assertEquals(expectedOriginalUid, customerInDB.getOriginalUid());
		assertEquals(expectedUndecoratedUid, customerInDB.getUndecoratedUid());

		assertNull(customerInDB.getSite());
	}
}