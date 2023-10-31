/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationservices.search.validation

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.integrationservices.item.IntegrationItem
import de.hybris.platform.integrationservices.search.ItemSearchRequest
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test

@UnitTest
class RequestedItemPresenceValidatorUnitTest extends JUnitPlatformSpecification {
    def validator = new RequestedItemPresenceValidator()

    @Test
    def 'a request with requested item passes the validation'() {
        given:
        def request = Stub(ItemSearchRequest) {
            getRequestedItem() >> Optional.of(Stub(IntegrationItem))
        }

        when:
        validator.validate(request)

        then:
        notThrown Throwable
    }

    @Test
    def 'rejects a request without requested item'() {
        given:
        def request = Stub(ItemSearchRequest) {
            getRequestedItem() >> Optional.empty()
        }

        when:
        validator.validate(request)

        then:
        def e = thrown MissingRequestedItemException
        e.message.contains "$request"
    }
}
