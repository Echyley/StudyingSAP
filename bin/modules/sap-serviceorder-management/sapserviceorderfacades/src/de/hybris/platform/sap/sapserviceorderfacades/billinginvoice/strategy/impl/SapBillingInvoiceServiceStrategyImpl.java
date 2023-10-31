/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapserviceorderfacades.billinginvoice.strategy.impl;

import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.sap.sapmodel.model.SAPOrderModel;
import de.hybris.platform.sap.sapmodel.services.SapPlantLogSysOrgService;
import de.hybris.platform.sap.sapserviceorder.billlinginvoice.service.SapServiceOrderBillingInvoiceService;
import de.hybris.platform.sap.sapserviceorder.constants.SapserviceorderConstants;
import de.hybris.platform.sap.sapserviceorderfacades.constants.SapserviceorderfacadesConstants;
import de.hybris.platform.servicelayer.config.ConfigurationService;
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
import com.sap.hybris.sapbillinginvoiceservices.constants.SapbillinginvoiceservicesConstants;
import com.sap.hybris.sapbillinginvoiceservices.exception.SapBillingInvoiceUserException;
import com.sap.hybris.sapbillinginvoiceservices.service.SapBillingInvoiceService;
/**
 * Strategy implementation for retrieving service order billing documents and invoices pdf
 */
public class SapBillingInvoiceServiceStrategyImpl implements SapBillingInvoiceStrategy
{
	private static final Logger LOG = Logger.getLogger(SapBillingInvoiceServiceStrategyImpl.class);
	private static final String BILLING_DOC_MSG = "Unexpected Error Occured while trying to fetch billing document from external systems ::";

	private SapServiceOrderBillingInvoiceService sapServiceOrderBillingInvoiceService;

	private SapPlantLogSysOrgService sapPlantLogSysOrgService;

	private SapBillingInvoiceUtils sapBillingInvoiceUtils;

	private Converter<JsonObject, ExternalSystemBillingDocumentData> externalSystemBillingDocumentConverter;
	
	private PriceDataFactory priceFactory;
	
	private ConfigurationService configurationService;
	

	@Override
	public List<ExternalSystemBillingDocumentData> getBillingDocuments(final SAPOrderModel sapOrder)
	{

		LOG.info("Start of service flow to Get of billing documents ");

		final List<ExternalSystemBillingDocumentData> billingItems = new ArrayList<>();
		try
		{
			final Map<String, Object> billingDocumentMap = getBusinessDocumentFromS4(sapOrder);
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
	
	public Map<String, Object> getBusinessDocumentFromS4(final SAPOrderModel sapOrder)
	{

		LOG.info("Start of Get billing documents for service order from S4CM");

		final String targetSuffixUrl = getConfigurationService().getConfiguration()
				.getString(SapserviceorderfacadesConstants.SERVICE_BILLING_DOCUMENT_SUFFIX_FIX_URL)
				.replace("{serviceOrderCode}", sapOrder.getServiceOrderId());

		LOG.info("End of Get billing documents for service order from S4CM");

		return getSapServiceOrderBillingInvoiceService().callS4forBillingDocuments(sapOrder, targetSuffixUrl);
	}


	@Override
	public byte[] getPDFData(final SAPOrderModel sapOrder, final String billingDocId) throws SapBillingInvoiceUserException
	{
		return getSapServiceOrderBillingInvoiceService().getPDFData(sapOrder, billingDocId);
	}

	public SapServiceOrderBillingInvoiceService getSapServiceOrderBillingInvoiceService() {
		return sapServiceOrderBillingInvoiceService;
	}

	public void setSapServiceOrderBillingInvoiceService(SapServiceOrderBillingInvoiceService sapServiceOrderBillingInvoiceService) {
		this.sapServiceOrderBillingInvoiceService = sapServiceOrderBillingInvoiceService;
	}
	
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

	public ConfigurationService getConfigurationService() {
		return configurationService;
	}

	public void setConfigurationService(ConfigurationService configurationService) {
		this.configurationService = configurationService;
	}


}
