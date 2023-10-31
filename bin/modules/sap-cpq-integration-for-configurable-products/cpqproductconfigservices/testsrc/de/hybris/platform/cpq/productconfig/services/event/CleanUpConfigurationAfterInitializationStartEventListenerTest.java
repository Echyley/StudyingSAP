/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.services.event;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.servicelayer.data.PaginationData;
import de.hybris.platform.core.servicelayer.data.SearchPageData;
import de.hybris.platform.cpq.productconfig.services.CloudCPQOrderEntryProductInfoDao;
import de.hybris.platform.cpq.productconfig.services.ConfigurationService;
import de.hybris.platform.cpq.productconfig.services.ConfigurationServiceLayerHelper;
import de.hybris.platform.cpq.productconfig.services.impl.DefaultConfigurationServiceLayerHelper;
import de.hybris.platform.cpq.productconfig.services.model.CloudCPQOrderEntryProductInfoModel;
import de.hybris.platform.servicelayer.event.events.AfterInitializationStartEvent;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;


/**
 * Unit test for {@link CleanUpConfigurationAfterInitializationStartEventListener}
 */
@UnitTest
public class CleanUpConfigurationAfterInitializationStartEventListenerTest
{
	private static final String CONFIG_ID = "configId";
	private static final String CONFIG_ID_2 = "configId-2";
	private static final String CONFIG_ID_3 = "configId-3";

	private CleanUpConfigurationAfterInitializationStartEventListener classUnderTest;
	private CleanUpConfigurationAfterInitializationStartEventListener realClassUnderTest;

	@Mock
	private BaseSiteService baseSiteService;
	@Mock
	private UserService userService;
	@Mock
	private ConfigurationService cpqService;
	@Mock
	private CloudCPQOrderEntryProductInfoDao productInfoDao;

	private ConfigurationServiceLayerHelper serviceLayerHelper;
	private SearchPageData<CloudCPQOrderEntryProductInfoModel> searchResult;
	private SearchPageData<CloudCPQOrderEntryProductInfoModel> searchResult2;
	private AfterInitializationStartEvent event;
	private CloudCPQOrderEntryProductInfoModel cpqInfo;
	private final BaseSiteModel orderSite = new BaseSiteModel();



	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new CleanUpConfigurationAfterInitializationStartEventListenerForTest();
		realClassUnderTest = new CleanUpConfigurationAfterInitializationStartEventListener();
		serviceLayerHelper = new DefaultConfigurationServiceLayerHelper(baseSiteService, userService);

		searchResult = new SearchPageData<>();
		when(productInfoDao.getAllConfigurationProductInfos(DefaultConfigurationServiceLayerHelper.PAGE_SIZE, 0))
				.thenReturn(searchResult);
		final List<CloudCPQOrderEntryProductInfoModel> results = new ArrayList<>();
		cpqInfo = new CloudCPQOrderEntryProductInfoModel();
		cpqInfo.setConfigurationId(CONFIG_ID);
		final AbstractOrderEntryModel entry = new AbstractOrderEntryModel();
		final AbstractOrderModel order = new AbstractOrderModel();
		order.setSite(orderSite);
		entry.setOrder(order);
		cpqInfo.setOrderEntry(entry);
		results.add(cpqInfo);
		final CloudCPQOrderEntryProductInfoModel cpqInfo2 = new CloudCPQOrderEntryProductInfoModel();
		cpqInfo2.setConfigurationId(CONFIG_ID_2);
		cpqInfo2.setOrderEntry(entry);
		results.add(cpqInfo2);
		searchResult.setResults(results);
		final PaginationData pagination = new PaginationData();
		pagination.setTotalNumberOfResults(2);
		pagination.setHasNext(false);
		pagination.setPageSize(DefaultConfigurationServiceLayerHelper.PAGE_SIZE);
		searchResult.setPagination(pagination);

		searchResult2 = new SearchPageData<>();
		when(productInfoDao.getAllConfigurationProductInfos(DefaultConfigurationServiceLayerHelper.PAGE_SIZE, 1))
				.thenReturn(searchResult2);

		final List<CloudCPQOrderEntryProductInfoModel> resultsPage2 = new ArrayList<>();
		final CloudCPQOrderEntryProductInfoModel cpqInfo3 = new CloudCPQOrderEntryProductInfoModel();
		cpqInfo3.setConfigurationId(CONFIG_ID_3);
		cpqInfo3.setOrderEntry(entry);
		resultsPage2.add(cpqInfo3);
		searchResult2.setResults(resultsPage2);
		final PaginationData pagination2 = new PaginationData();
		pagination2.setTotalNumberOfResults(101);
		pagination2.setHasNext(false);
		pagination2.setPageSize(DefaultConfigurationServiceLayerHelper.PAGE_SIZE);
		searchResult2.setPagination(pagination);

