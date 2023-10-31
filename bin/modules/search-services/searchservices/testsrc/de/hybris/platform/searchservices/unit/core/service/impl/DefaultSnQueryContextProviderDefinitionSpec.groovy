/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.searchservices.unit.core.service.impl


import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.searchservices.core.service.SnQueryContextProvider
import de.hybris.platform.searchservices.core.service.impl.DefaultSnQueryContextProviderDefinition
import de.hybris.platform.testframework.JUnitPlatformSpecification

import org.junit.Test


@UnitTest
public class DefaultSnQueryContextProviderDefinitionSpec extends JUnitPlatformSpecification {

	private static final int HIGH_PRIORITY = 200;
	private static final int DEFAULT_PRIORITY = 100;
	private static final int LOW_PRIORITY = 50;

	SnQueryContextProvider queryContextProvider1 = Mock()
	SnQueryContextProvider queryContextProvider2 = Mock()

	@Test
	def "Query context provider definitions are equal"() {
		given:
		DefaultSnQueryContextProviderDefinition queryContextProviderDefinition1 = new DefaultSnQueryContextProviderDefinition(priority: DEFAULT_PRIORITY, queryContextProvider: queryContextProvider1)
		DefaultSnQueryContextProviderDefinition queryContextProviderDefinition2 = new DefaultSnQueryContextProviderDefinition(priority: DEFAULT_PRIORITY, queryContextProvider: queryContextProvider1)

		expect:
		queryContextProviderDefinition1.compareTo(queryContextProviderDefinition2) == 0
		queryContextProviderDefinition1.equals(queryContextProviderDefinition2)
		queryContextProviderDefinition1.hashCode() == queryContextProviderDefinition2.hashCode()
	}

	@Test
	def "Query context provider definitions are not equal 1"() {
		given:
		DefaultSnQueryContextProviderDefinition queryContextProviderDefinition1 = new DefaultSnQueryContextProviderDefinition(priority: LOW_PRIORITY, queryContextProvider: queryContextProvider1)
		DefaultSnQueryContextProviderDefinition queryContextProviderDefinition2 = new DefaultSnQueryContextProviderDefinition(priority: HIGH_PRIORITY, queryContextProvider: queryContextProvider1)

		expect:
		!queryContextProviderDefinition1.equals(queryContextProviderDefinition2)
	}

	@Test
	def "Query context provider definitions are not equal 2"() {
		given:
		DefaultSnQueryContextProviderDefinition queryContextProviderDefinition1 = new DefaultSnQueryContextProviderDefinition(priority: DEFAULT_PRIORITY, queryContextProvider: queryContextProvider1)
		DefaultSnQueryContextProviderDefinition queryContextProviderDefinition2 = new DefaultSnQueryContextProviderDefinition(priority: DEFAULT_PRIORITY, queryContextProvider: queryContextProvider2)

		expect:
		!queryContextProviderDefinition1.equals(queryContextProviderDefinition2)
	}
}
