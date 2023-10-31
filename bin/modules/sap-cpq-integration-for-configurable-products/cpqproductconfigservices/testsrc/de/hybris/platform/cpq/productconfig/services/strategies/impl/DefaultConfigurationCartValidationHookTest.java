/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.services.strategies.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationStatus;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.cpq.productconfig.services.AbstractOrderIntegrationService;
import de.hybris.platform.cpq.productconfig.services.ConfigurableChecker;
import de.hybris.platform.cpq.productconfig.services.ConfigurationService;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


/**
 * Unit test for {@link DefaultConfigurationCartValidationHook}
 */
@UnitTest
public class DefaultConfigurationCartValidationHookTest
{
	private static final String CONFIG_ID = "1234";
	private static final String CONFIG_ID2 = "5678";

	@InjectMocks
	private DefaultConfigurationCartValidationHook classUnderTest;
	@Mock
	private ConfigurationService configurationService;
	@Mock
	private ConfigurableChecker configurableChecker;
	@Mock
	private AbstractOrderIntegrationService abstractOrderIntegrationService;
	@Mock
	private CartModel cart;
	@Mock
	private AbstractOrderEntryModel entry1;
	@Mock
	private AbstractOrderEntryModel entry2;
	@Mock
	private ProductModel product1;
	@Mock
	private ProductModel product2;

	private final CommerceCartParameter parameter = new CommerceCartParameter();
	private final List<CommerceCartModification> modifications = new ArrayList<CommerceCartModification>();
	private final CommerceCartModification successfulModification = new CommerceCartModification();
	private final CommerceCartModification errorModification = new CommerceCartModification();


	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
		when(configurableChecker.isCloudCPQConfigurableProduct(product1)).thenReturn(true);
		when(configurableChecker.isCloudCPQConfigurableProduct(product2)).thenReturn(false);
		when(entry1.getProduct()).thenReturn(product1);
		when(entry2.getProduct()).thenReturn(product2);
		when(entry1.getPk()).thenReturn(PK.fromLong(1));
		when(entry2.getPk()).thenReturn(PK.fromLong(2));
		final List<AbstractOrderEntryModel> entries = new ArrayList<AbstractOrderEntryModel>();
		entries.add(entry1);
		entries.add(entry2);
		when(cart.getEntries()).thenReturn(entries);
		parameter.setCart(cart);
		successfulModification.setStatusCode(CommerceCartModificationStatus.SUCCESS);
		successfulModification.setEntry(entry1);
		errorModification.setStatusCode(CommerceCartModificationStatus.NO_STOCK);
		errorModification.setEntry(entry2);
		modifications.add(successfulModification);
		modifications.add(errorModification);
		when(abstractOrderIntegrationService.getConfigIdForAbstractOrderEntry(entry1)).thenReturn(CONFIG_ID);
		when(configurationService.hasConfigurationIssues(CONFIG_ID)).thenReturn(true);
	}

	@Test
	public void testAfterValidateHookNoConfigurationProblems()
	{
		Mockito.when(configurationService.hasConfigurationIssues(CONFIG_ID)).thenReturn(false);
		classUnderTest.afterValidateCart(parameter, modifications);
		assertEquals(2, modifications.size());
		assertEquals(CommerceCartModificationStatus.SUCCESS, modifications.get(0).getStatusCode());
		assertEquals(CommerceCartModificationStatus.NO_STOCK, modifications.get(1).getStatusCode());
	}

	@Test
	public void testAfterValidateHookConfigurationProblem()
	{
		classUnderTest.afterValidateCart(parameter, modifications);
		assertEquals(2, modifications.size());
		assertEquals(CommerceCartModificationStatus.NO_STOCK, modifications.get(0).getStatusCode());
		assertEquals(entry2, modifications.get(0).getEntry());
		assertEquals(DefaultConfigurationCartValidationHook.REVIEW_CONFIGURATION, modifications.get(1).getStatusCode());
		assertEquals(entry1, modifications.get(1).getEntry());
	}

	@Test
	public void testAfterValidateHookIgnoreConfigurableEntryWithOtherProblem()
	{
		// second product is also configurable
		when(configurableChecker.isCloudCPQConfigurableProduct(product2)).thenReturn(true);
		when(abstractOrderIntegrationService.getConfigIdForAbstractOrderEntry(entry2)).thenReturn(CONFIG_ID2);

		// second configuration has also issues
		when(configurationService.hasConfigurationIssues(CONFIG_ID2)).thenReturn(true);

		classUnderTest.afterValidateCart(parameter, modifications);
		assertEquals(2, modifications.size());
		// Don't change error-status for entry2
		assertEquals(CommerceCartModificationStatus.NO_STOCK, modifications.get(0).getStatusCode());
		assertEquals(entry2, modifications.get(0).getEntry());
		assertEquals(DefaultConfigurationCartValidationHook.REVIEW_CONFIGURATION, modifications.get(1).getStatusCode());
		assertEquals(entry1, modifications.get(1).getEntry());
	}

	@Test
	public void testValidateConfiguration()
	{
		classUnderTest.validateConfiguration(modifications, entry1);
		assertEquals(2, modifications.size());
		assertEquals(DefaultConfigurationCartValidationHook.REVIEW_CONFIGURATION, modifications.get(1).getStatusCode());
	}

	@Test
	public void testValidateConfigurationNoIssues()
	{
		Mockito.when(configurationService.hasConfigurationIssues(CONFIG_ID)).thenReturn(false);
		classUnderTest.validateConfiguration(modifications, entry1);
		assertEquals(2, modifications.size());
		assertEquals(CommerceCartModificationStatus.SUCCESS, modifications.get(0).getStatusCode());
	}

	@Test
	public void testValidateConfigurationAttachedToEntry()
	{
		final CommerceCartModification modification = classUnderTest.validateConfigurationAttachedToEntry(entry1);
		assertNotNull(modification);
	}

	@Test
	public void testValidateConfigurationsAttachedToEntryNoIssues()
	{
		Mockito.when(configurationService.hasConfigurationIssues(CONFIG_ID)).thenReturn(false);
		assertNull(classUnderTest.validateConfigurationAttachedToEntry(entry1));
	}

	@Test
	public void testValidateConfigurationsAttachedToEntryShouldRemoveCache()
	{
		classUnderTest.validateConfigurationAttachedToEntry(entry1);
		verify(configurationService, times(1)).removeCachedConfigurationSummary(CONFIG_ID);
	}

	@Test
	public void testIsEntrySuccess()
	{
		assertTrue(classUnderTest.isEntrySuccess(entry1, modifications));
	}

	@Test
	public void testIsEntrySuccessFalse()
	{
		assertFalse(classUnderTest.isEntrySuccess(entry2, modifications));
	}

	@Test
	public void testRetrieveModificationForEntry()
	{
		final CommerceCartModification result = classUnderTest.retrieveModificationForEntry(entry1, modifications);
		assertEquals(successfulModification, result);
	}


	@Test(expected = IllegalStateException.class)
	public void testRetrieveModificationForEntryNotFound()
	{
		final AbstractOrderEntryModel anotherEntry = mock(AbstractOrderEntryModel.class);
		when(anotherEntry.getPk()).thenReturn(PK.fromLong(3));
		classUnderTest.retrieveModificationForEntry(anotherEntry, modifications);
	}
}
