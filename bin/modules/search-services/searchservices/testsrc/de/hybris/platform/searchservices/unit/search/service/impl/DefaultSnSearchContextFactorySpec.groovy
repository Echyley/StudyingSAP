/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.searchservices.unit.search.service.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.searchservices.admin.SnIndexConfigurationNotFoundException
import de.hybris.platform.searchservices.admin.SnIndexTypeNotFoundException
import de.hybris.platform.searchservices.admin.data.SnIndexConfiguration
import de.hybris.platform.searchservices.admin.data.SnIndexType
import de.hybris.platform.searchservices.admin.service.SnIndexConfigurationService
import de.hybris.platform.searchservices.admin.service.SnIndexTypeService
import de.hybris.platform.searchservices.core.service.SnQualifier
import de.hybris.platform.searchservices.core.service.SnQualifierProvider
import de.hybris.platform.searchservices.core.service.SnQualifierType
import de.hybris.platform.searchservices.core.service.SnQualifierTypeFactory
import de.hybris.platform.searchservices.search.data.SnSearchQuery
import de.hybris.platform.searchservices.search.service.SnSearchContext
import de.hybris.platform.searchservices.search.service.SnSearchRequest
import de.hybris.platform.searchservices.search.service.SnSearchResponse
import de.hybris.platform.searchservices.search.service.impl.DefaultSnSearchContextFactory
import de.hybris.platform.searchservices.search.service.impl.DefaultSnSearchRequest
import de.hybris.platform.testframework.JUnitPlatformSpecification

import org.junit.Test


@UnitTest
public class DefaultSnSearchContextFactorySpec extends JUnitPlatformSpecification {

	static final String INDEX_TYPE_ID = "indexType"
	static final String INDEX_CONFIGURATION_ID = "indexConfiguration"
	static final String QUALIFIER_TYPE_ID = "qualifierType"
	static final String INDEX_ID = "index"

	SnIndexConfigurationService snIndexConfigurationService = Mock()
	SnIndexTypeService snIndexTypeService = Mock()
	SnQualifierTypeFactory snQualifierTypeFactory = Mock()

	DefaultSnSearchContextFactory snSearchContextFactory

	def setup() {
		snSearchContextFactory = new DefaultSnSearchContextFactory()
		snSearchContextFactory.setSnIndexConfigurationService(snIndexConfigurationService)
		snSearchContextFactory.setSnIndexTypeService(snIndexTypeService)
		snSearchContextFactory.setSnQualifierTypeFactory(snQualifierTypeFactory)
	}

	@Test
	def "Fail to create context without request"() {
		when:
		snSearchContextFactory.createSearchContext(null)

		then:
		thrown(NullPointerException)
	}

	@Test
	def "Fail to create context without index type"() {
		given:
		final SnSearchQuery searchQuery = new SnSearchQuery()
		final SnSearchRequest searchRequest = new DefaultSnSearchRequest(null, searchQuery)

		when:
		snSearchContextFactory.createSearchContext(searchRequest)

		then:
		thrown(SnIndexTypeNotFoundException)
	}

	@Test
	def "Fail to create context for not existing index type"() {
		given:
		snIndexTypeService.getIndexTypeForId(INDEX_TYPE_ID) >> Optional.empty()

		final SnSearchQuery searchQuery = new SnSearchQuery()
		final SnSearchRequest searchRequest = new DefaultSnSearchRequest(INDEX_TYPE_ID, searchQuery)

		when:
		snSearchContextFactory.createSearchContext(searchRequest)

		then:
		thrown(SnIndexTypeNotFoundException)
	}

	@Test
	def "Fail to create context for index type without index configuration"() {
		given:
		SnIndexType indexType = new SnIndexType(id: INDEX_TYPE_ID)

		snIndexTypeService.getIndexTypeForId(INDEX_TYPE_ID) >> Optional.of(indexType)

		final SnSearchQuery searchQuery = new SnSearchQuery()
		final SnSearchRequest searchRequest = new DefaultSnSearchRequest(INDEX_TYPE_ID, searchQuery)

		when:
		snSearchContextFactory.createSearchContext(searchRequest)

		then:
		thrown(SnIndexConfigurationNotFoundException)
	}

	@Test
	def "Fail to create context for not existing index configuration"() {
		given:
		SnIndexType indexType = new SnIndexType(id: INDEX_TYPE_ID, indexConfigurationId: INDEX_CONFIGURATION_ID)

		snIndexConfigurationService.getIndexConfigurationForId(INDEX_CONFIGURATION_ID) >> Optional.empty()
		snIndexTypeService.getIndexTypeForId(INDEX_TYPE_ID) >> Optional.of(indexType)

		final SnSearchQuery searchQuery = new SnSearchQuery()
		final SnSearchRequest searchRequest = new DefaultSnSearchRequest(INDEX_TYPE_ID, searchQuery)

		when:
		snSearchContextFactory.createSearchContext(searchRequest)

		then:
		thrown(SnIndexConfigurationNotFoundException)
	}

