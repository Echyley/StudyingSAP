/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.services.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.apiregistryservices.exceptions.CredentialException;
import de.hybris.platform.commerceservices.impersonation.ImpersonationContext;
import de.hybris.platform.commerceservices.impersonation.ImpersonationService;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.cpq.productconfig.services.BusinessContextService;
import de.hybris.platform.cpq.productconfig.services.CacheAccessService;
import de.hybris.platform.cpq.productconfig.services.CacheKeyService;
import de.hybris.platform.cpq.productconfig.services.ConfigurationServiceLayerHelper;
import de.hybris.platform.cpq.productconfig.services.StrategyDeterminationService;
import de.hybris.platform.cpq.productconfig.services.cache.DefaultCacheKey;
import de.hybris.platform.cpq.productconfig.services.data.ConfigurationSummaryData;
import de.hybris.platform.cpq.productconfig.services.data.ConfigurationSummaryDataContent;
import de.hybris.platform.cpq.productconfig.services.data.ConfigurationSummaryLineItemData;
import de.hybris.platform.cpq.productconfig.services.data.ConfigurationSummaryMessagesData;
import de.hybris.platform.cpq.productconfig.services.event.ProductConfigurationCPQCacheInvalidationEvent;
import de.hybris.platform.cpq.productconfig.services.strategies.ConfigurationLifecycleStrategy;
import de.hybris.platform.servicelayer.event.EventService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit test for {@link DefaultConfigurationService}
 */
