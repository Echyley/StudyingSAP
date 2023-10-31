package de.hybris.platform.outboundsync.interceptor

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.core.PK
import de.hybris.platform.integrationservices.model.IntegrationObjectModel
import de.hybris.platform.outboundsync.exceptions.CannotDeleteIntegrationObjectLinkedWithOutboundChannelConfigException
import de.hybris.platform.outboundsync.model.OutboundChannelConfigurationModel
import de.hybris.platform.servicelayer.interceptor.InterceptorException
import de.hybris.platform.servicelayer.search.FlexibleSearchService
import de.hybris.platform.servicelayer.search.SearchResult
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import spock.lang.Specification

@UnitTest
class NoOutboundChannelConfigBeforeRemoveIOCheckerUnitTest extends JUnitPlatformSpecification {
    private static final def IO_CODE = "TestIO-OCC"
    private static final def OCC_CODE = "TestOCC"
    def flexibleSearchService = Mock FlexibleSearchService
    def checker = new NoOutboundChannelConfigBeforeRemoveIOChecker(flexibleSearchService)
    def integrationObject = Stub(IntegrationObjectModel) {
        getPk() >> PK.fromLong(1)
        getCode() >> IO_CODE
    }

    @Test
    def "NoOutboundChannelConfigBeforeRemoveIOChecker must be created with a FlexibleSearchService"() {
        when:
        new NoOutboundChannelConfigBeforeRemoveIOChecker(null)

        then:
        def e = thrown IllegalArgumentException
        e.message.contains "Non-null flexibleSearchService must be provided"
    }

    @Test
    def "exception is thrown when flexibleSearchService throws an exception"() {
        given:
        def errorMsg = "this error message doesn't matter"
        flexibleSearchService.search(_, _) >> {throw new RuntimeException(errorMsg)}

        when:
        checker.checkIfIntegrationObjectInUse integrationObject

        then:
        def e = thrown InterceptorException
        e.message.contains "Failed finding OutboundChannelConfiguration by integration object: $IO_CODE"
    }

    @Test
    def "exception is thrown when deleting an Integration Object that is referenced by an OutboundChannelConfiguration"() {
        given:
        def occ = Stub(OutboundChannelConfigurationModel) {
            getCode() >> OCC_CODE
        }
        def searchResult = Stub(SearchResult) {
            getResult() >> [occ]
        }
        flexibleSearchService.search(_, _) >> searchResult

        when:
        checker.checkIfIntegrationObjectInUse integrationObject

        then:
        def e = thrown CannotDeleteIntegrationObjectLinkedWithOutboundChannelConfigException
        e.message.contains "The [$IO_CODE] cannot be deleted because it is in use with OutboundChannelConfiguration: $OCC_CODE . Please delete the related OutboundChannelConfiguration and try again."
    }

    @Test
    def "no exception is thrown when deleting an Integration Object that has no OutboundChannelConfiguration referencing it"() {
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
