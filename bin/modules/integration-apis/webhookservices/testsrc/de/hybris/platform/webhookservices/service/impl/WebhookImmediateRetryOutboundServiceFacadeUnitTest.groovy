/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.webhookservices.service.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel
import de.hybris.platform.core.model.ItemModel
import de.hybris.platform.integrationservices.model.IntegrationObjectModel
import de.hybris.platform.integrationservices.service.IntegrationObjectService
import de.hybris.platform.integrationservices.util.TestApplicationContext
import de.hybris.platform.outboundservices.facade.SyncParameters
import de.hybris.platform.outboundservices.facade.SyncParametersBuilder
import de.hybris.platform.outboundservices.facade.impl.DefaultOutboundServiceFacade
import de.hybris.platform.outboundservices.service.DestinationSearchService
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Rule
import org.junit.Test
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.retry.backoff.FixedBackOffPolicy
import org.springframework.retry.policy.SimpleRetryPolicy
import org.springframework.retry.support.RetryTemplate
import rx.Observable
import spock.lang.Shared

@UnitTest
class WebhookImmediateRetryOutboundServiceFacadeUnitTest extends JUnitPlatformSpecification {

    private static final def TEST_INTEGRATION_OBJECT_CODE = "TestIntegrationObject"
    private static final def TEST_INTEGRATION_OBJECT = new IntegrationObjectModel(code: TEST_INTEGRATION_OBJECT_CODE)
    private static final def TEST_DESTINATION_ID = "TestDestination"
    private static final def TEST_DESTINATION = new ConsumedDestinationModel(id: TEST_DESTINATION_ID)

    @Shared
    def CHANGED_ITEM = Stub(ItemModel)

    @Rule
    TestApplicationContext applicationContext

    def outboundServiceFacade = Mock(DefaultOutboundServiceFacade)
    def webhookServicesRetryTemplate = retryTemplate()
    def webhookImmediateRetryOutboundServiceFacade =
            new WebhookImmediateRetryOutboundServiceFacade(outboundServiceFacade, webhookServicesRetryTemplate)

    def ioService = Stub(IntegrationObjectService) {
        findIntegrationObject(TEST_INTEGRATION_OBJECT_CODE) >> TEST_INTEGRATION_OBJECT
    }
    def searchService = Stub(DestinationSearchService) {
        findDestination(TEST_DESTINATION_ID) >> TEST_DESTINATION
    }

    def setup() {
        resetStaticServices()
        applicationContext.addBean('integrationObjectService', ioService)
        applicationContext.addBean('destinationSearchService', searchService)
    }

    def cleanupSpec() {
        resetStaticServices()
    }

    private void resetStaticServices() {
        SyncParametersBuilder.ioService = null
        SyncParametersBuilder.destinationSearchService = null
    }

    @Test
    def "#attempts attempts to send the item are based when the response is #status"() {
        given:
        def parameters = syncParameters()

        when:
        webhookImmediateRetryOutboundServiceFacade.send(parameters)

        then:
        attempts * outboundServiceFacade.send(syncParameters()) >> stubObservable(status)

        where:
        attempts | status
        2        | HttpStatus.INTERNAL_SERVER_ERROR
        1        | HttpStatus.BAD_REQUEST
        1        | HttpStatus.OK
        1        | HttpStatus.MULTIPLE_CHOICES
        1        | HttpStatus.CONTINUE
    }

    @Test
    def "#attempts attempts to send the item are based when the response is #status for send without sync parameters"() {
        when:
        webhookImmediateRetryOutboundServiceFacade.send(CHANGED_ITEM, TEST_INTEGRATION_OBJECT_CODE, TEST_DESTINATION_ID)

        then:
        attempts * outboundServiceFacade.send(_ as SyncParameters) >> stubObservable(status)

        where:
        attempts | status
        2        | HttpStatus.INTERNAL_SERVER_ERROR
        1        | HttpStatus.BAD_REQUEST
        1        | HttpStatus.OK
        1        | HttpStatus.MULTIPLE_CHOICES
        1        | HttpStatus.CONTINUE
    }

    def syncParameters() {
        SyncParameters.syncParametersBuilder()
                .withIntegrationObjectCode(TEST_INTEGRATION_OBJECT_CODE)
                .withPayloadObject(CHANGED_ITEM)
                .withDestinationId(TEST_DESTINATION_ID)
                .build()
    }

    def stubObservable(HttpStatus httpStatus) {
        return Observable.just(Stub(ResponseEntity) {
            getStatusCode() >> httpStatus
        })
    }

    def retryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate()

        FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy()
        fixedBackOffPolicy.setBackOffPeriod(1000l)
        retryTemplate.setBackOffPolicy(fixedBackOffPolicy)

        Map<Class<? extends Throwable>, Boolean> exceptions = new HashMap()
        exceptions.put(WebhookRetryableException.class, true)
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy(2, exceptions, true)
        retryTemplate.setRetryPolicy(retryPolicy)

        return retryTemplate
    }
}
