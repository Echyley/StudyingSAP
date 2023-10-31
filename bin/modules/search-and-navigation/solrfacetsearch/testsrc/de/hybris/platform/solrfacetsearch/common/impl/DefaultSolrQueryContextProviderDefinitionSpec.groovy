/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.solrfacetsearch.common.impl


import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.solrfacetsearch.common.SolrQueryContextProvider
import de.hybris.platform.testframework.JUnitPlatformSpecification

import org.junit.Test


@UnitTest
public class DefaultSolrQueryContextProviderDefinitionSpec extends JUnitPlatformSpecification {

	private static final int HIGH_PRIORITY = 200;
	private static final int DEFAULT_PRIORITY = 100;
	private static final int LOW_PRIORITY = 50;

	SolrQueryContextProvider queryContextProvider1 = Mock()
	SolrQueryContextProvider queryContextProvider2 = Mock()

	@Test
	def "Query context provider definitions are equal"() {
		given:
		DefaultSolrQueryContextProviderDefinition queryContextProviderDefinition1 = new DefaultSolrQueryContextProviderDefinition(priority: DEFAULT_PRIORITY, queryContextProvider: queryContextProvider1)
		DefaultSolrQueryContextProviderDefinition queryContextProviderDefinition2 = new DefaultSolrQueryContextProviderDefinition(priority: DEFAULT_PRIORITY, queryContextProvider: queryContextProvider1)

		expect:
		queryContextProviderDefinition1.compareTo(queryContextProviderDefinition2) == 0
		queryContextProviderDefinition1.equals(queryContextProviderDefinition2)
		queryContextProviderDefinition1.hashCode() == queryContextProviderDefinition2.hashCode()
	}

	@Test
	def "Query context provider definitions are not equal 1"() {
		given:
		DefaultSolrQueryContextProviderDefinition queryContextProviderDefinition1 = new DefaultSolrQueryContextProviderDefinition(priority: LOW_PRIORITY, queryContextProvider: queryContextProvider1)
		DefaultSolrQueryContextProviderDefinition queryContextProviderDefinition2 = new DefaultSolrQueryContextProviderDefinition(priority: HIGH_PRIORITY, queryContextProvider: queryContextProvider1)

		expect:
		!queryContextProviderDefinition1.equals(queryContextProviderDefinition2)
	}

	@Test
	def "Query context provider definitions are not equal 2"() {
		given:
		DefaultSolrQueryContextProviderDefinition queryContextProviderDefinition1 = new DefaultSolrQueryContextProviderDefinition(priority: DEFAULT_PRIORITY, queryContextProvider: queryContextProvider1)
		DefaultSolrQueryContextProviderDefinition queryContextProviderDefinition2 = new DefaultSolrQueryContextProviderDefinition(priority: DEFAULT_PRIORITY, queryContextProvider: queryContextProvider2)

		expect:
		!queryContextProviderDefinition1.equals(queryContextProviderDefinition2)
	}
}
