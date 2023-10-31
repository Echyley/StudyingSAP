/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.searchservices.unit.indexer.service.impl

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
import de.hybris.platform.searchservices.enums.SnDocumentOperationType
import de.hybris.platform.searchservices.enums.SnIndexerOperationType
import de.hybris.platform.searchservices.indexer.service.SnIndexerContext
import de.hybris.platform.searchservices.indexer.service.SnIndexerItemSource
import de.hybris.platform.searchservices.indexer.service.SnIndexerItemSourceOperation
import de.hybris.platform.searchservices.indexer.service.SnIndexerRequest
import de.hybris.platform.searchservices.indexer.service.SnIndexerResponse
import de.hybris.platform.searchservices.indexer.service.impl.DefaultSnIndexerContextFactory
import de.hybris.platform.searchservices.indexer.service.impl.DefaultSnIndexerItemSourceOperation
import de.hybris.platform.searchservices.indexer.service.impl.DefaultSnIndexerRequest
import de.hybris.platform.searchservices.indexer.service.impl.TypeSnIndexerItemSource
import de.hybris.platform.testframework.JUnitPlatformSpecification

import org.junit.Test


@UnitTest
public class DefaultSnIndexerContextFactorySpec extends JUnitPlatformSpecification {

	static final String INDEX_TYPE_ID = "indexType"
	static final String INDEX_CONFIGURATION_ID = "indexConfiguration"
	static final String QUALIFIER_TYPE_ID = "qualifierType"
	static final String INDEX_ID = "index"
	static final String INDEXER_OPERATION_ID = "indexerOperation"

	SnIndexConfigurationService snIndexConfigurationService = Mock()
	SnIndexTypeService snIndexTypeService = Mock()
	SnQualifierTypeFactory snQualifierTypeFactory = Mock()

	DefaultSnIndexerContextFactory snIndexerContextFactory

	def setup() {
		snIndexerContextFactory = new DefaultSnIndexerContextFactory()
		snIndexerContextFactory.setSnIndexConfigurationService(snIndexConfigurationService)
		snIndexerContextFactory.setSnIndexTypeService(snIndexTypeService)
		snIndexerContextFactory.setSnQualifierTypeFactory(snQualifierTypeFactory)
	}

	@Test
	def "Fail to create context without request"() {
		when:
		snIndexerContextFactory.createIndexerContext(null)

		then:
		thrown(NullPointerException)
	}

	@Test
	def "Fail to create context for request without index type"() {
		given:
		SnIndexerItemSource itemSource = new TypeSnIndexerItemSource()
		SnIndexerItemSourceOperation itemSourceOperation = new DefaultSnIndexerItemSourceOperation(SnDocumentOperationType.CREATE_UPDATE, itemSource)
		List<SnIndexerItemSourceOperation> itemSourceOperations = List.of(itemSourceOperation)
		SnIndexerRequest indexerRequest = new DefaultSnIndexerRequest(null, SnIndexerOperationType.FULL, itemSourceOperations)

		when:
		snIndexerContextFactory.createIndexerContext(indexerRequest)

		then:
		thrown(SnIndexTypeNotFoundException)
	}

	@Test
	def "Fail to create context for not existing index type"() {
		given:
		snIndexTypeService.getIndexTypeForId(INDEX_TYPE_ID) >> Optional.empty();

		SnIndexerItemSource itemSource = new TypeSnIndexerItemSource()
		SnIndexerItemSourceOperation itemSourceOperation = new DefaultSnIndexerItemSourceOperation(SnDocumentOperationType.CREATE_UPDATE, itemSource)
		List<SnIndexerItemSourceOperation> itemSourceOperations = List.of(itemSourceOperation)
		SnIndexerRequest indexerRequest = new DefaultSnIndexerRequest(INDEX_TYPE_ID, SnIndexerOperationType.FULL, itemSourceOperations)

		when:
		snIndexerContextFactory.createIndexerContext(indexerRequest)

		then:
		thrown(SnIndexTypeNotFoundException)
	}

	@Test
	def "Fail to create context for index type without index configuration"() {
		given:
		SnIndexType indexType = new SnIndexType(id: INDEX_TYPE_ID)

		snIndexTypeService.getIndexTypeForId(INDEX_TYPE_ID) >> Optional.of(indexType)

		SnIndexerItemSource itemSource = new TypeSnIndexerItemSource()
		SnIndexerItemSourceOperation itemSourceOperation = new DefaultSnIndexerItemSourceOperation(SnDocumentOperationType.CREATE_UPDATE, itemSource)
		List<SnIndexerItemSourceOperation> itemSourceOperations = List.of(itemSourceOperation)
		SnIndexerRequest indexerRequest = new DefaultSnIndexerRequest(INDEX_TYPE_ID, SnIndexerOperationType.FULL, itemSourceOperations)

		when:
		snIndexerContextFactory.createIndexerContext(indexerRequest)

		then:
		thrown(SnIndexConfigurationNotFoundException)
	}

