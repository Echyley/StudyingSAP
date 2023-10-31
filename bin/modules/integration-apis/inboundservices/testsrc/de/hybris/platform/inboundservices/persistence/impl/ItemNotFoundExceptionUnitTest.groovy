/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.inboundservices.persistence.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.integrationservices.item.IntegrationItem
import de.hybris.platform.integrationservices.model.TypeDescriptor
import de.hybris.platform.integrationservices.search.ItemNotFoundException
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test

@UnitTest
class ItemNotFoundExceptionUnitTest extends JUnitPlatformSpecification {
    @Test
    def 'exception context is correctly populated when integration item is provided'() {
        given:
        def key = "some key"
        def type = 'some type'
        def item = stubIntegrationItem(key, type)

        when:
        def ex = new ItemNotFoundException(item)

        then:
        with(ex) {
            requestedItem == item
            integrationKey == item.integrationKey
            message.contains item.integrationKey
            message.contains type
        }
    }

    @Test
    def 'exception context is not populated when integration item is not provided'() {
        given:
        def ex = new ItemNotFoundException(null)

        expect:
        !ex.requestedItem
        !ex.integrationKey
        ex.message == "[null] with integration key [null] was not found."
    }

    private IntegrationItem stubIntegrationItem(key, type) {
        Stub(IntegrationItem) {
            getIntegrationKey() >> key
            getItemType() >> Stub(TypeDescriptor) {
                getItemCode() >> type
            }
        }
    }
}