		event = new AfterInitializationStartEvent();
		event.setParams(Collections.singletonMap(CleanUpConfigurationAfterInitializationStartEventListener.PARAM_NAME_INITMETHOD,
				CleanUpConfigurationAfterInitializationStartEventListener.PARAM_VALUE_INITIALIZE));
	}


	@Test
	public void testDeleteConfig()
	{
		classUnderTest.deleteConfig(cpqInfo, new StringBuilder(), new HashSet<>());
		verify(cpqService).deleteConfiguration(CONFIG_ID);
	}

	@Test
	public void testDeleteConfigWithBaseSite()
	{
		classUnderTest.deleteConfig(cpqInfo, new StringBuilder(), new HashSet<>());
		verify(cpqService).deleteConfiguration(CONFIG_ID);
		verify(baseSiteService).setCurrentBaseSite(orderSite, false);
	}

	@Test
	public void testDeleteConfigException()
	{
		given(cpqService.deleteConfiguration(CONFIG_ID)).willThrow(new RuntimeException());
		final StringBuilder builder = new StringBuilder();
		classUnderTest.deleteConfig(cpqInfo, builder, new HashSet<>());
		assertNotEquals("some error message should be appended to the string builder parameter", builder.length(), 0);
	}

	@Test
	public void testDeleteConfigForMock()
	{
		cpqInfo.setConfigurationId(CleanUpConfigurationAfterInitializationStartEventListener.PREFIX_FOR_MOCK + CONFIG_ID);
		final StringBuilder builder = new StringBuilder();
		classUnderTest.deleteConfig(cpqInfo, builder, new HashSet<>());
		verifyZeroInteractions(cpqService);
	}

	@Test
	public void testGetProductInfos()
	{

		final SearchPageData<CloudCPQOrderEntryProductInfoModel> productInfos = classUnderTest.getProductInfos(Integer.valueOf(0));
		verify(productInfoDao).getAllConfigurationProductInfos(DefaultConfigurationServiceLayerHelper.PAGE_SIZE, 0);
		assertSame(searchResult, productInfos);
	}

	@Test
	public void testOnEventInternalMultiPage()
	{
		searchResult.getPagination().setTotalNumberOfResults(101);
		searchResult.getPagination().setHasNext(true);
		final List<CloudCPQOrderEntryProductInfoModel> list = new ArrayList<>();
		for (int ii = 0; ii < 100; ii++)
		{
			list.add(cpqInfo);
		}
		searchResult.setResults(list);
		classUnderTest.onEventInternal(event);
		verify(cpqService, times(2)).deleteConfiguration(any(String.class));
	}

	@Test
	public void testOnEventInternalOnePage()
	{
		classUnderTest.onEventInternal(event);
		verify(cpqService, times(2)).deleteConfiguration(any(String.class));
	}

	@Test
	public void testOnEventInternalOnePageHandelsErrors()
	{
		given(cpqService.deleteConfiguration(CONFIG_ID)).willThrow(new RuntimeException());
		classUnderTest.onEventInternal(event);
		verify(cpqService, times(2)).deleteConfiguration(any(String.class));
	}

	@Test
	public void testOnEventInternalOnePageOnUpdateEvent()
	{
		event.setParams(
				Collections.singletonMap(CleanUpConfigurationAfterInitializationStartEventListener.PARAM_NAME_INITMETHOD, "update"));
		classUnderTest.onEventInternal(event);
		verifyZeroInteractions(cpqService);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testGetBaseSiteService()
	{
		realClassUnderTest.getBaseSiteService();
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testGetConfigurationService()
	{
		realClassUnderTest.getConfigurationService();
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testGetProductInfoDao()
	{
		realClassUnderTest.getProductInfoDao();
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testGetConfigurationServiceLayerHelper()
	{
		realClassUnderTest.getConfigurationServiceLayerHelper();
	}


	private class CleanUpConfigurationAfterInitializationStartEventListenerForTest
			extends CleanUpConfigurationAfterInitializationStartEventListener
	{
		@Override
		protected CloudCPQOrderEntryProductInfoDao getProductInfoDao()
		{

			return productInfoDao;
		}

		@Override
		protected BaseSiteService getBaseSiteService()
		{
			return baseSiteService;
		}

		@Override
		protected ConfigurationService getConfigurationService()
		{
			return cpqService;
		}

		@Override
		protected ConfigurationServiceLayerHelper getConfigurationServiceLayerHelper()
		{
			return serviceLayerHelper;
		}
	}
}
