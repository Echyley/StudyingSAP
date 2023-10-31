/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.apiregistryservices.interceptors

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.apiregistryservices.constants.ApiregistryservicesConstants
import de.hybris.platform.apiregistryservices.exceptions.ConsumedDestinationRemoveException
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel
import de.hybris.platform.apiregistryservices.services.ConsumedDestinationVerifyUsageService
import de.hybris.platform.core.model.ItemModel
import de.hybris.platform.servicelayer.interceptor.InterceptorContext
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import spock.lang.Issue
import spock.lang.Unroll

@UnitTest
@Issue('https://cxjira.sap.com/browse/IAPI-4703')
class RemoveConsumedDestinationInterceptorUnitTest extends JUnitPlatformSpecification {

    private static final def NAME_ATTRIBUTE = 'itemName'
    def consumedDestinationVerifyUsageService = Stub(ConsumedDestinationVerifyUsageService)
    def consumedDestinationPreventRemoveList = []
    def interceptor = new RemoveConsumedDestinationInterceptor(consumedDestinationVerifyUsageService, consumedDestinationPreventRemoveList)

    def item = Mock(ItemModel) {
        getProperty(_) >> NAME_ATTRIBUTE
    }
    def consumedDestination = Mock(ConsumedDestinationModel){
        getId() >> "test"
    }

    @Test
    @Unroll
    def "throw an exception when deleting a consumed destination that is assigned to an item model"() {
        given:
        consumedDestinationVerifyUsageService.findModelsAssignedConsumedDestination(_,_,_) >> Optional.of([item])
        consumedDestinationPreventRemoveList.add([(ApiregistryservicesConstants.ITEM_TYPE_CODE):'TYPECODE',
                                                  (ApiregistryservicesConstants.ITEM_DESTINATION_ATTRIBUTE):'ATTRIBUTE',
                                                  (ApiregistryservicesConstants.ITEM_NAME_ATTRIBUTE):nameAttributeInfo])

        when:
        interceptor.onRemove consumedDestination, Stub(InterceptorContext)

        then:
        def e = thrown ConsumedDestinationRemoveException
        e.message.contains "ConsumedDestinationModel [test] cannot be deleted because it is used in one or more [TYPECODE${result}"

        where:
        nameAttributeInfo | result
        null              | ""
        "nameAttribute"   | " - ${NAME_ATTRIBUTE}"
    }

    @Test
    def "successfully delete a consumed destination when it is not assigned to any item model"() {
        given:
        consumedDestinationVerifyUsageService.findModelsAssignedConsumedDestination(_,_,_) >> Optional.empty()

        when:
        interceptor.onRemove consumedDestination, Stub(InterceptorContext)

        then:
        noExceptionThrown()
    }

    @Test
    def "successfully delete a consumed destination when consumedDestinationPreventRemoveList is empty"() {
        given:
        consumedDestinationVerifyUsageService.findModelsAssignedConsumedDestination(_,_,_) >> Optional.of([item])
        consumedDestinationPreventRemoveList = []
        interceptor = new RemoveConsumedDestinationInterceptor(consumedDestinationVerifyUsageService, consumedDestinationPreventRemoveList)

        when:
        interceptor.onRemove consumedDestination, Stub(InterceptorContext)

        then:
        noExceptionThrown()
    }

    @Test
    @Unroll
    def "null #description fails precondition check"()
    {
        when:
        new RemoveConsumedDestinationInterceptor(consumedDestinationService, list)
        then:
        thrown(IllegalArgumentException)

        where:
        description                             | consumedDestinationService                  | list
        "consumedDestinationVerifyUsageService" | null                                        | []
        "consumedDestinationPreventRemoveList"  | Stub(ConsumedDestinationVerifyUsageService) | null
    }
}
