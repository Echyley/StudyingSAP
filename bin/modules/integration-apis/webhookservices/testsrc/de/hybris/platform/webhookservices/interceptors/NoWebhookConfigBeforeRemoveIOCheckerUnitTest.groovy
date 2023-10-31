package de.hybris.platform.webhookservices.interceptors

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.core.PK
import de.hybris.platform.integrationservices.model.IntegrationObjectModel
import de.hybris.platform.servicelayer.interceptor.InterceptorException
import de.hybris.platform.servicelayer.search.FlexibleSearchService
import de.hybris.platform.servicelayer.search.SearchResult
import de.hybris.platform.testframework.JUnitPlatformSpecification
import de.hybris.platform.webhookservices.exceptions.CannotDeleteIntegrationObjectLinkedWithWebhookConfigException
import de.hybris.platform.webhookservices.interceptor.NoWebhookConfigBeforeRemoveIOChecker
import de.hybris.platform.webhookservices.model.WebhookConfigurationModel
import org.junit.Test
import spock.lang.Specification

@UnitTest
class NoWebhookConfigBeforeRemoveIOCheckerUnitTest extends JUnitPlatformSpecification {
    private static final def IO_CODE = "TestIO-WH"
    def flexibleSearchService = Mock FlexibleSearchService
    def checker = new NoWebhookConfigBeforeRemoveIOChecker(flexibleSearchService)
    def integrationObject = Stub(IntegrationObjectModel) {
        getPk() >> PK.fromLong(1)
        getCode() >> IO_CODE
    }
    def searchResult = Stub(SearchResult) {
        getResult() >> [Stub(WebhookConfigurationModel)]
    }

    @Test
    def "NoWebhookConfigBeforeRemoveIOChecker must be created with a FlexibleSearchService"() {
        when:
        new NoWebhookConfigBeforeRemoveIOChecker(null)

        then:
        def e = thrown IllegalArgumentException
        e.message.contains "Non-null flexibleSearchService must be provided"
    }

    @Test
    def "exception is thrown when flexibleSearchService throws an exception"() {
        given:
        def errorMsg = "this error message doesn't matter"
        flexibleSearchService.search(_, _) >> { throw new RuntimeException(errorMsg) }

        when:
        checker.checkIfIntegrationObjectInUse integrationObject

        then:
        def e = thrown InterceptorException
        e.message.contains "Failed finding WebhookConfiguration by integration object: $IO_CODE"
    }

    @Test
    def "exception is thrown when deleting an Integration Object that is referenced by a WebhookConfiguration"() {
        given:
        flexibleSearchService.search(_, _) >> searchResult

        when:
        checker.checkIfIntegrationObjectInUse integrationObject

        then:
        def e = thrown CannotDeleteIntegrationObjectLinkedWithWebhookConfigException
        e.message.contains "The [$IO_CODE] cannot be deleted because it is in use with at least one WebhookConfiguration. Please delete the related WebhookConfiguration and try again."
    }

    @Test
    def "no exception is thrown when deleting an Integration Object that has no WebhookConfiguration referencing it"() {
        given:
        def searchResult = Stub(SearchResult) {
            getResult() >> []
        }
        flexibleSearchService.search(_, _) >> searchResult

        when:
        checker.checkIfIntegrationObjectInUse integrationObject

        then:
        noExceptionThrown()
    }

    @Test
    def "do nothing if the Integration Object is null"() {
        when:
        checker.checkIfIntegrationObjectInUse null

        then:
        noExceptionThrown()
        0 * flexibleSearchService.search(_, _)
    }

}
