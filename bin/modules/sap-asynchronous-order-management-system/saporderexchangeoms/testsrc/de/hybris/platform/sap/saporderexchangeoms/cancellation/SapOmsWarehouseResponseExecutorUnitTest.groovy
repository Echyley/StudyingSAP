/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saporderexchangeoms.cancellation

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.core.model.order.OrderEntryModel
import de.hybris.platform.core.model.order.OrderModel
import de.hybris.platform.warehousing.cancellation.OmsOrderCancelService
import org.junit.Test
import spock.lang.Issue
import de.hybris.platform.testframework.JUnitPlatformSpecification

import de.hybris.platform.ordercancel.OrderCancelEntry
import de.hybris.platform.ordercancel.OrderCancelNotificationServiceAdapter
import de.hybris.platform.ordercancel.OrderCancelPaymentServiceAdapter
import de.hybris.platform.ordercancel.OrderCancelRecordsHandler
import de.hybris.platform.ordercancel.OrderCancelResponse
import de.hybris.platform.ordercancel.OrderStatusChangeStrategy
import de.hybris.platform.ordercancel.model.OrderCancelRecordEntryModel
import de.hybris.platform.servicelayer.model.ModelService

@UnitTest
@Issue('https://cxjira.sap.com/browse/GRIFFIN-4770')
class SapOmsWarehouseResponseExecutorUnitTest extends JUnitPlatformSpecification {
    def omsOrderCancelService = Stub OmsOrderCancelService
    def cancelRequestRecordEntry = new OrderCancelRecordEntryModel()
    def sapConsignmentCancellationService = Mock(SapConsignmentCancellationService)
    def order = buildOrder()
    def sapOmsWarehouseResponseExecutor = new SapOmsWarehouseResponseExecutor(
            omsOrderCancelService: omsOrderCancelService,
            sapConsignmentCancellationService: sapConsignmentCancellationService,
            orderCancelRecordsHandler: Stub(OrderCancelRecordsHandler),
            modelService: Stub(ModelService),
            paymentServiceAdapter: Stub(OrderCancelPaymentServiceAdapter),
            completeCancelStatusChangeStrategy: Stub(OrderStatusChangeStrategy),
            partialCancelStatusChangeStrategy: Stub(OrderStatusChangeStrategy),
            notificationServiceAdapter: Stub(OrderCancelNotificationServiceAdapter)
    )
    
    @Test
    def "no exception is thrown when when order cancelled response was denied"() {
        given:
        def cancelDeniedResponse = new OrderCancelResponse(order, OrderCancelResponse.ResponseStatus.denied, "Order shipped")
        and:
        omsOrderCancelService.processOrderCancel(cancelRequestRecordEntry) >> []

        when:
        sapOmsWarehouseResponseExecutor.processCancelResponse(cancelDeniedResponse, cancelRequestRecordEntry)
        then:
        noExceptionThrown()
    }

    @Test
    def "cancel order is processed when order was not shipped"() {
        given:
        def cancelDeniedResponse = new OrderCancelResponse(order)

        and:
        omsOrderCancelService.processOrderCancel(cancelRequestRecordEntry) >> [Stub(OrderCancelEntry) {
            getOrderEntry() >> orderEntryModel(order)
        }]

        when:
        sapOmsWarehouseResponseExecutor.processCancelResponse(cancelDeniedResponse, cancelRequestRecordEntry)
        then:
        1 * sapConsignmentCancellationService.processSapConsignmentCancellation(cancelDeniedResponse)
    }

    private buildOrder(){
        def order = Stub(OrderModel){
            getCode() >> "O-K2010-C0000-001"
        }

        def orderEntry = orderEntryModel order
        order.getEntries() >> [orderEntry]
        order
    }

    OrderEntryModel orderEntryModel(final OrderModel order){
        Stub(OrderEntryModel){
            getEntryNumber() >> Integer.valueOf(1)
            getOrder() >> order
        }
    }
}
