/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sapbillinginvoice.facade.utils;


import de.hybris.bootstrap.annotations.UnitTest;

import de.hybris.platform.sap.sapmodel.enums.SAPOrderType;
import de.hybris.platform.sap.sapmodel.model.SAPOrderModel;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.sap.hybris.sapbillinginvoicefacades.utils.SapBillingInvoiceUtils;


/**
 *
 */
@UnitTest
public class SapBillingInvoiceUtilsTest
{

	@InjectMocks
	SapBillingInvoiceUtils sapBillingInvoiceUtils;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void getSalesOrderType() throws NoSuchMethodException,
			IllegalAccessException, InvocationTargetException
	{

		final SAPOrderModel sapOrder = new SAPOrderModel();
		final String assertOrderType = "SALES";

		sapOrder.setCode("12345");
		sapOrder.setSapOrderType(SAPOrderType.SALES);

		final String orderType = sapBillingInvoiceUtils.getSapOrderType(sapOrder);

		Assert.assertEquals(assertOrderType, orderType);


	}

	@Test
	public void getServiceOrderType() throws NoSuchMethodException,
			IllegalAccessException, InvocationTargetException
	{

		final SAPOrderModel sapOrder = new SAPOrderModel();
		final String assertOrderType = "SERVICE";

		sapOrder.setCode("12345");
		sapOrder.setSapOrderType(SAPOrderType.SERVICE);

		final String orderType = sapBillingInvoiceUtils.getSapOrderType(sapOrder);

		Assert.assertEquals(assertOrderType, orderType);


	}

	@Test
	public void getS4DateStringToDate()
	{

		final Date s4Date = new Date(Long.parseLong("15652438380000"));

		final Date resultS4Date = sapBillingInvoiceUtils.s4DateStringToDate("/Date(15652438380000)/");

		Assert.assertEquals(s4Date, resultS4Date);

	}


	@Test
	public void getS4DateStringToDateNull()
	{

		final Date resultS4Date = sapBillingInvoiceUtils.s4DateStringToDate("/Date15652438380000/");

		Assert.assertEquals(null, resultS4Date);

	}

}
