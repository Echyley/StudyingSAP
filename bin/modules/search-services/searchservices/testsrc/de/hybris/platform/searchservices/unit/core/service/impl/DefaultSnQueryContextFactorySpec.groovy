/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.searchservices.unit.core.service.impl


import static java.util.Map.entry

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.searchservices.core.service.SnQueryContextProvider
import de.hybris.platform.searchservices.core.service.impl.DefaultSnQueryContextFactory
import de.hybris.platform.searchservices.core.service.impl.DefaultSnQueryContextProviderDefinition
import de.hybris.platform.testframework.JUnitPlatformSpecification

import org.junit.Test
import org.springframework.context.ApplicationContext


@UnitTest
public class DefaultSnQueryContextFactorySpec extends JUnitPlatformSpecification {

	static final String QUERY_CONTEXT_PROVIDER_1_BEAN_ID = "queryContextProvider1"
	static final String QUERY_CONTEXT_PROVIDER_2_BEAN_ID = "queryContextProvider2"

	private static final int HIGH_PRIORITY = 200;
	private static final int LOW_PRIORITY = 50;

	ApplicationContext applicationContext = Mock()

	SnQueryContextProvider queryContextProvider1 = Mock()
	SnQueryContextProvider queryContextProvider2 = Mock()

	def setup() {
		applicationContext.getBean(QUERY_CONTEXT_PROVIDER_1_BEAN_ID, SnQueryContextProvider.class) >> queryContextProvider1
		applicationContext.getBean(QUERY_CONTEXT_PROVIDER_2_BEAN_ID, SnQueryContextProvider.class) >> queryContextProvider2
	}

	DefaultSnQueryContextFactory createQueryContextFactory() {
		DefaultSnQueryContextFactory queryContextFactory = new DefaultSnQueryContextFactory()
		queryContextFactory.setApplicationContext(applicationContext)

		queryContextFactory.afterPropertiesSet();

		return queryContextFactory;
	}

	@Test
	def "No query context provider configured"() {
		given:
		applicationContext.getBeansOfType(DefaultSnQueryContextProviderDefinition) >> Map.of()

		final DefaultSnQueryContextFactory queryContextFactory = createQueryContextFactory()

		when:
		List<String> queryContexts = queryContextFactory.getQueryContexts();

		then:
		queryContexts != null
		queryContexts.isEmpty()
	}

	@Test
	def "Single query context provider configured"() {
		given:
		DefaultSnQueryContextProviderDefinition queryContextProviderDefinition = new DefaultSnQueryContextProviderDefinition(priority: HIGH_PRIORITY, queryContextProvider: queryContextProvider1)
		applicationContext.getBeansOfType(DefaultSnQueryContextProviderDefinition) >> Map.of(QUERY_CONTEXT_PROVIDER_1_BEAN_ID, queryContextProviderDefinition)
		queryContextProvider1.getQueryContexts() >> List.of("context1", "context2")

		final DefaultSnQueryContextFactory queryContextFactory = createQueryContextFactory()

		when:
		List<String> queryContexts = queryContextFactory.getQueryContexts();

		then:
		queryContexts != null
		queryContexts == List.of("context1", "context2")
	}

	@Test
	def "Multiple query context providers configured are executed in the correct order 1"() {
		given:
		DefaultSnQueryContextProviderDefinition queryContextProviderDefinition1 = new DefaultSnQueryContextProviderDefinition(priority: HIGH_PRIORITY, queryContextProvider: queryContextProvider1)
		DefaultSnQueryContextProviderDefinition queryContextProviderDefinition2 = new DefaultSnQueryContextProviderDefinition(priority: LOW_PRIORITY, queryContextProvider: queryContextProvider2)
		applicationContext.getBeansOfType(DefaultSnQueryContextProviderDefinition) >> Map.ofEntries(
				entry(QUERY_CONTEXT_PROVIDER_1_BEAN_ID, queryContextProviderDefinition1),
				entry(QUERY_CONTEXT_PROVIDER_2_BEAN_ID, queryContextProviderDefinition2))

		queryContextProvider1.getQueryContexts() >> List.of("context1", "context2")
		queryContextProvider2.getQueryContexts() >> List.of("context3", "context4")

		final DefaultSnQueryContextFactory queryContextFactory = createQueryContextFactory()

		when:
		List<String> queryContexts = queryContextFactory.getQueryContexts();

		then:
		queryContexts != null
		queryContexts == List.of("context1", "context2", "context3", "context4")
	}

	@Test
	def "Multiple query context providers configured are executed in the correct order 2"() {
		given:
		DefaultSnQueryContextProviderDefinition queryContextProviderDefinition1 = new DefaultSnQueryContextProviderDefinition(priority: LOW_PRIORITY, queryContextProvider: queryContextProvider1)
		DefaultSnQueryContextProviderDefinition queryContextProviderDefinition2 = new DefaultSnQueryContextProviderDefinition(priority: HIGH_PRIORITY, queryContextProvider: queryContextProvider2)
		applicationContext.getBeansOfType(DefaultSnQueryContextProviderDefinition) >> Map.ofEntries(
				entry(QUERY_CONTEXT_PROVIDER_1_BEAN_ID, queryContextProviderDefinition1),
				entry(QUERY_CONTEXT_PROVIDER_2_BEAN_ID, queryContextProviderDefinition2))

		queryContextProvider1.getQueryContexts() >> List.of("context1", "context2")
		queryContextProvider2.getQueryContexts() >> List.of("context3", "context4")

		final DefaultSnQueryContextFactory queryContextFactory = createQueryContextFactory()

		when:
		List<String> queryContexts = queryContextFactory.getQueryContexts();

		then:
		queryContexts != null
		queryContexts == List.of("context3", "context4", "context1", "context2")
	}

	@Test
	def "Duplicate query contexts are removed"() {
		given:
		DefaultSnQueryContextProviderDefinition queryContextProviderDefinition1 = new DefaultSnQueryContextProviderDefinition(priority: HIGH_PRIORITY, queryContextProvider: queryContextProvider1)
		DefaultSnQueryContextProviderDefinition queryContextProviderDefinition2 = new DefaultSnQueryContextProviderDefinition(priority: LOW_PRIORITY, queryContextProvider: queryContextProvider2)
		applicationContext.getBeansOfType(DefaultSnQueryContextProviderDefinition) >> Map.ofEntries(
				entry(QUERY_CONTEXT_PROVIDER_1_BEAN_ID, queryContextProviderDefinition1),
				entry(QUERY_CONTEXT_PROVIDER_2_BEAN_ID, queryContextProviderDefinition2))

		queryContextProvider1.getQueryContexts() >> List.of("context1", "context2")
		queryContextProvider2.getQueryContexts() >> List.of("context2", "context3")

		final DefaultSnQueryContextFactory queryContextFactory = createQueryContextFactory()

		when:
		List<String> queryContexts = queryContextFactory.getQueryContexts();

		then:
		queryContexts != null
		queryContexts == List.of("context1", "context2", "context3")
	}
}
