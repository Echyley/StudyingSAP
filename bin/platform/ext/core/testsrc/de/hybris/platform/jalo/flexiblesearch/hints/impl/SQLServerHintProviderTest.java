/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 *
 */

package de.hybris.platform.jalo.flexiblesearch.hints.impl;

import static org.mockito.ArgumentMatchers.any;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.flexiblesearch.hints.Hint;
import de.hybris.platform.jalo.flexiblesearch.internal.MaxDOPWrongValueException;
import de.hybris.platform.jalo.flexiblesearch.internal.ReadOnlyConditionsHelper;
import de.hybris.platform.jalo.flexiblesearch.internal.SQLServerHintProvider;
import de.hybris.platform.jdbcwrapper.HybrisDataSource;
import de.hybris.platform.testframework.BulkPropertyConfigSwitcher;
import de.hybris.platform.testframework.HybrisJUnit4Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.ListAssert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;


@IntegrationTest
public class SQLServerHintProviderTest extends HybrisJUnit4Test
{
	private final BulkPropertyConfigSwitcher switcher = new BulkPropertyConfigSwitcher();

	private ReadOnlyConditionsHelper helper;
	private HybrisDataSource dataSource;
	private SQLServerHintProvider sqlServerHintProvider;

	@Before
	public void setUp()
	{
		helper = Mockito.mock(ReadOnlyConditionsHelper.class);
		dataSource = Mockito.mock(HybrisDataSource.class);
		sqlServerHintProvider = new SQLServerHintProvider(helper);
	}

	@After
	public void tearDown()
	{
		switcher.switchAllBack();
	}

	@Test
	public void shouldApplyMAXDOPHintsWhenAddedInSessionContext()
	{
		//given
		final SessionContext ctx = JaloSession.getCurrentSession().createLocalSessionContext();
		ctx.setAttribute(SQLServerQueryHints.FLEXIBLESEARCH_SQL_SERVER_MAXDOP_HINT, 1);

		final List<Hint> hints = new ArrayList<>();

		try
		{
			//when
			final List<Hint> result = sqlServerHintProvider.injectFlexibleSearchHints(ctx, hints, dataSource);

			//then
			assertThatSQLQueryHints(result).contains("MAXDOP 1");

		}
		finally
		{
			//cleanup
			JaloSession.getCurrentSession().removeLocalSessionContext();
		}
	}

	@Test
	public void shouldNotApplyMAXDOPHintsWhenOtherMAXDOPHintIsAlreadySet()
	{
		//given
		final SessionContext ctx = JaloSession.getCurrentSession().createLocalSessionContext();
		ctx.setAttribute(SQLServerQueryHints.FLEXIBLESEARCH_SQL_SERVER_MAXDOP_HINT, 1);

		final SQLServerQueryHints maxDOPHint = new SQLServerQueryHints(true);
		maxDOPHint.addMaxDOPHint(2);
		final List<Hint> hints = new ArrayList<>();
		hints.add(maxDOPHint);

		try
		{
			//when
			final List<Hint> result = sqlServerHintProvider.injectFlexibleSearchHints(ctx, hints, dataSource);

			//then
			assertThatSQLQueryHints(result).contains("MAXDOP 2");
			assertThatSQLQueryHints(result).doesNotContain("MAXDOP 1");
		}
		finally
		{
			//cleanup
			JaloSession.getCurrentSession().removeLocalSessionContext();
		}

	}

	@Test
	public void shouldApplyMAXDOPHintsWhenOnMasterDataSource()
	{
		//given
		final SessionContext ctx = jaloSession.getSessionContext();
		switcher.switchToValue(SQLServerQueryHints.FLEXIBLESEARCH_SQL_SERVER_MAXDOP_HINT, "6");

		final List<Hint> hints = new ArrayList<>();

		//when
		final List<Hint> result = sqlServerHintProvider.injectFlexibleSearchHints(ctx, hints, dataSource);

		//then
		assertThatSQLQueryHints(result).contains("MAXDOP 6");
	}

