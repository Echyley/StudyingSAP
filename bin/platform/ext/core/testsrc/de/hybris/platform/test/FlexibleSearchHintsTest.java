/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 *
 */

package de.hybris.platform.test;

import static org.mockito.Matchers.any;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.Registry;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.c2l.Language;
import de.hybris.platform.jalo.flexiblesearch.FlexibleSearch;
import de.hybris.platform.jalo.flexiblesearch.hints.Hint;
import de.hybris.platform.jalo.flexiblesearch.hints.impl.SQLServerQueryHints;
import de.hybris.platform.jalo.flexiblesearch.internal.FlexibleSearchExecutor;
import de.hybris.platform.jalo.flexiblesearch.internal.FlexibleSearchHintsProviderFactory;
import de.hybris.platform.jalo.flexiblesearch.internal.ReadOnlyConditionsHelper;
import de.hybris.platform.jalo.flexiblesearch.internal.SQLServerHintProvider;
import de.hybris.platform.persistence.flexiblesearch.TranslatedQuery;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.impl.DefaultFlexibleSearchService;
import de.hybris.platform.testframework.BulkPropertyConfigSwitcher;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

@IntegrationTest
public class FlexibleSearchHintsTest extends ServicelayerBaseTest
{
	private final BulkPropertyConfigSwitcher config = new BulkPropertyConfigSwitcher();
	private TestFlexibleSearch testFlexibleSearch;
	private ReadOnlyConditionsHelper helper;
	private FlexibleSearchExecutor spyFlexibleSearchExecutor;
	private FlexibleSearchHintsProviderFactory hintsProviderFactory;

	private static final String FS_QUERY = "SELECT {PK} FROM {Language}";
	private static final String TABLE_PREFIX = Registry.getCurrentTenantNoFallback().getDataSource().getTablePrefix();
	private static final String SQL_QUERY_TRANSLATED = "SELECT item_t0.PK FROM " + TABLE_PREFIX + "languages item_t0 WHERE (item_t0.TypePkString=? )";
	private static final String CONFIG_MAXDOP_VALUE = "6";
	private static final int SESSION_MAXDOP_VALUE = 3;

	@Resource
	private DefaultFlexibleSearchService defaultFlexibleSearchService;

	@Captor
	ArgumentCaptor<List<Hint>> hintsForQueryCaptor;

	@Captor
	ArgumentCaptor<TranslatedQuery> translatedQueryCaptor;

