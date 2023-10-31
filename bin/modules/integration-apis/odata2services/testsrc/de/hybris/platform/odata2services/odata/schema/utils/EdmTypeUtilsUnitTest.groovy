/*
 *  Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.odata.schema.utils

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.apache.olingo.odata2.api.edm.EdmSimpleTypeKind
import org.junit.Test

@UnitTest
class EdmTypeUtilsUnitTest extends JUnitPlatformSpecification {

    @Test
    void 'convert null throws an IllegalArgumentException'() {
        when:
        EdmTypeUtils.convert(null)

        then:
        def e = thrown IllegalArgumentException
        e.message.containsIgnoreCase 'type should never be null'
    }

    @Test
    void "convert #javaType converts to Edm Type #edmType"() {
        expect:
        EdmTypeUtils.convert(javaType.name) == edmType

        where:
        javaType       | edmType
        BigDecimal     | EdmSimpleTypeKind.Decimal
        BigInteger     | EdmSimpleTypeKind.String
        String         | EdmSimpleTypeKind.String
        Double         | EdmSimpleTypeKind.Double
        Double.TYPE    | EdmSimpleTypeKind.Double
        Integer        | EdmSimpleTypeKind.Int32
        Integer.TYPE   | EdmSimpleTypeKind.Int32
        Long           | EdmSimpleTypeKind.Int64
        Long.TYPE      | EdmSimpleTypeKind.Int64
        Boolean        | EdmSimpleTypeKind.Boolean
        Boolean.TYPE   | EdmSimpleTypeKind.Boolean
        Date           | EdmSimpleTypeKind.DateTime
        Byte           | EdmSimpleTypeKind.SByte
        Byte.TYPE      | EdmSimpleTypeKind.SByte
        Float          | EdmSimpleTypeKind.Single
        Float.TYPE     | EdmSimpleTypeKind.Single
        Short          | EdmSimpleTypeKind.Int16
        Short.TYPE     | EdmSimpleTypeKind.Int16
        Object         | EdmSimpleTypeKind.String
        Class          | EdmSimpleTypeKind.String
        Character      | EdmSimpleTypeKind.String
        Character.TYPE | EdmSimpleTypeKind.String
    }
}
