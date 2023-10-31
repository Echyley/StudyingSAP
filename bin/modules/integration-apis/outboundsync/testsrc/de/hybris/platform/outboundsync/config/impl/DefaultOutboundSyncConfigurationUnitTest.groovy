/*
 * [y] hybris Platform
 *
 * Copyright (c) 2019 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.outboundsync.config.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.outboundsync.model.OutboundSyncStreamConfigurationModel
import de.hybris.platform.servicelayer.config.ConfigurationService
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.apache.commons.configuration.Configuration
import org.junit.Test
import spock.lang.Unroll

@UnitTest
class DefaultOutboundSyncConfigurationUnitTest extends JUnitPlatformSpecification {

    private static final String AUTOGENERATE_EXCLUDED_STREAM_IDS = "outboundsync.disabled.info.expression.auto.generation.stream.ids";
    private static final String OUTBOUNDSYNC_MAX_RETRIES = "outboundsync.max.retries"
    private static final String OUTBOUNDSYNC_CRONJOBMODEL_SEARCH_SLEEP = "outboundsync.cronjob.search.sleep.milliseconds";
    private static final String OUTBOUNDSYNC_BATCH_LIMIT = "outboundsync.batch.limit";
    private static final String OUTBOUNDSYNC_BATCH_RELEASE_TIMEOUT = "outboundsync.batch.release.timeout";

    def syncConfig = new DefaultOutboundSyncConfiguration()

    def configurationService = Stub(ConfigurationService)
    def configuration = Stub(Configuration)

    def setup() {
        configurationService.getConfiguration() >> configuration

        syncConfig.setConfigurationService(configurationService)
    }

    @Test
    @Unroll
    def "when disabled stream ids configuration property #containsMsg stream.id the stream #isEnabledMsg for info expression generation"() {
        given:
        configuration.getString(AUTOGENERATE_EXCLUDED_STREAM_IDS) >> configurationPropertyValue

        expect:
        syncConfig.isInfoGenerationEnabledForStream(streamConfigWithId(streamId)) == isEnabled

        where:
        containsMsg        | isEnabledMsg     | isEnabled | configurationPropertyValue | streamId
        "contains"         | "is not enabled" | false     | "MyStream1"                | "MyStream1"
        "does not contain" | "is enabled"     | true      | "MyStream1"                | "DifferentStream"
        "does not contain" | "is enabled"     | true      | "null"                     | null
    }

    @Test
    def "when max retries property is configured its value is returned"() {
        given:
        configuration.getInt(OUTBOUNDSYNC_MAX_RETRIES) >> 5

        expect:
        syncConfig.getMaxOutboundSyncRetries() == 5
    }

    @Test
    def "when max retries property is not found we use the fallback value"() {
        given:
        configuration.getInt(OUTBOUNDSYNC_MAX_RETRIES) >> { throw new NoSuchElementException() }

        expect:
        syncConfig.getMaxOutboundSyncRetries() == 0
    }

    @Test
    def "when outboundsync batch limit property is configured its value is returned"() {
        given:
        configuration.getInt(OUTBOUNDSYNC_BATCH_LIMIT) >> 300

        expect:
        syncConfig.getOutboundBatchLimit() == 300
    }

    @Test
    def "when outboundsync batch limit is not found we use the fallback value"() {
        given:
        configuration.getInt(OUTBOUNDSYNC_BATCH_LIMIT) >> { throw new NoSuchElementException() }

        expect:
        syncConfig.getOutboundBatchLimit() == 200
    }

    @Test
    def "when outboundsync batch timeout property is configured its value is returned"() {
        given:
        configuration.getInt(OUTBOUNDSYNC_BATCH_RELEASE_TIMEOUT) >> 444

        expect:
        syncConfig.getOutboundBatchReleaseTimeout() == 444
    }

    @Test
    def "when outboundsync batch timeout is not found we use the fallback value"() {
        given:
        configuration.getInt(OUTBOUNDSYNC_BATCH_RELEASE_TIMEOUT) >> { throw new NoSuchElementException() }

        expect:
        syncConfig.getOutboundBatchReleaseTimeout() == 5000
    }

    @Test
    def "when OutboundSyncCronJobModel search sleep property is configured its value is returned"() {
        given:
        configuration.getInt(OUTBOUNDSYNC_CRONJOBMODEL_SEARCH_SLEEP) >> 1234

        expect:
        syncConfig.getOutboundSyncCronjobModelSearchSleep() == 1234
    }

    @Test
    def "when OutboundSyncCronJobModel search sleep property is not found we use the fallback value"() {
        given:
        configuration.getInt(OUTBOUNDSYNC_CRONJOBMODEL_SEARCH_SLEEP) >> { throw new NoSuchElementException() }

        expect:
        syncConfig.getOutboundSyncCronjobModelSearchSleep() == 1000
    }

    OutboundSyncStreamConfigurationModel streamConfigWithId(String id) {
        Stub(OutboundSyncStreamConfigurationModel) {
            getStreamId() >> id
        }
    }
}
