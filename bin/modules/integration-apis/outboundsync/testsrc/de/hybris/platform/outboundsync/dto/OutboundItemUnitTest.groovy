/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.outboundsync.dto

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.core.model.ItemModel
import de.hybris.platform.integrationservices.model.IntegrationObjectDescriptor
import de.hybris.platform.integrationservices.model.TypeDescriptor
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test

@UnitTest
class OutboundItemUnitTest extends JUnitPlatformSpecification {
    private static final def CHANGED_ITEM = new ItemModel()

    @Test
    def 'retrieves type descriptor for item present in the IO model'() {
        given: 'IO contains an item for the changed item'
        def io = Stub(IntegrationObjectDescriptor) {
            getTypeDescriptor(CHANGED_ITEM) >> Optional.of(Stub(TypeDescriptor))
        }

        expect:
        outboundItem(io).typeDescriptor.present
    }

    @Test
    def 'does not retrieve type descriptor for item not present in the IO model'() {
        given: 'IO does not contain an item for the changed item'
        def io = Stub(IntegrationObjectDescriptor) {
            getTypeDescriptor(CHANGED_ITEM) >> Optional.empty()
        }

        expect:
        outboundItem(io).typeDescriptor.empty
    }

    private static OutboundItem outboundItem(IntegrationObjectDescriptor io) {
        OutboundItem.outboundItem()
                .withChangedItemModel(CHANGED_ITEM)
                .withIntegrationObject(io)
                .build()
    }
}
