package de.hybris.platform.inboundservices.interceptor

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.core.PK
import de.hybris.platform.inboundservices.exceptions.CannotDeleteIntegrationObjectLinkedWithInboundChannelConfigException
import de.hybris.platform.integrationservices.model.InboundChannelConfigurationModel
import de.hybris.platform.integrationservices.model.IntegrationObjectModel
import de.hybris.platform.servicelayer.interceptor.InterceptorException
import de.hybris.platform.servicelayer.search.FlexibleSearchService
import de.hybris.platform.servicelayer.search.SearchResult
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test

@UnitTest
class NoInboundChannelConfigBeforeRemoveIOCheckerUnitTest extends JUnitPlatformSpecification {
    private static final def IO_CODE = "TestIO-ICC"
    def flexibleSearchService = Mock FlexibleSearchService
    def integrationObject = Stub(IntegrationObjectModel) {
        getPk() >> PK.fromLong(1)
        getCode() >> IO_CODE
    }
    def checker = new NoInboundChannelConfigBeforeRemoveIOChecker(flexibleSearchService)


    @Test
    def "NoInboundChannelConfigBeforeRemoveIOChecker must be created with a FlexibleSearchService"() {
        when:
        new NoInboundChannelConfigBeforeRemoveIOChecker(null)

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
        e.message.contains "Failed finding InboundChannelConfiguration by integration object: $IO_CODE"
    }

    @Test
    def "exception is thrown when deleting an Integration Object that is referenced by an InboundChannelConfiguration"() {
        given:
        def searchResult = Stub(SearchResult) {
            getResult() >> [Stub(InboundChannelConfigurationModel)]
        }
        flexibleSearchService.search(_, _) >> searchResult

        when:
        checker.checkIfIntegrationObjectInUse integrationObject

        then:
        def e = thrown CannotDeleteIntegrationObjectLinkedWithInboundChannelConfigException
        e.message.contains "The [$IO_CODE] cannot be deleted because it is in use with at least one InboundChannelConfiguration for Authentication. Please delete the related InboundChannelConfiguration and try again."
    }

    @Test
    def "no exception is thrown when deleting an Integration Object that has no InboundChannelConfiguration referencing it"() {
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
