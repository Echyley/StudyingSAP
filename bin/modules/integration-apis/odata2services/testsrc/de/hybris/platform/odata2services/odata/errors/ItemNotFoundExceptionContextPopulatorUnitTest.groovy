/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.odata2services.odata.errors

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.integrationservices.exception.InvalidAttributeValueException
import de.hybris.platform.integrationservices.item.IntegrationItem
import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor
import de.hybris.platform.integrationservices.model.TypeDescriptor
import de.hybris.platform.integrationservices.search.ItemNotFoundException
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.apache.olingo.odata2.api.commons.HttpStatusCodes
import org.apache.olingo.odata2.api.processor.ODataErrorContext
import org.junit.Test

@UnitTest
class ItemNotFoundExceptionContextPopulatorUnitTest extends JUnitPlatformSpecification {
    def populator = new ItemNotFoundExceptionContextPopulator()

    @Test
    def 'does not populate context values if context does not contain ItemNotFoundException'() {
        given:
        def attributeDesc = Stub(TypeAttributeDescriptor)
        def context = new ODataErrorContext(exception: new InvalidAttributeValueException('value', attributeDesc))

        when:
        populator.populate context

        then:
        with(context) {
            !httpStatus
            !errorCode
            !message
            !locale
            !innerError
        }
    }

    @Test
    def 'populates error context values'() {
        given:
        def key = "some key"
        def type = 'some type'
        def item = stubIntegrationItem(key, type)
        def exception = new ItemNotFoundException(item)
        def context = new ODataErrorContext(exception: exception)

        when:
        populator.populate context

        then:
        with(context) {
            httpStatus == HttpStatusCodes.NOT_FOUND
            errorCode == 'not_found'
            message == "[$type] with integration key [$key] was not found."
            locale == Locale.ENGLISH
            innerError == key
        }
    }

    @Test
    def 'handles ItemNotFoundException'() {
        expect:
        populator.exceptionClass == ItemNotFoundException
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
