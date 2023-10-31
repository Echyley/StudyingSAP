package de.hybris.platform.cms2.version

import static de.hybris.platform.cms2.constants.Cms2Constants.VERSION_GC_MAX_AGE_DAYS_PROPERTY
import static de.hybris.platform.cms2.constants.Cms2Constants.VERSION_GC_MAX_NUMBER_VERSIONS_PROPERTY

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.catalog.CatalogVersionService
import de.hybris.platform.cms2.model.CMSVersionModel
import de.hybris.platform.cronjob.enums.CronJobResult
import de.hybris.platform.cronjob.enums.CronJobStatus
import de.hybris.platform.cronjob.model.CronJobModel
import de.hybris.platform.servicelayer.ServicelayerTransactionalSpockSpecification
import de.hybris.platform.servicelayer.config.ConfigurationService
import de.hybris.platform.servicelayer.cronjob.CronJobService
import de.hybris.platform.servicelayer.model.ModelService
import org.junit.Test

import javax.annotation.Resource
import java.time.Instant
import java.time.temporal.ChronoUnit

@IntegrationTest
class CMSVersionGCIntegrationTest extends ServicelayerTransactionalSpockSpecification {

	def maxAgeBefore
	def maxNumberBefore

	@Resource
	CMSVersionGCPerformable cmsVersionGCPerformable

	@Resource
	CatalogVersionService catalogVersionService

	@Resource
	CronJobService cronJobService

	@Resource
	ModelService modelService

	@Resource
	ConfigurationService configurationService

	def itemCatalogVersion

	def job

	def configureGCParameters() {
		maxAgeBefore = configurationService.getConfiguration().getProperty(VERSION_GC_MAX_AGE_DAYS_PROPERTY)
		maxNumberBefore = configurationService.getConfiguration().getProperty(VERSION_GC_MAX_NUMBER_VERSIONS_PROPERTY)

		configurationService.getConfiguration().setProperty(VERSION_GC_MAX_AGE_DAYS_PROPERTY, String.valueOf(20))
		configurationService.getConfiguration().setProperty(VERSION_GC_MAX_NUMBER_VERSIONS_PROPERTY, String.valueOf(0))
	}

	def setup() {
		createCoreData()
		createDefaultCatalog()
		importCsv("/test/cmsCatalogVersionTestData.csv", "UTF-8")
		itemCatalogVersion = catalogVersionService.getCatalogVersion("cms_Catalog", "CatalogVersion1")

		importCsv("/impex/essentialdata-cms2-jobs.impex", "UTF-8")
		job = cronJobService.getJob("cmsVersionGCJob")

		configureGCParameters()
	}

	def resetGCParameters() {
		configurationService.getConfiguration().setProperty(VERSION_GC_MAX_AGE_DAYS_PROPERTY, maxAgeBefore)
		configurationService.getConfiguration().setProperty(VERSION_GC_MAX_NUMBER_VERSIONS_PROPERTY, maxNumberBefore)
	}

	def cleanup() {
		resetGCParameters()
	}

	@Test
	def "CMS version GC job removes expired versions"() {
		given:
		def valid1 = createTaggedCMSVersion("valid-age-1", false, Date.from(Instant.now()))
		def valid2 = createTaggedCMSVersion("valid-age-2",false, Date.from(Instant.now().minus(15, ChronoUnit.DAYS)))
		def valid3 = createTaggedCMSVersion("valid-old-but-retained", true, Date.from(Instant.now().minus(300, ChronoUnit.DAYS)))

		def invalidAge1 = createTaggedCMSVersion("invalid-age-1", false, Date.from(Instant.now().minus(30, ChronoUnit.DAYS)))
		def invalidAge2 = createCMSVersion("invalid-age-2", null, false, Date.from(Instant.now().minus(300, ChronoUnit.DAYS)))
		def invalidAge3 = createTaggedCMSVersion("invalid-age-3", false, Date.from(Instant.now().minus(300, ChronoUnit.DAYS)))

		CronJobModel dummyJob = modelService.create(CronJobModel.class)
		dummyJob.job = job
		//force pagination
		dummyJob.queryCount = 2
		modelService.save(dummyJob)

		when:
		def result = cmsVersionGCPerformable.perform(dummyJob)
		modelService.refresh(dummyJob)

		then:
		result.result == CronJobResult.SUCCESS
		result.status == CronJobStatus.FINISHED

		modelService.isRemoved(invalidAge1)
		modelService.isRemoved(invalidAge2)
		modelService.isRemoved(invalidAge3)

		!modelService.isRemoved(valid1)
		!modelService.isRemoved(valid2)
		!modelService.isRemoved(valid3)

	}

	@Test
	def "CMS version GC job is abortable"() {
		given:
		def invalid = createTaggedCMSVersion("invalid-old", false, Date.from(Instant.now().minus(300, ChronoUnit.DAYS)))

		CronJobModel abortedJob = modelService.create(CronJobModel.class)
		//fake RUNNING state to be able to abort
		abortedJob.status = CronJobStatus.RUNNING
		abortedJob.job = job
		modelService.save(abortedJob)

		when:
		cronJobService.requestAbortCronJob(abortedJob)
		def result = cmsVersionGCPerformable.perform(abortedJob)

		then:
		result.result == CronJobResult.UNKNOWN
		result.status == CronJobStatus.ABORTED

		!modelService.isRemoved(invalid)
	}

	protected CMSVersionModel createTaggedCMSVersion(final String uid, final boolean retain, final Date creationTime)
	{
		return createCMSVersion(uid, "test label", retain, creationTime)
	}

	protected CMSVersionModel createCMSVersion(final String uid, final String label, final boolean retain, final Date creationTime)
	{
		final CMSVersionModel cmsVersionModel = modelService.create(CMSVersionModel.class)
		cmsVersionModel.setUid(uid)
		cmsVersionModel.setTransactionId(UUID.randomUUID().toString())
		cmsVersionModel.setItemCatalogVersion(itemCatalogVersion)
		cmsVersionModel.setItemUid("dummy")
		cmsVersionModel.setLabel(label)
		cmsVersionModel.setRetain(retain)
		cmsVersionModel.setCreationtime(creationTime)

		modelService.save(cmsVersionModel)

		return cmsVersionModel
	}
}
