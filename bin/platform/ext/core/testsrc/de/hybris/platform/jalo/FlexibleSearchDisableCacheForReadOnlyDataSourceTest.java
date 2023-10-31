/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.jalo;

import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.PK;
import de.hybris.platform.jalo.flexiblesearch.FlexibleSearch;
import de.hybris.platform.jalo.flexiblesearch.internal.ReadOnlyConditionsHelper;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.testframework.PropertyConfigSwitcher;
import de.hybris.platform.util.StandardSearchResult;

import org.junit.After;
import org.junit.Test;

@IntegrationTest
public class FlexibleSearchDisableCacheForReadOnlyDataSourceTest extends ServicelayerBaseTest
{
	final String ENABLE_CACHING_ON_READ_REPLICA = "ctx.enable.fs.cache.on.read-replica";
	final String READ_ONLY_DATASOURCE_ID = "f";
	final String MASTER_DATASOURCE_ID = "master";
	final PropertyConfigSwitcher readOnlyDataSourceProperty = new PropertyConfigSwitcher(
			ReadOnlyConditionsHelper.PARAM_FS_READ_ONLY_DATASOURCE);

	@After
	public void tearDown()
	{
		readOnlyDataSourceProperty.switchBackToDefault();
	}

	@Test
	public void shouldDisableCacheWhenReadOnlyDSIsUsedAntEnablingAttributeIsSetToFalse()
	{
		final SessionContext ctx = setUpReadOnlyDataSource();
		ctx.setAttribute(ENABLE_CACHING_ON_READ_REPLICA, false);
		assertThat(performSearches(READ_ONLY_DATASOURCE_ID).isFromCache()).isFalse();
	}

	@Test
	public void shouldDisableCacheWhenReadOnlyDSIsUsedAndEnablingAttributeIsSetToFalseAsAString()
	{
		final SessionContext ctx = setUpReadOnlyDataSource();
		ctx.setAttribute(ENABLE_CACHING_ON_READ_REPLICA, "false");
		assertThat(performSearches(READ_ONLY_DATASOURCE_ID).isFromCache()).isFalse();
	}

	@Test
	public void shouldDisableCacheWhenReadOnlyDSIsUsedWhenEnablingAttributeIsSetToNotMeaningfulString()
	{
		final SessionContext ctx = setUpReadOnlyDataSource();
		ctx.setAttribute(ENABLE_CACHING_ON_READ_REPLICA, "NothingLikeThatExists");
		assertThat(performSearches(READ_ONLY_DATASOURCE_ID).isFromCache()).isTrue();
	}

	@Test
	public void shouldEnableCacheWhenReadOnlyDSIsUsedAndEnablingAttributeValueIsAnInteger()
	{
		final SessionContext ctx = setUpReadOnlyDataSource();
		ctx.setAttribute(ENABLE_CACHING_ON_READ_REPLICA, 0);
		assertThat(performSearches(READ_ONLY_DATASOURCE_ID).isFromCache()).isTrue();
	}

	@Test
	public void shouldNotDisableCacheWhenMainDSIsUsed()
	{
		final SessionContext ctx = JaloSession.getCurrentSession().createLocalSessionContext();
		ctx.setAttribute(ENABLE_CACHING_ON_READ_REPLICA, false);
		assertThat(performSearches(MASTER_DATASOURCE_ID).isFromCache()).isTrue();
	}

	@Test
	public void shouldUseCacheWhenEnablingAttributeIsNotProvided()
	{
		final SessionContext ctx = setUpReadOnlyDataSource();
		assertThat(performSearches(READ_ONLY_DATASOURCE_ID).isFromCache()).isTrue();
	}

	@Test
	public void shouldUseCacheWhenEnablingAttributeIsSetToTrue()
	{
		final SessionContext ctx = setUpReadOnlyDataSource();
		ctx.setAttribute(ENABLE_CACHING_ON_READ_REPLICA, true);
		assertThat(performSearches(READ_ONLY_DATASOURCE_ID).isFromCache()).isTrue();
	}

	@Test
	public void shouldUseCacheWhenEnablingAttributeIsSetToTrueAsAString()
	{
		final SessionContext ctx = setUpReadOnlyDataSource();
		ctx.setAttribute(ENABLE_CACHING_ON_READ_REPLICA, "true");
		assertThat(performSearches(READ_ONLY_DATASOURCE_ID).isFromCache()).isTrue();
	}

	@Test
	public void testSettingEnablingAttributeToFalseAsAStringWithMixedCaseDisablesCaching()
	{
		final SessionContext ctx = setUpReadOnlyDataSource();
		ctx.setAttribute(ENABLE_CACHING_ON_READ_REPLICA, "FaLSe");
		assertThat(performSearches(READ_ONLY_DATASOURCE_ID).isFromCache()).isFalse();
	}

	@Test
	public void testSettingEnablingAttributeToTrueDoesNothingWhenDisableCacheAttributeIsSetToTrue()
	{
		final SessionContext ctx = setUpReadOnlyDataSource();
		ctx.setAttribute("disableCache", true);
		assertThat(performSearches(READ_ONLY_DATASOURCE_ID).isFromCache()).isFalse();
	}

	@Test
	public void testSettingEnablingAttributeToFalseWhenDisablingAttributeIsSetToTrueResultsInCacheNotBeingUsed()
	{
		final SessionContext ctx = setUpReadOnlyDataSource();
		ctx.setAttribute("disableCache", false);
		assertThat(performSearches(READ_ONLY_DATASOURCE_ID).isFromCache()).isTrue();
	}

	private SessionContext setUpReadOnlyDataSource()
	{
		readOnlyDataSourceProperty.switchToValue(READ_ONLY_DATASOURCE_ID);

		final SessionContext ctx = JaloSession.getCurrentSession().createLocalSessionContext();
		ctx.setAttribute(ReadOnlyConditionsHelper.CTX_ENABLE_FS_ON_READ_REPLICA, true);

		return ctx;
	}

	private StandardSearchResult<PK> performSearches(final String dataSourceId)
	{
		try
		{
			final FlexibleSearch flexibleSearch = new FlexibleSearch();
			final SearchResult<PK> searchResult1 = flexibleSearch.search("SELECT {PK} FROM {User}", PK.class);
			final SearchResult<PK> searchResult2 = flexibleSearch.search("SELECT {PK} FROM {User}", PK.class);

			assertThat(searchResult1.getDataSourceId()).isEqualTo(dataSourceId);
			assertThat(searchResult2.getDataSourceId()).isEqualTo(dataSourceId);
			return (StandardSearchResult<PK>) searchResult2;
		}
		finally
		{
			JaloSession.getCurrentSession().removeLocalSessionContext();
		}
	}
}
