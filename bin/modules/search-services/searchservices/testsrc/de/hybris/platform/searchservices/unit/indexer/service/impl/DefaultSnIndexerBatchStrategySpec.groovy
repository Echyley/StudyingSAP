/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.searchservices.unit.indexer.service.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.core.PK
import de.hybris.platform.core.model.ItemModel
import de.hybris.platform.searchservices.admin.data.SnIndexConfiguration
import de.hybris.platform.searchservices.admin.data.SnIndexType
import de.hybris.platform.searchservices.core.SnException
import de.hybris.platform.searchservices.core.service.SnIdentityProvider
import de.hybris.platform.searchservices.core.service.SnListenerFactory
import de.hybris.platform.searchservices.core.service.SnQualifierTypeFactory
import de.hybris.platform.searchservices.core.service.SnSessionService
import de.hybris.platform.searchservices.document.data.SnDocumentBatchOperationResponse
import de.hybris.platform.searchservices.document.data.SnDocumentBatchResponse
import de.hybris.platform.searchservices.enums.SnDocumentOperationStatus
import de.hybris.platform.searchservices.enums.SnDocumentOperationType
import de.hybris.platform.searchservices.enums.SnIndexerOperationStatus
import de.hybris.platform.searchservices.enums.SnIndexerOperationType
import de.hybris.platform.searchservices.indexer.SnIndexerException
import de.hybris.platform.searchservices.indexer.data.SnIndexerOperation
import de.hybris.platform.searchservices.indexer.service.SnIndexerBatchContextFactory
import de.hybris.platform.searchservices.indexer.service.SnIndexerBatchListener
import de.hybris.platform.searchservices.indexer.service.SnIndexerBatchRequest
import de.hybris.platform.searchservices.indexer.service.SnIndexerItemSource
import de.hybris.platform.searchservices.indexer.service.SnIndexerItemSourceOperation
import de.hybris.platform.searchservices.indexer.service.SnIndexerListener
import de.hybris.platform.searchservices.indexer.service.SnIndexerResponse
import de.hybris.platform.searchservices.indexer.service.impl.DefaultSnIndexerBatchContext
import de.hybris.platform.searchservices.indexer.service.impl.DefaultSnIndexerBatchStrategy
import de.hybris.platform.searchservices.spi.service.SnSearchProvider
import de.hybris.platform.searchservices.spi.service.SnSearchProviderFactory
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery
import de.hybris.platform.servicelayer.search.FlexibleSearchService
import de.hybris.platform.servicelayer.search.SearchResult
import de.hybris.platform.testframework.JUnitPlatformSpecification

import org.junit.Test
import org.springframework.context.ApplicationContext


@UnitTest
public class DefaultSnIndexerBatchStrategySpec extends JUnitPlatformSpecification {

	static final String INDEX_CONFIGURATION_ID = "indexConfiguration"
	static final String INDEX_TYPE_ID = "indexType"
	static final String IDENTITY_PROVIDER_ID = "identityProvider"
	static final String INDEX_ID = "index"
	static final String INDEXER_OPERATION_ID = "indexerOperation"
	static final String INDEXER_BATCH_ID = "indexerBatch"

	SnSessionService snSessionService = Mock()
	SnIndexerBatchContextFactory snIndexerBatchContextFactory = Mock()
	SnListenerFactory snListenerFactory = Mock()
	FlexibleSearchService flexibleSearchService = Mock()
	SnQualifierTypeFactory snQualifierTypeFactory = Mock()
	SnSearchProviderFactory snSearchProviderFactory = Mock()
	ApplicationContext applicationContext = Mock()

	SnIdentityProvider identityProvider = Mock()
	SnIndexerItemSource indexerItemSource = Mock()
	SnIndexerItemSourceOperation indexerItemSourceOperation = Mock()
	SnIndexerBatchRequest indexerBatchRequest = Mock()
	SnIndexConfiguration indexConfiguration = Mock()
	SnIndexType indexType = Mock()
	SnSearchProvider<?> searchProvider = Mock()
	SnIndexerOperation indexerOperation = Mock()
	SearchResult flexibleSearchResult = Mock()

	SnIndexerBatchListener listener1 = Mock()
	SnIndexerBatchListener listener2 = Mock()

	PK itemPk = PK.fromLong(1)
	ItemModel item = Mock()

	DefaultSnIndexerBatchContext indexerBatchContext
	DefaultSnIndexerBatchStrategy snIndexerBatchStrategy

