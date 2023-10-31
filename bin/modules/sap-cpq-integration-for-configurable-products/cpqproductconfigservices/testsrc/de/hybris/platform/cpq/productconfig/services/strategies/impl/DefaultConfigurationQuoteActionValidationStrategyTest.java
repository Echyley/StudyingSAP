/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.services.strategies.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.enums.QuoteAction;
import de.hybris.platform.commerceservices.order.exceptions.IllegalQuoteStateException;
import de.hybris.platform.commerceservices.order.strategies.QuoteActionValidationStrategy;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.cpq.productconfig.services.AbstractOrderIntegrationService;
import de.hybris.platform.cpq.productconfig.services.ConfigurableChecker;
import de.hybris.platform.cpq.productconfig.services.ConfigurationService;
import de.hybris.platform.cpq.productconfig.services.data.QuoteEntryValidationData;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


/**
 * Unit test for {@link DefaultConfigurationQuoteActionValidationStrategy}
 */
@UnitTest
public class DefaultConfigurationQuoteActionValidationStrategyTest
{
	private static final String PRODUCT_CODE = "CONF_LAPTOP";

	private static final String CONFIG_ID = "1234";

	@InjectMocks
	DefaultConfigurationQuoteActionValidationStrategy classUnderTest;

	@Mock
	QuoteActionValidationStrategy defaultQuoteActionValidationStrategy;

	private QuoteAction quoteAction;

	@Mock
	private QuoteModel quoteModel;

	@Mock
	private UserModel userModel;

	@Mock
	private ConfigurableChecker configurableChecker;

	@Mock
	private AbstractOrderIntegrationService abstractOrderIntegrationService;

	@Mock
	private ConfigurationService configurationService;

	private final List<AbstractOrderEntryModel> quoteEntries = new ArrayList<>();

	@Mock
	private AbstractOrderEntryModel quoteEntry;

	@Mock
	private ProductModel product;

	@Mock
	private ProductModel productNotConfigurable;

	@Before
	public void initialize()
	{
		MockitoAnnotations.initMocks(this);
		quoteAction = QuoteAction.CHECKOUT;
		Mockito.when(quoteModel.getEntries()).thenReturn(quoteEntries);
		Mockito.when(quoteEntry.getProduct()).thenReturn(product);
		Mockito.when(product.getCode()).thenReturn(PRODUCT_CODE);
		Mockito.when(configurableChecker.isCloudCPQConfigurableProduct(product)).thenReturn(true);
		Mockito.when(configurableChecker.isCloudCPQConfigurableProduct(productNotConfigurable)).thenReturn(false);
		Mockito.when(abstractOrderIntegrationService.getConfigIdForAbstractOrderEntry(quoteEntry)).thenReturn(CONFIG_ID);
		Mockito.when(configurationService.hasConfigurationIssues(CONFIG_ID)).thenReturn(true);
		quoteEntries.add(quoteEntry);
	}

	@Test
	public void testQuoteActionValidationStrategy()
	{
		assertEquals(defaultQuoteActionValidationStrategy, classUnderTest.getDefaultQuoteActionValidationStrategy());
	}

	@Test
	public void testConfigurableChecker()
	{
		assertEquals(configurableChecker, classUnderTest.getConfigurableChecker());
	}

	@Test
	public void testAbstractOrderIntegrationService()
	{
		assertEquals(abstractOrderIntegrationService, classUnderTest.getAbstractOrderIntegrationService());
	}

	@Test
	public void testConfigurationService()
	{
		assertEquals(configurationService, classUnderTest.getConfigurationService());
	}

	@Test
	public void testisValidAction()
	{
		classUnderTest.isValidAction(quoteAction, quoteModel, userModel);
		verify(defaultQuoteActionValidationStrategy).isValidAction(quoteAction, quoteModel, userModel);
	}

	@Test
	public void testValidateNoCheckoutAction()
	{
		quoteAction = QuoteAction.APPROVE;
		classUnderTest.validate(quoteAction, quoteModel, userModel);
		verify(defaultQuoteActionValidationStrategy).validate(quoteAction, quoteModel, userModel);
	}

	@Test(expected = IllegalQuoteStateException.class)
	public void testValidate()
	{
		classUnderTest.validate(quoteAction, quoteModel, userModel);

	}

	@Test
	public void testValidateNotQuoteEntries()
	{
		quoteEntries.clear();
		classUnderTest.validate(quoteAction, quoteModel, userModel);
		verify(defaultQuoteActionValidationStrategy).validate(quoteAction, quoteModel, userModel);
	}

	@Test
	public void testPerformConfigurationCheckoutValidation()
	{
		try
		{
			classUnderTest.performConfigurationCheckoutValidation(quoteModel, quoteAction);
			Assert.fail();
		}
		catch (final IllegalQuoteStateException ex)
		{
			assertEquals(classUnderTest.getLocalizedText(PRODUCT_CODE), ex.getLocalizedMessage());
		}

	}

	@Test
	public void testGetFirstConfigurationIssue()
	{
		final Optional<QuoteEntryValidationData> firstConfigurationIssue = classUnderTest.getFirstConfigurationIssue(quoteModel);
		assertNotNull(firstConfigurationIssue);
		assertTrue(firstConfigurationIssue.isPresent());
	}

	@Test(expected = NullPointerException.class)
	public void testGetFirstConfigurationIssueQuoteNull()
	{
		classUnderTest.getFirstConfigurationIssue(null);
	}

	@Test
	public void testGetConfigurationIssue()
	{
		final QuoteEntryValidationData configurationIssue = classUnderTest.getConfigurationIssue(quoteEntry);
		assertNotNull(configurationIssue);
		assertEquals(PRODUCT_CODE, configurationIssue.getProductCode());
		assertTrue(configurationIssue.isWithConfigurationIssue());
	}

	@Test
	public void testGetConfigurationIssueNoCPQProduct()
	{
		Mockito.when(quoteEntry.getProduct()).thenReturn(productNotConfigurable);
		final QuoteEntryValidationData configurationIssue = classUnderTest.getConfigurationIssue(quoteEntry);
		assertNotNull(configurationIssue);
		assertFalse(configurationIssue.isWithConfigurationIssue());
	}

	@Test
	public void testGetConfigurationIssueNoConfigurationIssues()
	{
		Mockito.when(configurationService.hasConfigurationIssues(CONFIG_ID)).thenReturn(false);
		final QuoteEntryValidationData configurationIssue = classUnderTest.getConfigurationIssue(quoteEntry);
		assertNotNull(configurationIssue);
		assertFalse(configurationIssue.isWithConfigurationIssue());
	}

	@Test(expected = IllegalStateException.class)
	public void testGetConfigurationIssueNoConfigId()
	{
		Mockito.when(abstractOrderIntegrationService.getConfigIdForAbstractOrderEntry(quoteEntry)).thenReturn(null);
		classUnderTest.getConfigurationIssue(quoteEntry);

	}


}
