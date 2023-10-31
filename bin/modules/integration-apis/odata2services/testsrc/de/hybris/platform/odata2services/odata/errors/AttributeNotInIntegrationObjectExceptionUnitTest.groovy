package de.hybris.platform.odata2services.odata.errors

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import spock.lang.Unroll

@UnitTest
class AttributeNotInIntegrationObjectExceptionUnitTest extends JUnitPlatformSpecification {

    @Test
    def "Constructor does not leak reference when content list is not null"() {
        given:
        def content = ['test']
        def exception = new AttributeNotInIntegrationObjectException(content)

        when:
        content.clear()

        then:
        !exception.getContent().empty
    }

    @Test
    def "Constructor sets content to immutable empty list if the given content is null"() {
        given:
        def exception = new AttributeNotInIntegrationObjectException(null)

        expect:
        exception.getContent().empty
    }

    @Test
    @Unroll
    def "getContent returns immutable list #msg"() {
        given:
        def exception = new AttributeNotInIntegrationObjectException(content)

        when:
        exception.getContent() << new AttributeNotInIntegrationObjectException(['test2'])

        then:
        thrown UnsupportedOperationException

        where:
        msg                        | content
        "when content is null"     | null as List
        "when content is not null" | ['test1']
    }
}
