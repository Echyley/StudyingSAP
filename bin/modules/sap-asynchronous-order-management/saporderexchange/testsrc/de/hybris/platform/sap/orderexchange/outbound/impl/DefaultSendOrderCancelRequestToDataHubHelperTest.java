/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.orderexchange.outbound.impl;

import de.hybris.platform.basecommerce.enums.CancelReason;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.ordercancel.model.OrderCancelRecordEntryModel;
import de.hybris.platform.ordermodify.model.OrderModificationRecordModel;
import de.hybris.platform.sap.orderexchange.outbound.RawItemContributor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.hybris.platform.sap.orderexchange.outbound.impl.DefaultOrderCancelRequestBuilder;
import de.hybris.platform.sap.orderexchange.outbound.impl.DefaultOrderCancelRequestContributor;
import de.hybris.platform.sap.orderexchange.outbound.impl.DefaultSendOrderCancelRequestToDataHubHelper;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.hybris.datahub.core.services.DataHubOutboundService;


@SuppressWarnings("javadoc")
public class DefaultSendOrderCancelRequestToDataHubHelperTest
{
	@Mock
	private DataHubOutboundService dataHubOutboundServiceMock;

	private final DefaultSendOrderCancelRequestToDataHubHelper cut = new DefaultSendOrderCancelRequestToDataHubHelper();

	@SuppressWarnings(
	{ "rawtypes" })
	@Test
	public void testRunHeaderLine() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		final OrderModel order = new OrderModel();
		final OrderModificationRecordModel orderModificationRecord = new OrderModificationRecordModel();
		final OrderCancelRecordEntryModel orderCancelRecordEntry = new OrderCancelRecordEntryModel();
		final DefaultOrderCancelRequestBuilder csvBuilder = new DefaultOrderCancelRequestBuilder();
		final DefaultOrderCancelRequestContributor csvContributor = new DefaultOrderCancelRequestContributor();
		
		Map<String, String> batchIdAttributes = new HashMap<String, String>();
		batchIdAttributes.put("dh_batchId", "000001");
		batchIdAttributes.put("dh_sourceId", "HYBRIS");
		batchIdAttributes.put("dh_type", "ORDERS05");
		
		csvContributor.setBatchIdAttributes(batchIdAttributes);
		

		csvBuilder.setContributors(Arrays.asList((RawItemContributor<OrderCancelRecordEntryModel>) csvContributor));

		order.setCode("OrderCode");
		final OrderEntryModel orderEntryModel = new OrderEntryModel();
		orderEntryModel.setOrder(order);
		orderEntryModel.setEntryNumber(Integer.valueOf(10));
		orderEntryModel.setProduct(new ProductModel());
		order.setEntries((List) Arrays.asList(orderEntryModel));

		cut.setRawItemType("BLA");
		cut.setRawItemBuilder(csvBuilder);
		cut.setDataHubOutboundService(dataHubOutboundServiceMock);


		orderModificationRecord.setOrder(order);
		orderCancelRecordEntry.setModificationRecord(orderModificationRecord);
		orderCancelRecordEntry.setCancelReason(CancelReason.LATEDELIVERY);
		cut.createAndSendRawItem(orderCancelRecordEntry);
		final Map<String, Object> expectedMap = new HashMap<>();
		expectedMap.put("rejectionReason", "LateDelivery");
		expectedMap.put("entryNumber", Integer.valueOf(10));
		expectedMap.put("productCode", "");
		expectedMap.put("orderId", "OrderCode");
		expectedMap.put("dh_batchId", "OrderCode");
		expectedMap.put("dh_sourceId", "HYBRIS");
		expectedMap.put("dh_type", "ORDERS05");
		final List<Map<String, Object>> expectedList = new LinkedList<>();
		expectedList.add(expectedMap);
		Mockito.verify(dataHubOutboundServiceMock, Mockito.times(1)).sendToDataHub(Mockito.eq("DEFAULT_FEED"), Mockito.eq("BLA"),
				Mockito.eq(expectedList));
	}
}
