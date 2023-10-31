/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.searchservices.unit.indexer.service.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.core.PK
import de.hybris.platform.searchservices.admin.data.SnIndexConfiguration
import de.hybris.platform.searchservices.admin.data.SnIndexType
import de.hybris.platform.searchservices.admin.service.SnCommonConfigurationService
import de.hybris.platform.searchservices.core.service.SnContext
import de.hybris.platform.searchservices.core.service.SnListenerFactory
import de.hybris.platform.searchservices.core.service.SnSessionService
import de.hybris.platform.searchservices.enums.SnDocumentOperationType
import de.hybris.platform.searchservices.enums.SnIndexerOperationStatus
import de.hybris.platform.searchservices.enums.SnIndexerOperationType
import de.hybris.platform.searchservices.index.service.SnIndexService
import de.hybris.platform.searchservices.indexer.SnIndexerException
import de.hybris.platform.searchservices.indexer.data.SnIndexerOperation
import de.hybris.platform.searchservices.indexer.service.SnIndexerBatchCallable
import de.hybris.platform.searchservices.indexer.service.SnIndexerBatchResponse
import de.hybris.platform.searchservices.indexer.service.SnIndexerContext
import de.hybris.platform.searchservices.indexer.service.SnIndexerContextFactory
import de.hybris.platform.searchservices.indexer.service.SnIndexerItemSource
import de.hybris.platform.searchservices.indexer.service.SnIndexerItemSourceOperation
import de.hybris.platform.searchservices.indexer.service.SnIndexerListener
import de.hybris.platform.searchservices.indexer.service.SnIndexerRequest
import de.hybris.platform.searchservices.indexer.service.SnIndexerResponse
import de.hybris.platform.searchservices.indexer.service.impl.DefaultSnIndexerContext
import de.hybris.platform.searchservices.indexer.service.impl.DefaultSnIndexerStrategy
import de.hybris.platform.searchservices.spi.service.SnSearchProvider
import de.hybris.platform.searchservices.spi.service.SnSearchProviderFactory
import de.hybris.platform.testframework.JUnitPlatformSpecification

import org.junit.Test
import org.springframework.context.ApplicationContext


@UnitTest
public class DefaultSnIndexerStrategySpec extends JUnitPlatformSpecification {

	static final String INDEX_CONFIGURATION_ID = "indexConfiguration"
	static final String INDEX_TYPE_ID = "indexType"
	static final String INDEX_ID = "index"
	static final String INDEXER_OPERATION_ID = "indexerOperation"

	static final String INDEXER_BATCH_CALLABLE_ID = "indexerBatchCallable"

	SnCommonConfigurationService snCommonConfigurationService = Mock()
	SnSessionService snSessionService = Mock()
	SnIndexerContextFactory snIndexerContextFactory = Mock()
	SnListenerFactory snListenerFactory = Mock()
	SnIndexService snIndexService = Mock()
	SnSearchProviderFactory snSearchProviderFactory = Mock()
	ApplicationContext applicationContext = Mock()

	SnIndexerItemSource indexerItemSource = Mock()
	SnIndexerItemSourceOperation indexerItemSourceOperation = Mock()
	SnIndexerRequest indexerRequest = Mock()
	SnIndexConfiguration indexConfiguration = Mock()
	SnIndexType indexType = Mock()
	SnSearchProvider<?> searchProvider = Mock()
	SnIndexerOperation indexerOperation = Mock()
	SnIndexerBatchResponse indexerBatchResponse = Mock()

	SnIndexerListener listener1 = Mock()
	SnIndexerListener listener2 = Mock()

	DefaultSnIndexerContext indexerContext
	DefaultSnIndexerStrategy snIndexerStrategy

