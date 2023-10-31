/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.c4ccpiquote.outbound.service.impl;

import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.sap.hybris.c4ccpiquote.constants.C4ccpiquoteConstants;
import com.sap.hybris.c4ccpiquote.model.C4CSalesOrderNotificationModel;
import com.sap.hybris.c4ccpiquote.model.SAPC4CCpiOutboundQuoteModel;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.comments.model.CommentModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.util.Config;

/**
 * JUnit Tests for the DefaultSapCpiOutboundC4CQuoteConversionService in
 * c4ccpiquote extension.
 */
@UnitTest
public class DefaultSapCpiOutboundC4CQuoteConversionServiceTest extends ServicelayerTest {

	@InjectMocks
	private DefaultSapCpiOutboundC4CQuoteConversionService quoteConversion = new DefaultSapCpiOutboundC4CQuoteConversionService();

	@Mock
	private ModelService modelService;

	@Mock
	private QuoteModel model;

	@Mock
	private B2BUnitService<B2BUnitModel, CustomerModel> b2bUnitService;

	@Mock
	private B2BCustomerModel customer;

	@Mock
	private B2BUnitModel b2bUnitModel;

	@Mock
	private B2BUnitModel rootUnit;

	@Mock
	private AbstractOrderEntryModel quoteItem;

	@Mock
	private CommentModel comment;

	@Mock
	private CurrencyModel cModel;
	@Mock
	private ProductModel pModel;

	@Mock
	private UnitModel uModel;

	@Mock
	private BaseStoreModel bsModel;

	@Mock
	private SAPConfigurationModel configModel;
	
	@Mock
	private QuoteModel quote;

	/**
	 * Test setup.
	 */
	@Before
	public void setUp() {
		Config.setParameter(C4ccpiquoteConstants.QUOTE_ACTION_CODE, "04");
		Config.setParameter(C4ccpiquoteConstants.ITEM_COMPLETE_TRANSMISSION_INDICATOR, "true");
		Config.setParameter(C4ccpiquoteConstants.OTHER_PARTY_LIST_COMPLETE_TRANSMISSION_INDICATOR, "true");
		Config.setParameter(
				C4ccpiquoteConstants.BUSINESS_TRANSACTION_DOCUMENT_REFERENCE_COMPLETE_TRANSMISSION_INDICATOR, "true");
		Config.setParameter(C4ccpiquoteConstants.SALES_EMPLOYEE_PARTY_LIST_COMPLETE_TRANSMISSION_INDICATOR, "true");
		Config.setParameter(C4ccpiquoteConstants.ITEM_ACTION_CODE, "04");
		Config.setParameter(C4ccpiquoteConstants.ITEM_CUSTOM_DEFINED_PARTY_LIST_COMPLETE_TRANSMISSION_INDICATOR, "04");
		Config.setParameter(C4ccpiquoteConstants.SCHEDULELINE_COMPLETE_TRANSMISSION_INDICATOR, "04");
		Config.setParameter(C4ccpiquoteConstants.PARTY_ID_TYPE, "BP");
		Config.setParameter(C4ccpiquoteConstants.SCHEDULE_LINE_ID, "01");
		Config.setParameter(C4ccpiquoteConstants.SCHEDULE_LINE_TYPE_CODE, "ab");
        
		Config.setParameter(C4ccpiquoteConstants.BUSINESS_TRANSACTION_DOCUMENT_ACTION_CODE, "abc");
		Config.setParameter(C4ccpiquoteConstants.BUSINESS_TRANSACTION_DOCUMENT_REFERENCE_LIST_COMPLETE_TRANSMISSION_INDICATOR, "abd");
		Config.setParameter(C4ccpiquoteConstants.ITEM_LIST_COMPLETE_TRANSMISSION_INDICATOR, "abc");
		Config.setParameter(C4ccpiquoteConstants.BUSINEES_TRANSACTION_DOCUMENT_TYPE_CODE, "abd");
		Config.setParameter(C4ccpiquoteConstants.SALES_ORDER_TYPE_CODE, "xyz");

		MockitoAnnotations.initMocks(this);
	}

	/**
	 * Test for convertQuoteToSapCpiQuote.
	 */
	@Test
	public void testConvertQuoteToSapCpiQuote() {
		long quant = 45;
		when(model.getName()).thenReturn("test");
		when(model.getCode()).thenReturn("1234");
		when(model.getCurrency()).thenReturn(cModel);
		when(cModel.getIsocode()).thenReturn("USD");
		when(model.getStore()).thenReturn(bsModel);
		when(bsModel.getSAPConfiguration()).thenReturn(configModel);
		when(configModel.getSapcommon_distributionChannel()).thenReturn("01");
		when(configModel.getSapcommon_salesOrganization()).thenReturn("0001");
		when(configModel.getSapcommon_division()).thenReturn("01");
		when(configModel.getProcessingTypeCode()).thenReturn("ZRFQ");
		when(configModel.getLogicalSystemId()).thenReturn("qw9clnt300");
		when(model.getCode()).thenReturn("123456");
		when(model.getUser()).thenReturn(customer);
		when(b2bUnitService.getParent(customer)).thenReturn(b2bUnitModel);
		when(b2bUnitService.getRootUnit(b2bUnitModel)).thenReturn(rootUnit);
		when(rootUnit.getUid()).thenReturn("pronoto");
		when(customer.getCustomerID()).thenReturn("23432");
		when(model.getEntries()).thenReturn(Collections.singletonList(quoteItem));
		when(model.getVersion()).thenReturn(1);
		when(quoteItem.getC4cItemEntryId()).thenReturn("1");
		when(quoteItem.getProduct()).thenReturn(pModel);
		when(pModel.getDescription()).thenReturn("abcd");
		when(quoteItem.getUnit()).thenReturn(uModel);
		when(uModel.getCode()).thenReturn("1234");
		when(quoteItem.getQuantity()).thenReturn(quant);
		when(pModel.getCode()).thenReturn("3455");

		when(comment.getCommentCode()).thenReturn("4353");

		SAPC4CCpiOutboundQuoteModel salesQuoteModel = new SAPC4CCpiOutboundQuoteModel();
		salesQuoteModel = quoteConversion.convertQuoteToSapCpiQuote(model);
		Assert.assertNotNull(salesQuoteModel);
	}
	
	/**
	 * Test for ConvertQuoteToSalesOrderNotification.
	 */
	@Test
	public void testConvertQuoteToSalesOrderNotification() {
		C4CSalesOrderNotificationModel salesOrderNotification = new C4CSalesOrderNotificationModel();
		when(quote.getQuoteOrderId()).thenReturn("12345");
		when(quote.getC4cQuoteExternalQuoteId()).thenReturn("678965");
		when(quote.getStore()).thenReturn(bsModel);
		when(bsModel.getSAPConfiguration()).thenReturn(configModel);
		when(configModel.getLogicalSystemId()).thenReturn("qw9clnt300");
		salesOrderNotification = quoteConversion.convertQuoteToSalesOrderNotification(quote);
		Assert.assertNotNull(salesOrderNotification);
		
	}

}
