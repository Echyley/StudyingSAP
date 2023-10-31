/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.odata.processor.reader

import de.hybris.platform.testframework.JUnitPlatformSpecification
import de.hybris.bootstrap.annotations.UnitTest
import org.junit.Test
import de.hybris.platform.odata2services.odata.persistence.InternalProcessingException
import org.apache.olingo.odata2.api.uri.UriInfo

@UnitTest
class EntityReaderRegistryTest extends JUnitPlatformSpecification {

    def registry = new EntityReaderRegistry()

    @Test
    def "setEntityReaders does not leak reference when requests is not null"() {
        given:
        def uriInfo = Stub(UriInfo)
        def reader = Stub(EntityReader) {
            isApplicable(uriInfo) >> true
        }
        def readers = [reader]
        registry.setEntityReaders(readers)

        when:
        readers.clear()
        def foundReader = registry.getReader(uriInfo)

        then:
        foundReader == reader
    }

    @Test
    def "setEntityReaders sets the readers to empty immutable list when given readers is null"() {
        given:
        def uriInfo = Stub(UriInfo)
        registry.setEntityReaders(null)

        when:
        registry.getReader(uriInfo)

        then:
        thrown(InternalProcessingException)
    }
}
