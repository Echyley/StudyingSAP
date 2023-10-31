/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.inboundservices.persistence.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.inboundservices.persistence.PersistenceContext
import de.hybris.platform.integrationservices.item.IntegrationItem
import de.hybris.platform.integrationservices.model.TypeDescriptor
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test

@UnitTest
class PersistenceFailedExceptionUnitTest extends JUnitPlatformSpecification {
    private static final def ITEM_TYPE = 'TestItem'

    @Test
    def 'exception carries correct context'() {
        given:
        def context = persistenceContext(ITEM_TYPE)
        def rootCause = new Throwable()

        when:
        def exception = new PersistenceFailedException(context, rootCause)

        then:
        with(exception) {
            persistenceContext.is context
            cause.is rootCause
            message == "Failed to persist item model of [$ITEM_TYPE] type"
        }
    }

    @Test
    def 'exception handles null parameters'() {
        given:
        def exception = new PersistenceFailedException(null, null)

        expect:
        with(exception) {
            !persistenceContext
            !cause
            message == 'Failed to persist item model of [null] type'
        }
    }

    private PersistenceContext persistenceContext(String type) {
        Stub(PersistenceContext) {
            getIntegrationItem() >> Stub(IntegrationItem) {
                getItemType() >> Stub(TypeDescriptor) {
                    getItemCode() >> type
                }
            }
        }
    }
}
