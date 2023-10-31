/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapbillinginvoiceocctests.strategy.impl;

import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.sap.sapmodel.model.SAPOrderModel;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.sap.hybris.sapbillinginvoicefacades.document.data.ExternalSystemBillingDocumentData;
import com.sap.hybris.sapbillinginvoicefacades.strategy.SapBillingInvoiceStrategy;
import com.sap.hybris.sapbillinginvoiceservices.exception.SapBillingInvoiceUserException;



/**
 * Mock SapBillingInvoiceStrategy to avoid communication with back end.
 */
public class SapBillingInvoiceMockStrategyImpl implements SapBillingInvoiceStrategy
{
	/**
	 *
	 */
	private static final String BILLING_FILE_PATH = "/sapbillinginvoiceocctests/import/billingpdf/testBillingDocumentId1.pdf";
	private static final Logger LOG = Logger.getLogger(SapBillingInvoiceMockStrategyImpl.class);
	private static final byte[] NO_CONTENT = new byte[0];

	@Override
	public List<ExternalSystemBillingDocumentData> getBillingDocuments(final SAPOrderModel sapOrder)
	{
		final List<ExternalSystemBillingDocumentData> billingItems = new ArrayList<>();
		final ExternalSystemBillingDocumentData data = new ExternalSystemBillingDocumentData();
		data.setBillingDocType("MOCK_ORDER_TYPE");
		data.setBillingDocumentId("testBillingDocumentId1");
		data.setBillingInvoiceDate(new Date());
		final PriceData price = new PriceData();
		price.setValue(BigDecimal.valueOf(10));
		price.setCurrencyIso("USD");
		price.setPriceType(PriceDataType.BUY);
		data.setTotalPrice(price);
		data.setNetAmount(price);
		data.setSapOrderCode("testSAPOrder1");
		billingItems.add(data);


		final ExternalSystemBillingDocumentData data1 = new ExternalSystemBillingDocumentData();
		data1.setBillingDocType("MOCK_ORDER_TYPE");
		data1.setBillingDocumentId("testBillingDocumentId2");
		data1.setBillingInvoiceDate(new Date());
		final PriceData price1 = new PriceData();
		price1.setValue(BigDecimal.valueOf(10));
		price1.setCurrencyIso("USD");
		price1.setPriceType(PriceDataType.BUY);
		data1.setTotalPrice(price);
		data1.setNetAmount(price);
		data1.setSapOrderCode("testSAPOrder1");
		billingItems.add(data1);

		return billingItems;
	}


	@Override
	public byte[] getPDFData(final SAPOrderModel sapOrderData, final String billingDocId) throws SapBillingInvoiceUserException
	{
		try (final InputStream inputStream = this.getClass()
				.getResourceAsStream(BILLING_FILE_PATH))
		{
			return inputStream != null ? IOUtils.toByteArray(inputStream) : NO_CONTENT;
		}
		catch (final IOException e)
		{
			LOG.warn("Failed to read pdf data",e);
		}

		return NO_CONTENT;
	}


}
