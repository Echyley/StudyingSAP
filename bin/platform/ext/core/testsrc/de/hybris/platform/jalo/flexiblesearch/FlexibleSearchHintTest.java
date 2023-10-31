/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.jalo.flexiblesearch;

import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.Registry;
import de.hybris.platform.jalo.flexiblesearch.hints.Hint;
import de.hybris.platform.jalo.flexiblesearch.hints.impl.FlexibleSearchHints;
import de.hybris.platform.jalo.flexiblesearch.hints.impl.SQLServerQueryHints;
import de.hybris.platform.persistence.flexiblesearch.TranslatedQuery;
import de.hybris.platform.testframework.HybrisJUnit4Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.assertj.core.util.Sets;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

@IntegrationTest
public class FlexibleSearchHintTest extends HybrisJUnit4Test
{
	@Test
	public void testFlexibleSearchKeyWithNoHints()
	{
		final TranslatedQuery translatedQueryOne = new TranslatedQuery("query", null, null, false, Set.of(), null, false, false);
		final FlexibleSearch.FlexibleSearchCacheKey flexibleSearchCacheKeyOne = new FlexibleSearch.FlexibleSearchCacheKey(
				translatedQueryOne, Map.of(), null, List.of(PK.class), true, 0, 0, Sets.newLinkedHashSet(1), false, -1,
				Registry.getCurrentTenant().getTenantID(), null, List.of());

		final FlexibleSearch.FlexibleSearchCacheKey flexibleSearchCacheKeyTwo = new FlexibleSearch.FlexibleSearchCacheKey(
				translatedQueryOne, Map.of(), null, List.of(PK.class), true, 0, 0, Sets.newLinkedHashSet(1), false, -1,
				Registry.getCurrentTenant().getTenantID(), null, List.of());

		assertThat(flexibleSearchCacheKeyOne).isEqualTo(flexibleSearchCacheKeyTwo);
	}

	@Test
	public void testFlexibleSearchKeyWithCategorisedHints()
	{
		final TranslatedQuery translatedQueryOne = new TranslatedQuery("query", null, null, false, Set.of(), null, false, false);
		final Hint categorizedQueryHintOne = FlexibleSearchHints.categorizedQuery("cat1");
		final Hint categorizedQueryHintTwo = FlexibleSearchHints.categorizedQuery("cat2");
		final FlexibleSearch.FlexibleSearchCacheKey flexibleSearchCacheKeyOne = new FlexibleSearch.FlexibleSearchCacheKey(
				translatedQueryOne, Map.of(), null, List.of(PK.class), true, 0, 0, Sets.newLinkedHashSet(1), false, -1,
				Registry.getCurrentTenant().getTenantID(), null, List.of(categorizedQueryHintOne));

		FlexibleSearch.FlexibleSearchCacheKey flexibleSearchCacheKeyTwo = new FlexibleSearch.FlexibleSearchCacheKey(
				translatedQueryOne, Map.of(), null, List.of(PK.class), true, 0, 0, Sets.newLinkedHashSet(1), false, -1,
				Registry.getCurrentTenant().getTenantID(), null, List.of(categorizedQueryHintOne));

		assertThat(flexibleSearchCacheKeyOne).isEqualTo(flexibleSearchCacheKeyTwo);

		flexibleSearchCacheKeyTwo = new FlexibleSearch.FlexibleSearchCacheKey(translatedQueryOne, Map.of(), null,
				List.of(PK.class), true, 0, 0, Sets.newLinkedHashSet(1), false, -1, Registry.getCurrentTenant().getTenantID(),
				null, List.of(categorizedQueryHintTwo));

		assertThat(flexibleSearchCacheKeyOne).isNotEqualTo(flexibleSearchCacheKeyTwo);
	}

	@Test
	public void testFlexibleSearchKeyWithSQLServerQueryHints()
	{
		final TranslatedQuery translatedQueryOne = new TranslatedQuery("query", null, null, false, Set.of(), null, false, false);
		final SQLServerQueryHints sqlServerQueryHints = SQLServerQueryHints.create();
		sqlServerQueryHints.addMaxDOPHint(5);
		final FlexibleSearch.FlexibleSearchCacheKey flexibleSearchCacheKeyOne = new FlexibleSearch.FlexibleSearchCacheKey(
				translatedQueryOne, Map.of(), null, List.of(PK.class), true, 0, 0, Sets.newLinkedHashSet(1), false, -1,
				Registry.getCurrentTenant().getTenantID(), null, List.of(sqlServerQueryHints));

		FlexibleSearch.FlexibleSearchCacheKey anotherFlexibleSearchCacheKey = new FlexibleSearch.FlexibleSearchCacheKey(
				translatedQueryOne, Map.of(), null, List.of(PK.class), true, 0, 0, Sets.newLinkedHashSet(1), false, -1,
				Registry.getCurrentTenant().getTenantID(), null, List.of(sqlServerQueryHints));

		assertThat(flexibleSearchCacheKeyOne).isEqualTo(anotherFlexibleSearchCacheKey);

		final SQLServerQueryHints anotherSqlServerQueryHints = SQLServerQueryHints.create();
		anotherSqlServerQueryHints.addMaxDOPHint(6);
		anotherFlexibleSearchCacheKey = new FlexibleSearch.FlexibleSearchCacheKey(translatedQueryOne, Map.of(), null,
				List.of(PK.class), true, 0, 0, Sets.newLinkedHashSet(1), false, -1, Registry.getCurrentTenant().getTenantID(),
				null, List.of(anotherSqlServerQueryHints));

		assertThat(flexibleSearchCacheKeyOne).isNotEqualTo(anotherFlexibleSearchCacheKey);

	}

	@Test
	public void testFlexibleSearchKeyWithDisableSQLServerQueryHints()
	{
		final TranslatedQuery translatedQueryOne = new TranslatedQuery("query", null, null, false, Set.of(), null, false, false);
		final SQLServerQueryHints sqlServerQueryHints = SQLServerQueryHints.create();
		ReflectionTestUtils.setField(sqlServerQueryHints, "isSQLServerUsed", false);
		final FlexibleSearch.FlexibleSearchCacheKey flexibleSearchCacheKeyOne = new FlexibleSearch.FlexibleSearchCacheKey(
				translatedQueryOne, Map.of(), null, List.of(PK.class), true, 0, 0, Sets.newLinkedHashSet(1), false, -1,
				Registry.getCurrentTenant().getTenantID(), null, List.of(sqlServerQueryHints));

		final SQLServerQueryHints anotherSqlServerQueryHints = SQLServerQueryHints.create();
		anotherSqlServerQueryHints.addMaxDOPHint(6);
		final FlexibleSearch.FlexibleSearchCacheKey anotherFlexibleSearchCacheKey = new FlexibleSearch.FlexibleSearchCacheKey(
				translatedQueryOne, Map.of(), null,
				List.of(PK.class), true, 0, 0, Sets.newLinkedHashSet(1), false, -1, Registry.getCurrentTenant().getTenantID(),
				null, List.of(anotherSqlServerQueryHints));

		assertThat(flexibleSearchCacheKeyOne).isNotEqualTo(anotherFlexibleSearchCacheKey);
	}
}