	@Test
	public void shouldNotApplyMAXDOPHintsWhenOnReadOnlyDataSource()
	{
		//given
		activateReadOnlyDatasource();
		final SessionContext ctx = jaloSession.getSessionContext();
		switcher.switchToValue(SQLServerQueryHints.FLEXIBLESEARCH_SQL_SERVER_MAXDOP_HINT, "6");

		final List<Hint> hints = new ArrayList<>();

		//when
		final List<Hint> result = sqlServerHintProvider.injectFlexibleSearchHints(ctx, hints, dataSource);

		//then
		assertThatSQLQueryHints(result).doesNotContain("MAXDOP 6");
	}

	@Test
	public void shouldUsePreviousValidMAXDOPValueWhenGivenInvalidValueInConfig()
	{
		//given
		final SessionContext ctx = jaloSession.getSessionContext();
		switcher.switchToValue(SQLServerQueryHints.FLEXIBLESEARCH_SQL_SERVER_MAXDOP_HINT, "1");

		//when
		final List<Hint> hints = new ArrayList<>();

		switcher.switchToValue(SQLServerQueryHints.FLEXIBLESEARCH_SQL_SERVER_MAXDOP_HINT, "testvalue");
		final List<Hint> injectedHints = sqlServerHintProvider.injectFlexibleSearchHints(ctx, hints, dataSource);

		//then
		assertThatSQLQueryHints(injectedHints).contains("MAXDOP 1");
	}

	@Test(expected = MaxDOPWrongValueException.class)
	public void shouldNotApplyMAXDOPHintsForStringValueInSession()
	{
		//given
		final SessionContext ctx = JaloSession.getCurrentSession().createLocalSessionContext();
		switcher.switchToValue(SQLServerQueryHints.FLEXIBLESEARCH_SQL_SERVER_MAXDOP_HINT, "-1");
		ctx.setAttribute(SQLServerQueryHints.FLEXIBLESEARCH_SQL_SERVER_MAXDOP_HINT, "testvalue");

		final List<Hint> hints = new ArrayList<>();

		try
		{
			//when
			sqlServerHintProvider.injectFlexibleSearchHints(ctx, hints, dataSource);
		}
		finally
		{
			//then cleanup
			JaloSession.getCurrentSession().removeLocalSessionContext();
		}
	}

	@Test(expected = MaxDOPWrongValueException.class)
	public void shouldNotApplyMAXDOPHintsForTooBigValueInSession()
	{
		//given
		final SessionContext ctx = JaloSession.getCurrentSession().createLocalSessionContext();
		switcher.switchToValue(SQLServerQueryHints.FLEXIBLESEARCH_SQL_SERVER_MAXDOP_HINT, "-1");
		ctx.setAttribute(SQLServerQueryHints.FLEXIBLESEARCH_SQL_SERVER_MAXDOP_HINT, 1000000000);

		final List<Hint> hints = new ArrayList<>();

		try
		{
			//when
			sqlServerHintProvider.injectFlexibleSearchHints(ctx, hints, dataSource);
		}
		finally
		{
			//then cleanup
			JaloSession.getCurrentSession().removeLocalSessionContext();
		}
	}

	@Test(expected = MaxDOPWrongValueException.class)
	public void shouldNotApplyMAXDOPHintsForTooBigValueInConfig()
	{
		//given
		final SessionContext ctx = jaloSession.getSessionContext();
		switcher.switchToValue(SQLServerQueryHints.FLEXIBLESEARCH_SQL_SERVER_MAXDOP_HINT, "1000000");

		final List<Hint> hints = new ArrayList<>();

		//when
		sqlServerHintProvider.injectFlexibleSearchHints(ctx, hints, dataSource);
	}