	def setup() {
		indexerBatchRequest.getIndexTypeId() >> INDEX_TYPE_ID
		indexerBatchRequest.getIndexId() >> INDEX_ID
		indexerBatchRequest.getIndexerOperationType() >> SnIndexerOperationType.FULL
		indexerBatchRequest.getIndexerItemSourceOperations() >> List.of(indexerItemSourceOperation)
		indexerItemSourceOperation.getDocumentOperationType() >> SnDocumentOperationType.CREATE
		indexerItemSourceOperation.getIndexerItemSource() >> indexerItemSource

		indexConfiguration.getId() >> INDEX_TYPE_ID
		indexType.getId() >> INDEX_TYPE_ID
		indexType.getIdentityProvider() >> IDENTITY_PROVIDER_ID

		indexerBatchContext = new DefaultSnIndexerBatchContext()
		indexerBatchContext.setIndexConfiguration(indexConfiguration)
		indexerBatchContext.setIndexType(indexType)
		indexerBatchContext.setIndexerRequest(indexerBatchRequest)
		indexerBatchContext.setIndexerBatchRequest(indexerBatchRequest)
		indexerBatchContext.setIndexerItemSourceOperations(List.of(indexerItemSourceOperation))
		indexerBatchContext.setIndexId(INDEX_ID)
		indexerBatchContext.setIndexerOperationId(INDEXER_OPERATION_ID)
		indexerBatchContext.setIndexerBatchId(INDEXER_BATCH_ID)

		snIndexerBatchContextFactory.createIndexerBatchContext(indexerBatchRequest) >> indexerBatchContext
		indexerItemSource.getPks(indexerBatchContext) >> List.of(itemPk)
		snSearchProviderFactory.getSearchProviderForContext(indexerBatchContext) >> searchProvider

		flexibleSearchService.search(_ as FlexibleSearchQuery) >> flexibleSearchResult
		flexibleSearchResult.getResult() >> List.of(item)

		applicationContext.getBean(IDENTITY_PROVIDER_ID, SnIdentityProvider.class) >> identityProvider

		snIndexerBatchStrategy = new DefaultSnIndexerBatchStrategy()
		snIndexerBatchStrategy.setSnSessionService(snSessionService)
		snIndexerBatchStrategy.setSnIndexerBatchContextFactory(snIndexerBatchContextFactory)
		snIndexerBatchStrategy.setSnListenerFactory(snListenerFactory)
		snIndexerBatchStrategy.setFlexibleSearchService(flexibleSearchService)
		snIndexerBatchStrategy.setSnSearchProviderFactory(snSearchProviderFactory)
		snIndexerBatchStrategy.setSnQualifierTypeFactory(snQualifierTypeFactory)
		snIndexerBatchStrategy.setApplicationContext(applicationContext)
	}

	@Test
	def "Execute indexer batch strategy with document operation status '#documentOperationStatus' '#testId'"(testId, documentOperationStatus, expectedIndexerOperationStatus) {
		given:
		searchProvider.executeDocumentBatch(indexerBatchContext, INDEX_ID, _, _) >>
				new SnDocumentBatchResponse(responses: List.of(new SnDocumentBatchOperationResponse(status: documentOperationStatus)))

		when:
		SnIndexerResponse indexerResponse = snIndexerBatchStrategy.execute(indexerBatchRequest)

		then:
		indexerResponse.status == expectedIndexerOperationStatus

		where:
		testId | documentOperationStatus           || expectedIndexerOperationStatus
		1      | SnDocumentOperationStatus.CREATED || SnIndexerOperationStatus.COMPLETED
		2      | SnDocumentOperationStatus.UPDATED || SnIndexerOperationStatus.COMPLETED
		3      | SnDocumentOperationStatus.DELETED || SnIndexerOperationStatus.COMPLETED
		4      | SnDocumentOperationStatus.FAILED  || SnIndexerOperationStatus.FAILED
	}

	@Test
	def "Fail to execute indexer batch strategy because exception '#exception' is thrown '#testId'"(testId, exception) {
		given:
		searchProvider.executeDocumentBatch(indexerBatchContext, INDEX_ID, _, _) >> {
			throw exception
		}

		when:
		SnIndexerResponse indexerResponse = snIndexerBatchStrategy.execute(indexerBatchRequest)

		then:
		thrown(SnIndexerException)

		where:
		testId | exception
		1      | new SnException()
		2      | new SnIndexerException()
		3      | new RuntimeException()
	}

