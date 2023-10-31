package de.hybris.platform.integrationbackoffice.utility

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test

@UnitTest
class QualifierNameUtilsUnitTest extends JUnitPlatformSpecification {

    @Test
    def "Test the regex that determines if the value is alphanumeric"() {
        when:
        def result = QualifierNameUtils.isAlphaNumericName(name)

        then:
        result == isAlphaNumeric

        where:
        name     | isAlphaNumeric
        "test"   | true
        "test1"  | true
        "1234"   | true
        "test!"  | false
        "1234!"  | false
        "test1!" | false
        "tes_t"  | true
        "123_4"  | true
        "test_!" | false
        ""       | false
        null     | false
    }

    @Test
    def "Test removing non alphanumeric characters from string using regex"() {
        when:
        def result = QualifierNameUtils.removeNonAlphaNumericCharacters(name)

        then:
        result == alphaNumericName

        where:
        name        | alphaNumericName
        "test"      | "test"
        "test1"     | "test1"
        "test!"     | "test"
        null        | null
        ""          | ""
        "test!1234" | "test1234"
    }
}
