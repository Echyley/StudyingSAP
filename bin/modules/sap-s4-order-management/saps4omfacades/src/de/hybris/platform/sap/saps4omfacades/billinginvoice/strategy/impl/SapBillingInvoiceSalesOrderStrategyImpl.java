/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saps4omfacades.billinginvoice.strategy.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.log4j.Logger;

import com.sap.hybris.sapbillinginvoicefacades.document.data.ExternalSystemBillingDocumentData;
import com.sap.hybris.sapbillinginvoicefacades.strategy.SapBillingInvoiceStrategy;
import com.sap.hybris.sapbillinginvoicefacades.utils.SapBillingInvoiceUtils;
import com.sap.hybris.sapbillinginvoiceservices.exception.SapBillingInvoiceUserException;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.sap.sapmodel.model.SAPOrderModel;
import de.hybris.platform.sap.saps4omservices.filter.dto.FilterData;
import de.hybris.platform.sap.saps4omservices.order.services.SapS4OMOrderFilterBuilder;
import de.hybris.platform.sap.saps4omservices.services.SapS4OMOutboundService;
import de.hybris.platform.sap.saps4omservices.utils.SapS4OrderUtil;
import de.hybris.platform.saps4omservices.dto.SAPS4OMBillingData;
import de.hybris.platform.saps4omservices.dto.SAPS4OMBillingResponseData;
import de.hybris.platform.saps4omservices.dto.SAPS4OMBillingResponseDataResults;
import de.hybris.platform.servicelayer.user.UserService;

/**
 * Strategy implementation for retrieving S4OM sales order billing documents and
 * invoices pdf
 */
public class SapBillingInvoiceSalesOrderStrategyImpl implements SapBillingInvoiceStrategy {

	private static final Logger LOG = Logger.getLogger(SapBillingInvoiceSalesOrderStrategyImpl.class);
	private static final String BILLING_DOC_ITEM_MSG = "Unexpected Error Occured while trying to fetch billing document item from external systems ::";
	private static final String SALES_ORDER = "SALES";
	private SapS4OMOutboundService sapS4OMOutboundService;
	private SapBillingInvoiceUtils sapBillingInvoiceUtils;
	private PriceDataFactory priceFactory;
	private SapS4OMOrderFilterBuilder sapS4OMOrderFilterBuilder;
	private UserService userService;


	@Override
	public List<ExternalSystemBillingDocumentData> getBillingDocuments(SAPOrderModel sapOrder) {

		LOG.info("Start of S4OM Sales flow to Get of billing documents ");
        String orderCode = sapOrder.getOrder().getCode();
		final List<ExternalSystemBillingDocumentData> billingItems = new ArrayList<>();
		try
		{
			Map<String, List<FilterData>> filterData = getSapS4OMOrderFilterBuilder()
					.getBillingDetailFilters(orderCode);

			final SAPS4OMBillingData billingData = getSapS4OMOutboundService()
				  .fetchBillingDocumentsfromS4( SapS4OrderUtil.BILLINGDESTINATION,  SapS4OrderUtil.BILLINGDESTINATIONTARGET, filterData, orderCode);
			SAPS4OMBillingResponseDataResults billingDocResult = billingData.getResults();
			Optional<SAPS4OMBillingResponseData> billingDocOptional = billingDocResult.getResults().stream()
					.findFirst();
			SAPS4OMBillingResponseData billingDoc = billingDocOptional.isPresent() ? billingDocOptional.get() : null;
			
			if (null != billingDoc) {
				BigDecimal netAmount = new BigDecimal(billingDoc.getToBillingDocument().getTotalNetAmount());
				BigDecimal taxAmount = new BigDecimal(billingDoc.getToBillingDocument().getTaxAmount());
				String transactionCurrency = billingDoc.getTransactionCurrency();
				final ExternalSystemBillingDocumentData billingDocItemData = new ExternalSystemBillingDocumentData();
				billingDocItemData.setSapOrderCode(sapOrder.getCode());
				billingDocItemData.setBillingDocumentId(billingDoc.getBillingDocument());
				billingDocItemData.setBillingDocType(SALES_ORDER);
				billingDocItemData.setBillingInvoiceDate(
						getSapBillingInvoiceUtils().s4DateStringToDate(billingDoc.getCreationDate()));

				final String billingInvoiceNetAmount = transactionCurrency + " "
						+ (netAmount.add(taxAmount)).toString();

				billingDocItemData.setBillingInvoiceNetAmount(billingInvoiceNetAmount);
				
				if (null != transactionCurrency)
				{
					billingDocItemData.setNetAmount(getPriceFactory().create(PriceDataType.BUY, netAmount, transactionCurrency));
				}			                                              

				BigDecimal totalGrossAmount = netAmount.add(taxAmount);

				if (null != transactionCurrency && null != totalGrossAmount) {
					billingDocItemData.setTotalPrice(
							getPriceFactory().create(PriceDataType.BUY, totalGrossAmount, transactionCurrency));
				}

				billingItems.add(billingDocItemData);

			} else {
				LOG.info("No billing details found");
			}
		}
		catch (final Exception e)
		{
			LOG.error(BILLING_DOC_ITEM_MSG, e);
		}

		LOG.info("End of S4OM Sales flow to Get of billing documents ");
		

		return billingItems;
	}

