/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationservices.model.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.core.model.ItemModel
import de.hybris.platform.integrationservices.model.TypeDescriptor
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test

@UnitTest
class NullIntegrationObjectDescriptorUnitTest extends JUnitPlatformSpecification {

    @Test
    def "builds a descriptor with the expected values when a code is provided"() {
        when:
        def ioCode = "ioCode"
        def descriptor = new NullIntegrationObjectDescriptor(ioCode)

        then:
        with(descriptor) {
            code == ioCode
            itemTypeDescriptors.isEmpty()
            getItemTypeDescriptor(new ItemModel()).empty
            getTypeDescriptor(new Object()).empty
            rootItemType.empty
            rootType.empty
        }
    }

    @Test
    def "Descriptor code is created with empty string when request is built with #condition code"() {
        when:
        def descriptor = new NullIntegrationObjectDescriptor(code)

        then:
        descriptor.code == ""

        where:
        condition | code
        "null"    | null
        "empty"   | ""
    }

    @Test
    def 'contains always returns false'() {
        given:
        def descriptor = new NullIntegrationObjectDescriptor('DoesNotMatter')

        and:
        def anyType = Stub TypeDescriptor

        expect:
        !descriptor.contains(anyType)
    }
}