	@Test(expected = MaxDOPWrongValueException.class)
	public void shouldNotApplyMAXDOPHintsForNegativeValueInSession()
	{
		//given
		final SessionContext ctx = JaloSession.getCurrentSession().createLocalSessionContext();
		switcher.switchToValue(SQLServerQueryHints.FLEXIBLESEARCH_SQL_SERVER_MAXDOP_HINT, "-1");
		ctx.setAttribute(SQLServerQueryHints.FLEXIBLESEARCH_SQL_SERVER_MAXDOP_HINT, -2);

		final List<Hint> hints = new ArrayList<>();

		try
		{
			//when
			sqlServerHintProvider.injectFlexibleSearchHints(ctx, hints, dataSource);
		}
		finally
		{
			//then cleanup
			JaloSession.getCurrentSession().removeLocalSessionContext();
		}
	}

	@Test(expected = MaxDOPWrongValueException.class)
	public void shouldNotApplyMAXDOPHintsForNegativeValueInConfig()
	{
		//given
		final SessionContext ctx = jaloSession.getSessionContext();
		switcher.switchToValue(SQLServerQueryHints.FLEXIBLESEARCH_SQL_SERVER_MAXDOP_HINT, "-5");

		final List<Hint> hints = new ArrayList<>();

		//when
		final List<Hint> result = sqlServerHintProvider.injectFlexibleSearchHints(ctx, hints, dataSource);
	}

	@Test(expected = MaxDOPWrongValueException.class)
	public void shouldNotApplyMAXDOPHintsForDecimalValueInSession()
	{
		//given
		final SessionContext ctx = JaloSession.getCurrentSession().createLocalSessionContext();
		switcher.switchToValue(SQLServerQueryHints.FLEXIBLESEARCH_SQL_SERVER_MAXDOP_HINT, "-1");
		ctx.setAttribute(SQLServerQueryHints.FLEXIBLESEARCH_SQL_SERVER_MAXDOP_HINT, 1.2);

		final List<Hint> hints = new ArrayList<>();

		try
		{
			//when
			sqlServerHintProvider.injectFlexibleSearchHints(ctx, hints, dataSource);
		}
		finally
		{
			//then cleanup
			JaloSession.getCurrentSession().removeLocalSessionContext();
		}
	}

	@Test
	public void shouldNotApplyMAXDOPHintsForDecimalValueInConfig()
	{
		//given
		final SessionContext ctx = jaloSession.getSessionContext();
		switcher.switchToValue(SQLServerQueryHints.FLEXIBLESEARCH_SQL_SERVER_MAXDOP_HINT, "1");

		final List<Hint> hints = new ArrayList<>();

		//when
		switcher.switchToValue(SQLServerQueryHints.FLEXIBLESEARCH_SQL_SERVER_MAXDOP_HINT, "1.2");
		final List<Hint> result = sqlServerHintProvider.injectFlexibleSearchHints(ctx, hints, dataSource);

		//then
		assertThatSQLQueryHints(result).contains("MAXDOP 1");
	}

	@Test
	public void shouldNotApplyMAXDOPHintsForNullValueInConfig()
	{
		//given
		final SessionContext ctx = jaloSession.getSessionContext();
		switcher.switchToValue(SQLServerQueryHints.FLEXIBLESEARCH_SQL_SERVER_MAXDOP_HINT, "");

		final List<Hint> hints = new ArrayList<>();

		//when
		final List<Hint> result = sqlServerHintProvider.injectFlexibleSearchHints(ctx, hints, dataSource);

		//then
		assertThatSQLQueryHints(result).doesNotContain("MAXDOP");
	}

	@Test
	public void shouldNotApplyMAXDOPHintsForNullValueInSession()
	{
		//given
		final SessionContext ctx = JaloSession.getCurrentSession().createLocalSessionContext();
		switcher.switchToValue(SQLServerQueryHints.FLEXIBLESEARCH_SQL_SERVER_MAXDOP_HINT, "-1");
		ctx.setAttribute(SQLServerQueryHints.FLEXIBLESEARCH_SQL_SERVER_MAXDOP_HINT, null);

		final List<Hint> hints = new ArrayList<>();

		try
		{
			//when
			final List<Hint> result = sqlServerHintProvider.injectFlexibleSearchHints(ctx, hints, dataSource);

			//then
			assertThatSQLQueryHints(result).doesNotContain("MAXDOP");
		}
		finally
		{
			//then cleanup
			JaloSession.getCurrentSession().removeLocalSessionContext();
		}
	}

