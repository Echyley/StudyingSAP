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
import de.hybris.platform.searchservices.indexer.service.SnIndexerBatchContext
import de.hybris.platform.searchservices.indexer.service.SnIndexerBatchRequest
import de.hybris.platform.searchservices.indexer.service.SnIndexerBatchResponse
import de.hybris.platform.searchservices.indexer.service.SnIndexerItemSource
import de.hybris.platform.searchservices.indexer.service.SnIndexerItemSourceOperation
import de.hybris.platform.searchservices.indexer.service.impl.DefaultSnIndexerBatchContextFactory
import de.hybris.platform.searchservices.indexer.service.impl.DefaultSnIndexerBatchRequest
import de.hybris.platform.searchservices.indexer.service.impl.DefaultSnIndexerItemSourceOperation
import de.hybris.platform.searchservices.indexer.service.impl.TypeSnIndexerItemSource
import de.hybris.platform.testframework.JUnitPlatformSpecification

import org.junit.Test


@UnitTest
public class DefaultSnIndexerBatchContextFactorySpec extends JUnitPlatformSpecification {

	static final String INDEX_TYPE_ID = "indexType"
	static final String INDEX_CONFIGURATION_ID = "indexConfiguration"
	static final String QUALIFIER_TYPE_ID = "qualifierType"
	static final String INDEX_ID = "index"
	static final String INDEXER_OPERATION_ID = "indexerOperation"
	static final String INDEXER_BATCH_ID = "indexerBatch"

	SnIndexConfigurationService snIndexConfigurationService = Mock()
	SnIndexTypeService snIndexTypeService = Mock()
	SnQualifierTypeFactory snQualifierTypeFactory = Mock()

	DefaultSnIndexerBatchContextFactory snIndexerBatchContextFactory

	def setup() {
		snIndexerBatchContextFactory = new DefaultSnIndexerBatchContextFactory()
		snIndexerBatchContextFactory.setSnIndexConfigurationService(snIndexConfigurationService)
		snIndexerBatchContextFactory.setSnIndexTypeService(snIndexTypeService)
		snIndexerBatchContextFactory.setSnQualifierTypeFactory(snQualifierTypeFactory)
	}

	@Test
	def "Fail to create context without request"() {
		when:
		snIndexerBatchContextFactory.createIndexerBatchContext(null)

		then:
		thrown(NullPointerException)
	}

