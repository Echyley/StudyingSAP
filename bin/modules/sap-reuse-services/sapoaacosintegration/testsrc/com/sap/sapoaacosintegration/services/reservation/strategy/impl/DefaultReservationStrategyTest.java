/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacosintegration.services.reservation.strategy.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.sap.retail.oaa.commerce.services.common.util.CommonUtils;
import com.sap.retail.oaa.commerce.services.rest.util.exception.BackendDownException;
import com.sap.sapoaacosintegration.services.reservation.ReservationService;
import com.sap.sapoaacosintegration.services.reservation.exception.ReservationException;
import com.sap.sapoaacosintegration.services.reservation.impl.DefaultCosReservationService;


/**
 *
 */
@UnitTest
public class DefaultReservationStrategyTest
{
	private DefaultReservationStrategy classUnderTest;

	@Before
	public void setup()
	{
		classUnderTest = new DefaultReservationStrategy();
		//Mock Commonuutils
		final CommonUtils commonUtilsMock = EasyMock.createNiceMock(CommonUtils.class);
		EasyMock.expect(commonUtilsMock.isCAREnabled()).andReturn(false);
		EasyMock.expect(commonUtilsMock.isCOSEnabled()).andReturn(true);
		EasyMock.replay(commonUtilsMock);
		classUnderTest.setCommonUtils(commonUtilsMock);
	}

	@Test
	public void deleteReservationTest()
	{
		//Mock Reservation Service
		final ReservationService reservationService = EasyMock.createNiceMock(DefaultCosReservationService.class);
		EasyMock.replay(reservationService);
		classUnderTest.setReservationService(reservationService);
		Assert.assertNotNull(classUnderTest.getCommonUtils());
		final AbstractOrderModel orderModel = new AbstractOrderModel();
		orderModel.setGuid("Test");
		orderModel.setSapBackendReservation(Boolean.TRUE);
		Assert.assertTrue(classUnderTest.deleteReservation(orderModel));
	}

	@Test
	public void deleteReservationItemTest()
	{
		//Mock Reservation Service
		final ReservationService reservationService = EasyMock.createNiceMock(DefaultCosReservationService.class);
		EasyMock.replay(reservationService);
		classUnderTest.setReservationService(reservationService);
		final AbstractOrderModel orderModel = new AbstractOrderModel();
		orderModel.setGuid("Test");
		orderModel.setSapBackendReservation(Boolean.TRUE);
		final AbstractOrderEntryModel orderEntryModel = new AbstractOrderEntryModel();
		orderEntryModel.setEntryNumber(Integer.valueOf(0));
		orderEntryModel.setSapBackendReservation(Boolean.TRUE);
		Assert.assertTrue(classUnderTest.deleteReservationItem(orderModel, orderEntryModel));
	}

	@Test
	public void deleteReservationNoReservationTest()
	{
		//Mock Reservation Service
		final ReservationService reservationService = EasyMock.createNiceMock(DefaultCosReservationService.class);
		EasyMock.replay(reservationService);
		classUnderTest.setReservationService(reservationService);
		final AbstractOrderModel orderModel = new AbstractOrderModel();
		orderModel.setGuid("Test");
		orderModel.setSapBackendReservation(Boolean.FALSE);
		Assert.assertTrue(classUnderTest.deleteReservation(orderModel));
	}

	@Test
	public void deleteReservationItemNoReservationTest()
	{
		//Mock Reservation Service
		final ReservationService reservationService = EasyMock.createNiceMock(DefaultCosReservationService.class);
		EasyMock.replay(reservationService);
		classUnderTest.setReservationService(reservationService);
		final AbstractOrderModel orderModel = new AbstractOrderModel();
		orderModel.setGuid("Test");
		orderModel.setSapBackendReservation(Boolean.TRUE);
		final AbstractOrderEntryModel orderEntryModel = new AbstractOrderEntryModel();
		orderEntryModel.setEntryNumber(Integer.valueOf(0));
		orderEntryModel.setSapBackendReservation(Boolean.FALSE);
		Assert.assertTrue(classUnderTest.deleteReservationItem(orderModel, orderEntryModel));
	}

	@Test
	public void deleteReservationItemExceptionTest()
	{
		//Mock Reservation Service
		final ReservationService reservationService = EasyMock.createNiceMock(DefaultCosReservationService.class);
		reservationService.deleteReservationItem(EasyMock.anyObject(AbstractOrderModel.class),
				EasyMock.anyObject(AbstractOrderEntryModel.class));
		EasyMock.expectLastCall().andThrow(new ReservationException());
		EasyMock.replay(reservationService);
		classUnderTest.setReservationService(reservationService);
		final AbstractOrderModel orderModel = new AbstractOrderModel();
		orderModel.setGuid("Test");
		orderModel.setSapBackendReservation(Boolean.TRUE);
		final AbstractOrderEntryModel orderEntryModel = new AbstractOrderEntryModel();
		orderEntryModel.setEntryNumber(Integer.valueOf(0));
		orderEntryModel.setSapBackendReservation(Boolean.TRUE);
		Assert.assertFalse(classUnderTest.deleteReservationItem(orderModel, orderEntryModel));
	}

	@Test
	public void deleteReservationOfflineExceptionTest()
	{
		//Mock Reservation Service
		final ReservationService reservationService = EasyMock.createNiceMock(DefaultCosReservationService.class);
		reservationService.deleteReservation(EasyMock.anyObject(AbstractOrderModel.class));
		EasyMock.expectLastCall().andThrow(new BackendDownException());
		EasyMock.replay(reservationService);
		classUnderTest.setReservationService(reservationService);
		final AbstractOrderModel orderModel = new AbstractOrderModel();
		orderModel.setGuid("Test");
		orderModel.setSapBackendReservation(Boolean.TRUE);
		Assert.assertTrue(classUnderTest.deleteReservation(orderModel));
	}

	@Test
	public void deleteReservationItemOfflineExceptionTest()
	{
		//Mock Reservation Service
		final ReservationService reservationService = EasyMock.createNiceMock(DefaultCosReservationService.class);
		reservationService.deleteReservationItem(EasyMock.anyObject(AbstractOrderModel.class),
				EasyMock.anyObject(AbstractOrderEntryModel.class));
		EasyMock.expectLastCall().andThrow(new BackendDownException());
		EasyMock.replay(reservationService);
		classUnderTest.setReservationService(reservationService);
		final AbstractOrderModel orderModel = new AbstractOrderModel();
		orderModel.setGuid("Test");
		orderModel.setSapBackendReservation(Boolean.TRUE);
		final AbstractOrderEntryModel orderEntryModel = new AbstractOrderEntryModel();
		orderEntryModel.setEntryNumber(Integer.valueOf(0));
		orderEntryModel.setSapBackendReservation(Boolean.TRUE);
		Assert.assertTrue(classUnderTest.deleteReservationItem(orderModel, orderEntryModel));
	}
}