	@Test
	def "Create context"() {
		given:
		SnIndexConfiguration indexConfiguration = new SnIndexConfiguration(id: INDEX_CONFIGURATION_ID)
		SnIndexType indexType = new SnIndexType(id: INDEX_TYPE_ID, indexConfigurationId: INDEX_CONFIGURATION_ID)

		snIndexConfigurationService.getIndexConfigurationForId(INDEX_CONFIGURATION_ID) >> Optional.of(indexConfiguration)
		snIndexTypeService.getIndexTypeForId(INDEX_TYPE_ID) >> Optional.of(indexType)

		final SnSearchQuery searchQuery = new SnSearchQuery()
		final SnSearchRequest searchRequest = new DefaultSnSearchRequest(INDEX_TYPE_ID, searchQuery)

		when:
		SnSearchContext context = snSearchContextFactory.createSearchContext(searchRequest)

		then:
		context != null
		context.getIndexType() == indexType
		context.getIndexConfiguration() == indexConfiguration
		context.getQualifiers() == [:]
		context.getIndexId() == null
		context.getSearchRequest() == searchRequest
		context.getSearchResponse() == null
	}

	@Test
	def "Create context with qualifiers"() {
		given:
		SnIndexConfiguration indexConfiguration = new SnIndexConfiguration(id: INDEX_CONFIGURATION_ID)
		SnIndexType indexType = new SnIndexType(id: INDEX_TYPE_ID, indexConfigurationId: INDEX_CONFIGURATION_ID)

		snIndexConfigurationService.getIndexConfigurationForId(INDEX_CONFIGURATION_ID) >> Optional.of(indexConfiguration)
		snIndexTypeService.getIndexTypeForId(INDEX_TYPE_ID) >> Optional.of(indexType)

		final SnSearchQuery searchQuery = new SnSearchQuery()
		final SnSearchRequest searchRequest = new DefaultSnSearchRequest(INDEX_TYPE_ID, searchQuery)

		SnQualifierType qualifierType = Mock()
		SnQualifierProvider qualifierProvider = Mock()
		SnQualifier qualifier = Mock()

		snQualifierTypeFactory.getAllQualifierTypes() >> List.of(qualifierType)
		qualifierType.getId() >> QUALIFIER_TYPE_ID
		qualifierType.getQualifierProvider() >> qualifierProvider
		qualifierProvider.getCurrentQualifiers(_) >> List.of(qualifier)

		when:
		SnSearchContext context = snSearchContextFactory.createSearchContext(searchRequest)

		then:
		context != null
		context.getIndexType() == indexType
		context.getIndexConfiguration() == indexConfiguration
		context.getQualifiers() == [
			(QUALIFIER_TYPE_ID): List.of(qualifier)
		]
		context.getIndexId() == null
		context.getSearchRequest() == searchRequest
		context.getSearchResponse() == null
	}

	@Test
	def "Can update context index id"() {
		given:
		SnIndexConfiguration indexConfiguration = new SnIndexConfiguration(id: INDEX_CONFIGURATION_ID)
		SnIndexType indexType = new SnIndexType(id: INDEX_TYPE_ID, indexConfigurationId: INDEX_CONFIGURATION_ID)

		snIndexConfigurationService.getIndexConfigurationForId(INDEX_CONFIGURATION_ID) >> Optional.of(indexConfiguration)
		snIndexTypeService.getIndexTypeForId(INDEX_TYPE_ID) >> Optional.of(indexType)

		final SnSearchQuery searchQuery = new SnSearchQuery()
		final SnSearchRequest searchRequest = new DefaultSnSearchRequest(INDEX_TYPE_ID, searchQuery)

		SnSearchContext context = snSearchContextFactory.createSearchContext(searchRequest)

		when:
		context.setIndexId(INDEX_ID)

		then:
		context != null
		context.getIndexType() == indexType
		context.getIndexConfiguration() == indexConfiguration
		context.getQualifiers() == [:]
		context.getIndexId() == INDEX_ID
		context.getSearchRequest() == searchRequest
		context.getSearchResponse() == null
	}

	@Test
	def "Can update context search response"() {
		given:
		SnIndexConfiguration indexConfiguration = new SnIndexConfiguration(id: INDEX_CONFIGURATION_ID)
		SnIndexType indexType = new SnIndexType(id: INDEX_TYPE_ID, indexConfigurationId: INDEX_CONFIGURATION_ID)

		snIndexConfigurationService.getIndexConfigurationForId(INDEX_CONFIGURATION_ID) >> Optional.of(indexConfiguration)
		snIndexTypeService.getIndexTypeForId(INDEX_TYPE_ID) >> Optional.of(indexType)

		final SnSearchQuery searchQuery = new SnSearchQuery()
		final SnSearchRequest searchRequest = new DefaultSnSearchRequest(INDEX_TYPE_ID, searchQuery)
		SnSearchResponse searchResponse = Mock()

		SnSearchContext context = snSearchContextFactory.createSearchContext(searchRequest)

		when:
		context.setSearchResponse(searchResponse)

		then:
		context != null
		context.getIndexType() == indexType
		context.getIndexConfiguration() == indexConfiguration
		context.getQualifiers() == [:]
		context.getIndexId() == null
		context.getSearchRequest() == searchRequest
		context.getSearchResponse() == searchResponse
	}
}