	@Override
	public byte[] getPDFData(SAPOrderModel sapOrder, String billingDocId) throws SapBillingInvoiceUserException {
		
		LOG.info("Start of check for customer validation to view the billing document");
		final CustomerModel customer = ((CustomerModel) getUserService().getCurrentUser());
		if (customer instanceof B2BCustomerModel) {
			final B2BCustomerModel b2bCustomer = (B2BCustomerModel) customer;

			if (!b2bCustomer.getPk().equals(sapOrder.getOrder().getUser().getPk())) {
				LOG.error("Error Invalid user trying to access the invoice");
				throw new SapBillingInvoiceUserException("Invalid user trying to access the invoice");
			}

		}

		LOG.info("End of check for customer validation to view the billing document");

		Map<String,List<FilterData>> filterData = getSapS4OMOrderFilterBuilder().getBillingPDFFilters(billingDocId);
		    return  getSapS4OMOutboundService().fetchPDFData(SapS4OrderUtil.BILLINGDPDFESTINATION, SapS4OrderUtil.BILLINGPDFDESTINATIONTARGET, filterData, billingDocId, sapOrder);
	}


	public PriceDataFactory getPriceFactory() {
		return priceFactory;
	}

	public void setPriceFactory(PriceDataFactory priceFactory) {
		this.priceFactory = priceFactory;
	}

	public SapBillingInvoiceUtils getSapBillingInvoiceUtils() {
		return sapBillingInvoiceUtils;
	}

	public void setSapBillingInvoiceUtils(final SapBillingInvoiceUtils sapBillingInvoiceUtils) {
		this.sapBillingInvoiceUtils = sapBillingInvoiceUtils;
	}
	
	public SapS4OMOrderFilterBuilder getSapS4OMOrderFilterBuilder() {
		return sapS4OMOrderFilterBuilder;
	}

	public void setSapS4OMOrderFilterBuilder(SapS4OMOrderFilterBuilder sapS4OMOrderFilterBuilder) {
		this.sapS4OMOrderFilterBuilder = sapS4OMOrderFilterBuilder;
	}
	
	public UserService getUserService() {
		return userService;
	}

	public void setUserService(final UserService userService) {
		this.userService = userService;
	}
	
	public SapS4OMOutboundService getSapS4OMOutboundService() {
		return sapS4OMOutboundService;
	}

	public void setSapS4OMOutboundService(SapS4OMOutboundService sapS4OMOutboundService) {
		this.sapS4OMOutboundService = sapS4OMOutboundService;
	}

	
	

}