	def setup() {
		indexerRequest.getIndexerOperationType() >> SnIndexerOperationType.FULL
		indexerRequest.getIndexerItemSourceOperations() >> List.of(indexerItemSourceOperation)
		indexerRequest.getIndexTypeId() >> INDEX_TYPE_ID
		indexerItemSourceOperation.getDocumentOperationType() >> SnDocumentOperationType.CREATE
		indexerItemSourceOperation.getIndexerItemSource() >> indexerItemSource
		indexConfiguration.getId() >> INDEX_TYPE_ID
		indexType.getId() >> INDEX_TYPE_ID

		indexerContext = new DefaultSnIndexerContext()
		indexerContext.setIndexConfiguration(indexConfiguration)
		indexerContext.setIndexType(indexType)
		indexerContext.setIndexerRequest(indexerRequest)
		indexerContext.setIndexerItemSourceOperations(List.of(indexerItemSourceOperation))

		snIndexerContextFactory.createIndexerContext(indexerRequest) >> indexerContext
		snIndexService.getDefaultIndexId(INDEX_TYPE_ID) >> INDEX_TYPE_ID
		indexerItemSource.getPks(indexerContext) >> List.of(PK.fromLong(1))
		snSearchProviderFactory.getSearchProviderForContext(indexerContext) >> searchProvider

		indexerOperation.getId() >> INDEXER_OPERATION_ID
		indexerOperation.getIndexTypeId() >> INDEX_TYPE_ID
		indexerOperation.getIndexId() >> INDEX_ID
		indexerOperation.getOperationType() >> SnIndexerOperationType.FULL
		indexerOperation.getStatus() >> SnIndexerOperationStatus.RUNNING

		searchProvider.createIndexerOperation(indexerContext, SnIndexerOperationType.FULL, 1) >> indexerOperation

		applicationContext.getBean(INDEXER_BATCH_CALLABLE_ID, SnIndexerBatchCallable.class) >> new TestSnIndexerBatchCallable({
			return indexerBatchResponse
		})

		snIndexerStrategy = new DefaultSnIndexerStrategy()
		snIndexerStrategy.setSnCommonConfigurationService(snCommonConfigurationService)
		snIndexerStrategy.setSnSessionService(snSessionService)
		snIndexerStrategy.setSnIndexerContextFactory(snIndexerContextFactory)
		snIndexerStrategy.setSnListenerFactory(snListenerFactory)
		snIndexerStrategy.setSnIndexService(snIndexService)
		snIndexerStrategy.setSnSearchProviderFactory(snSearchProviderFactory)
		snIndexerStrategy.setIndexerBatchCallableId(INDEXER_BATCH_CALLABLE_ID)
		snIndexerStrategy.setApplicationContext(applicationContext)
	}

	@Test
	def "Execute indexer strategy with indexer batch batch response '#indexerBatchOperationStatus' '#testId'"(testId, indexerBatchOperationStatus, expectedIndexerOperationStatus) {
		given:
		indexerBatchResponse.getStatus() >> indexerBatchOperationStatus

		when:
		SnIndexerResponse indexerResponse = snIndexerStrategy.execute(indexerRequest)

		then:
		indexerResponse.status == expectedIndexerOperationStatus

		where:
		testId | indexerBatchOperationStatus        || expectedIndexerOperationStatus
		1      | SnIndexerOperationStatus.COMPLETED || SnIndexerOperationStatus.COMPLETED
		2      | SnIndexerOperationStatus.RUNNING   || SnIndexerOperationStatus.FAILED
		3      | SnIndexerOperationStatus.ABORTED   || SnIndexerOperationStatus.ABORTED
		4      | SnIndexerOperationStatus.FAILED    || SnIndexerOperationStatus.FAILED
	}

	@Test
	def "Fail to execute indexer strategy because exception '#exception' is thrown '#testId'"(testId, exception) {
		given:
		applicationContext.getBean(INDEXER_BATCH_CALLABLE_ID, SnIndexerBatchCallable.class) >> new TestSnIndexerBatchCallable({
			throw exception
		})

		when:
		SnIndexerResponse indexerResponse = snIndexerStrategy.execute(indexerRequest)

		then:
		indexerResponse.status == SnIndexerOperationStatus.FAILED

		where:
		testId | exception
		1      | new SnIndexerException()
		2      | new RuntimeException()
	}

	@Test
	def "No listener"() {
		given:
		final List<SnIndexerListener> listeners = List.of()

		snListenerFactory.getListeners(indexerContext, SnIndexerListener) >> listeners

		when:
		snIndexerStrategy.execute(indexerRequest)

		then:
		0 * listener1.beforeIndex(indexerContext)
		0 * listener2.beforeIndex(indexerContext)
		0 * listener2.beforeIndex(indexerContext)
		0 * listener1.beforeIndex(indexerContext)
	}

