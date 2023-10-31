/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sapbillinginvoice.facade.impl;

import static org.mockito.BDDMockito.given;

import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.sap.hybris.sapbillinginvoicefacades.facade.impl.SapBillingInvoiceFacadeImpl;
import com.sap.hybris.sapbillinginvoicefacades.strategy.SapBillingInvoiceStrategy;
import com.sap.hybris.sapbillinginvoicefacades.utils.SapBillingInvoiceUtils;
import com.sap.hybris.sapbillinginvoiceservices.exception.SapBillingInvoiceUserException;
import com.sap.hybris.sapbillinginvoiceservices.service.SapBillingInvoiceService;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.sapmodel.enums.SAPOrderType;
import de.hybris.platform.sap.sapmodel.model.SAPOrderModel;


/**
 *
 */
@UnitTest
public class SapBillingInvoiceFacadeImplTest
{

	@InjectMocks
	SapBillingInvoiceFacadeImpl sapBillingInvoiceFacadeImpl;

	@Mock
	private SapBillingInvoiceService sapBillingInvoiceService;

	@Mock
	private Map<String, SapBillingInvoiceStrategy> handlers;

	@Mock
	private SapBillingInvoiceUtils sapBillingInvoiceUtils;
	
	@Mock
	private SapBillingInvoiceStrategy sapBillingInvoiceServiceStrategy;

	final byte[] pdfData = new byte[0];
	
	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void getPDFTest() throws SapBillingInvoiceUserException
	{
		final String orderCode = "12345";

		final SAPOrderModel sapOrder = new SAPOrderModel();
		sapOrder.setCode(orderCode);
		sapOrder.setSapOrderType(SAPOrderType.SALES);

		
		given(sapBillingInvoiceService.getSapOrderBySapOrderCode(orderCode)).willReturn(sapOrder);
		given(sapBillingInvoiceUtils.getSapOrderType(sapOrder)).willReturn("SALES");
		given(handlers.get("SALES")).willReturn(sapBillingInvoiceServiceStrategy);
		given(sapBillingInvoiceServiceStrategy.getPDFData(sapOrder, "BILL12345")).willReturn(pdfData);

		final byte[] responsePdfData = sapBillingInvoiceFacadeImpl.getPDFData(orderCode, "BILL12345");

		Assert.assertEquals(pdfData, responsePdfData);

	}


}
