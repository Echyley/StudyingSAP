/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapbillinginvoicefacades.strategy.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.sap.hybris.sapbillinginvoicefacades.document.data.ExternalSystemBillingDocumentData;
import com.sap.hybris.sapbillinginvoicefacades.facade.impl.SapBillingInvoiceFacadeImpl;
import com.sap.hybris.sapbillinginvoiceservices.exception.SapBillingInvoiceUserException;

import de.hybris.platform.commercefacades.invoice.data.SAPInvoiceData;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

@RunWith(MockitoJUnitRunner.class)
public class SapInvoiceS4SalesStrategyTest {

    private static final String INVOICE_ID = "invoiceId";

	@Mock
    private SapBillingInvoiceFacadeImpl sapBillingInvoiceOrderFacade;

    @Mock
    private Converter<ExternalSystemBillingDocumentData, SAPInvoiceData> externalBillingToInvoiceDataConverter;

    @InjectMocks
    private SapInvoiceS4SalesStrategy sapInvoiceS4SalesStrategy;

    private OrderModel orderModel;

    @Before
    public void setUp() {
        orderModel = mock(OrderModel.class);
    }

    @Test(expected = NullPointerException.class)
    public void testGetInvoicesNullOrderModel() {
        sapInvoiceS4SalesStrategy.getInvoices(null);
    }

    @Test
    public void testGetInvoices() {
        List<ExternalSystemBillingDocumentData> externalInvoices = new ArrayList<>();
        externalInvoices.add(new ExternalSystemBillingDocumentData());
        List<SAPInvoiceData> invoices = sapInvoiceS4SalesStrategy.getInvoices(orderModel);

        assertEquals(0, invoices.size());
    }

    @Test(expected = NullPointerException.class)
    public void testGetInvoiceBinaryNullOrderModel() {
        sapInvoiceS4SalesStrategy.getInvoiceBinary(null, INVOICE_ID);
    }
    
    @Test(expected = UnknownIdentifierException.class)
    public void testGetInvoiceBinaryIdentifierException() throws SapBillingInvoiceUserException {
    	orderModel.setCode(INVOICE_ID);
        List<ExternalSystemBillingDocumentData> externalInvoices = new ArrayList<>();
        ExternalSystemBillingDocumentData billingData= new ExternalSystemBillingDocumentData();
        billingData.setBillingDocumentId(INVOICE_ID);
        externalInvoices.add(billingData);
        when(sapBillingInvoiceOrderFacade.getBillingDocumentsForOrder(orderModel.getCode())).thenReturn(externalInvoices);
        sapInvoiceS4SalesStrategy.getInvoiceBinary(orderModel, "invoiceId2");
    }

    @Test
    public void testGetInvoiceBinary() throws SapBillingInvoiceUserException {
    	orderModel.setCode(INVOICE_ID);
        byte[] pdfData = {0x50, 0x44};
        List<ExternalSystemBillingDocumentData> externalInvoices = new ArrayList<>();
        ExternalSystemBillingDocumentData billingData= new ExternalSystemBillingDocumentData();
        billingData.setBillingDocumentId(INVOICE_ID);
        externalInvoices.add(billingData);
        when(sapBillingInvoiceOrderFacade.getBillingDocumentsForOrder(orderModel.getCode())).thenReturn(externalInvoices);
        when(sapBillingInvoiceOrderFacade.getPDFData(orderModel.getCode(), INVOICE_ID)).thenReturn(pdfData);

        byte[] result = sapInvoiceS4SalesStrategy.getInvoiceBinary(orderModel, INVOICE_ID);

        assertEquals(pdfData, result);
    }

    @Test
    public void testGetInvoiceBinaryWhenExceptionThrown() throws SapBillingInvoiceUserException {
    	orderModel.setCode(INVOICE_ID);
    	List<ExternalSystemBillingDocumentData> externalInvoices = new ArrayList<>();
        ExternalSystemBillingDocumentData billingData= new ExternalSystemBillingDocumentData();
        billingData.setBillingDocumentId(INVOICE_ID);
        externalInvoices.add(billingData);
        when(sapBillingInvoiceOrderFacade.getBillingDocumentsForOrder(orderModel.getCode())).thenReturn(externalInvoices);
    	when(sapBillingInvoiceOrderFacade.getPDFData(orderModel.getCode(), INVOICE_ID)).thenThrow(SapBillingInvoiceUserException.class);

        byte[] result = sapInvoiceS4SalesStrategy.getInvoiceBinary(orderModel, INVOICE_ID);

        assertEquals(0, result.length);
    }

}
