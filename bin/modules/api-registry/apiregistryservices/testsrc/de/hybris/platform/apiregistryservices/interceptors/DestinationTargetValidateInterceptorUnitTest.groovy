/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.apiregistryservices.interceptors

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.apiregistryservices.constants.ApiregistryservicesConstants
import de.hybris.platform.apiregistryservices.enums.DestinationChannel
import de.hybris.platform.apiregistryservices.exceptions.DestinationTargetValidationException
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel
import de.hybris.platform.apiregistryservices.model.DestinationTargetModel
import de.hybris.platform.apiregistryservices.services.ConsumedDestinationVerifyUsageService
import de.hybris.platform.core.model.ItemModel
import de.hybris.platform.servicelayer.interceptor.InterceptorContext
import de.hybris.platform.testframework.JUnitPlatformSpecification
import de.hybris.platform.servicelayer.model.ItemModelContext
import org.junit.Test

@UnitTest
class DestinationTargetValidateInterceptorUnitTest extends JUnitPlatformSpecification {

    private static final def NAME_ATTRIBUTE = 'itemName'
    private static final def CONSUMED_DESTINATION_ID = 'consumedDestinationId'
    private static final def ITEM_TYPE_CODE = 'typeCode'
    private static final def ITEM_DESTINATION_ATTRIBUTE = 'attribute'

    def consumedDestinationVerifyUsageService = Stub(ConsumedDestinationVerifyUsageService)
    def consumedDestinationPreventRemoveList = []
    def interceptor = new DestinationTargetValidateInterceptor(consumedDestinationVerifyUsageService, consumedDestinationPreventRemoveList)

    def item = Stub(ItemModel) {
        getProperty(_) >> NAME_ATTRIBUTE
    }

    def consumedDestination = Stub(ConsumedDestinationModel) {
        getId() >> CONSUMED_DESTINATION_ID
        getDestinationTarget() >> Stub(DestinationTargetModel) {
            getTemplate() >> false
        }
    }

    def destinationTarget = Stub(DestinationTargetModel) {
        getDestinationChannel() >> DestinationChannel.DEFAULT
        getTemplate() >> false
        getItemModelContext() >> Stub(ItemModelContext) {
            getOriginalValue(DestinationTargetModel.TEMPLATE) >> Boolean.FALSE
            getOriginalValue(DestinationTargetModel.DESTINATIONCHANNEL) >> DestinationChannel.DEFAULT
        }
    }

    @Test
    def "null #description fails precondition check"() {
        when:
        new DestinationTargetValidateInterceptor(consumedDestinationService, list)
        then:
        thrown IllegalArgumentException

        where:
        description                             | consumedDestinationService                  | list
        "consumedDestinationVerifyUsageService" | null                                        | []
        "consumedDestinationPreventResetList"   | Stub(ConsumedDestinationVerifyUsageService) | null
    }

    @Test
    def "successfully assign a ConsumeDestination to DestinationTarget when it is not assigned to any item model"() {
        given:
        consumedDestinationVerifyUsageService.findModelsAssignedConsumedDestination(_, _, _) >> Optional.empty()

        and: "assign ConsumedDestination to DestinationTarget"
        destinationTarget.getDestinations() >> [consumedDestination]

        when:
        interceptor.onValidate destinationTarget, Stub(InterceptorContext)

        then:
        noExceptionThrown()
    }

    @Test
    def "throw an exception when assign a ConsumeDestination to DestinationTarget that is assigned to an item model"() {
        given:
        consumedDestinationPreventRemoveList.add([(ApiregistryservicesConstants.ITEM_TYPE_CODE)            : ITEM_TYPE_CODE,
                                                  (ApiregistryservicesConstants.ITEM_DESTINATION_ATTRIBUTE): ITEM_DESTINATION_ATTRIBUTE])

        consumedDestinationVerifyUsageService.findModelsAssignedConsumedDestination(ITEM_TYPE_CODE, ITEM_DESTINATION_ATTRIBUTE, consumedDestination) >> Optional.of([item])


        and:
        consumedDestination.getDestinationTarget().getDestinationChannel() >> null

        and: "assign ConsumedDestination to DestinationTarget"
        destinationTarget.getDestinations() >> [consumedDestination]

        when:
        interceptor.onValidate destinationTarget, Stub(InterceptorContext)

        then:
        def e = thrown DestinationTargetValidationException
        e.message.contains "ConsumedDestinationModel [$CONSUMED_DESTINATION_ID] cannot be assigned to Destination Target"
    }
}
