/*
 *  Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.webhookservices.service.impl

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.integrationservices.IntegrationObjectModelBuilder
import de.hybris.platform.integrationservices.util.IntegrationTestUtil
import de.hybris.platform.integrationservices.util.impex.ModuleEssentialData
import de.hybris.platform.outboundservices.facade.OutboundServiceFacade
import de.hybris.platform.outboundservices.util.TestOutboundFacade
import de.hybris.platform.servicelayer.ServicelayerSpockSpecification
import de.hybris.platform.webhookservices.WebhookConfigurationBuilder
import de.hybris.platform.webhookservices.util.WebhookServicesEssentialData
import org.junit.ClassRule
import org.junit.Rule
import org.junit.Test
import spock.lang.AutoCleanup
import spock.lang.Issue
import spock.lang.Shared

import javax.annotation.Resource
import java.time.Duration

import static de.hybris.platform.integrationservices.IntegrationObjectItemAttributeModelBuilder.integrationObjectItemAttribute
import static de.hybris.platform.integrationservices.IntegrationObjectItemModelBuilder.integrationObjectItem
import static de.hybris.platform.integrationservices.IntegrationObjectModelBuilder.integrationObject
import static de.hybris.platform.integrationservices.util.EventualCondition.eventualCondition
import static de.hybris.platform.integrationservices.util.IntegrationTestUtil.createCatalogWithId
import static de.hybris.platform.outboundservices.ConsumedDestinationBuilder.consumedDestinationBuilder
import static de.hybris.platform.webhookservices.WebhookConfigurationBuilder.webhookConfiguration

@IntegrationTest
@Issue('https://cxjira.sap.com/browse/IAPI-4453')
class WebhookImmediateRetryIntegrationTest extends ServicelayerSpockSpecification {
    private static final String TEST_NAME = 'WebhookImmediateRetry'
    private static final String CATALOG_IO = "${TEST_NAME}_CatalogIO"
    private static final Duration REASONABLE_TIME = Duration.ofSeconds(6)

    private OutboundServiceFacade originalOutboundServiceFacade
    @Resource
    private WebhookImmediateRetryOutboundServiceFacade webhookImmediateRetryOutboundServiceFacade

    @Shared
    @ClassRule
    ModuleEssentialData essentialData = WebhookServicesEssentialData.webhookServicesEssentialData()
    @Shared
    @ClassRule
    IntegrationObjectModelBuilder io = integrationObject().withCode(CATALOG_IO)
            .withItem(integrationObjectItem().withCode('Catalog').root()
                    .withAttribute(integrationObjectItemAttribute().withName('id')))
    @Rule
    TestOutboundFacade mockOutboundServiceFacade = new TestOutboundFacade()
    @AutoCleanup('cleanup')
    WebhookConfigurationBuilder webhookBuilder = webhookConfiguration().withIntegrationObject(CATALOG_IO)

    def setup() {
        webhookForCatalogItem()
        originalOutboundServiceFacade = webhookImmediateRetryOutboundServiceFacade.outboundServiceFacade
        webhookImmediateRetryOutboundServiceFacade.outboundServiceFacade = mockOutboundServiceFacade
    }

    def cleanup() {
        webhookImmediateRetryOutboundServiceFacade.outboundServiceFacade = originalOutboundServiceFacade
    }

    @Test
    def 'webhook does not retry after a 400 error'() {
        given: 'Outbound service facade responds with a 400 error'
        mockOutboundServiceFacade.respondWithBadRequest()

        when: 'Catalog created'
        def catalog = createCatalogWithId("${TEST_NAME}_Catalog")

        then:
        eventualCondition().within(REASONABLE_TIME).retains {
            assert mockOutboundServiceFacade.invocations() == 1
        }

        cleanup:
        IntegrationTestUtil.remove catalog
    }

    @Test
    def 'webhook is only retried once'() {
        given: 'Outbound service facade responds with a 500 error'
        mockOutboundServiceFacade.respondWithServerError()

        when: 'Catalog created'
        def catalog = createCatalogWithId("${TEST_NAME}_Catalog")

        then:
        eventualCondition().within(REASONABLE_TIME).retains {
            assert mockOutboundServiceFacade.invocations() == 2
        }

        cleanup:
        IntegrationTestUtil.remove catalog
    }

    def webhookForCatalogItem() {
        def consumedDestination = consumedDestinationBuilder()
                .withId("${TEST_NAME}_Destination")
                .withUrl("https://path/to/webhooks")
                .withDestinationTarget('webhookServices') // created in essential data
        webhookBuilder
                .withDestination(consumedDestination)
                .build()
    }
}