	@Test
	def "Fail to create context for not existing index configuration"() {
		given:
		SnIndexType indexType = new SnIndexType(id: INDEX_TYPE_ID, indexConfigurationId: INDEX_CONFIGURATION_ID)

		snIndexConfigurationService.getIndexConfigurationForId(INDEX_CONFIGURATION_ID) >> Optional.empty()
		snIndexTypeService.getIndexTypeForId(INDEX_TYPE_ID) >> Optional.of(indexType)

		SnIndexerItemSource itemSource = new TypeSnIndexerItemSource()
		SnIndexerItemSourceOperation itemSourceOperation = new DefaultSnIndexerItemSourceOperation(SnDocumentOperationType.CREATE_UPDATE, itemSource)
		List<SnIndexerItemSourceOperation> itemSourceOperations = List.of(itemSourceOperation)
		SnIndexerRequest indexerRequest = new DefaultSnIndexerRequest(INDEX_TYPE_ID, SnIndexerOperationType.FULL, itemSourceOperations)

		when:
		snIndexerContextFactory.createIndexerContext(indexerRequest)

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

		SnIndexerItemSource itemSource = new TypeSnIndexerItemSource()
		SnIndexerItemSourceOperation itemSourceOperation = new DefaultSnIndexerItemSourceOperation(SnDocumentOperationType.CREATE_UPDATE, itemSource)
		List<SnIndexerItemSourceOperation> itemSourceOperations = List.of(itemSourceOperation)
		SnIndexerRequest indexerRequest = new DefaultSnIndexerRequest(INDEX_TYPE_ID, SnIndexerOperationType.FULL, itemSourceOperations)

		when:
		SnIndexerContext context = snIndexerContextFactory.createIndexerContext(indexerRequest)

		then:
		context != null
		context.getIndexType() == indexType
		context.getIndexConfiguration() == indexConfiguration
		context.getQualifiers() == [:]
		context.getIndexId() == null
		context.getIndexerRequest() == indexerRequest
		context.getIndexerResponse() == null
		context.getIndexerOperationType() == SnIndexerOperationType.FULL
		context.getIndexerItemSourceOperations() == itemSourceOperations
		context.getIndexerOperationId() == null
	}

	@Test
	def "Create context with qualifiers"() {
		given:
		SnIndexConfiguration indexConfiguration = new SnIndexConfiguration(id: INDEX_CONFIGURATION_ID)
		SnIndexType indexType = new SnIndexType(id: INDEX_TYPE_ID, indexConfigurationId: INDEX_CONFIGURATION_ID)

		snIndexConfigurationService.getIndexConfigurationForId(INDEX_CONFIGURATION_ID) >> Optional.of(indexConfiguration)
		snIndexTypeService.getIndexTypeForId(INDEX_TYPE_ID) >> Optional.of(indexType)

		SnIndexerItemSource itemSource = new TypeSnIndexerItemSource()
		SnIndexerItemSourceOperation itemSourceOperation = new DefaultSnIndexerItemSourceOperation(SnDocumentOperationType.CREATE_UPDATE, itemSource)
		List<SnIndexerItemSourceOperation> itemSourceOperations = List.of(itemSourceOperation)
		SnIndexerRequest indexerRequest = new DefaultSnIndexerRequest(INDEX_TYPE_ID, SnIndexerOperationType.FULL, itemSourceOperations)

		SnQualifierType qualifierType = Mock()
		SnQualifierProvider qualifierProvider = Mock()
		SnQualifier qualifier = Mock()

		snQualifierTypeFactory.getAllQualifierTypes() >> List.of(qualifierType)
		qualifierType.getId() >> QUALIFIER_TYPE_ID
		qualifierType.getQualifierProvider() >> qualifierProvider
		qualifierProvider.getCurrentQualifiers(_) >> List.of(qualifier)

		when:
		SnIndexerContext context = snIndexerContextFactory.createIndexerContext(indexerRequest)

		then:
		context != null
		context.getIndexType() == indexType
		context.getIndexConfiguration() == indexConfiguration
		context.getQualifiers() == [
			(QUALIFIER_TYPE_ID): List.of(qualifier)
		]
		context.getIndexId() == null
		context.getIndexerRequest() == indexerRequest
		context.getIndexerResponse() == null
		context.getIndexerOperationType() == SnIndexerOperationType.FULL
		context.getIndexerItemSourceOperations() == itemSourceOperations
		context.getIndexerOperationId() == null
	}