	@Test
	def "No listener"() {
		given:
		searchProvider.executeDocumentBatch(indexerBatchContext, INDEX_ID, _, _) >>
				new SnDocumentBatchResponse(responses: List.of(new SnDocumentBatchOperationResponse(status: SnDocumentOperationStatus.CREATED)))

		final List<SnIndexerListener> listeners = List.of()

		snListenerFactory.getListeners(indexerBatchContext, SnIndexerBatchListener) >> listeners

		when:
		snIndexerBatchStrategy.execute(indexerBatchRequest)

		then:
		0 * listener1.beforeIndexBatch(indexerBatchContext)
		0 * listener2.beforeIndexBatch(indexerBatchContext)
		0 * listener2.beforeIndexBatch(indexerBatchContext)
		0 * listener1.beforeIndexBatch(indexerBatchContext)
	}

	@Test
	def "Single listener"() {
		given:
		searchProvider.executeDocumentBatch(indexerBatchContext, INDEX_ID, _, _) >>
				new SnDocumentBatchResponse(responses: List.of(new SnDocumentBatchOperationResponse(status: SnDocumentOperationStatus.CREATED)))

		final List<SnIndexerBatchListener> listeners = List.of(listener1)

		snListenerFactory.getListeners(indexerBatchContext, SnIndexerBatchListener) >> listeners

		when:
		snIndexerBatchStrategy.execute(indexerBatchRequest)

		then:
		1 * listener1.beforeIndexBatch(indexerBatchContext)

		then:
		1 * listener1.afterIndexBatch(indexerBatchContext)

		then:
		0 * listener1.afterIndexBatchError(indexerBatchContext)
	}

	@Test
	def "Single listener exception on before index batch"() {
		given:
		searchProvider.executeDocumentBatch(indexerBatchContext, INDEX_ID, _, _) >>
				new SnDocumentBatchResponse(responses: List.of(new SnDocumentBatchOperationResponse(status: SnDocumentOperationStatus.CREATED)))

		final List<SnIndexerBatchListener> listeners = List.of(listener1)

		snListenerFactory.getListeners(indexerBatchContext, SnIndexerBatchListener) >> listeners

		when:
		snIndexerBatchStrategy.execute(indexerBatchRequest)

		then:
		1 * listener1.beforeIndexBatch(indexerBatchContext) >> {
			throw new RuntimeException()
		}

		then:
		0 * listener1.afterIndexBatch(indexerBatchContext)

		then:
		1 * listener1.afterIndexBatchError(indexerBatchContext)

		then:
		thrown(SnIndexerException)
	}

	@Test
	def "Single listener exception on execute"() {
		given:
		searchProvider.executeDocumentBatch(indexerBatchContext, INDEX_ID, _, _) >>
				new SnDocumentBatchResponse(responses: List.of(new SnDocumentBatchOperationResponse(status: SnDocumentOperationStatus.CREATED)))

		final List<SnIndexerBatchListener> listeners = List.of(listener1)

		snListenerFactory.getListeners(indexerBatchContext, SnIndexerBatchListener) >> listeners
		identityProvider.getIdentifier(indexerBatchContext, item) >> {
			throw new RuntimeException()
		}

		when:
		snIndexerBatchStrategy.execute(indexerBatchRequest)

		then:
		1 * listener1.beforeIndexBatch(indexerBatchContext)

		then:
		0 * listener1.afterIndexBatch(indexerBatchContext)

		then:
		1 * listener1.afterIndexBatchError(indexerBatchContext)

		then:
		thrown(SnIndexerException)
	}

	@Test
	def "Single listener exception on after index batch"() {
		given:
		searchProvider.executeDocumentBatch(indexerBatchContext, INDEX_ID, _, _) >>
				new SnDocumentBatchResponse(responses: List.of(new SnDocumentBatchOperationResponse(status: SnDocumentOperationStatus.CREATED)))

		final List<SnIndexerBatchListener> listeners = List.of(listener1)

		snListenerFactory.getListeners(indexerBatchContext, SnIndexerBatchListener) >> listeners

		when:
		snIndexerBatchStrategy.execute(indexerBatchRequest)

		then:
		1 * listener1.beforeIndexBatch(indexerBatchContext)

		then:
		1 * listener1.afterIndexBatch(indexerBatchContext) >> {
			throw new RuntimeException()
		}

		then:
		1 * listener1.afterIndexBatchError(indexerBatchContext)

		then:
		thrown(SnIndexerException)
	}

