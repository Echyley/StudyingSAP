/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.services.validator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.enums.QuoteAction;
import de.hybris.platform.commerceservices.order.exceptions.IllegalQuoteStateException;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.services.data.QuoteEntryStatus;
import de.hybris.platform.sap.productconfig.services.impl.CPQConfigurableChecker;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationAbstractOrderIntegrationStrategy;
import de.hybris.platform.store.BaseStoreModel;

import java.util.Collections;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ProductConfigQuoteValidatorTest
{
	private static final String PRODUCT_CODE = "product";
	private final String quoteCode = "1224";

	@InjectMocks
	private ProductConfigQuoteValidator classUnderTest;

	@Mock
	private  CPQConfigurableChecker cpqConfigurableChecker;
	@Mock
	private ConfigurationAbstractOrderIntegrationStrategy configurationAbstractOrderIntegrationStrategy;


	@Mock
	private QuoteModel quoteModel;
	@Mock
	private CustomerModel customerModel;
	@Mock
	private BaseStoreModel baseStore;
	@Mock
	private AbstractOrderEntryModel quoteEntry;
	@Mock
	private ConfigModel configurationModel;
	@Mock
	private ProductModel productModel;

	private final QuoteAction quoteAction = QuoteAction.CHECKOUT;


	@Before
	public void initialize()
	{
		given(quoteModel.getEntries()).willReturn(Collections.singletonList(quoteEntry));
		given(quoteEntry.getProduct()).willReturn(productModel);
		given(productModel.getCode()).willReturn(PRODUCT_CODE);
		given(configurationAbstractOrderIntegrationStrategy.getConfigurationForAbstractOrderEntryForOneTimeAccess(quoteEntry))
				.willReturn(configurationModel);
		given(configurationModel.isComplete()).willReturn(true);
		given(configurationModel.isConsistent()).willReturn(true);
		given(cpqConfigurableChecker.isCPQConfiguratorApplicableProduct(Mockito.any())).willReturn(true);
	}



	@Test
	public void testValidateNoIssues()
	{
		classUnderTest.validate(quoteAction, quoteModel, customerModel);
	}

	@Test
	public void testValidateNoConfigurableProducts()
	{
		given(cpqConfigurableChecker.isCPQConfiguratorApplicableProduct(Mockito.any())).willReturn(false);
		classUnderTest.validate(quoteAction, quoteModel, customerModel);
	}

	@Test
	public void testValidateDifferentQuoteAction()
	{
		classUnderTest.validate(QuoteAction.APPROVE, quoteModel, customerModel);
	}

	@Test
	public void testValidateThereAreIssues()
	{
		given(configurationModel.isConsistent()).willReturn(false);
		try
		{
			classUnderTest.validate(quoteAction, quoteModel, customerModel);
		}
		catch (final IllegalQuoteStateException ex)
		{
			assertNotNull(ex.getLocalizedMessage());
			assertEquals(classUnderTest.getLocalizedText(PRODUCT_CODE), ex.getLocalizedMessage());
		}
	}


	@Test
	public void testHasConfigurationIssues()
	{
		final boolean result = classUnderTest.hasConfigurationIssue(quoteEntry);
		assertFalse(result);
	}

	@Test
	public void testHasConfigurationIssuesNonConfigurableProduct()
	{
		when(cpqConfigurableChecker.isCPQConfiguratorApplicableProduct(Mockito.any())).thenReturn(false);
		assertFalse(classUnderTest.hasConfigurationIssue(quoteEntry));
	}

	@Test
	public void testHasConfigurationIssuesIncompleteConfiguration()
	{
		when(configurationModel.isComplete()).thenReturn(false);
		assertTrue(classUnderTest.hasConfigurationIssue(quoteEntry));
	}

	@Test
	public void testHasConfigurationIssuesConflictingConfiguration()
	{
		when(configurationModel.isConsistent()).thenReturn(false);
		assertTrue(classUnderTest.hasConfigurationIssue(quoteEntry));
	}

	@Test
	public void testGetQuoteEntryStatus()
	{
		final QuoteEntryStatus quoteEntryStatus = classUnderTest.getQuoteEntryStatus(quoteEntry);
		assertNotNull(quoteEntryStatus);
		assertEquals(PRODUCT_CODE, quoteEntryStatus.getProductCode());
		assertFalse(quoteEntryStatus.getHasConfigurationIssue());
	}

	@Test
	public void testGetSingleConfigurationIssueNoIssues()
	{
		final Optional<QuoteEntryStatus> configurationIssue = classUnderTest.getSingleConfigurationIssue(quoteModel);
		assertNotNull(configurationIssue);
		assertFalse(configurationIssue.isPresent());
	}

	@Test
	public void testGetSingleConfigurationIssueIssuesExist()
	{
		when(configurationModel.isConsistent()).thenReturn(false);
		final Optional<QuoteEntryStatus> configurationIssue = classUnderTest.getSingleConfigurationIssue(quoteModel);
		assertNotNull(configurationIssue);
		assertTrue(configurationIssue.isPresent());
		assertEquals(PRODUCT_CODE, configurationIssue.get().getProductCode());
	}

	@Test
	public void testGetSingleConfigurationIssueNoConfigurableProducts()
	{
		when(cpqConfigurableChecker.isCPQConfiguratorApplicableProduct(Mockito.any())).thenReturn(false);
		final Optional<QuoteEntryStatus> configurationIssue = classUnderTest.getSingleConfigurationIssue(quoteModel);
		assertNotNull(configurationIssue);
		assertFalse(configurationIssue.isPresent());
	}

	@Test
	public void testGetSingleConfigurationIssueNoEntriesInQuote()
	{
		when(quoteModel.getEntries()).thenReturn(Collections.emptyList());
		final Optional<QuoteEntryStatus> configurationIssue = classUnderTest.getSingleConfigurationIssue(quoteModel);
		assertNotNull(configurationIssue);
		assertFalse(configurationIssue.isPresent());
	}
}
