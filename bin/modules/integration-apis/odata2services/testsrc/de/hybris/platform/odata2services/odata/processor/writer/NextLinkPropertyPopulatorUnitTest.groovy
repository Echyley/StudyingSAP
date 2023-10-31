/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.odata.processor.writer

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.integrationservices.item.IntegrationItem
import de.hybris.platform.odata2services.odata.persistence.ItemLookupRequest
import de.hybris.platform.odata2services.odata.persistence.lookup.ItemLookupResult
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.apache.olingo.odata2.api.ep.EntityProviderWriteProperties
import org.junit.Test

@UnitTest
class NextLinkPropertyPopulatorUnitTest extends JUnitPlatformSpecification {

	def TOTAL_COUNT = 45

	def request = Mock(ItemLookupRequest) {
		getRequestUri() >> URI.create("www.domain.es")
		getSkip() >> 0
		getTop() >> 2
	}

	def populator = new NextLinkPropertyPopulator()

	@Test
	def "isApplicable is true when ODataEntry is null"() {
		expect:
		populator.isApplicable(Mock(ItemLookupRequest))
	}

	@Test
	def "isApplicable is false for GET request of single item"() {
		given:
		request.getIntegrationItem() >> Stub(IntegrationItem)

		expect:
		! populator.isApplicable(request)
	}

	@Test
	def "populate provides builder with next link"() {

		def result = Mock(ItemLookupResult) {
			getTotalCount() >> TOTAL_COUNT
		}

		when:
		def response = populator.populate(Mock(EntityProviderWriteProperties), request, result)

		then:
		response.build().nextLink == "www.domain.es?\$skiptoken=2"
	}

	@Test
	def "populate provides builder with existing properties provided"() {

		def properties = new EntityProviderWriteProperties.ODataEntityProviderPropertiesBuilder().inlineCount(6).build()

		when:
		def response = populator.populate(properties, request, Mock(ItemLookupResult))

		then:
		response.build().inlineCount == 6
	}
}
