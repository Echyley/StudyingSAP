/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.odata.processor.writer

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.integrationservices.item.IntegrationItem
import de.hybris.platform.odata2services.odata.persistence.ItemLookupRequest
import de.hybris.platform.odata2services.odata.persistence.lookup.ItemLookupResult
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.apache.olingo.odata2.api.edm.EdmEntitySet
import org.apache.olingo.odata2.api.ep.EntityProviderWriteProperties
import org.apache.olingo.odata2.api.ep.entry.ODataEntry
import org.apache.olingo.odata2.api.exception.ODataException
import org.apache.olingo.odata2.api.processor.ODataResponse
import org.junit.Test

@UnitTest
class DefaultResponseWriterUnitTest extends JUnitPlatformSpecification {
	def responseWriter = Spy(DefaultResponseWriter)

	def populator = Mock(ResponseWriterPropertyPopulator)
	def populatorRegistry = Stub(ResponseWriterPropertyPopulatorRegistry) {
		getPopulators(_ as ItemLookupRequest) >> [populator]
	}

	def setup() {
		responseWriter.setPopulatorRegistry(populatorRegistry)
	}

	@Test
	def "writes a single entity"() throws ODataException {
		given:
		populator.populate(_ as EntityProviderWriteProperties, _ as ItemLookupRequest, null) >> Stub(EntityProviderWriteProperties.ODataEntityProviderPropertiesBuilder)
		def expected = Stub(ODataResponse)
		responseWriter.write(_, _, _, _) >> expected

		expect:
		responseWriter.write(itemLookupRequest(), Stub(EdmEntitySet), [:]) == expected
	}

	@Test
	def "writes a collection of entities"() throws ODataException {
		given:
		def result = entitySetResult()
		def request = itemLookupRequest()
		result.getTotalCount() >> 5
		def expected = Stub(ODataResponse)
		populator.populate(_ as EntityProviderWriteProperties, request, result) >> new EntityProviderWriteProperties.ODataEntityProviderPropertiesBuilder().inlineCount(5)

		when:
		def actual = responseWriter.write(request, Stub(EdmEntitySet), result)

		then:
		1 * responseWriter.write(_, _, _, _) >> { args ->
			assert args[3].inlineCount == result.totalCount
			expected
		}
		actual == expected
	}

	@Test
	def "writes a collection of entities with unchanged write properties when they are not present in the request"() throws ODataException {
		given:
		def request = itemLookupRequest()
		request.getIntegrationItem() >> Stub(IntegrationItem)
		def result = entitySetResult()
		def expected = Stub ODataResponse
		responseWriter.write(_, _, _, _) >> expected

		when:
		def actual = responseWriter.write(request, Stub(EdmEntitySet.class), result)

		then:
		actual == expected
	}

	@Test
	def "writes a collection of entities without inlined count when it's not present in the request"() throws ODataException {
		given:
		def result = entitySetResult()
		def expected = Stub(ODataResponse)
		def request = itemLookupRequestWithNoCount()
		populator.populate(_ as EntityProviderWriteProperties, request, result) >> new EntityProviderWriteProperties.ODataEntityProviderPropertiesBuilder()

		when:
		def actual = responseWriter.write(request, Stub(EdmEntitySet.class), result)

		then:
		1 * responseWriter.write(_, _, _, _) >> { List args ->
			assert args[3].inlineCount == null
			assert args[3].inlineCountType == null
			expected
		}
		actual == expected
	}

	ItemLookupRequest itemLookupRequestWithNoCount() {
		Mock(ItemLookupRequest) {
			getContentType() >> "application/json"
		}
	}

	ItemLookupRequest itemLookupRequest() {
		Mock(ItemLookupRequest) {
			getContentType() >> "application/json"
			getRequestUri() >> URI.create("http://test")
			getSkip() >> 0
			getTop() >> 100
		}
	}

	ItemLookupResult entitySetResult() {
		def oDataEntry = Mock(ODataEntry) {
			getProperties() >> ["key": "value"]
		}

		Mock(ItemLookupResult) {
			getEntries() >> [oDataEntry]
		}
	}
}
