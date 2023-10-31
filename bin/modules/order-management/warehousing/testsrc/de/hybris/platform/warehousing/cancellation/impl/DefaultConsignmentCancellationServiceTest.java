/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.warehousing.cancellation.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.enums.CancelReason;
import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.commerceservices.util.GuidKeyGenerator;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.ordercancel.OrderCancelEntry;
import de.hybris.platform.ordercancel.OrderCancelResponse;
import de.hybris.platform.ordermodify.model.OrderEntryModificationRecordEntryModel;
import de.hybris.platform.ordermodify.model.OrderModificationRecordEntryModel;
import de.hybris.platform.ordermodify.model.OrderModificationRecordModel;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.warehousing.comment.WarehousingCommentService;
import de.hybris.platform.warehousing.data.comment.WarehousingCommentContext;
import de.hybris.platform.warehousing.inventoryevent.service.InventoryEventService;
import de.hybris.platform.warehousing.model.CancellationEventModel;
import de.hybris.platform.warehousing.process.WarehousingBusinessProcessService;
import de.hybris.platform.warehousing.taskassignment.services.WarehousingConsignmentWorkflowService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultConsignmentCancellationServiceTest
{
	@InjectMocks
	private final DefaultConsignmentCancellationService cancellationService = new DefaultConsignmentCancellationService();

	@Mock
	private InventoryEventService inventoryEventService;
	@Mock
	private ModelService modelService;
	@Mock
	private WarehousingCommentService consignmentEntryCommentService;
	@Mock
	private WarehousingBusinessProcessService<ConsignmentModel> consignmentBusinessProcessService;
	@Mock
	private WarehousingConsignmentWorkflowService warehousingConsignmentWorkflowService;
	@Mock
	private OrderCancelResponse orderCancelResponse;
	@Mock
	private ProductModel mouse;
	@Mock
	private ProductModel batteries;
	@Mock
	private UserModel author;
	@Spy
	private ConsignmentModel consignment1;
	@Spy
	private ConsignmentModel consignment2;
	@Spy
	private ConsignmentEntryModel consignmentEntry1;
	@Spy
	private ConsignmentEntryModel consignmentEntry2;
	@Spy
	private ConsignmentEntryModel consignmentEntry3;
	@Spy
	private ConsignmentEntryModel consignmentEntry4;
	@Spy
	private WarehouseModel warehouse;
	@Spy
	private OrderEntryModel orderEntry1;
	@Spy
	private OrderEntryModel orderEntry2;
	@Spy
	private OrderEntryModel originalOrderEntry1;
	@Spy
	private OrderEntryModel originalOrderEntry2;
	private OrderModel order;
	private CancellationEventModel consignmentCancellationEvent;
	private CancellationEventModel orderCancellationEvent;
	private GuidKeyGenerator guidKeyGenerator;

	@Before
	public void setUp()
	{
		order = new OrderModel();
		orderEntry1.setEntryNumber(1);
		orderEntry1.setQuantity(20L);
		orderEntry1.setOrder(order);
		orderEntry2.setEntryNumber(2);
		orderEntry2.setQuantity(10L);
		orderEntry2.setOrder(order);
		order.setEntries(Lists.newArrayList(orderEntry1, orderEntry2));
		order.setUser(author);

		warehouse.setExternal(true);

		consignment1.setCode("consignment1234_1");
		consignment1.setWarehouse(warehouse);
		consignmentEntry1.setOrderEntry(orderEntry1);
		consignmentEntry1.setQuantity(10L);
		consignmentEntry1.setConsignment(consignment1);

		consignmentEntry2.setOrderEntry(orderEntry2);
		consignmentEntry2.setQuantity(5L);
		consignmentEntry2.setConsignment(consignment1);
		consignment1.setConsignmentEntries(Sets.newHashSet(consignmentEntry1, consignmentEntry2));
		orderEntry1.setConsignmentEntries(Sets.newHashSet(consignmentEntry1, consignmentEntry2));

		consignment2 = new ConsignmentModel();
		consignment2.setCode("consignment1234_2");
		consignmentEntry3.setOrderEntry(orderEntry1);
		consignmentEntry3.setQuantity(10L);

		consignmentEntry4.setOrderEntry(orderEntry2);
		consignmentEntry4.setQuantity(5L);
		consignment2.setConsignmentEntries(Sets.newHashSet(consignmentEntry3, consignmentEntry4));
		orderEntry2.setConsignmentEntries(Sets.newHashSet(consignmentEntry3, consignmentEntry4));

		order.setConsignments(Sets.newHashSet(consignment1, consignment2));
		consignmentCancellationEvent = new CancellationEventModel();
		when(modelService.create(CancellationEventModel.class)).thenReturn(consignmentCancellationEvent);
		orderCancellationEvent = new CancellationEventModel();
		Mockito.lenient().when(modelService.create(CancellationEventModel.class)).thenReturn(orderCancellationEvent);

		Mockito.lenient().when(orderEntry1.getProduct()).thenReturn(mouse);
		Mockito.lenient().when(mouse.getName()).thenReturn("Wireless Mouse");
		Mockito.lenient().when(orderEntry2.getProduct()).thenReturn(batteries);
		Mockito.lenient().when(batteries.getName()).thenReturn("Rechargeable batteries");

		guidKeyGenerator = new GuidKeyGenerator();
		cancellationService.setGuidKeyGenerator(guidKeyGenerator);
		cancellationService.setNonCancellableConsignmentStatus(
				Lists.newArrayList(ConsignmentStatus.CANCELLED, ConsignmentStatus.PICKUP_COMPLETE, ConsignmentStatus.SHIPPED));
		cancellationService.setConsignmentBusinessProcessService(consignmentBusinessProcessService);
		cancellationService.setWarehousingConsignmentWorkflowService(warehousingConsignmentWorkflowService);
	}

	@Test
	public void shouldCancelConsignment()
	{
		// Given
		final OrderCancelEntry cancellationEntry = new OrderCancelEntry(orderEntry1, 10L, "notes", CancelReason.LATEDELIVERY);
		when(orderCancelResponse.getEntriesToCancel()).thenReturn(ImmutableList.of(cancellationEntry));
		Mockito.lenient().when(consignmentEntry1.getQuantityPending()).thenReturn(10L);
		when(orderCancelResponse.getCancelReason()).thenReturn(CancelReason.OTHER);

		// When
		cancellationService.cancelConsignment(consignment1, orderCancelResponse);

		// Then
		verify(consignmentEntryCommentService).createAndSaveComment(any(WarehousingCommentContext.class), anyString());
	}

	@Test
	public void shouldCancelConsignment_InternalWarehouse()
	{
		// Given
		warehouse.setExternal(false);
		final OrderCancelEntry cancellationEntry = new OrderCancelEntry(orderEntry1, 10L, "notes", CancelReason.LATEDELIVERY);
		when(orderCancelResponse.getEntriesToCancel()).thenReturn(ImmutableList.of(cancellationEntry));
		Mockito.lenient().when(consignmentEntry1.getQuantityPending()).thenReturn(10L);
		when(orderCancelResponse.getCancelReason()).thenReturn(CancelReason.OTHER);

		// When
		cancellationService.cancelConsignment(consignment1, orderCancelResponse);

		// Then
		verify(consignmentEntryCommentService).createAndSaveComment(any(WarehousingCommentContext.class), anyString());
	}

	@Test
	public void shouldProcessConsignmentCancellation_MultipleCancellations()
	{
		// Given
		final OrderCancelEntry cancellationEntry1 = new OrderCancelEntry(orderEntry1, 5L, "notes", CancelReason.LATEDELIVERY);
		final OrderCancelEntry cancellationEntry2 = new OrderCancelEntry(orderEntry2, 5L, "notes", CancelReason.LATEDELIVERY);
		final OrderEntryModificationRecordEntryModel orderEntryModificationRecordEntryModel1 = new OrderEntryModificationRecordEntryModel();
		orderEntryModificationRecordEntryModel1.setOrderEntry(orderEntry1);
		orderEntryModificationRecordEntryModel1.setOriginalOrderEntry(originalOrderEntry1);
		final OrderEntryModificationRecordEntryModel orderEntryModificationRecordEntryModel2 = new OrderEntryModificationRecordEntryModel();
		orderEntryModificationRecordEntryModel2.setOrderEntry(orderEntry2);
		orderEntryModificationRecordEntryModel2.setOriginalOrderEntry(originalOrderEntry2);
		final OrderModificationRecordEntryModel orderModificationRecordEntryModel = new OrderModificationRecordEntryModel();
		orderModificationRecordEntryModel.setOrderEntriesModificationEntries(ImmutableList.of(orderEntryModificationRecordEntryModel1,orderEntryModificationRecordEntryModel2));
		final OrderModificationRecordModel orderModificationRecordModel = new OrderModificationRecordModel();
		orderModificationRecordModel.setModificationRecordEntries(Collections.singleton(orderModificationRecordEntryModel));
		order.setModificationRecords(Collections.singleton(orderModificationRecordModel));
		Mockito.lenient().when(orderCancelResponse.getEntriesToCancel()).thenReturn(ImmutableList.of(cancellationEntry1, cancellationEntry2));
		Mockito.lenient().when(orderCancelResponse.getCancelReason()).thenReturn(CancelReason.OTHER);
		Mockito.lenient().when(consignment1.getStatus()).thenReturn(ConsignmentStatus.READY);
		Mockito.lenient().when(orderCancelResponse.getOrder()).thenReturn(order);

		// When
		cancellationService.processConsignmentCancellation(orderCancelResponse);

		// Then
		verify(consignmentBusinessProcessService, atLeastOnce())
				.triggerChoiceEvent(any(ConsignmentModel.class), anyString(), anyString());
		verify(warehousingConsignmentWorkflowService, atLeastOnce()).terminateConsignmentWorkflow(any(ConsignmentModel.class));
		Assert.assertTrue(orderEntry1.getConsignmentEntries().stream().anyMatch(result -> result.getSourceOrderEntry().equals(originalOrderEntry1)));
		Assert.assertTrue(orderEntry2.getConsignmentEntries().stream().anyMatch(result -> result.getSourceOrderEntry().equals(originalOrderEntry2)));
	}

	@Test
	public void shouldNotProcessConsignmentCancellation_WrongConsignmentStatus()
	{
		// Given
		prepareProcessConsignmentCancellationData();
		Mockito.lenient().when(consignment1.getStatus()).thenReturn(ConsignmentStatus.CANCELLED);
		// When
		cancellationService.processConsignmentCancellation(orderCancelResponse);

		// Then
		verify(consignmentBusinessProcessService, never())
				.triggerChoiceEvent(any(ConsignmentModel.class), anyString(), anyString());
		verify(warehousingConsignmentWorkflowService, never()).terminateConsignmentWorkflow(any(ConsignmentModel.class));
	}

	@Test
	public void shouldProcessConsignmentCancellation()
	{
		// Given
		prepareProcessConsignmentCancellationData();
		Mockito.lenient().when(consignment1.getStatus()).thenReturn(ConsignmentStatus.READY);

		// When
		cancellationService.processConsignmentCancellation(orderCancelResponse);

		// Then
		verify(consignmentBusinessProcessService, atLeastOnce())
				.triggerChoiceEvent(any(ConsignmentModel.class), anyString(), anyString());
		verify(warehousingConsignmentWorkflowService, atLeastOnce()).terminateConsignmentWorkflow(any(ConsignmentModel.class));
		Assert.assertTrue(orderEntry1.getConsignmentEntries().stream().anyMatch(result -> originalOrderEntry1.equals(result.getSourceOrderEntry())));
	}

	public void prepareProcessConsignmentCancellationData()
	{
		final OrderCancelEntry cancellationEntry = new OrderCancelEntry(orderEntry1, 5L, "notes", CancelReason.LATEDELIVERY);
		final OrderEntryModificationRecordEntryModel orderEntryModificationRecordEntryModel = new OrderEntryModificationRecordEntryModel();
		orderEntryModificationRecordEntryModel.setOrderEntry(orderEntry1);
		orderEntryModificationRecordEntryModel.setOriginalOrderEntry(originalOrderEntry1);
		final OrderModificationRecordEntryModel orderModificationRecordEntryModel = new OrderModificationRecordEntryModel();
		orderModificationRecordEntryModel.setOrderEntriesModificationEntries(Collections.singleton(orderEntryModificationRecordEntryModel));
		final OrderModificationRecordModel orderModificationRecordModel = new OrderModificationRecordModel();
		orderModificationRecordModel.setModificationRecordEntries(Collections.singleton(orderModificationRecordEntryModel));
		order.setModificationRecords(Collections.singleton(orderModificationRecordModel));

		Mockito.lenient().when(orderCancelResponse.getEntriesToCancel()).thenReturn(ImmutableList.of(cancellationEntry));
		Mockito.lenient().when(orderCancelResponse.getCancelReason()).thenReturn(CancelReason.OTHER);
		Mockito.lenient().when(orderCancelResponse.getOrder()).thenReturn(order);
	}


}