	@Test
	def "Can update context index id"() {
		given:
		SnIndexConfiguration indexConfiguration = new SnIndexConfiguration(id: INDEX_CONFIGURATION_ID)
		SnIndexType indexType = new SnIndexType(id: INDEX_TYPE_ID, indexConfigurationId: INDEX_CONFIGURATION_ID)

		snIndexConfigurationService.getIndexConfigurationForId(INDEX_CONFIGURATION_ID) >> Optional.of(indexConfiguration)
		snIndexTypeService.getIndexTypeForId(INDEX_TYPE_ID) >> Optional.of(indexType)

		SnIndexerItemSource itemSource = new TypeSnIndexerItemSource()
		SnIndexerItemSourceOperation itemSourceOperation = new DefaultSnIndexerItemSourceOperation(SnDocumentOperationType.CREATE_UPDATE, itemSource)
		List<SnIndexerItemSourceOperation> itemSourceOperations = List.of(itemSourceOperation)
		SnIndexerRequest indexerRequest = new DefaultSnIndexerRequest(INDEX_TYPE_ID, SnIndexerOperationType.FULL, itemSourceOperations)

		SnIndexerContext context = snIndexerContextFactory.createIndexerContext(indexerRequest)

		when:
		context.setIndexId(INDEX_ID)

		then:
		context != null
		context.getIndexType() == indexType
		context.getIndexConfiguration() == indexConfiguration
		context.getQualifiers() == [:]
		context.getIndexId() == INDEX_ID
		context.getIndexerRequest() == indexerRequest
		context.getIndexerResponse() == null
		context.getIndexerOperationType() == SnIndexerOperationType.FULL
		context.getIndexerItemSourceOperations() == itemSourceOperations
		context.getIndexerOperationId() == null
	}

	@Test
	def "Can update context indexer operation id"() {
		given:
		SnIndexConfiguration indexConfiguration = new SnIndexConfiguration(id: INDEX_CONFIGURATION_ID)
		SnIndexType indexType = new SnIndexType(id: INDEX_TYPE_ID, indexConfigurationId: INDEX_CONFIGURATION_ID)

		snIndexConfigurationService.getIndexConfigurationForId(INDEX_CONFIGURATION_ID) >> Optional.of(indexConfiguration)
		snIndexTypeService.getIndexTypeForId(INDEX_TYPE_ID) >> Optional.of(indexType)

		SnIndexerItemSource itemSource = new TypeSnIndexerItemSource()
		SnIndexerItemSourceOperation itemSourceOperation = new DefaultSnIndexerItemSourceOperation(SnDocumentOperationType.CREATE_UPDATE, itemSource)
		List<SnIndexerItemSourceOperation> itemSourceOperations = List.of(itemSourceOperation)
		SnIndexerRequest indexerRequest = new DefaultSnIndexerRequest(INDEX_TYPE_ID, SnIndexerOperationType.FULL, itemSourceOperations)

		SnIndexerContext context = snIndexerContextFactory.createIndexerContext(indexerRequest)

		when:
		context.setIndexerOperationId(INDEXER_OPERATION_ID)

		then:
		context != null
		context.getIndexType() == indexType
		context.getIndexConfiguration() == indexConfiguration
		context.getQualifiers() == [:]
		context.getIndexId() == null
		context.getIndexerRequest() == indexerRequest
		context.getIndexerResponse() == null
		context.getIndexerOperationType() == SnIndexerOperationType.FULL
		context.getIndexerItemSourceOperations() == itemSourceOperations
		context.getIndexerOperationId() == INDEXER_OPERATION_ID
	}

	@Test
	def "Can update context indexer response"() {
		given:
		SnIndexConfiguration indexConfiguration = new SnIndexConfiguration(id: INDEX_CONFIGURATION_ID)
		SnIndexType indexType = new SnIndexType(id: INDEX_TYPE_ID, indexConfigurationId: INDEX_CONFIGURATION_ID)

		snIndexConfigurationService.getIndexConfigurationForId(INDEX_CONFIGURATION_ID) >> Optional.of(indexConfiguration)
		snIndexTypeService.getIndexTypeForId(INDEX_TYPE_ID) >> Optional.of(indexType)

		SnIndexerItemSource itemSource = new TypeSnIndexerItemSource()
		SnIndexerItemSourceOperation itemSourceOperation = new DefaultSnIndexerItemSourceOperation(SnDocumentOperationType.CREATE_UPDATE, itemSource)
		List<SnIndexerItemSourceOperation> itemSourceOperations = List.of(itemSourceOperation)
		SnIndexerRequest indexerRequest = new DefaultSnIndexerRequest(INDEX_TYPE_ID, SnIndexerOperationType.FULL, itemSourceOperations)
		SnIndexerResponse indexerResponse = Mock()

		SnIndexerContext context = snIndexerContextFactory.createIndexerContext(indexerRequest)

		when:
		context.setIndexerResponse(indexerResponse)

		then:
		context != null
		context.getIndexType() == indexType
		context.getIndexConfiguration() == indexConfiguration
		context.getQualifiers() == [:]
		context.getIndexId() == null
		context.getIndexerRequest() == indexerRequest
		context.getIndexerResponse() == indexerResponse
		context.getIndexerOperationType() == SnIndexerOperationType.FULL
		context.getIndexerItemSourceOperations() == itemSourceOperations
		context.getIndexerOperationId() == null
	}
}