	@Test
	def "Single listener"() {
		given:
		final List<SnIndexerListener> listeners = List.of(listener1)

		snListenerFactory.getListeners(indexerContext, SnIndexerListener) >> listeners

		when:
		snIndexerStrategy.execute(indexerRequest)

		then:
		1 * listener1.beforeIndex(indexerContext)

		then:
		1 * listener1.afterIndex(indexerContext)

		then:
		0 * listener1.afterIndexError(indexerContext)
	}

	@Test
	def "Single listener exception on before index"() {
		given:
		final List<SnIndexerListener> listeners = List.of(listener1)

		snListenerFactory.getListeners(indexerContext, SnIndexerListener) >> listeners

		when:
		snIndexerStrategy.execute(indexerRequest)

		then:
		1 * listener1.beforeIndex(indexerContext) >> {
			throw new RuntimeException()
		}

		then:
		0 * listener1.afterIndex(indexerContext)

		then:
		1 * listener1.afterIndexError(indexerContext)

		then:
		thrown(SnIndexerException)
	}

	@Test
	def "Single listener exception on execute"() {
		given:
		final List<SnIndexerListener> listeners = List.of(listener1)

		snListenerFactory.getListeners(indexerContext, SnIndexerListener) >> listeners
		searchProvider.commit(indexerContext as SnContext, INDEX_ID) >> {
			throw new RuntimeException()
		}

		when:
		snIndexerStrategy.execute(indexerRequest)

		then:
		1 * listener1.beforeIndex(indexerContext)

		then:
		0 * listener1.afterIndex(indexerContext)

		then:
		1 * listener1.afterIndexError(indexerContext)

		then:
		thrown(SnIndexerException)
	}

	@Test
	def "Single listener exception on after index"() {
		given:
		final List<SnIndexerListener> listeners = List.of(listener1)

		snListenerFactory.getListeners(indexerContext, SnIndexerListener) >> listeners

		when:
		snIndexerStrategy.execute(indexerRequest)

		then:
		1 * listener1.beforeIndex(indexerContext)

		then:
		1 * listener1.afterIndex(indexerContext) >> {
			throw new RuntimeException()
		}

		then:
		1 * listener1.afterIndexError(indexerContext)

		then:
		thrown(SnIndexerException)
	}

	@Test
	def "Multiple listeners"() {
		given:
		final List<SnIndexerListener> listeners = List.of(listener1, listener2)

		snListenerFactory.getListeners(indexerContext, SnIndexerListener) >> listeners

		when:
		snIndexerStrategy.execute(indexerRequest)

		then:
		1 * listener1.beforeIndex(indexerContext)

		then:
		1 * listener2.beforeIndex(indexerContext)

		then:
		1 * listener2.afterIndex(indexerContext)

		then:
		1 * listener1.afterIndex(indexerContext)

		then:
		0 * listener2.afterIndexError(indexerContext)

		then:
		0 * listener1.afterIndexError(indexerContext)
	}

	@Test
	def "Multiple listeners exception on before index 1"() {
		given:
		final List<SnIndexerListener> listeners = List.of(listener1, listener2)

		snListenerFactory.getListeners(indexerContext, SnIndexerListener) >> listeners

		when:
		snIndexerStrategy.execute(indexerRequest)

		then:
		1 * listener1.beforeIndex(indexerContext) >> {
			throw new RuntimeException()
		}

		then:
		0 * listener2.beforeIndex(indexerContext)

		then:
		0 * listener2.afterIndex(indexerContext)

		then:
		0 * listener1.afterIndex(indexerContext)

		then:
		1 * listener2.afterIndexError(indexerContext)

		then:
		1 * listener1.afterIndexError(indexerContext)

		then:
		thrown(SnIndexerException)
	}

	@Test
	def "Multiple listeners exception on before index 2"() {
		given:
		final List<SnIndexerListener> listeners = List.of(listener1, listener2)

		snListenerFactory.getListeners(indexerContext, SnIndexerListener) >> listeners

		when:
		snIndexerStrategy.execute(indexerRequest)

		then:
		1 * listener1.beforeIndex(indexerContext)

		then:
		1 * listener2.beforeIndex(indexerContext) >> {
			throw new RuntimeException()
		}

		then:
		0 * listener2.afterIndex(indexerContext)

		then:
		0 * listener1.afterIndex(indexerContext)

		then:
		1 * listener2.afterIndexError(indexerContext)

		then:
		1 * listener1.afterIndexError(indexerContext)

		then:
		thrown(SnIndexerException)
	}