	@Test
	def "Fail to create context for request without index type"() {
		given:
		SnIndexerItemSource itemSource = new TypeSnIndexerItemSource()
		SnIndexerItemSourceOperation itemSourceOperation = new DefaultSnIndexerItemSourceOperation(SnDocumentOperationType.CREATE_UPDATE, itemSource)
		List<SnIndexerItemSourceOperation> itemSourceOperations = List.of(itemSourceOperation)
		SnIndexerBatchRequest indexerBatchRequest = new DefaultSnIndexerBatchRequest(null, INDEX_ID, SnIndexerOperationType.FULL, itemSourceOperations, INDEXER_OPERATION_ID, INDEXER_BATCH_ID)

		when:
		snIndexerBatchContextFactory.createIndexerBatchContext(indexerBatchRequest)

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
		SnIndexerBatchRequest indexerBatchRequest = new DefaultSnIndexerBatchRequest(INDEX_TYPE_ID, INDEX_ID, SnIndexerOperationType.FULL, itemSourceOperations, INDEXER_OPERATION_ID, INDEXER_BATCH_ID)

		when:
		snIndexerBatchContextFactory.createIndexerBatchContext(indexerBatchRequest)

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
		SnIndexerBatchRequest indexerBatchRequest = new DefaultSnIndexerBatchRequest(INDEX_TYPE_ID, INDEX_ID, SnIndexerOperationType.FULL, itemSourceOperations, INDEXER_OPERATION_ID, INDEXER_BATCH_ID)

		when:
		snIndexerBatchContextFactory.createIndexerBatchContext(indexerBatchRequest)

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
		SnIndexerBatchRequest indexerBatchRequest = new DefaultSnIndexerBatchRequest(INDEX_TYPE_ID, INDEX_ID, SnIndexerOperationType.FULL, itemSourceOperations, INDEXER_OPERATION_ID, INDEXER_BATCH_ID)

		when:
		snIndexerBatchContextFactory.createIndexerBatchContext(indexerBatchRequest)

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
		SnIndexerBatchRequest indexerBatchRequest = new DefaultSnIndexerBatchRequest(INDEX_TYPE_ID, INDEX_ID, SnIndexerOperationType.FULL, itemSourceOperations, INDEXER_OPERATION_ID, INDEXER_BATCH_ID)

		when:
		SnIndexerBatchContext context = snIndexerBatchContextFactory.createIndexerBatchContext(indexerBatchRequest)

		then:
		context != null
		context.getIndexType() == indexType
		context.getIndexConfiguration() == indexConfiguration
		context.getQualifiers() == [:]
		context.getIndexId() == INDEX_ID
		context.getIndexerRequest() == indexerBatchRequest
		context.getIndexerBatchRequest() == indexerBatchRequest
		context.getIndexerResponse() == null
		context.getIndexerBatchResponse() == null
		context.getIndexerOperationType() == SnIndexerOperationType.FULL
		context.getIndexerItemSourceOperations() == itemSourceOperations
		context.getIndexerOperationId() == INDEXER_OPERATION_ID
		context.getIndexerBatchId() == INDEXER_BATCH_ID
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
		SnIndexerBatchRequest indexerBatchRequest = new DefaultSnIndexerBatchRequest(INDEX_TYPE_ID, INDEX_ID, SnIndexerOperationType.FULL, itemSourceOperations, INDEXER_OPERATION_ID, INDEXER_BATCH_ID)

		SnQualifierType qualifierType = Mock()
		SnQualifierProvider qualifierProvider = Mock()
		SnQualifier qualifier = Mock()

		snQualifierTypeFactory.getAllQualifierTypes() >> List.of(qualifierType)
		qualifierType.getId() >> QUALIFIER_TYPE_ID
		qualifierType.getQualifierProvider() >> qualifierProvider
		qualifierProvider.getCurrentQualifiers(_) >> List.of(qualifier)

		when:
		SnIndexerBatchContext context = snIndexerBatchContextFactory.createIndexerBatchContext(indexerBatchRequest)

		then:
		context != null
		context.getIndexType() == indexType
		context.getIndexConfiguration() == indexConfiguration
		context.getQualifiers() == [
			(QUALIFIER_TYPE_ID): List.of(qualifier)
		]
		context.getIndexId() == INDEX_ID
		context.getIndexerRequest() == indexerBatchRequest
		context.getIndexerBatchRequest() == indexerBatchRequest
		context.getIndexerResponse() == null
		context.getIndexerBatchResponse() == null
		context.getIndexerOperationType() == SnIndexerOperationType.FULL
		context.getIndexerItemSourceOperations() == itemSourceOperations
		context.getIndexerOperationId() == INDEXER_OPERATION_ID
		context.getIndexerBatchId() == INDEXER_BATCH_ID
	}

	@Test
	def "Can update context indexer batch response"() {
		given:
		SnIndexConfiguration indexConfiguration = new SnIndexConfiguration(id: INDEX_CONFIGURATION_ID)
		SnIndexType indexType = new SnIndexType(id: INDEX_TYPE_ID, indexConfigurationId: INDEX_CONFIGURATION_ID)

		snIndexConfigurationService.getIndexConfigurationForId(INDEX_CONFIGURATION_ID) >> Optional.of(indexConfiguration)
		snIndexTypeService.getIndexTypeForId(INDEX_TYPE_ID) >> Optional.of(indexType)

		SnIndexerItemSource itemSource = new TypeSnIndexerItemSource()
		SnIndexerItemSourceOperation itemSourceOperation = new DefaultSnIndexerItemSourceOperation(SnDocumentOperationType.CREATE_UPDATE, itemSource)
		List<SnIndexerItemSourceOperation> itemSourceOperations = List.of(itemSourceOperation)
		SnIndexerBatchRequest indexerBatchRequest = new DefaultSnIndexerBatchRequest(INDEX_TYPE_ID, INDEX_ID, SnIndexerOperationType.FULL, itemSourceOperations, INDEXER_OPERATION_ID, INDEXER_BATCH_ID)
		SnIndexerBatchResponse indexerBatchResponse = Mock()

		SnIndexerBatchContext context = snIndexerBatchContextFactory.createIndexerBatchContext(indexerBatchRequest)

		when:
		context.setIndexerResponse(indexerBatchResponse)
		context.setIndexerBatchResponse(indexerBatchResponse)

		then:
		context != null
		context.getIndexType() == indexType
		context.getIndexConfiguration() == indexConfiguration
		context.getQualifiers() == [:]
		context.getIndexId() == INDEX_ID
		context.getIndexerRequest() == indexerBatchRequest
		context.getIndexerBatchRequest() == indexerBatchRequest
		context.getIndexerResponse() == indexerBatchResponse
		context.getIndexerBatchResponse() == indexerBatchResponse
		context.getIndexerOperationType() == SnIndexerOperationType.FULL
		context.getIndexerItemSourceOperations() == itemSourceOperations
		context.getIndexerOperationId() == INDEXER_OPERATION_ID
		context.getIndexerBatchId() == INDEXER_BATCH_ID
	}
}
