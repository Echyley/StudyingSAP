package de.hybris.platform.commerceservices.multisite.strategies.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.multisite.impl.DefaultMultiSiteUidDecorationService;
import de.hybris.platform.site.BaseSiteService;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultMultiSiteUidDecorationStrategyTest
{

	private static final String ADMIN_USER_ID = "admin";
	private static final String ANONYMOUS_USER_ID = "anonymous";

	private static final String DEFAULT_PLAIN_CUSTOMER_UID = "test@sap.com";
	private static final String DEFAULT_BASE_SITE_UID = "baseSiteUID";
	private static final String DEFAULT_DECORATED_UID = "test@sap.com|baseSiteUID";

	@Mock
	private BaseSiteService baseSiteService;
	@Mock
	private BaseSiteModel baseSiteModel;

	private DefaultMultiSiteUidDecorationStrategy multiSiteUidDecorationStrategy;

	@Before
	public void setup()
	{
		this.multiSiteUidDecorationStrategy = new DefaultMultiSiteUidDecorationStrategy();

		this.multiSiteUidDecorationStrategy.setBaseSiteService(baseSiteService);
		this.multiSiteUidDecorationStrategy.setMultiSiteUidDecorationService(new DefaultMultiSiteUidDecorationService());

		when(baseSiteService.getCurrentBaseSite()).thenReturn(baseSiteModel);
	}

	@Test
	public void testSpecialUserShouldNotBeDecorated()
	{
		assertTrue(multiSiteUidDecorationStrategy.decorateUserId(ADMIN_USER_ID).isEmpty());
		assertTrue(multiSiteUidDecorationStrategy.decorateUserId(ANONYMOUS_USER_ID).isEmpty());
	}

	@Test
	public void testWhenDataIsolationDisabledShouldNotDecorateUid()
	{
		when(baseSiteModel.getDataIsolationEnabled()).thenReturn(false);

		assertTrue(multiSiteUidDecorationStrategy.decorateUserId(DEFAULT_PLAIN_CUSTOMER_UID).isEmpty());
	}

	@Test
	public void testWhenDataIsolationEnabledShouldDecorateUid()
	{
		when(baseSiteModel.getDataIsolationEnabled()).thenReturn(true);
		when(baseSiteModel.getUid()).thenReturn(DEFAULT_BASE_SITE_UID);

		// then
		final Optional<String> decoratedUid = multiSiteUidDecorationStrategy.decorateUserId(DEFAULT_PLAIN_CUSTOMER_UID);

		// expected
		assertTrue(decoratedUid.isPresent());
		assertEquals(DEFAULT_DECORATED_UID, decoratedUid.get());
	}

	@Test
	public void testReDecorateDecoratedUidShouldGetEmpty()
	{
		when(baseSiteModel.getDataIsolationEnabled()).thenReturn(true);
		when(baseSiteModel.getUid()).thenReturn(DEFAULT_BASE_SITE_UID);

		// then
		final Optional<String> reDecoratedUid = multiSiteUidDecorationStrategy.decorateUserId(DEFAULT_DECORATED_UID);
		assertTrue(reDecoratedUid.isEmpty());
	}
}
