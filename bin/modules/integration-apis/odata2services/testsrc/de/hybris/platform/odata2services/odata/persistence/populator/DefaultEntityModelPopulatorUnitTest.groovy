/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.odata2services.odata.persistence.populator

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.odata2services.odata.persistence.ItemConversionRequest
import de.hybris.platform.odata2services.odata.persistence.populator.processor.PropertyProcessor
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.apache.olingo.odata2.api.edm.EdmException
import org.apache.olingo.odata2.api.ep.entry.ODataEntry
import org.apache.olingo.odata2.api.exception.MessageReference
import org.junit.Test
import spock.lang.Unroll

@UnitTest
class DefaultEntityModelPopulatorUnitTest extends JUnitPlatformSpecification {
    def entityModelPopulator = new DefaultEntityModelPopulator(propertyProcessors: [])

    @Test
    @Unroll
    def "populateEntity() throws exception when #param is null"() {
        when:
        entityModelPopulator.populateEntity entry, request

        then:
        thrown IllegalArgumentException

        where:
        param                   | entry            | request
        'ODataEntry'            | null             | Stub(ItemConversionRequest)
        'ItemConversionRequest' | Stub(ODataEntry) | null
    }

    @Test
    def 'populateEntity() invokes PropertyProcessors in order they are configured'() {
        given:
        def processor1 = Mock PropertyProcessor
        def processor2 = Mock PropertyProcessor
        entityModelPopulator.propertyProcessors = [processor1, processor2]
        and:
        def entry = Stub ODataEntry
        def request = Stub ItemConversionRequest

        when:
        entityModelPopulator.populateEntity entry, request

        then:
        1 * processor1.processEntity(entry, request)
        then:
        1 * processor2.processEntity(entry, request)
    }

    @Test
    def 'populateEntity() rethrows EdmException from PropertyProcessors'() {
        given:
        def propertyProcessor = Stub(PropertyProcessor) {
            processEntity(_ as ODataEntry, _ as ItemConversionRequest) >> { throw new EdmException(Stub(MessageReference)) }
        }
        entityModelPopulator.propertyProcessors = [propertyProcessor]

        when:
        entityModelPopulator.populateEntity Stub(ODataEntry), Stub(ItemConversionRequest)

        then:
        thrown EdmException
    }

    @Test
    def 'populateEntity() ignores excluded property processors'() {
        given:
        def processor = Mock PropertyProcessor
        entityModelPopulator.propertyProcessors = [processor]

        when:
        entityModelPopulator.populateEntity Stub(ODataEntry), Stub(ItemConversionRequest)

        then:
        1 * processor.processEntity(_, _)
    }

    @Test
    def "setPropertyProcessors does not leak reference when propertyProcessors is not null"() {
        given:
        def processors = [Stub(PropertyProcessor)]
        entityModelPopulator.propertyProcessors = processors

        when:
        processors.clear()

        then:
        !entityModelPopulator.propertyProcessors.empty
    }

    @Test
    def "setPropertyProcessors sets propertyProcessor to immutable empty list when given propertyProcessors is null"() {
        given:
        entityModelPopulator.propertyProcessors = null

        expect:
        entityModelPopulator.propertyProcessors.empty
    }

    @Test
    @Unroll
    def "getPropertyProcessors returns an immutable list #msg"() {
        given:
        entityModelPopulator.propertyProcessors = propertyProcessors

        when:
        entityModelPopulator.propertyProcessors << Stub(PropertyProcessor)

        then:
        thrown UnsupportedOperationException

        where:
        msg                                   | propertyProcessors
        "given propertyProcessor is not null" | []
        "given propertyProcessor is null"     | null as List
    }
}
