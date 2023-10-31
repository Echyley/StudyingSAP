/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapbillinginvoicefacades.strategy.impl;

import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.sap.sapmodel.model.SAPOrderModel;
import de.hybris.platform.sap.sapmodel.services.SapPlantLogSysOrgService;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sap.hybris.sapbillinginvoicefacades.constants.SapbillinginvoicefacadesConstants;
import com.sap.hybris.sapbillinginvoicefacades.document.data.ExternalSystemBillingDocumentData;
import com.sap.hybris.sapbillinginvoicefacades.strategy.SapBillingInvoiceStrategy;
import com.sap.hybris.sapbillinginvoicefacades.utils.SapBillingInvoiceUtils;
import com.sap.hybris.sapbillinginvoiceservices.exception.SapBillingInvoiceUserException;
import com.sap.hybris.sapbillinginvoiceservices.service.SapBillingInvoiceService;
/**
 *  @deprecated since 2108, This class will be moved to sapserviceorderfacades extension as this strategy implementation retrieves service billing documents and service invoice pdf
 */
@Deprecated(since = "2108", forRemoval = true)
public class SapBillingInvoiceServiceStrategyImpl implements SapBillingInvoiceStrategy
{
	private static final Logger LOG = Logger.getLogger(SapBillingInvoiceServiceStrategyImpl.class);
	private static final String BILLING_DOC_MSG = "Unexpected Error Occured while trying to fetch billing document from external systems ::";

	private SapBillingInvoiceService sapBillingInvoiceService;

	private SapPlantLogSysOrgService sapPlantLogSysOrgService;

	private SapBillingInvoiceUtils sapBillingInvoiceUtils;

	private Converter<JsonObject, ExternalSystemBillingDocumentData> externalSystemBillingDocumentConverter;
	
	private PriceDataFactory priceFactory;

	public PriceDataFactory getPriceFactory() {
		return priceFactory;
	}

	public void setPriceFactory(PriceDataFactory priceFactory) {
		this.priceFactory = priceFactory;
	}

	public SapBillingInvoiceUtils getSapBillingInvoiceUtils()
	{
		return sapBillingInvoiceUtils;
	}

	public void setSapBillingInvoiceUtils(final SapBillingInvoiceUtils sapBillingInvoiceUtils)
	{
		this.sapBillingInvoiceUtils = sapBillingInvoiceUtils;
	}

	public Converter<JsonObject, ExternalSystemBillingDocumentData> getExternalSystemBillingDocumentConverter()
	{
		return externalSystemBillingDocumentConverter;
	}

	public void setExternalSystemBillingDocumentConverter(
			final Converter<JsonObject, ExternalSystemBillingDocumentData> externalSystemBillingDocumentConverter)
	{
		this.externalSystemBillingDocumentConverter = externalSystemBillingDocumentConverter;
	}

	public SapPlantLogSysOrgService getSapPlantLogSysOrgService()
	{
		return sapPlantLogSysOrgService;
	}

	public void setSapPlantLogSysOrgService(final SapPlantLogSysOrgService sapPlantLogSysOrgService)
	{
		this.sapPlantLogSysOrgService = sapPlantLogSysOrgService;
	}

	public SapBillingInvoiceService getSapBillingInvoiceService()
	{
		return sapBillingInvoiceService;
	}

	public void setSapBillingInvoiceService(final SapBillingInvoiceService sapBillingInvoiceService)
	{
		this.sapBillingInvoiceService = sapBillingInvoiceService;
	}

	/**
	 *  @deprecated since 2108, This method will be moved to sapserviceorderfacades extension as this strategy implementation retrieves service billing documents
	 */
	@Override
	@Deprecated(since = "2108", forRemoval = true)
	public List<ExternalSystemBillingDocumentData> getBillingDocuments(final SAPOrderModel sapOrder)
	{

		LOG.info("Start of service flow to Get of billing documents ");

		final List<ExternalSystemBillingDocumentData> billingItems = new ArrayList<>();
		try
		{
			final Map<String, Object> billingDocumentMap = getSapBillingInvoiceService()
					.getBusinessDocumentFromS4ServiceOrderCode(sapOrder);
				final JsonArray billingDocumentArray = (JsonArray) billingDocumentMap.get("billingDocumentArray");

				for (final JsonElement billingDoc : billingDocumentArray)
				{
					final ExternalSystemBillingDocumentData billingDocItemData = getExternalSystemBillingDocumentConverter()
							.convert(billingDoc.getAsJsonObject());
					billingDocItemData.setSapOrderCode(sapOrder.getCode());
					billingDocItemData.setBillingDocType(SapbillinginvoicefacadesConstants.SERVICE_ORDER);
					billingDocItemData
							.setBillingInvoiceDate(getSapBillingInvoiceUtils()
									.s4DateStringToDate(billingDoc.getAsJsonObject().get("CreationDate").getAsString()));
					
					final String billingInvoiceNetAmount = billingDoc.getAsJsonObject().get("TransactionCurrency").getAsString() + " "
							+ billingDoc.getAsJsonObject().get("TotalGrossAmount").getAsString();

					billingDocItemData.setBillingInvoiceNetAmount(billingInvoiceNetAmount);
					
					JsonElement currency = billingDoc.getAsJsonObject().get("TransactionCurrency");
					JsonElement totalGrossAmount = billingDoc.getAsJsonObject().get("TotalGrossAmount");
					BigDecimal netAmount = billingDoc.getAsJsonObject().get("NetAmount").getAsBigDecimal();
		            
					if (null != currency && null != netAmount)
					{
						billingDocItemData.setNetAmount(getPriceFactory().create(PriceDataType.BUY, netAmount, currency.getAsString()));
					}			   
					
					if (null != currency && null != totalGrossAmount)
					{
						billingDocItemData.setTotalPrice(getPriceFactory().create(PriceDataType.BUY, totalGrossAmount.getAsBigDecimal(), currency.getAsString()));
					}	

					billingItems.add(billingDocItemData);
				}

		}
		catch (final Exception e)
		{
			LOG.error(BILLING_DOC_MSG, e);
		}

		return billingItems;
	}


	/**
	 *  @deprecated since 2108, This method will be moved to sapserviceorderfacades extension as this strategy implementation retrieves service invoice pdf
	 */
	@Override
	@Deprecated(since = "2108", forRemoval = true)
	public byte[] getPDFData(final SAPOrderModel sapOrderData, final String billingDocId) throws SapBillingInvoiceUserException
	{
		return getSapBillingInvoiceService().getPDFData(sapOrderData, billingDocId);
	}

}
