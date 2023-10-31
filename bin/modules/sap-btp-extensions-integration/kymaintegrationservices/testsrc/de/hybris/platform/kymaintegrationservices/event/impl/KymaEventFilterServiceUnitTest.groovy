package de.hybris.platform.kymaintegrationservices.event.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.apiregistryservices.dto.EventSourceData
import de.hybris.platform.apiregistryservices.model.events.EventConfigurationModel
import de.hybris.platform.order.events.SubmitOrderEvent
import de.hybris.platform.scripting.engine.ScriptExecutable
import de.hybris.platform.scripting.engine.ScriptExecutionResult
import de.hybris.platform.scripting.engine.ScriptingLanguagesService
import de.hybris.platform.scripting.engine.exception.ScriptingException
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import spock.lang.Unroll

@UnitTest
class KymaEventFilterServiceUnitTest extends JUnitPlatformSpecification {

    private static final SCRIPT_URI = 'model://filterScriptName'
    def scriptingLanguagesService = Stub(ScriptingLanguagesService)
    def kymaEventFilterService = new KymaEventFilterServiceImpl(scriptingLanguagesService)

    @Test
    @Unroll
    def 'filter returns #filterReturn when the filtering script returns #scriptReturn'() {
        given:
        def dataSource = getDataSource(SCRIPT_URI)
        scriptingLanguagesService.getExecutableByURI(SCRIPT_URI) >> Stub(ScriptExecutable) {
            execute([event: dataSource.getEvent()]) >> Stub(ScriptExecutionResult) {
                getScriptResult() >> scriptReturn
            }
        }

        expect:
        kymaEventFilterService.filterKymaEvent(dataSource) == filterReturn

        where:
        filterReturn | scriptReturn
        true         | true
        false        | false
    }

    @Test
    @Unroll
    def 'filter returns true when the filtering script location is #scriptUriLocation'() {
        given:
        def dataSource = getDataSource(scriptUri)

        expect:
        kymaEventFilterService.filterKymaEvent(dataSource)

        where:
        scriptUri | scriptUriLocation
        ''        | 'empty'
        null      | null
    }

    @Test
    def 'filter returns false when the filtering script throws an exception'() {
        given:
        def dataSource = getDataSource(SCRIPT_URI)
        scriptingLanguagesService.getExecutableByURI(SCRIPT_URI) >> {
            throw new ScriptingException("Kyma filtering script exception!")
        }

        expect:
        !kymaEventFilterService.filterKymaEvent(dataSource)
    }

    @Test
    def 'an exception is thrown when instantiating the Kyma filter without the scripting service'() {
        when:
        new KymaEventFilterServiceImpl(null)

        then:
        def e = thrown IllegalArgumentException
        e.message == 'Scripting language service cannot be null!'
    }

    def getDataSource(def scriptUri) {

        def eventConfiguration = new EventConfigurationModel()
        eventConfiguration.setEventClass(SubmitOrderEvent.class.getCanonicalName())
        eventConfiguration.setFilterLocation(scriptUri)

        def eventSourceData = new EventSourceData()
        eventSourceData.setEventConfig(eventConfiguration)
        eventSourceData.setEvent(new SubmitOrderEvent())

        return eventSourceData
    }

}
