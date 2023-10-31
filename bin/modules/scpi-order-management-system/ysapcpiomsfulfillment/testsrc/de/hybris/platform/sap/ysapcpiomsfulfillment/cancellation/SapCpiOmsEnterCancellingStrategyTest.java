/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.ysapcpiomsfulfillment.cancellation;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.anyObject;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordercancel.model.OrderCancelRecordEntryModel;
import de.hybris.platform.ordermodify.model.OrderEntryModificationRecordEntryModel;
import de.hybris.platform.ordermodify.model.OrderModificationRecordModel;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderCancellationModel;
import de.hybris.platform.sap.sapcpiadapter.service.SapCpiOutboundService;
import de.hybris.platform.sap.sapcpiorderexchange.service.SapCpiOrderOutboundConversionService;
import de.hybris.platform.sap.sapmodel.model.SAPOrderModel;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.lenient;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.google.common.collect.Maps;

import rx.Observable;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SapCpiOmsEnterCancellingStrategyTest
{
	@InjectMocks
	private SapCpiOmsEnterCancellingStrategy sapCpiOmsEnterCancellingStrategy;
	@Mock
	private SapCpiOutboundService sapCpiOutboundService;
	@Mock
	private SapCpiOrderOutboundConversionService sapCpiOrderOutboundConversionService;
	@Mock
	private ModelService modelService;
	private final OrderModificationRecordModel orderModificationRecordModel = new OrderModificationRecordModel();
	private final OrderCancelRecordEntryModel orderCancelRecordEntryModel = new OrderCancelRecordEntryModel();
	private final OrderEntryModificationRecordEntryModel orderEntryModificationRecordEntryModel = new OrderEntryModificationRecordEntryModel();
	private final SAPCpiOutboundOrderCancellationModel sapCpiOutboundOrderCancellationModel = new SAPCpiOutboundOrderCancellationModel();

	@Before
	public void setup()
	{
		final OrderModel orderModel = new OrderModel();
		final OrderEntryModel orderEntryModel = new OrderEntryModel();
		final SAPOrderModel sapOrderModel = new SAPOrderModel();
		final ConsignmentModel consignmentModel = new ConsignmentModel();
		final ConsignmentEntryModel consignmentEntryModel = new ConsignmentEntryModel();

		orderModel.setCode("9999999675");
		sapOrderModel.setCode("9999999676");
		consignmentModel.setSapOrder(sapOrderModel);
		consignmentEntryModel.setConsignment(consignmentModel);
		orderEntryModel.setConsignmentEntries(Set.of(consignmentEntryModel));
		orderEntryModificationRecordEntryModel.setOrderEntry(orderEntryModel);
		orderModificationRecordModel.setOrder(orderModel);
		orderCancelRecordEntryModel.setModificationRecord(orderModificationRecordModel);
		orderCancelRecordEntryModel.setOrderEntriesModificationEntries(Arrays.asList(orderEntryModificationRecordEntryModel));

		final Map<String, Map> map = Maps.newHashMap();
		final Map<String, String> innerMap = Maps.newHashMap();
		innerMap.put("responseMessage", "The order cancellation has been sent successfully to S/4HANA through SCPI!");
		innerMap.put("responseStatus", "Success");
		map.put("SAPCpiOutboundOrderCancellation", innerMap);
		final ResponseEntity<Map> objectResponseEntity = new ResponseEntity<>(map, HttpStatus.OK);

		lenient().doNothing().when(modelService).save(any());
		when(sapCpiOutboundService.sendOrderCancellation(anyObject())).thenReturn(Observable.just(objectResponseEntity));

	}

	@Test
	public void verifySendOrderCancellationIsCalled()
	{
		sapCpiOutboundOrderCancellationModel.setOrderId("9999999676");
		when(sapCpiOrderOutboundConversionService.convertCancelOrderToSapCpiCancelOrder(orderCancelRecordEntryModel)).thenReturn(
				Arrays.asList(sapCpiOutboundOrderCancellationModel));

		sapCpiOmsEnterCancellingStrategy.changeOrderStatusAfterCancelOperation(orderCancelRecordEntryModel, false);
		verify(sapCpiOutboundService, times(1)).sendOrderCancellation(sapCpiOutboundOrderCancellationModel);
	}

	@Test
	public void verifySendOrderCancellationIsNotCalled()
	{
		sapCpiOutboundOrderCancellationModel.setOrderId("9999999677");
		when(sapCpiOrderOutboundConversionService.convertCancelOrderToSapCpiCancelOrder(orderCancelRecordEntryModel)).thenReturn(
				Arrays.asList(sapCpiOutboundOrderCancellationModel));

		sapCpiOmsEnterCancellingStrategy.changeOrderStatusAfterCancelOperation(orderCancelRecordEntryModel, false);
		verify(sapCpiOutboundService, times(0)).sendOrderCancellation(sapCpiOutboundOrderCancellationModel);

	}

}
