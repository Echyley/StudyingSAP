/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.outboundsync.job.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import spock.lang.Specification
import spock.lang.Unroll

@UnitTest
class DefaultChangeInfoParserUnitTest extends JUnitPlatformSpecification {
    def parser = new DefaultChangeInfoParser()

    @Test
    @Unroll
    def "parses empty info when info string is #info"() {
        expect:
        parser.parse(info).empty

        where:
        info << [null, '', 'malformed']
    }

    @Test
    @Unroll
    def "parses info from string #info"() {
        given:
        def result = parser.parse info

        expect:
        result.present
        with(result.get()) {
            integrationKey == key
            itemType == type
            rootItemType == rootType
        }

        where:
        info                                                           | key   | type | rootType
        '{ "key": "A|B" }'                                             | 'A|B' | null | null
        '{ "type": "C" }'                                              | null  | 'C'  | null
        '{ "rootType": "D" }'                                          | null  | null | 'D'
        '{ "key": "A|B", "type": "C", "rootType": "D" }'               | 'A|B' | 'C'  | 'D'
        '{ "type": "C", "rootType": "D", "extra": "E", "key": "A|B" }' | 'A|B' | 'C'  | 'D'
        '{ "key": "", "type": "", "rootType": "" }'                    | ''    | ''   | ''
        '{ "extra": "something" }'                                     | null  | null | null
        '{}'                                                           | null  | null | null
    }

    @Test
    @Unroll
    def "reserved info expression value is not parsed for value #reservedInfoValue"() {
        given:
        def result = parser.parse reservedInfoValue

        expect:
        result.empty

        where:
        reservedInfoValue << ["created"]
    }

    @Test
    def 'parser can be configured to fail for unknown JSON properties'() {
        given:
        parser.failOnUnknownProperty = true

        expect:
        parser.parse('{ "extra": "something" }').empty
    }
}
