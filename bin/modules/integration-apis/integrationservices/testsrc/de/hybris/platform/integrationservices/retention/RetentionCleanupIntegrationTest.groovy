/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationservices.retention

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.integrationservices.model.IntegrationApiMediaModel
import de.hybris.platform.integrationservices.util.IntegrationTestUtil
import de.hybris.platform.integrationservices.util.impex.IntegrationServicesEssentialData
import de.hybris.platform.servicelayer.ServicelayerSpockSpecification
import de.hybris.platform.servicelayer.cronjob.CronJobService
import org.junit.ClassRule
import org.junit.Test
import spock.lang.Shared

import javax.annotation.Resource
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@IntegrationTest
class RetentionCleanupIntegrationTest extends ServicelayerSpockSpecification {
    private static final String TEST_NAME = 'RetentionCleanup'
    private static final String MEDIA_CODE = "${TEST_NAME}_integrationApiMedia"
    private static final String INTEGRATION_API_MEDIA_CLEANUP_CRON_JOB_NAME = 'integrationApiMediaCleanupCronJob'
    private static final int DAYS_PAST_RETENTION_PERIOD = 10

    @Shared
    @ClassRule
    IntegrationServicesEssentialData essentialData = IntegrationServicesEssentialData.integrationServicesEssentialData()
    @Resource
    private CronJobService cronJobService

    @Test
    void 'cleanup rule cleans up IntegrationApiMedia older than retention period configured'() {
        given:
        var tenDaysAgoString = LocalDateTime.now().minusDays(DAYS_PAST_RETENTION_PERIOD)
                .format((DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")))

        IntegrationTestUtil.importImpEx(
                "INSERT_UPDATE IntegrationApiMedia; code[unique=true]; creationtime[dateformat=dd.MM.yyyy HH:mm]",
                "                                 ; $MEDIA_CODE      ; $tenDaysAgoString                        "
        )

        when:
        executeMediaCleanupCronJob()

        then:
        createdIntegrationApiMedia() == null
    }

    void executeMediaCleanupCronJob() {
        var cronJob = cronJobService.getCronJob(INTEGRATION_API_MEDIA_CLEANUP_CRON_JOB_NAME)
        cronJobService.performCronJob(cronJob, true)
    }

    IntegrationApiMediaModel createdIntegrationApiMedia() {
        var mediaModel = IntegrationTestUtil.findAny(IntegrationApiMediaModel,
                { MEDIA_CODE == it.getCode() }).orElse(null)
        mediaModel
    }
}
