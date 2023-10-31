package de.hybris.platform.commerceservices.multisite.impl;

import de.hybris.bootstrap.annotations.UnitTest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultMultiSiteUidDecorationServiceTest
{

	private DefaultMultiSiteUidDecorationService multiSiteUidDecorationService;

	@Before
	public void setUp()
	{
		multiSiteUidDecorationService = new DefaultMultiSiteUidDecorationService();
	}

	@Test
	public void testUidIsDecorated() {
		assertTrue(multiSiteUidDecorationService.isDecorated("test@sap.com|baseSite", "baseSite"));
		assertTrue(multiSiteUidDecorationService.isDecorated("uuid|test@sap.com|baseSite", "baseSite"));

		assertFalse(multiSiteUidDecorationService.isDecorated("", "baseSite"));
		assertFalse(multiSiteUidDecorationService.isDecorated("test@sap.com|", "baseSite"));
		assertFalse(multiSiteUidDecorationService.isDecorated("|test@sap.com", "baseSite"));
		assertFalse(multiSiteUidDecorationService.isDecorated("|test@sap.com|", "baseSite"));

		assertFalse(multiSiteUidDecorationService.isDecorated("", ""));
		assertFalse(multiSiteUidDecorationService.isDecorated("test@sap.com", ""));
		assertTrue(multiSiteUidDecorationService.isDecorated("test@sap.com|", ""));
		assertFalse(multiSiteUidDecorationService.isDecorated("|test@sap.com", ""));
		assertTrue(multiSiteUidDecorationService.isDecorated("|test@sap.com|", ""));
	}

	@Test
	public void testUidDecorationAndUnDecoration()
	{
		verify("test@sap.com", "baseSite", false);
		verify("uuid-1|test@sap.com", "baseSite", false);
		verify("uuid-1|test@sap.com|", "baseSite", false);
		verify("uuid-1|other-name|test@sap.com", "baseSite", false);

		verify("test@sap.com|baseSite", "baseSite", true);
		verify("uuid-1|test@sap.com|baseSite", "baseSite", true);
		verify("uuid-1|other-name|test@sap.com|baseSite", "baseSite", true);

		verify("test@sap.com|baseSite", "changedBaseSite", false);
	}

	private void verify(final String customerUid, final String baseSiteUid, final boolean isDecorated)
	{
		final boolean inputCustomerUidDecorated = multiSiteUidDecorationService.isDecorated(customerUid, baseSiteUid);

		assertEquals(isDecorated, inputCustomerUidDecorated);

		final String[] uidElements = multiSiteUidDecorationService.undecorate(customerUid, baseSiteUid);
		if (inputCustomerUidDecorated)
		{
			assertEquals(2, uidElements.length);

			assertEquals(baseSiteUid, uidElements[1]);

			final String reDecoratedCustomerUid = multiSiteUidDecorationService.decorate(uidElements[0], uidElements[1]);
			assertEquals(customerUid, reDecoratedCustomerUid);
		}
		else
		{
			assertEquals(1, uidElements.length);

			assertEquals(customerUid, uidElements[0]);

			final String decoratedCustomerUid = multiSiteUidDecorationService.decorate(customerUid, baseSiteUid);
			final String[] unDecoratedElements = multiSiteUidDecorationService.undecorate(decoratedCustomerUid, baseSiteUid);

			assertEquals(2, unDecoratedElements.length);
			assertEquals(decoratedCustomerUid,
					multiSiteUidDecorationService.decorate(unDecoratedElements[0], unDecoratedElements[1]));

			assertEquals(customerUid + "|" + baseSiteUid, decoratedCustomerUid);
		}
	}
}
