/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.facades.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.order.CommerceSaveCartException;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.QuoteEntryModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.cpq.productconfig.services.AbstractOrderIntegrationService;
import de.hybris.platform.order.QuoteService;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit test for {@link DefaultQuoteIntegrationFacade}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultQuoteIntegrationFacadeTest
{
	private static final String CONFIG_ID = "config123";
	private static final String QUOTE_CODE = "quote123";
	private static final Integer ENTRY_NUMBER = Integer.valueOf(3);
	private static final String P_CODE = "product123";
	@Mock
	private AbstractOrderIntegrationService abstractOrderIntegrationService;
	@Mock
	private QuoteService quoteService;

	@InjectMocks
	private DefaultQuoteIntegrationFacade classUnderTest;
	private final QuoteModel quoteModel = new QuoteModel();
	private final QuoteEntryModel quoteEntry = new QuoteEntryModel();

	@Before
	public void setUp()
	{
		quoteModel.setEntries(new ArrayList<AbstractOrderEntryModel>());
		quoteModel.getEntries().add(quoteEntry);
		quoteEntry.setEntryNumber(ENTRY_NUMBER);
		quoteEntry.setProduct(new ProductModel());
		quoteEntry.getProduct().setCode(P_CODE);
		when(quoteService.getCurrentQuoteForCode(QUOTE_CODE)).thenReturn(quoteModel);
		when(abstractOrderIntegrationService.getConfigIdForAbstractOrderEntry(quoteEntry)).thenReturn(CONFIG_ID);
	}

	@Test
	public void testGetConfigurationId()
	{
		assertEquals(CONFIG_ID, classUnderTest.getConfigurationId(QUOTE_CODE, ENTRY_NUMBER.intValue()));
	}

	@Test
	public void testGetProductCode() throws CommerceSaveCartException
	{
		assertEquals(P_CODE, classUnderTest.getProductCode(QUOTE_CODE, ENTRY_NUMBER.intValue()));
	}

	@Test(expected = ModelNotFoundException.class)
	public void testGetProductCodeInavildEntry() throws CommerceSaveCartException
	{
		classUnderTest.getProductCode(QUOTE_CODE, -1);
	}
}