@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class DefaultConfigurationServiceTest
{
	private static final String PRODUCT_CODE = "product";
	private static final String CONFIG_ID = "eab2-5243";
	private static final String CONFIG_ID_CLONED = "fdc2534";
	private static final String OWNER_ID = "user123";


	@InjectMocks
	private DefaultConfigurationService classUnderTest;

	@Mock
	private EventService eventService;
	@Mock
	private CacheAccessService<DefaultCacheKey, ConfigurationSummaryData> cacheAccessService;
	@Mock
	private CacheKeyService cacheKeyService;
	@Mock
	private StrategyDeterminationService<ConfigurationLifecycleStrategy> strategyDeterminationService;
	@Mock
	private ConfigurationLifecycleStrategy defaultLifecycleStrategy;
	@Mock
	private BusinessContextService businesContextService;
	@Mock
	private ConfigurationServiceLayerHelper serviceLayerHelper;
	@Spy
	private final ImpersonationService impersonationService = new ImpersonationService()
	{
		@Override
		public <R, T extends Throwable> R executeInContext(final ImpersonationContext context, final Executor<R, T> wrapper)
				throws T
		{
			return wrapper.execute();
		}
	};

	private ConfigurationSummaryData configurationSummary;
	private ConfigurationSummaryDataContent configurationSummaryContent;

	private ConfigurationSummaryMessagesData messages;

	private final UserModel user = new UserModel();
	private final OrderEntryModel orderEntry = new OrderEntryModel();


	@Before
	public void initialize() throws CredentialException
	{
		configurationSummary = new ConfigurationSummaryData();
		configurationSummaryContent = new ConfigurationSummaryDataContent();
		messages = new ConfigurationSummaryMessagesData();
		configurationSummary.setConfiguration(configurationSummaryContent);
		configurationSummaryContent.setMessages(messages);

		when(strategyDeterminationService.get()).thenReturn(defaultLifecycleStrategy);
		when(defaultLifecycleStrategy.createConfiguration(PRODUCT_CODE, OWNER_ID)).thenReturn(CONFIG_ID);
		when(defaultLifecycleStrategy.cloneConfiguration(eq(CONFIG_ID), anyBoolean())).thenReturn(CONFIG_ID_CLONED);
		when(defaultLifecycleStrategy.deleteConfiguration(CONFIG_ID)).thenReturn(true);
		when(cacheAccessService.getWithSupplier(Mockito.any(), Mockito.any())).thenReturn(configurationSummary);
		when(businesContextService.getOwnerId()).thenReturn(OWNER_ID);
	}

	@Test
	public void testCreateConfiguration()
	{
		assertEquals(CONFIG_ID, classUnderTest.createConfiguration(PRODUCT_CODE));
	}

	@Test
	public void testCloneConfiguration()
	{
		assertEquals(CONFIG_ID_CLONED, classUnderTest.cloneConfiguration(CONFIG_ID, true));
	}

	@Test
	public void testRetrieveConfigurationSummary()
	{
		assertEquals(configurationSummary, classUnderTest.getConfigurationSummary(CONFIG_ID));
	}

	@Test
	public void testDeleteConfiguration()
	{
		assertTrue(classUnderTest.deleteConfiguration(CONFIG_ID));
	}

	@Test
	public void testDeleteConfigurationPublishesEvent()
	{
		assertTrue(classUnderTest.deleteConfiguration(CONFIG_ID));
		verify(eventService)
				.publishEvent(argThat(arg -> CONFIG_ID.equals(((ProductConfigurationCPQCacheInvalidationEvent) arg).getConfigId())));
	}


	@Test
	public void testCacheAccessIsCalledCorrectly()
	{
		classUnderTest.getConfigurationSummary(CONFIG_ID);
		verify(cacheKeyService, times(1)).createConfigurationSummaryCacheKey(CONFIG_ID);
		verify(cacheAccessService, times(1)).getWithSupplier(Mockito.any(), Mockito.any());
	}

	@Test
	public void testRemoveCachedConfigurationSummaryPublishesEvent()
	{
		classUnderTest.removeCachedConfigurationSummary(CONFIG_ID);
		verify(eventService)
				.publishEvent(argThat(arg -> CONFIG_ID.equals(((ProductConfigurationCPQCacheInvalidationEvent) arg).getConfigId())));
	}

	@Test
	public void testRemoveCachedConfigurationSummary()
	{
		classUnderTest.removeCachedConfigurationSummary(CONFIG_ID);
		Mockito.verify(cacheKeyService).createConfigurationSummaryCacheKey(CONFIG_ID);
		Mockito.verify(cacheAccessService).remove(Mockito.any());
	}

	@Test
	public void testHasConfigurationIssues()
	{
		assertFalse(classUnderTest.hasConfigurationIssues(CONFIG_ID));
	}

	@Test(expected = NullPointerException.class)
	public void testHasConfigurationIssuesNoSummary()
	{
		when(cacheAccessService.getWithSupplier(Mockito.any(), Mockito.any())).thenReturn(null);
		classUnderTest.hasConfigurationIssues(CONFIG_ID);
	}

	@Test
	public void testHasConfigurationIssuesIncompletenessMessagesAvailable()
	{
		messages.setIncompleteMessages(Arrays.asList("Huhu"));
		assertTrue(classUnderTest.hasConfigurationIssues(CONFIG_ID));
	}

	@Test
	public void testHasConfigurationIssuesErrorMessagesAvailable()
	{
		messages.setErrorMessages(Arrays.asList("Huhu"));
		assertTrue(classUnderTest.hasConfigurationIssues(CONFIG_ID));
	}

	@Test
	public void testHasConfigurationIssuesFailedValidationsAvailable()
	{
		messages.setFailedValidations(Arrays.asList("Huhu"));
		assertTrue(classUnderTest.hasConfigurationIssues(CONFIG_ID));
	}

	@Test
	public void testHasConfigurationIssuesInvalidMessagesAvailable()
	{
		messages.setInvalidMessages(Arrays.asList("Huhu"));
		assertTrue(classUnderTest.hasConfigurationIssues(CONFIG_ID));
	}

	@Test
	public void testHasConfigurationIssuesMessagesAvailableButEmpty()
	{
		assertFalse(classUnderTest.hasConfigurationIssues(CONFIG_ID));
	}

	@Test
	public void testHasConfigurationIssuesMessagesAvailableButNull()
	{
		configurationSummaryContent.setMessages(null);
		assertFalse(classUnderTest.hasConfigurationIssues(CONFIG_ID));
	}

	@Test
	public void testGetNumberOfConfigurationIssues()
	{
		prepareMessages();
		assertEquals(3, classUnderTest.getNumberOfConfigurationIssues(CONFIG_ID));
	}

	@Test
	public void testMakeConfigurationPermanent()
	{
		classUnderTest.makeConfigurationPermanent(CONFIG_ID);
		verify(defaultLifecycleStrategy).makeConfigurationPermanent(CONFIG_ID);
	}

	@Test
	public void testGetLineItems()
	{
		configurationSummaryContent.setLineItems(new ArrayList<ConfigurationSummaryLineItemData>());
		assertNotNull(classUnderTest.getLineItems(CONFIG_ID));
		assertEquals(0, classUnderTest.getLineItems(CONFIG_ID).size());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetNumberOfIssuesConfigIdNull()
	{
		classUnderTest.getNumberOfConfigurationIssues(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testHasConfigurationIssuesConfigIdEmpty()
	{
		classUnderTest.hasConfigurationIssues("");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetLineItemsConfigIdNull()
	{
		classUnderTest.getLineItems(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetConfigurationSummaryConfigIdEmpty()
	{
		classUnderTest.getConfigurationSummary("");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testMakeConfigurationPermanentConfigIdNull()
	{
		classUnderTest.makeConfigurationPermanent(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testDeleteConfigurationConfigIdEmpty()
	{
		classUnderTest.deleteConfiguration("");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRemoveCachedConfigurationSummaryConfigIdNull()
	{
		classUnderTest.removeCachedConfigurationSummary(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCloneConfigurationConfigIdNull()
	{
		classUnderTest.cloneConfiguration(null, true);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateConfigurationProductCodeNull()
	{
		classUnderTest.createConfiguration(null);
	}

	@Test
	public void testGetConfigurationSummary_Impersonation()
	{
		when(serviceLayerHelper.retrieveUserForAbstractOrderEntryIfRelevant(orderEntry)).thenReturn(user);
		assertEquals(configurationSummary, classUnderTest.getConfigurationSummary(CONFIG_ID, orderEntry));
		verify(impersonationService).executeInContext(any(ImpersonationContext.class), any());
	}

	@Test
	public void testGetConfigurationSummary_No_Impersonation()
	{
		when(serviceLayerHelper.retrieveUserForAbstractOrderEntryIfRelevant(orderEntry)).thenReturn(null);
		assertEquals(configurationSummary, classUnderTest.getConfigurationSummary(CONFIG_ID, orderEntry));
		verify(impersonationService, never()).executeInContext(any(), any());
	}

	@Test
	public void testGetNumberOfConfigurationIssues_Impersonation() throws Throwable
	{
		prepareMessages();
		when(serviceLayerHelper.retrieveUserForAbstractOrderEntryIfRelevant(orderEntry)).thenReturn(user);
		assertEquals(3, classUnderTest.getNumberOfConfigurationIssues(CONFIG_ID, orderEntry));
		verify(impersonationService).executeInContext(any(ImpersonationContext.class), any());
	}

	@Test
	public void testGetNumberOfConfigurationIssues_No_Impersonation()
	{
		prepareMessages();
		when(serviceLayerHelper.retrieveUserForAbstractOrderEntryIfRelevant(orderEntry)).thenReturn(null);
		assertEquals(3, classUnderTest.getNumberOfConfigurationIssues(CONFIG_ID, orderEntry));
		verify(impersonationService, never()).executeInContext(any(), any());
	}



	@Test
	public void testHasConfigurationIssues_Impersonation() throws Throwable
	{
		when(serviceLayerHelper.retrieveUserForAbstractOrderEntryIfRelevant(orderEntry)).thenReturn(user);
		assertFalse(classUnderTest.hasConfigurationIssues(CONFIG_ID, orderEntry));
		verify(impersonationService).executeInContext(any(ImpersonationContext.class), any());
	}

	@Test
	public void testHasConfigurationIssues_No_Impersonation()
	{
		when(serviceLayerHelper.retrieveUserForAbstractOrderEntryIfRelevant(orderEntry)).thenReturn(null);
		assertFalse(classUnderTest.hasConfigurationIssues(CONFIG_ID, orderEntry));
		verify(impersonationService, never()).executeInContext(any(), any());
	}

	protected void prepareMessages()
	{
		final List<String> incompleteMessages = new ArrayList<>();
		incompleteMessages.add("message 1");
		incompleteMessages.add("message 2");
		messages.setIncompleteMessages(incompleteMessages);
		messages.setErrorMessages(Collections.singletonList("message 3"));
	}
}
