/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationservices.model.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import spock.lang.Unroll

@UnitTest
class DescriptorUtilsUnitTest extends JUnitPlatformSpecification {
    private static final def IO_ITEM = new IntegrationObjectItemModel()

    @Test
    @Unroll
    def "item model extracted from a type descriptor is #res when the type descriptor is #condition"() {
        expect:
        DescriptorUtils.extractModelFrom(type) == res

        where:
        condition         | type                      | res
        'null'            | null                      | Optional.empty()
        'for a primitive' | primitiveTypeDescriptor() | Optional.empty()
        'for an item'     | itemTypeDescriptor()      | Optional.of(IO_ITEM)
    }

    private ItemTypeDescriptor itemTypeDescriptor() {
        Stub(ItemTypeDescriptor) {
            getItemTypeModel() >> IO_ITEM
        }
    }

    private PrimitiveTypeDescriptor primitiveTypeDescriptor() {
        Stub(PrimitiveTypeDescriptor)
    }
}
