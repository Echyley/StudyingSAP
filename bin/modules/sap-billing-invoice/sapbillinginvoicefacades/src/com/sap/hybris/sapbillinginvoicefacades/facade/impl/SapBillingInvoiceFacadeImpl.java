/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapbillinginvoicefacades.facade.impl;

import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.sap.sapmodel.model.SAPOrderModel;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

import java.util.Map;
import java.util.Set;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.sap.hybris.sapbillinginvoicefacades.document.data.ExternalSystemBillingDocumentData;
import com.sap.hybris.sapbillinginvoicefacades.facade.SapBillingInvoiceFacade;
import com.sap.hybris.sapbillinginvoicefacades.strategy.SapBillingInvoiceStrategy;
import com.sap.hybris.sapbillinginvoicefacades.utils.SapBillingInvoiceUtils;
import com.sap.hybris.sapbillinginvoiceservices.exception.SapBillingInvoiceUserException;
import com.sap.hybris.sapbillinginvoiceservices.service.SapBillingInvoiceService;

public class SapBillingInvoiceFacadeImpl implements SapBillingInvoiceFacade{

	private static final Logger LOG = Logger.getLogger(SapBillingInvoiceFacadeImpl.class);

	private SapBillingInvoiceService sapBillingInvoiceService;

	private Map<String, SapBillingInvoiceStrategy> handlers;

	private SapBillingInvoiceUtils sapBillingInvoiceUtils;
	
	private CustomerAccountService customerAccountService;
    
    private BaseStoreService baseStoreService;
    
    public CustomerAccountService getCustomerAccountService() {
        return customerAccountService;
    }
    public void setCustomerAccountService(CustomerAccountService customerAccountService) {
        this.customerAccountService = customerAccountService;
    }
    public BaseStoreService getBaseStoreService() {
        return baseStoreService;
    }
    public void setBaseStoreService(BaseStoreService baseStoreService) {
        this.baseStoreService = baseStoreService;
    }

	public void setHandlers(final Map<String, SapBillingInvoiceStrategy> handlers)
	{
		this.handlers = handlers;
	}

	public void registerHandler(final String stringValue, final SapBillingInvoiceStrategy handler)
	{
		handlers.put(stringValue, handler);
	}

	public void removeHandler(final String stringValue)
	{
		handlers.remove(stringValue);
	}

	public SapBillingInvoiceUtils getSapBillingInvoiceUtils()
	{
		return sapBillingInvoiceUtils;
	}

	public void setSapBillingInvoiceUtils(final SapBillingInvoiceUtils sapBillingInvoiceUtils)
	{
		this.sapBillingInvoiceUtils = sapBillingInvoiceUtils;
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
	 * @deprecated since 2108, method name is misleading as this method is just retrieving SAP Order by SAP order code. Please use method getSapOrderBySapOrderCode() for same functionality
	 */
	@Deprecated(since = "2108", forRemoval = true)
	@Override
	public SAPOrderModel getServiceOrderBySapOrderCode(final String sapOrderCode)
	{
		return getSapBillingInvoiceService().getServiceOrderBySapOrderCode(sapOrderCode);
	}
	
	/**
	 *  @deprecated since 2108, Unused method
	 */
	@Deprecated(since = "2108", forRemoval = true)
	@Override
	public Map<String, Object> getBusinessDocumentFromS4ServiceOrderCode(final SAPOrderModel serviceOrderData) {
		return getSapBillingInvoiceService().getBusinessDocumentFromS4ServiceOrderCode(serviceOrderData);
	}

	@Override
	public SAPOrderModel getSapOrderBySapOrderCode(String sapOrderCode) {
		return getSapBillingInvoiceService().getSapOrderBySapOrderCode(sapOrderCode);
	}

	@Override
	public byte[] getPDFData(final String sapOrderCode, final String billingDocId) throws SapBillingInvoiceUserException
	{
		LOG.info("Start of Get PDF facade layer");
		byte[] pdfData = new byte[0];

		final SAPOrderModel sapOrder = getSapOrderBySapOrderCode(sapOrderCode);
		final String orderType = getSapBillingInvoiceUtils().getSapOrderType(sapOrder);

		if (orderType != null && !orderType.isEmpty())
		{
			final SapBillingInvoiceStrategy sapBillingInvoiceStrategy = handlers.get(orderType);
			if (sapBillingInvoiceStrategy != null)
			{
				pdfData = sapBillingInvoiceStrategy.getPDFData(sapOrder, billingDocId);
			}
		}

		LOG.info("End of Get PDF facade layer");

		return pdfData;
	}
	
	
	@Override
    public List<ExternalSystemBillingDocumentData> getBillingDocumentsForOrder(final String orderCode) {
        final List<ExternalSystemBillingDocumentData> billingItems = new ArrayList<>();
        final BaseStoreModel baseStoreModel = baseStoreService.getCurrentBaseStore();
        OrderModel order = customerAccountService.getOrderForCode(orderCode, baseStoreModel);
        if(order!=null) {
            final Set<SAPOrderModel> sapOrders = order.getSapOrders();
            for (final SAPOrderModel sapOrder : sapOrders)
            {
                final String orderType = getSapBillingInvoiceUtils().getSapOrderType(sapOrder);
                getBillingDocumentsForSAPOrder(billingItems, sapOrder, orderType);
            }
        }   
        return billingItems;
    } 
    
    
    
    protected void getBillingDocumentsForSAPOrder(final List<ExternalSystemBillingDocumentData> billingItems, final SAPOrderModel sapOrder,
            final String orderType) {
        if (orderType != null && !orderType.isEmpty())
        {
            final SapBillingInvoiceStrategy sapBillingInvoiceStrategy = handlers.get(orderType);
            if (sapBillingInvoiceStrategy != null)
            {
                final List<ExternalSystemBillingDocumentData> billingItemsStrategyResponse = sapBillingInvoiceStrategy
                        .getBillingDocuments(sapOrder);
                billingItems.addAll(billingItemsStrategyResponse);
            }
        }
    }
	

}
