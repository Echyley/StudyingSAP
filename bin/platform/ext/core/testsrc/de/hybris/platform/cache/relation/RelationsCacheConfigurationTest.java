/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.cache.relation;

import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.testframework.BulkPropertyConfigSwitcher;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

@UnitTest
public class RelationsCacheConfigurationTest
{
	static final String RELATION_1_NAME = "FooRelation";
	static final String RELATION_2_NAME = "BarRelation";
	private static final String RELATION_3_NAME = "OtherRelation";

	private static final int RELATION_1_CAPACITY = 100;
	private static final int RELATION_2_CAPACITY = 200;
	private static final int DEFAULT_CACHE_CAPACITY = 500;

	private final BulkPropertyConfigSwitcher switcher = new BulkPropertyConfigSwitcher();
	private RelationsCacheConfiguration configuration;

	@Before
	public void setUp()
	{
		switcher.switchToValue(String.format("relation.cache.%s.capacity", RELATION_1_NAME), String.valueOf(RELATION_1_CAPACITY));
		switcher.switchToValue(String.format("relation.cache.%s.capacity", RELATION_2_NAME), String.valueOf(RELATION_2_CAPACITY));
		switcher.switchToValue(RelationsCacheConfiguration.PROPERTY_DEFAULT_CAPACITY, String.valueOf(DEFAULT_CACHE_CAPACITY));

		configuration = new RelationsCacheConfiguration();
	}

	@After
	public void tearDown()
	{
		switcher.switchAllBack();
	}

	@Test
	public void shouldGetCacheCapacityForExplicitlyConfiguredRelations()
	{
		assertThat(configuration.getCapacityForRelation(RELATION_1_NAME)).isEqualTo(RELATION_1_CAPACITY);
		assertThat(configuration.getCapacityForRelation(RELATION_2_NAME)).isEqualTo(RELATION_2_CAPACITY);
	}

	@Test
	public void shouldGetDefaultCacheCapacityForOtherRelations()
	{
		assertThat(configuration.getCapacityForRelation(RELATION_3_NAME)).isEqualTo(DEFAULT_CACHE_CAPACITY);
		assertThat(configuration.getCapacityForRelation("")).isEqualTo(DEFAULT_CACHE_CAPACITY);
		assertThat(configuration.getCapacityForRelation(null)).isEqualTo(DEFAULT_CACHE_CAPACITY);
	}
}
