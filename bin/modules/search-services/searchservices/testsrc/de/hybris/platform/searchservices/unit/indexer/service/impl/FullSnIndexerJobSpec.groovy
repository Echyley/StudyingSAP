/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.searchservices.unit.indexer.service.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.cronjob.enums.CronJobResult
import de.hybris.platform.cronjob.enums.CronJobStatus
import de.hybris.platform.searchservices.core.service.SnCloseableProgressTracker
import de.hybris.platform.searchservices.core.service.SnCronJobProgressTrackerFactory
import de.hybris.platform.searchservices.enums.SnIndexerOperationStatus
import de.hybris.platform.searchservices.indexer.SnIndexerException
import de.hybris.platform.searchservices.indexer.dao.SnIndexerCronJobDao
import de.hybris.platform.searchservices.indexer.service.SnIndexerItemSource
import de.hybris.platform.searchservices.indexer.service.SnIndexerRequest
import de.hybris.platform.searchservices.indexer.service.SnIndexerResponse
import de.hybris.platform.searchservices.indexer.service.SnIndexerService
import de.hybris.platform.searchservices.indexer.service.impl.FullSnIndexerJob
import de.hybris.platform.searchservices.model.AbstractSnIndexerItemSourceModel
import de.hybris.platform.searchservices.model.FullSnIndexerCronJobModel
import de.hybris.platform.searchservices.model.SnIndexTypeModel
import de.hybris.platform.servicelayer.cronjob.PerformResult
import de.hybris.platform.servicelayer.model.ModelService
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test


@UnitTest
public class FullSnIndexerJobSpec extends JUnitPlatformSpecification {

	static final String INDEX_TYPE_ID = "indexType"

	ModelService modelService = Mock()
	SnIndexerService snIndexerService = Mock()
	SnIndexerCronJobDao snIndexerCronJobDao = Mock()
	SnCronJobProgressTrackerFactory snCronJobProgressTrackerFactory = Mock()

	FullSnIndexerCronJobModel cronJob = Mock()
	SnIndexTypeModel cronJobIndexType = Mock()
	AbstractSnIndexerItemSourceModel cronJobIndexerItemSource = Mock()

	SnCloseableProgressTracker progressTracker = Mock()
	SnIndexerItemSource indexerItemSource = Mock()
	SnIndexerRequest indexerRequest = Mock()
	SnIndexerResponse indexerResponse = Mock()


	FullSnIndexerJob fullSnIndexerJob

	def setup() {
		cronJob.getIndexType() >> cronJobIndexType
		cronJob.getIndexerItemSource() >> cronJobIndexerItemSource

		cronJobIndexType.getId() >> INDEX_TYPE_ID

		snCronJobProgressTrackerFactory.createProgressTracker(cronJob) >> progressTracker

		snIndexerService.createItemSource(cronJobIndexerItemSource, _) >> indexerItemSource
		snIndexerService.createFullIndexerRequest(INDEX_TYPE_ID, indexerItemSource, progressTracker) >> indexerRequest
		snIndexerService.index(indexerRequest) >> indexerResponse

		fullSnIndexerJob = new FullSnIndexerJob()
		fullSnIndexerJob.setModelService(modelService)
		fullSnIndexerJob.setSnIndexerService(snIndexerService)
		fullSnIndexerJob.setSnIndexerCronJobDao(snIndexerCronJobDao)
		fullSnIndexerJob.setSnCronJobProgressTrackerFactory(snCronJobProgressTrackerFactory)
	}

	@Test
	def "Fail to run full indexer job without index type"() {
		given:
		cronJobIndexType.getId() >> null

		when:
		PerformResult result = fullSnIndexerJob.perform(cronJob);

		then:
		result.result == CronJobResult.FAILURE
		result.status == CronJobStatus.ABORTED
	}

	@Test
	def "Run full indexer job"() {
		given:
		indexerResponse.getStatus() >> SnIndexerOperationStatus.COMPLETED

		when:
		PerformResult result = fullSnIndexerJob.perform(cronJob);

		then:
		result.result == CronJobResult.SUCCESS
		result.status == CronJobStatus.FINISHED
	}

	@Test
	def "Fail to run full indexer job because indexer response is '#indexerOperationStatus' '#testId'"(testId, indexerOperationStatus) {
		given:
		indexerResponse.getStatus() >> indexerOperationStatus

		when:
		PerformResult result = fullSnIndexerJob.perform(cronJob);

		then:
		result.result == CronJobResult.FAILURE
		result.status == CronJobStatus.ABORTED

		where:
		testId | indexerOperationStatus
		1      | SnIndexerOperationStatus.RUNNING
		2      | SnIndexerOperationStatus.ABORTED
		3      | SnIndexerOperationStatus.FAILED
	}

	@Test
	def "Fail to run full indexer job because exception '#exception' is thrown '#testId'"(testId, exception) {
		given:
		snIndexerService.index(indexerRequest) >> {
			throw exception
		}

		when:
		PerformResult result = fullSnIndexerJob.perform(cronJob);

		then:
		result.result == CronJobResult.FAILURE
		result.status == CronJobStatus.ABORTED

		where:
		testId | exception
		1      | new SnIndexerException()
		2      | new RuntimeException()
	}
}
