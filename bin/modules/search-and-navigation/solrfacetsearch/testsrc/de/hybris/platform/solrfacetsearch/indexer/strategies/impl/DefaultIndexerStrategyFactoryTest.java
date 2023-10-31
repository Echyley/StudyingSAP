/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.solrfacetsearch.indexer.strategies.impl;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.indexer.exceptions.IndexerException;
import de.hybris.platform.solrfacetsearch.indexer.strategies.IndexerStrategy;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultIndexerStrategyFactoryTest
{
	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Mock
	private ApplicationContext applicationContext;

	@Mock
	private FacetSearchConfig facetSearchConfig;

	@Mock
	private IndexConfig indexConfig;

	private DefaultIndexerStrategyFactory indexerStrategyFactory;

	@Before
	public void setUp()
	{
		indexerStrategyFactory = new DefaultIndexerStrategyFactory();
		indexerStrategyFactory.setApplicationContext(applicationContext);
	}

	@Test
	public void createDefaultIndexerStrategy() throws Exception
	{
		// given
		final String indexerStrategyBeanId = "indexerStrategy";
		final DefaultIndexerStrategy expectedIndexerStrategy = mock(DefaultIndexerStrategy.class);

		indexerStrategyFactory.setIndexerStrategyBeanId(indexerStrategyBeanId);
		when(facetSearchConfig.getIndexConfig()).thenReturn(indexConfig);
		when(facetSearchConfig.getIndexConfig().isDistributedIndexing()).thenReturn(false);
		when(applicationContext.getBean(indexerStrategyBeanId, IndexerStrategy.class)).thenReturn(expectedIndexerStrategy);

		// when
		final IndexerStrategy indexerStrategy = indexerStrategyFactory.createIndexerStrategy(facetSearchConfig);

		// then
		assertSame(expectedIndexerStrategy, indexerStrategy);
	}

	@Test
	public void createDistributedIndexerStrategy() throws Exception
	{
		// given
		final String distributedIndexerStrategyBeanId = "distributedIndexerStrategy";
		final DistributedIndexerStrategy expectedIndexerStrategy = mock(DistributedIndexerStrategy.class);

		indexerStrategyFactory.setDistributedIndexerStrategyBeanId(distributedIndexerStrategyBeanId);
		when(facetSearchConfig.getIndexConfig()).thenReturn(indexConfig);
		when(facetSearchConfig.getIndexConfig().isDistributedIndexing()).thenReturn(true);
		when(applicationContext.getBean(distributedIndexerStrategyBeanId, IndexerStrategy.class)).thenReturn(
				expectedIndexerStrategy);

		// when
		final IndexerStrategy indexerStrategy = indexerStrategyFactory.createIndexerStrategy(facetSearchConfig);

		// then
		assertSame(expectedIndexerStrategy, indexerStrategy);
	}

	@Test
	public void shouldFailToCreateIndexerStrategy() throws Exception
	{
		// given
		final String indexerStrategyBeanId = "indexerStrategy";
		final BeansException exception = new NoSuchBeanDefinitionException(indexerStrategyBeanId);

		indexerStrategyFactory.setIndexerStrategyBeanId(indexerStrategyBeanId);
		when(facetSearchConfig.getIndexConfig()).thenReturn(indexConfig);
		when(facetSearchConfig.getIndexConfig().isDistributedIndexing()).thenReturn(false);
		when(applicationContext.getBean(indexerStrategyBeanId, IndexerStrategy.class)).thenThrow(exception);

		// expect
		expectedException.expect(IndexerException.class);

		// when
		indexerStrategyFactory.createIndexerStrategy(facetSearchConfig);
	}
}
