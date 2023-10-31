/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationservices.model.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.core.model.ItemModel
import de.hybris.platform.core.model.type.AttributeDescriptorModel
import de.hybris.platform.servicelayer.model.ModelService
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import spock.lang.Unroll

@UnitTest
class SettableForExistingItemAttributeHandlerUnitTest extends JUnitPlatformSpecification {

    def modelService = Stub(ModelService)
    def handler = new SettableForExistingItemAttributeHandler(modelService)

    @Test
    @Unroll
    def "isApplicable returns #result when isNew is #isApplicableNew"() {
        given:
        def itemType = 'String'
        def item = Stub(ItemModel) {
            getItemtype() >> itemType
        }

        modelService.isNew(_) >> isApplicableNew


        expect:
        handler.isApplicable(item) == result

        where:
        isApplicableNew | result
        true            | false
        false           | true
    }

    @Test
    @Unroll
    def "isSettable returns #result when isWritable=#isWritable"() {
        given:
        def attributeDescriptor = Stub(AttributeDescriptorModel) {
            getWritable() >> isWritable
        }

        expect:
        handler.isSettable(attributeDescriptor) == result

        where:
        isWritable | result
        true       | true
        false      | false
        null       | false
    }
}
