/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapbillinginvoicefacades.strategy.impl;

import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.hybris.sapbillinginvoicefacades.document.data.ExternalSystemBillingDocumentData;
import com.sap.hybris.sapbillinginvoicefacades.facade.impl.SapBillingInvoiceFacadeImpl;
import com.sap.hybris.sapbillinginvoiceservices.exception.SapBillingInvoiceUserException;

import de.hybris.platform.commercefacades.invoice.data.SAPInvoiceData;
import de.hybris.platform.commercefacades.invoice.strategies.InvoiceStrategy;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

public class SapInvoiceS4SalesStrategy implements InvoiceStrategy
{
	private static final Logger LOG = LoggerFactory.getLogger(SapInvoiceS4SalesStrategy.class);
	private static final String INVOICE_NOT_FOUND_FOR_ORDER = "Invoice with id [%s] not found for order [%s]";
	private SapBillingInvoiceFacadeImpl sapBillingInvoiceOrderFacade;
	private Converter<ExternalSystemBillingDocumentData, SAPInvoiceData> externalBillingToInvoiceDataConverter;
	

	
	@Override
	public List<SAPInvoiceData> getInvoices(final OrderModel orderModel)
	{
		 Objects.requireNonNull(orderModel, "orderModel is null");
		 List<ExternalSystemBillingDocumentData> externalInvoices = sapBillingInvoiceOrderFacade.getBillingDocumentsForOrder(orderModel.getCode());
		 return externalBillingToInvoiceDataConverter.convertAll(externalInvoices);
	}

	@Override
	public byte[] getInvoiceBinary(final OrderModel orderModel, final String invoiceId)
	{
		Objects.requireNonNull(orderModel, "orderModel is null");
		try 
		{
			final ExternalSystemBillingDocumentData documentData = getBillingDocumentByOrderAndInvoice(orderModel.getCode(), invoiceId);
			if (documentData == null)
			{
				throw new UnknownIdentifierException(String.format(INVOICE_NOT_FOUND_FOR_ORDER, invoiceId,orderModel.getCode()));
			}
			return sapBillingInvoiceOrderFacade.getPDFData(documentData.getSapOrderCode(), invoiceId);
		} 
		catch (SapBillingInvoiceUserException e) 
		{
			LOG.debug("Not able to fetch the invoice binary",e);
			return new byte[0];
		}
	}
	
	protected ExternalSystemBillingDocumentData getBillingDocumentByOrderAndInvoice(final String orderCode,
			final String invoiceId) {
		final List<ExternalSystemBillingDocumentData> documentList = sapBillingInvoiceOrderFacade
				.getBillingDocumentsForOrder(orderCode);
		return documentList.stream().filter(Objects::nonNull)
				.filter(entry -> Objects.equals(invoiceId, entry.getBillingDocumentId())).findFirst().orElse(null);

	}

	
	public void setSapBillingInvoiceOrderFacade(SapBillingInvoiceFacadeImpl sapBillingInvoiceOrderFacade) {
		this.sapBillingInvoiceOrderFacade = sapBillingInvoiceOrderFacade;
	}

	public Converter<ExternalSystemBillingDocumentData, SAPInvoiceData> getExternalBillingToInvoiceDataConverter() {
		return externalBillingToInvoiceDataConverter;
	}

	public void setExternalBillingToInvoiceDataConverter(
			Converter<ExternalSystemBillingDocumentData, SAPInvoiceData> externalBillingToInvoiceDataConverter) {
		this.externalBillingToInvoiceDataConverter = externalBillingToInvoiceDataConverter;
	}

}