	@Test
	public void shouldApplyMAXDOPHintsFromSessionWhenBothSet()
	{
		//given
		final SessionContext ctx = JaloSession.getCurrentSession().createLocalSessionContext();
		ctx.setAttribute(SQLServerQueryHints.FLEXIBLESEARCH_SQL_SERVER_MAXDOP_HINT, 1);
		switcher.switchToValue(SQLServerQueryHints.FLEXIBLESEARCH_SQL_SERVER_MAXDOP_HINT, "4");

		final List<Hint> hints = new ArrayList<>();

		try
		{
			//when
			final List<Hint> result = sqlServerHintProvider.injectFlexibleSearchHints(ctx, hints, dataSource);

			//then
			assertThatSQLQueryHints(result).contains("MAXDOP 1");
			assertThatSQLQueryHints(result).doesNotContain("MAXDOP 4");

		}
		finally
		{
			//cleanup
			JaloSession.getCurrentSession().removeLocalSessionContext();
		}
	}

	@Test
	public void shouldNotApplyMAXDOPHintsWhenTurnedOffInSessionContext()
	{
		//given
		switcher.switchToValue(SQLServerQueryHints.FLEXIBLESEARCH_SQL_SERVER_MAXDOP_HINT, "4");
		final SessionContext ctx = JaloSession.getCurrentSession().createLocalSessionContext();
		ctx.setAttribute(SQLServerQueryHints.FLEXIBLESEARCH_SQL_SERVER_MAXDOP_HINT, -1);

		final List<Hint> hints = new ArrayList<>();

		try
		{
			//when
			final List<Hint> result = sqlServerHintProvider.injectFlexibleSearchHints(ctx, hints, dataSource);

			//then
			assertThatSQLQueryHints(result).doesNotContain("MAXDOP 4");

		}
		finally
		{
			//cleanup
			JaloSession.getCurrentSession().removeLocalSessionContext();
		}
	}

	@Test
	public void shouldApplyMAXDOPHintsFromSessionWhenValueWithSpaces()
	{
		//given
		final SessionContext ctx = JaloSession.getCurrentSession().createLocalSessionContext();
		ctx.setAttribute(SQLServerQueryHints.FLEXIBLESEARCH_SQL_SERVER_MAXDOP_HINT, " 1 ");

		final List<Hint> hints = new ArrayList<>();

		try
		{
			//when
			final List<Hint> result = sqlServerHintProvider.injectFlexibleSearchHints(ctx, hints, dataSource);

			//then
			assertThatSQLQueryHints(result).contains("MAXDOP 1");

		}
		finally
		{
			//cleanup
			JaloSession.getCurrentSession().removeLocalSessionContext();
		}
	}

	@Test
	public void shouldApplyMAXDOPHintsFromConfigWhenValueWithSpaces()
	{
		//given
		final SessionContext ctx = jaloSession.getSessionContext();
		switcher.switchToValue(SQLServerQueryHints.FLEXIBLESEARCH_SQL_SERVER_MAXDOP_HINT,  " 6 ");

		final List<Hint> hints = new ArrayList<>();

		//when
		final List<Hint> result = sqlServerHintProvider.injectFlexibleSearchHints(ctx, hints, dataSource);

		//then
		assertThatSQLQueryHints(result).contains("MAXDOP 6");
	}

	private void activateReadOnlyDatasource()
	{
		Mockito.when(helper.couldUseReadOnlyDataSource(any(), any())).thenReturn(true);
		Mockito.when(helper.getReadOnlyDataSource(any())).thenReturn(Optional.of(dataSource));
		Mockito.when(dataSource.getID()).thenReturn("readonly");
	}

	private ListAssert<String> assertThatSQLQueryHints(final List<Hint> result)
	{
		return Assertions.assertThat(result)
		                 .filteredOn(SQLServerQueryHints.class::isInstance)
		                 .flatExtracting(hint -> ((SQLServerQueryHints) hint).getHints());
	}

}
