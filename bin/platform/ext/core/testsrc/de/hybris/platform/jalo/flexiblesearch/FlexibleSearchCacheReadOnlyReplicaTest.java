/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.jalo.flexiblesearch;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.PK;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.SearchResult;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.flexiblesearch.hints.Hint;
import de.hybris.platform.jalo.flexiblesearch.internal.ReadOnlyConditionsHelper;
import de.hybris.platform.persistence.flexiblesearch.TranslatedQuery;
import de.hybris.platform.testframework.PropertyConfigSwitcher;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

@IntegrationTest
public class FlexibleSearchCacheReadOnlyReplicaTest extends AbstractSwitchingDataSourceTest
{
	final String ENABLE_CACHING_ON_READ_REPLICA = "ctx.enable.fs.cache.on.read-replica";
	final String READ_ONLY_DATASOURCE_ID = "f";
	final PropertyConfigSwitcher readOnlyDataSourceProperty = new PropertyConfigSwitcher(
			ReadOnlyConditionsHelper.PARAM_FS_READ_ONLY_DATASOURCE);
	private SessionContext ctx;

	@Override
	@Before
	public void setUp() throws Exception
	{
		ctx = JaloSession.getCurrentSession().createLocalSessionContext();
	}

	@Override
	@After
	public void tearDown()
	{
		JaloSession.getCurrentSession().removeLocalSessionContext();
		readOnlyDataSourceProperty.switchBackToDefault();
	}

	@Test
	public void testUsingCustomFlexibleSearchImplementationWithCacheKeyWithTenantItRRPrefixResultsInProperExecutionOfTheStatement()
	{
		readOnlyDataSourceProperty.switchToValue(READ_ONLY_DATASOURCE_ID);
		ctx.setAttribute(ENABLE_CACHING_ON_READ_REPLICA, true);
		ctx.setAttribute(ReadOnlyConditionsHelper.CTX_ENABLE_FS_ON_READ_REPLICA, true);

		final FlexibleSearch flexibleSearch = new TestRRFlexibleSearch();
		final SearchResult<PK> searchResult = flexibleSearch.search("SELECT {PK} FROM {User}", PK.class);

		assertThat(searchResult.getDataSourceId()).isEqualTo(READ_ONLY_DATASOURCE_ID);
	}

	@Test
	public void testUsingCustomFFImplementationWithCacheKeyWithInvalidTenantIDResultsInFlexibleSearchExceptionBeingThrown()
	{
		readOnlyDataSourceProperty.switchToValue(READ_ONLY_DATASOURCE_ID);
		ctx.setAttribute(ENABLE_CACHING_ON_READ_REPLICA, true);
		ctx.setAttribute(ReadOnlyConditionsHelper.CTX_ENABLE_FS_ON_READ_REPLICA, true);

		final FlexibleSearch flexibleSearch = new TestRRFlexibleSearchWithInvalidTenantId();
		assertThatThrownBy(() -> {
			flexibleSearch.search("SELECT {PK} FROM {User}", PK.class);
		}).isInstanceOf(FlexibleSearchException.class).hasMessageContaining("Tenant with id: junitfR not found");
	}

	private class TestRRFlexibleSearch extends FlexibleSearch
	{
		@Override
		FlexibleSearchCacheKey createCacheKey(final List<Class<?>> resultClasses, final boolean dontNeedTotal, final int start,
		                                      final int count, final TranslatedQuery tQuery, final boolean doExecuteQuery, final PK langPK, final Map _values,
		                                      final Set<Integer> beanTCs, final int ttl, final List<Hint> hints)
		{
			return new FlexibleSearchCacheKey(tQuery, _values, langPK, resultClasses, dontNeedTotal, start,
					count, beanTCs, doExecuteQuery, 100, getTenant().getTenantID() + "RR", hints);
		}
	}

	private class TestRRFlexibleSearchWithInvalidTenantId extends FlexibleSearch
	{
		@Override
		FlexibleSearchCacheKey createCacheKey(final List<Class<?>> resultClasses, final boolean dontNeedTotal, final int start,
		                                      final int count, final TranslatedQuery tQuery, final boolean doExecuteQuery, final PK langPK, final Map _values,
		                                      final Set<Integer> beanTCs, final int ttl, final List<Hint> hints)
		{
			return new FlexibleSearchCacheKey(tQuery, _values, langPK, resultClasses, dontNeedTotal, start,
					count, beanTCs, doExecuteQuery, 100, getTenant().getTenantID() + "fRRR", hints);
		}
	}
}
