package de.hybris.platform.kymaintegrationservices.event.impl

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.apiregistryservices.dto.EventSourceData
import de.hybris.platform.apiregistryservices.model.events.EventConfigurationModel
import de.hybris.platform.kymaintegrationservices.event.KymaEventFilterService
import de.hybris.platform.order.events.SubmitOrderEvent
import de.hybris.platform.servicelayer.ServicelayerSpockSpecification
import org.junit.Test
import spock.lang.Unroll

import javax.annotation.Resource

@IntegrationTest
class KymaEventFilterServiceIntegrationTest extends ServicelayerSpockSpecification {

    private static final SCRIPT_RETURNS_TRUE = 'KymaFilterScript_ReturnsTrue'
    private static final SCRIPT_RETURNS_FALSE = 'KymaFilterScript_ReturnsFalse'
    private static final SCRIPT_THROWS_EXCEPTION = 'KymaFilterScript_ThrowsException'

    @Resource
    private KymaEventFilterService kymaEventFilterService

    def setupSpec() {
        importCsv("/test/kymaFilteringScript.impex", "UTF-8")
    }

    def cleanupSpec() {
        importCsv("/test/kymaFilteringScriptRemove.impex", "UTF-8")
    }

    @Test
    @Unroll
    def 'filter returns #expectedResult when the #scriptName is executed'() {
        given:
        def dataSource = getDataSource(scriptName)

        expect:
        kymaEventFilterService.filterKymaEvent(dataSource) == expectedResult

        where:
        scriptName           | expectedResult
        SCRIPT_RETURNS_TRUE  | true
        SCRIPT_RETURNS_FALSE | false
    }

    @Test
    def 'filter returns true when the filtering script location is not configured'() {
        given:
        def dataSource = getDataSource(null)

        expect:
        kymaEventFilterService.filterKymaEvent(dataSource)
    }

    @Test
    def 'filter returns false when the filtering script throws an exception'() {
        given:
        def dataSource = getDataSource(SCRIPT_THROWS_EXCEPTION)

        expect:
        !kymaEventFilterService.filterKymaEvent(dataSource)
    }

    def getDataSource(def scriptName) {

        def eventConfiguration = new EventConfigurationModel()
        eventConfiguration.setEventClass(SubmitOrderEvent.class.getCanonicalName())
        if (scriptName != null) {
            eventConfiguration.setFilterLocation("model://$scriptName")
        }
        def eventSourceData = new EventSourceData()
        eventSourceData.setEventConfig(eventConfiguration)
        eventSourceData.setEvent(new SubmitOrderEvent())

        return eventSourceData
    }

}
