/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.solrfacetsearch.common.impl


import static java.util.Map.entry

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.solrfacetsearch.common.SolrQueryContextProvider
import de.hybris.platform.testframework.JUnitPlatformSpecification

import org.junit.Test
import org.springframework.context.ApplicationContext


@UnitTest
public class DefaultSolrQueryContextFactorySpec extends JUnitPlatformSpecification {

	static final String QUERY_CONTEXT_PROVIDER_1_BEAN_ID = "queryContextProvider1"
	static final String QUERY_CONTEXT_PROVIDER_2_BEAN_ID = "queryContextProvider2"

	private static final int HIGH_PRIORITY = 200;
	private static final int LOW_PRIORITY = 50;

	ApplicationContext applicationContext = Mock()

	SolrQueryContextProvider queryContextProvider1 = Mock()
	SolrQueryContextProvider queryContextProvider2 = Mock()

	def setup() {
		applicationContext.getBean(QUERY_CONTEXT_PROVIDER_1_BEAN_ID, SolrQueryContextProvider.class) >> queryContextProvider1
		applicationContext.getBean(QUERY_CONTEXT_PROVIDER_2_BEAN_ID, SolrQueryContextProvider.class) >> queryContextProvider2
	}

	DefaultSolrQueryContextFactory createQueryContextFactory() {
		DefaultSolrQueryContextFactory queryContextFactory = new DefaultSolrQueryContextFactory()
		queryContextFactory.setApplicationContext(applicationContext)

		queryContextFactory.afterPropertiesSet();

		return queryContextFactory;
	}

	@Test
	def "No query context provider configured"() {
		given:
		applicationContext.getBeansOfType(DefaultSolrQueryContextProviderDefinition) >> Map.of()

		final DefaultSolrQueryContextFactory queryContextFactory = createQueryContextFactory()

		when:
		List<String> queryContexts = queryContextFactory.getQueryContexts();

		then:
		queryContexts != null
		queryContexts.isEmpty()
	}

	@Test
	def "Single query context provider configured"() {
		given:
		DefaultSolrQueryContextProviderDefinition queryContextProviderDefinition = new DefaultSolrQueryContextProviderDefinition(priority: HIGH_PRIORITY, queryContextProvider: queryContextProvider1)
		applicationContext.getBeansOfType(DefaultSolrQueryContextProviderDefinition) >> Map.of(QUERY_CONTEXT_PROVIDER_1_BEAN_ID, queryContextProviderDefinition)
		queryContextProvider1.getQueryContexts() >> List.of("context1", "context2")

		final DefaultSolrQueryContextFactory queryContextFactory = createQueryContextFactory()

		when:
		List<String> queryContexts = queryContextFactory.getQueryContexts();

		then:
		queryContexts != null
		queryContexts == List.of("context1", "context2")
	}

	@Test
	def "Multiple query context providers configured are executed in the correct order 1"() {
		given:
		DefaultSolrQueryContextProviderDefinition queryContextProviderDefinition1 = new DefaultSolrQueryContextProviderDefinition(priority: HIGH_PRIORITY, queryContextProvider: queryContextProvider1)
		DefaultSolrQueryContextProviderDefinition queryContextProviderDefinition2 = new DefaultSolrQueryContextProviderDefinition(priority: LOW_PRIORITY, queryContextProvider: queryContextProvider2)
		applicationContext.getBeansOfType(DefaultSolrQueryContextProviderDefinition) >> Map.ofEntries(
				entry(QUERY_CONTEXT_PROVIDER_1_BEAN_ID, queryContextProviderDefinition1),
				entry(QUERY_CONTEXT_PROVIDER_2_BEAN_ID, queryContextProviderDefinition2))

		queryContextProvider1.getQueryContexts() >> List.of("context1", "context2")
		queryContextProvider2.getQueryContexts() >> List.of("context3", "context4")

		final DefaultSolrQueryContextFactory queryContextFactory = createQueryContextFactory()

		when:
		List<String> queryContexts = queryContextFactory.getQueryContexts();

		then:
		queryContexts != null
		queryContexts == List.of("context1", "context2", "context3", "context4")
	}

	@Test
	def "Multiple query context providers configured are executed in the correct order 2"() {
		given:
		DefaultSolrQueryContextProviderDefinition queryContextProviderDefinition1 = new DefaultSolrQueryContextProviderDefinition(priority: LOW_PRIORITY, queryContextProvider: queryContextProvider1)
		DefaultSolrQueryContextProviderDefinition queryContextProviderDefinition2 = new DefaultSolrQueryContextProviderDefinition(priority: HIGH_PRIORITY, queryContextProvider: queryContextProvider2)
		applicationContext.getBeansOfType(DefaultSolrQueryContextProviderDefinition) >> Map.ofEntries(
				entry(QUERY_CONTEXT_PROVIDER_1_BEAN_ID, queryContextProviderDefinition1),
				entry(QUERY_CONTEXT_PROVIDER_2_BEAN_ID, queryContextProviderDefinition2))

		queryContextProvider1.getQueryContexts() >> List.of("context1", "context2")
		queryContextProvider2.getQueryContexts() >> List.of("context3", "context4")

		final DefaultSolrQueryContextFactory queryContextFactory = createQueryContextFactory()

		when:
		List<String> queryContexts = queryContextFactory.getQueryContexts();

		then:
		queryContexts != null
		queryContexts == List.of("context3", "context4", "context1", "context2")
	}

	@Test
	def "Duplicate query contexts are removed"() {
		given:
		DefaultSolrQueryContextProviderDefinition queryContextProviderDefinition1 = new DefaultSolrQueryContextProviderDefinition(priority: HIGH_PRIORITY, queryContextProvider: queryContextProvider1)
		DefaultSolrQueryContextProviderDefinition queryContextProviderDefinition2 = new DefaultSolrQueryContextProviderDefinition(priority: LOW_PRIORITY, queryContextProvider: queryContextProvider2)
		applicationContext.getBeansOfType(DefaultSolrQueryContextProviderDefinition) >> Map.ofEntries(
				entry(QUERY_CONTEXT_PROVIDER_1_BEAN_ID, queryContextProviderDefinition1),
				entry(QUERY_CONTEXT_PROVIDER_2_BEAN_ID, queryContextProviderDefinition2))

		queryContextProvider1.getQueryContexts() >> List.of("context1", "context2")
		queryContextProvider2.getQueryContexts() >> List.of("context1", "context3")

		final DefaultSolrQueryContextFactory queryContextFactory = createQueryContextFactory()

		when:
		List<String> queryContexts = queryContextFactory.getQueryContexts();

		then:
		queryContexts != null
		queryContexts == List.of("context1", "context2", "context3")
	}
}
