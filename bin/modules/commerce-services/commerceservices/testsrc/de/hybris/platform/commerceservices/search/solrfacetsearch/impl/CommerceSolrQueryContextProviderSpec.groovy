/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commerceservices.search.solrfacetsearch.impl

import static org.assertj.core.api.Assertions.assertThat

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.commerceservices.enums.SearchQueryContext
import de.hybris.platform.commerceservices.search.searchservices.unit.AbstractSpockTest

import java.util.stream.Collectors

import org.junit.Test

@UnitTest
public class CommerceSolrQueryContextProviderSpec extends AbstractSpockTest {

	CommerceSolrQueryContextProvider queryContextProvider

	def setup() {
		queryContextProvider = new CommerceSolrQueryContextProvider()
	}

	@Test
	def "Return supported qualifier classes"() {
		when:
		List<String> queryContexts = queryContextProvider.getQueryContexts()

		then:
		assertThat(queryContexts).containsExactlyElementsOf(Arrays.stream(SearchQueryContext.values()).map(SearchQueryContext.&name)
				.collect(Collectors.toUnmodifiableList()))
	}
}