	@Test
	def "Multiple listeners"() {
		given:
		searchProvider.executeDocumentBatch(indexerBatchContext, INDEX_ID, _, _) >>
				new SnDocumentBatchResponse(responses: List.of(new SnDocumentBatchOperationResponse(status: SnDocumentOperationStatus.CREATED)))

		final List<SnIndexerBatchListener> listeners = List.of(listener1, listener2)

		snListenerFactory.getListeners(indexerBatchContext, SnIndexerBatchListener) >> listeners

		when:
		snIndexerBatchStrategy.execute(indexerBatchRequest)

		then:
		1 * listener1.beforeIndexBatch(indexerBatchContext)

		then:
		1 * listener2.beforeIndexBatch(indexerBatchContext)

		then:
		1 * listener2.afterIndexBatch(indexerBatchContext)

		then:
		1 * listener1.afterIndexBatch(indexerBatchContext)

		then:
		0 * listener2.afterIndexBatchError(indexerBatchContext)

		then:
		0 * listener1.afterIndexBatchError(indexerBatchContext)
	}

	@Test
	def "Multiple listeners exception on before index batch 1"() {
		given:
		searchProvider.executeDocumentBatch(indexerBatchContext, INDEX_ID, _, _) >>
				new SnDocumentBatchResponse(responses: List.of(new SnDocumentBatchOperationResponse(status: SnDocumentOperationStatus.CREATED)))

		final List<SnIndexerBatchListener> listeners = List.of(listener1, listener2)

		snListenerFactory.getListeners(indexerBatchContext, SnIndexerBatchListener) >> listeners

		when:
		snIndexerBatchStrategy.execute(indexerBatchRequest)

		then:
		1 * listener1.beforeIndexBatch(indexerBatchContext) >> {
			throw new RuntimeException()
		}

		then:
		0 * listener2.beforeIndexBatch(indexerBatchContext)

		then:
		0 * listener2.afterIndexBatch(indexerBatchContext)

		then:
		0 * listener1.afterIndexBatch(indexerBatchContext)

		then:
		1 * listener2.afterIndexBatchError(indexerBatchContext)

		then:
		1 * listener1.afterIndexBatchError(indexerBatchContext)

		then:
		thrown(SnIndexerException)
	}

	@Test
	def "Multiple listeners exception on before index batch 2"() {
		given:
		searchProvider.executeDocumentBatch(indexerBatchContext, INDEX_ID, _, _) >>
				new SnDocumentBatchResponse(responses: List.of(new SnDocumentBatchOperationResponse(status: SnDocumentOperationStatus.CREATED)))

		final List<SnIndexerBatchListener> listeners = List.of(listener1, listener2)

		snListenerFactory.getListeners(indexerBatchContext, SnIndexerBatchListener) >> listeners

		when:
		snIndexerBatchStrategy.execute(indexerBatchRequest)

		then:
		1 * listener1.beforeIndexBatch(indexerBatchContext)

		then:
		1 * listener2.beforeIndexBatch(indexerBatchContext) >> {
			throw new RuntimeException()
		}

		then:
		0 * listener2.afterIndexBatch(indexerBatchContext)

		then:
		0 * listener1.afterIndexBatch(indexerBatchContext)

		then:
		1 * listener2.afterIndexBatchError(indexerBatchContext)

		then:
		1 * listener1.afterIndexBatchError(indexerBatchContext)

		then:
		thrown(SnIndexerException)
	}

	@Test
	def "Multiple listeners exception on execute"() {
		given:
		searchProvider.executeDocumentBatch(indexerBatchContext, INDEX_ID, _, _) >>
				new SnDocumentBatchResponse(responses: List.of(new SnDocumentBatchOperationResponse(status: SnDocumentOperationStatus.CREATED)))

		final List<SnIndexerBatchListener> listeners = List.of(listener1, listener2)

		snListenerFactory.getListeners(indexerBatchContext, SnIndexerBatchListener) >> listeners
		identityProvider.getIdentifier(indexerBatchContext, item) >> {
			throw new RuntimeException()
		}

		when:
		snIndexerBatchStrategy.execute(indexerBatchRequest)

		then:
		1 * listener1.beforeIndexBatch(indexerBatchContext)

		then:
		1 * listener2.beforeIndexBatch(indexerBatchContext)

		then:
		0 * listener2.afterIndexBatch(indexerBatchContext)

		then:
		0 * listener1.afterIndexBatch(indexerBatchContext)

		then:
		1 * listener2.afterIndexBatchError(indexerBatchContext)

		then:
		1 * listener1.afterIndexBatchError(indexerBatchContext)

		then:
		thrown(SnIndexerException)
	}