	@Test
	def "Multiple listeners exception on execute"() {
		given:
		final List<SnIndexerListener> listeners = List.of(listener1, listener2)

		snListenerFactory.getListeners(indexerContext, SnIndexerListener) >> listeners
		searchProvider.commit(indexerContext as SnContext, INDEX_ID) >> {
			throw new RuntimeException()
		}

		when:
		snIndexerStrategy.execute(indexerRequest)

		then:
		1 * listener1.beforeIndex(indexerContext)

		then:
		1 * listener2.beforeIndex(indexerContext)

		then:
		0 * listener2.afterIndex(indexerContext)

		then:
		0 * listener1.afterIndex(indexerContext)

		then:
		1 * listener2.afterIndexError(indexerContext)

		then:
		1 * listener1.afterIndexError(indexerContext)

		then:
		thrown(SnIndexerException)
	}

	@Test
	def "Multiple listeners exception on after index 1"() {
		given:
		final List<SnIndexerListener> listeners = List.of(listener1, listener2)

		snListenerFactory.getListeners(indexerContext, SnIndexerListener) >> listeners

		when:
		snIndexerStrategy.execute(indexerRequest)

		then:
		1 * listener1.beforeIndex(indexerContext)

		then:
		1 * listener2.beforeIndex(indexerContext)

		then:
		1 * listener2.afterIndex(indexerContext)

		then:
		1 * listener1.afterIndex(indexerContext) >> {
			throw new RuntimeException()
		}

		then:
		1 * listener2.afterIndexError(indexerContext)

		then:
		1 * listener1.afterIndexError(indexerContext)

		then:
		thrown(SnIndexerException)
	}

	@Test
	def "Multiple listeners exception on after index 2"() {
		given:
		final List<SnIndexerListener> listeners = List.of(listener1, listener2)

		snListenerFactory.getListeners(indexerContext, SnIndexerListener) >> listeners

		when:
		snIndexerStrategy.execute(indexerRequest)

		then:
		1 * listener1.beforeIndex(indexerContext)

		then:
		1 * listener2.beforeIndex(indexerContext)

		then:
		1 * listener2.afterIndex(indexerContext) >> {
			throw new RuntimeException()
		}

		then:
		0 * listener1.afterIndex(indexerContext)

		then:
		1 * listener2.afterIndexError(indexerContext)

		then:
		1 * listener1.afterIndexError(indexerContext)

		then:
		thrown(SnIndexerException)
	}

	@Test
	def "Listener has access to indexer response"() {
		given:
		final List<SnIndexerListener> listeners = List.of(listener1)

		snListenerFactory.getListeners(indexerContext, SnIndexerListener) >> listeners

		when:
		snIndexerStrategy.execute(indexerRequest)

		then:
		1 * listener1.beforeIndex({
			it.indexerRequest == indexerRequest &&
					it.indexerResponse == null &&
					it.indexId == INDEX_ID &&
					it.indexerOperationId == INDEXER_OPERATION_ID
		})

		then:
		1 * listener1.afterIndex({
			it.indexerRequest == indexerRequest &&
					it.indexerResponse != null &&
					it.indexId == INDEX_ID &&
					it.indexerOperationId == INDEXER_OPERATION_ID
		})

		then:
		0 * listener1.afterIndexError(indexerContext)
	}

	static class TestSnIndexerBatchCallable implements SnIndexerBatchCallable {

		def closure
		String indexerBatchId

		TestSnIndexerBatchCallable(def closure) {
			this.closure = closure
		}

		@Override
		public SnIndexerBatchResponse call() throws Exception {
			return closure()
		}

		@Override
		public void initialize(final SnIndexerContext indexerContext,
				final List<SnIndexerItemSourceOperation> indexerItemSourceOperations, final String indexerBatchId) {
			this.indexerBatchId = indexerBatchId
		}

		@Override
		public String getIndexerBatchId() {
			return indexerBatchId
		}
	}
}