	@Before
	public void setUp()
	{
		helper = Mockito.mock(ReadOnlyConditionsHelper.class);

		spyFlexibleSearchExecutor = Mockito.spy(new FlexibleSearchExecutor(jaloSession.getTenant(), helper));
		hintsProviderFactory = Mockito.mock(FlexibleSearchHintsProviderFactory.class);
		Mockito.when(hintsProviderFactory.getFlexibleSearchProvider()).thenReturn(new SQLServerHintProvider(helper));
		testFlexibleSearch = new TestFlexibleSearch(spyFlexibleSearchExecutor, hintsProviderFactory);

		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void shouldAddMaxDOPHintFromConfig()
	{
		//given
		config.switchToValue(SQLServerQueryHints.FLEXIBLESEARCH_SQL_SERVER_MAXDOP_HINT, CONFIG_MAXDOP_VALUE);

		final SessionContext ctx = JaloSession.getCurrentSession().createLocalSessionContext();
		ctx.setAttribute(FlexibleSearch.DISABLE_CACHE, Boolean.TRUE);

		try
		{
			//when
			testFlexibleSearch.search(FS_QUERY, Language.class);

			//then
			Mockito.verify(spyFlexibleSearchExecutor)
			       .execute(any(Integer.class),
					       any(Integer.class),
					       any(Boolean.class),
					       translatedQueryCaptor.capture(),
					       any(List.class),
					       any(Map.class),
					       any(PK.class),
					       any(Integer.class),
					       any(Set.class),
					       hintsForQueryCaptor.capture(),
					       any(DataSource.class));

			final List<Hint> hintsList = hintsForQueryCaptor.getValue();

			Assertions.assertThat(translatedQueryCaptor.getValue().getSQLTemplate()).isEqualToIgnoringWhitespace(
					SQL_QUERY_TRANSLATED);
			Assertions.assertThat(hintsExistsAndIncludesMaxDOPValue(hintsList, "MAXDOP " + CONFIG_MAXDOP_VALUE)).isTrue();

			final Long hintsAdded = hintsList.stream()
			                                 .filter(SQLServerQueryHints.class::isInstance)
			                                 .count();
			Assertions.assertThat(hintsAdded).isEqualTo(Long.valueOf(1));
		}
		finally
		{
			//cleanup
			JaloSession.getCurrentSession().removeLocalSessionContext();
		}
	}

	@Test
	public void shouldAddMaxDOPHintWhenParameterAddedInSessionContext()
	{
		//given
		config.switchToValue(SQLServerQueryHints.FLEXIBLESEARCH_SQL_SERVER_MAXDOP_HINT, CONFIG_MAXDOP_VALUE);

		final SessionContext ctx = JaloSession.getCurrentSession().createLocalSessionContext();
		ctx.setAttribute(FlexibleSearch.DISABLE_CACHE, Boolean.TRUE);
		ctx.setAttribute(SQLServerQueryHints.FLEXIBLESEARCH_SQL_SERVER_MAXDOP_HINT, SESSION_MAXDOP_VALUE);

		try
		{
			//when
			testFlexibleSearch.search(FS_QUERY, Language.class);

			//then
			Mockito.verify(spyFlexibleSearchExecutor)
			       .execute(any(Integer.class),
					       any(Integer.class),
					       any(Boolean.class),
					       translatedQueryCaptor.capture(),
					       any(List.class),
					       any(Map.class),
					       any(PK.class),
					       any(Integer.class),
					       any(Set.class),
					       hintsForQueryCaptor.capture(),
					       any(DataSource.class));
			final List<Hint> hintsList = hintsForQueryCaptor.getValue();

			Assertions.assertThat(translatedQueryCaptor.getValue().getSQLTemplate()).isEqualToIgnoringWhitespace(
					SQL_QUERY_TRANSLATED);
			Assertions.assertThat(hintsExistsAndIncludesMaxDOPValue(hintsList, "MAXDOP " + SESSION_MAXDOP_VALUE)).isTrue();
		}
		finally
		{
			//cleanup
			JaloSession.getCurrentSession().removeLocalSessionContext();
		}
	}

	@Test
	public void shouldAddOnlyOneSQLServerQueryHint()
	{
		//given
		final SessionContext ctx = JaloSession.getCurrentSession().createLocalSessionContext();
		ctx.setAttribute(SQLServerQueryHints.FLEXIBLESEARCH_SQL_SERVER_MAXDOP_HINT, 5);

		try
		{
			//when
			final SQLServerQueryHints createdHints = SQLServerQueryHints.create();
			final FlexibleSearchQuery fsQuery = new FlexibleSearchQuery(FS_QUERY);
			fsQuery.addHints(createdHints);
			fsQuery.setDisableCaching(true);

			defaultFlexibleSearchService.search(fsQuery);

			//then
			Assertions.assertThat(fsQuery.getHints().stream()
			                             .filter(SQLServerQueryHints.class::isInstance)
			                             .count())
			          .isEqualTo(1);
		}
		finally
		{
			//cleanup
			JaloSession.getCurrentSession().removeLocalSessionContext();
		}
	}

	private boolean hintsExistsAndIncludesMaxDOPValue(final List<Hint> hintsList, final String parameter)
	{
		return hintsList.stream().filter(SQLServerQueryHints.class::isInstance)
		                .map(SQLServerQueryHints.class::cast)
		                .anyMatch(val -> val.getHints().stream()
		                                    .anyMatch(s -> s.startsWith(parameter)));
	}

	public static class TestFlexibleSearch extends FlexibleSearch
	{
		public TestFlexibleSearch(final FlexibleSearchExecutor flexibleSearchExecutor,
		                          final FlexibleSearchHintsProviderFactory hintsProviderFactory)
		{
			super(flexibleSearchExecutor, hintsProviderFactory);
		}
	}

}
