/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.outboundsync.dto

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import spock.lang.Specification
import spock.lang.Unroll

@UnitTest
class ChangeInfoUnitTest extends JUnitPlatformSpecification {
    private static final ChangeInfo INFO = new ChangeInfo('key', 'type', 'root type')

    @Test
    def 'info values can be read back'() {
        expect:
        INFO.integrationKey == 'key'
        INFO.itemType == 'type'
        INFO.rootItemType == 'root type'
    }

    @Test
    def 'info can be instantiated with null values'() {
        given:
        def i = new ChangeInfo(null, null, null)

        expect:
        with(i) {
            !integrationKey
            !itemType
            !rootItemType
        }
    }

    @Test
    @Unroll
    def "#info1 equals #info2"() {
        expect:
        info1 == info2

        where:
        info1                            | info2
        INFO                             | INFO
        INFO                             | new ChangeInfo(INFO.integrationKey, INFO.itemType, INFO.rootItemType)
        new ChangeInfo(null, null, null) | new ChangeInfo(null, null, null)
    }

    @Test
    @Unroll
    def "#info1 not equals #info2"() {
        expect:
        info1 != info2

        where:
        info1                            | info2
        INFO                             | null
        INFO                             | new ChangeInfo(null, INFO.itemType, INFO.rootItemType)
        INFO                             | new ChangeInfo(INFO.integrationKey, null, INFO.rootItemType)
        INFO                             | new ChangeInfo(INFO.integrationKey, INFO.itemType, null)
        new ChangeInfo(null, null, null) | INFO
    }

    @Test
    @Unroll
    def "#info1 hash code equals #info2 hash code"() {
        expect:
        info1.hashCode() == info2.hashCode()

        where:
        info1                            | info2
        INFO                             | INFO
        INFO                             | new ChangeInfo(INFO.integrationKey, INFO.itemType, INFO.rootItemType)
        new ChangeInfo(null, null, null) | new ChangeInfo(null, null, null)
    }

    @Test
    @Unroll
    def "#info1 has code not equals #info2 hash code"() {
        expect:
        info1.hashCode() != info2.hashCode()

        where:
        info1                            | info2
        INFO                             | new ChangeInfo(null, INFO.itemType, INFO.rootItemType)
        INFO                             | new ChangeInfo(INFO.integrationKey, null, INFO.rootItemType)
        INFO                             | new ChangeInfo(INFO.integrationKey, INFO.itemType, null)
        new ChangeInfo(null, null, null) | INFO
    }
}