	@Test
	def "Multiple listeners exception on after index batch 1"() {
		given:
		searchProvider.executeDocumentBatch(indexerBatchContext, INDEX_ID, _, _) >>
				new SnDocumentBatchResponse(responses: List.of(new SnDocumentBatchOperationResponse(status: SnDocumentOperationStatus.CREATED)))

		final List<SnIndexerBatchListener> listeners = List.of(listener1, listener2)

		snListenerFactory.getListeners(indexerBatchContext, SnIndexerBatchListener) >> listeners

		when:
		snIndexerBatchStrategy.execute(indexerBatchRequest)

		then:
		1 * listener1.beforeIndexBatch(indexerBatchContext)

		then:
		1 * listener2.beforeIndexBatch(indexerBatchContext)

		then:
		1 * listener2.afterIndexBatch(indexerBatchContext)

		then:
		1 * listener1.afterIndexBatch(indexerBatchContext) >> {
			throw new RuntimeException()
		}

		then:
		1 * listener2.afterIndexBatchError(indexerBatchContext)

		then:
		1 * listener1.afterIndexBatchError(indexerBatchContext)

		then:
		thrown(SnIndexerException)
	}

	@Test
	def "Multiple listeners exception on after index batch 2"() {
		given:
		searchProvider.executeDocumentBatch(indexerBatchContext, INDEX_ID, _, _) >>
				new SnDocumentBatchResponse(responses: List.of(new SnDocumentBatchOperationResponse(status: SnDocumentOperationStatus.CREATED)))

		final List<SnIndexerBatchListener> listeners = List.of(listener1, listener2)

		snListenerFactory.getListeners(indexerBatchContext, SnIndexerBatchListener) >> listeners

		when:
		snIndexerBatchStrategy.execute(indexerBatchRequest)

		then:
		1 * listener1.beforeIndexBatch(indexerBatchContext)

		then:
		1 * listener2.beforeIndexBatch(indexerBatchContext)

		then:
		1 * listener2.afterIndexBatch(indexerBatchContext) >> {
			throw new RuntimeException()
		}

		then:
		0 * listener1.afterIndexBatch(indexerBatchContext)

		then:
		1 * listener2.afterIndexBatchError(indexerBatchContext)

		then:
		1 * listener1.afterIndexBatchError(indexerBatchContext)

		then:
		thrown(SnIndexerException)
	}

	@Test
	def "Listener has access to indexer batch response"() {
		given:
		SnDocumentBatchResponse documentBatchResponse = new SnDocumentBatchResponse(responses: List.of(new SnDocumentBatchOperationResponse(status: SnDocumentOperationStatus.CREATED)))
		searchProvider.executeDocumentBatch(indexerBatchContext, INDEX_ID, _, _) >> documentBatchResponse

		final List<SnIndexerBatchListener> listeners = List.of(listener1)

		snListenerFactory.getListeners(indexerBatchContext, SnIndexerBatchListener) >> listeners

		when:
		snIndexerBatchStrategy.execute(indexerBatchRequest)

		then:
		1 * listener1.beforeIndexBatch({
			it.indexerRequest == indexerBatchRequest &&
					it.indexerBatchRequest == indexerBatchRequest &&
					it.indexerResponse == null &&
					it.indexerBatchResponse == null &&
					it.indexId == INDEX_ID &&
					it.indexerOperationId == INDEXER_OPERATION_ID &&
					it.indexerBatchId == INDEXER_BATCH_ID
		})

		then:
		1 * listener1.afterIndexBatch({
			it.indexerRequest == indexerBatchRequest &&
					it.indexerBatchRequest == indexerBatchRequest &&
					it.indexerResponse != null &&
					it.indexerBatchResponse != null &&
					it.indexerResponse == it.indexerBatchResponse &&
					it.indexerBatchResponse.documentBatchRequest != null &&
					it.indexerBatchResponse.documentBatchResponse != null &&
					it.indexId == INDEX_ID &&
					it.indexerOperationId == INDEXER_OPERATION_ID &&
					it.indexerBatchId == INDEXER_BATCH_ID
		})

		then:
		0 * listener1.afterIndexBatchError(indexerBatchContext)
	}
}
