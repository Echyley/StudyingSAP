/*
 * [y] hybris Platform
 *
 * Copyright (c) 2021 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.couponservices.order.eventlistener;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.couponservices.dao.CouponRedemptionDao;
import de.hybris.platform.couponservices.model.CouponRedemptionModel;
import de.hybris.platform.ordercancel.events.CancelFinishedEvent;
import de.hybris.platform.ordercancel.model.OrderCancelRecordEntryModel;
import de.hybris.platform.ordermodify.model.OrderModificationRecordModel;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;



@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CouponRedemptionOrderCancelFinishedEventListenerUnitTest
{
	@InjectMocks
	private CouponRedemptionOrderCancelFinishedEventListener listener;

	@Mock
	private ModelService modelService;

	@Mock
	private CancelFinishedEvent event;

	@Mock
	private CouponRedemptionDao couponRedemptionDao;

	@Mock
	private OrderCancelRecordEntryModel cancelRequestRecordEntry;

	@Mock
	private OrderModificationRecordModel orderModificationRecordModel;

	@Mock
	private OrderModel orderModel;

	@Mock
	private CouponRedemptionModel couponRedemptionModel;

	@Before()
	public void setUp()
	{
		Mockito.when(event.getCancelRequestRecordEntry()).thenReturn(cancelRequestRecordEntry);
		Mockito.when(cancelRequestRecordEntry.getModificationRecord()).thenReturn(orderModificationRecordModel);
		Mockito.when(orderModificationRecordModel.getOrder()).thenReturn(orderModel);
	}


	@Test
	public void shouldDeleteCouponRedemption()
	{
		final Collection<String> couponCodes = Arrays.asList("code1","code2","code3");
		final List<CouponRedemptionModel> redemption1 = Arrays.asList(couponRedemptionModel);
		final PK pk = PK.BIG_PK;

		Mockito.when(orderModel.getAppliedCouponCodes()).thenReturn(couponCodes);
		Mockito.when(orderModel.getStatus()).thenReturn(OrderStatus.CANCELLED);
		Mockito.when(couponRedemptionDao.findCouponRedemptionsByCodeAndOrder(anyString(),eq(orderModel))).thenReturn(redemption1);
		Mockito.when(couponRedemptionModel.getPk()).thenReturn(pk);

		listener.onEvent(event);

		verify(modelService, times(3)).remove(pk);
	}

	@Test
	public void shouldNotFindCouponRedemptions1()
	{
		final Collection<String> couponCodes = Arrays.asList("code1","code2","code3");

		Mockito.when(orderModel.getAppliedCouponCodes()).thenReturn(couponCodes);
		Mockito.when(orderModel.getStatus()).thenReturn(OrderStatus.CANCELLING);

		listener.onEvent(event);

		verify(couponRedemptionDao, never()).findCouponRedemptionsByCodeAndOrder(anyString(),eq(orderModel));
	}

	@Test
	public void shouldNotFindCouponRedemptions2()
	{
		final Collection<String> couponCodes = Collections.emptyList();

		Mockito.when(orderModel.getAppliedCouponCodes()).thenReturn(couponCodes);
		Mockito.when(orderModel.getStatus()).thenReturn(OrderStatus.CANCELLED);

		listener.onEvent(event);

		verify(couponRedemptionDao, never()).findCouponRedemptionsByCodeAndOrder(anyString(),eq(orderModel));
	}

	@Test
	public void shouldNotDeleteCouponRedemption()
	{
		final Collection<String> couponCodes = Arrays.asList("code1","code2","code3");
		final List<CouponRedemptionModel> emptyRedemption = Collections.emptyList();


		Mockito.when(orderModel.getAppliedCouponCodes()).thenReturn(couponCodes);
		Mockito.when(orderModel.getStatus()).thenReturn(OrderStatus.CANCELLED);
		Mockito.when(couponRedemptionDao.findCouponRedemptionsByCodeAndOrder(anyString(),eq(orderModel))).thenReturn(emptyRedemption);

		listener.onEvent(event);

		verify(modelService, never()).remove(any());
	}
}
