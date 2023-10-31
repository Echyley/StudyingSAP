package de.hybris.platform.odata2services.odata.processor.writer

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import spock.lang.Unroll

@UnitTest
class ResponseWriterPropertyPopulatorRegistryUnitTest extends JUnitPlatformSpecification {

    def registry = new ResponseWriterPropertyPopulatorRegistry()

    @Test
    def "setPopulators method does not leak reference when populators is not null"() {
        given:
        def populators = [Stub(ResponseWriterPropertyPopulator)]
        registry.setPopulators(populators)

        when:
        populators.clear()

        then:
        !registry.populators.empty
    }

    @Test
    def "populator list is set to empty list when given populator list is null"() {
        given:
        registry.setPopulators(null)

        expect:
        registry.populators.empty
    }

    @Test
    @Unroll
    def "getPopulators method returns immutable list when given #msg"() {
        given:
        registry.setPopulators(populators)

        when:
        registry.populators << Stub(ResponseWriterPropertyPopulator)

        then:
        thrown UnsupportedOperationException

        where:
        msg                          | populators
        "populator list is not null" | []
        "populator list is null"     | null as List
    }
}