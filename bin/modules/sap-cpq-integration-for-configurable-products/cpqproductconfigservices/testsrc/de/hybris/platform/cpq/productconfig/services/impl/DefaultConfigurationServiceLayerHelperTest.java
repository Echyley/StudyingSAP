/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.services.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.catalog.enums.ConfiguratorType;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.core.servicelayer.data.PaginationData;
import de.hybris.platform.core.servicelayer.data.SearchPageData;
import de.hybris.platform.cpq.productconfig.services.model.CloudCPQOrderEntryProductInfoModel;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;


/**
 * Unit test for {@link DefaultConfigurationServiceLayerHelper}
 */
@UnitTest
public class DefaultConfigurationServiceLayerHelperTest
{
	private static final String CONFIG_ID = "config123";
	private DefaultConfigurationServiceLayerHelper classUnderTest;
	private final AbstractOrderEntryModel entry = new AbstractOrderEntryModel();
	private final CloudCPQOrderEntryProductInfoModel infoModel = new CloudCPQOrderEntryProductInfoModel();
	@Mock
	private BaseSiteService baseSiteService;
	@Mock
	private UserService userService;
	private final AbstractOrderModel order = new AbstractOrderModel();
	private boolean dummyActionExecuted;
	private final BaseSiteModel orderSite = new BaseSiteModel();
	private final BaseSiteModel currentSite = new BaseSiteModel();
	private int invocationCounter;
	UserModel user = new UserModel();
	CartEntryModel cartEntry = new CartEntryModel();
	OrderEntryModel orderEntry = new OrderEntryModel();
	OrderModel orderForImpersonation = new OrderModel();

	@Before
	public void setUp()
	{
		invocationCounter = 0;
		dummyActionExecuted = false;
		MockitoAnnotations.initMocks(this);
		classUnderTest = new DefaultConfigurationServiceLayerHelper(baseSiteService, userService);
		order.setSite(orderSite);
		user.setUid("anonymous");
		when(userService.getCurrentUser()).thenReturn(user);
		orderForImpersonation.setUser(user);
		orderEntry.setOrder(orderForImpersonation);
	}

	@Test
	public void testGetCpqInfoEntryNull()
	{
		assertNull(classUnderTest.getCPQInfo(null));
	}

	@Test
	public void testGetCpqInfoEmptyEntry()
	{
		assertNull(classUnderTest.getCPQInfo(entry));
	}

	@Test
	public void testGetCpqInfoEmptyInfoModel()
	{
		entry.setProductInfos(Collections.singletonList(infoModel));
		assertNull(classUnderTest.getCPQInfo(entry));
	}

	@Test
	public void testGetCpqInfoEmptyInfoModelWithCPQType()
	{
		infoModel.setConfiguratorType(ConfiguratorType.CLOUDCPQCONFIGURATOR);
		entry.setProductInfos(Collections.singletonList(infoModel));
		assertSame(infoModel, classUnderTest.getCPQInfo(entry));
	}

	@Test
	public void testEnsureBaseSiteSetAndExecuteConfigurationAction()
	{
		classUnderTest.ensureBaseSiteSetAndExecuteConfigurationAction(order, baseSiteService -> dummyAction());
		assertTrue("Action was not executed", dummyActionExecuted);
	}

	@Test
	public void testEnsureBaseSiteSetAndExecuteConfigurationActionNoOrder()
	{
		classUnderTest.ensureBaseSiteSetAndExecuteConfigurationAction(null, baseSiteService -> dummyAction());
		assertTrue("Action was not executed", dummyActionExecuted);
		verify(baseSiteService, times(0)).setCurrentBaseSite(any(BaseSiteModel.class), anyBoolean());
	}

	@Test
	public void testEnsureBaseSiteSetAndExecuteConfigurationActionNoOrderSite()
	{
		order.setSite(null);
		classUnderTest.ensureBaseSiteSetAndExecuteConfigurationAction(order, baseSiteService -> dummyAction());
		assertTrue("Action was not executed", dummyActionExecuted);
		verify(baseSiteService, times(0)).setCurrentBaseSite(any(BaseSiteModel.class), anyBoolean());
	}


	@Test
	public void testEnsureBaseSiteSetAndExecuteConfigurationActionSwitchesAndRestoresBaseSite()
	{
		given(baseSiteService.getCurrentBaseSite()).willReturn(currentSite);
		classUnderTest.ensureBaseSiteSetAndExecuteConfigurationAction(order, baseSiteService -> dummyAction());
		verify(baseSiteService).setCurrentBaseSite(orderSite, false);
		verify(baseSiteService).setCurrentBaseSite(currentSite, false);
	}

	@Test
	public void testEnsureBaseSiteSetAndExecuteConfigurationActionRestoresBaseSiteOnException()
	{
		given(baseSiteService.getCurrentBaseSite()).willReturn(currentSite);
		try
		{
			classUnderTest.ensureBaseSiteSetAndExecuteConfigurationAction(order, baseSiteService -> dummyAction(true));
			fail("RuntimeException should not be catched");
		}
		catch (final RuntimeException ex)
		{
			//expected
		}
		verify(baseSiteService).setCurrentBaseSite(orderSite, false);
		verify(baseSiteService).setCurrentBaseSite(currentSite, false);
	}

	private void dummyAction()
	{
		dummyAction(false);
	}

	private void dummyAction(final boolean throwExecption)
	{
		dummyActionExecuted = true;
		if (throwExecption)
		{
			throw new RuntimeException();
		}
	}

	@Test
	public void testProcessPageWiseMaxPages()
	{
		final SearchPageData<?> fullSearchPageData = mockFullSearchResultPage();
		classUnderTest.processPageWise(currentPage -> dummySearchReturn(fullSearchPageData), list -> dummyListAction(list));
		assertEquals(DefaultConfigurationServiceLayerHelper.MAXIMUM_PAGES, invocationCounter);
	}

	@Test
	public void testProcessPageWiseOnePage()
	{
		final SearchPageData<?> fullSearchPageData = mockSearchResultWith10Items();
		classUnderTest.processPageWise(currentPage -> dummySearchReturn(fullSearchPageData), list -> dummyListAction(list));
		assertEquals(1, invocationCounter);
	}


	private void dummyListAction(final List<?> list)
	{
		invocationCounter++;
	}

	protected SearchPageData<?> dummySearchReturn(final SearchPageData<?> result)
	{
		return result;
	}

	protected SearchPageData<?> mockFullSearchResultPage()
	{
		final SearchPageData<Object> fullSearchPageData = new SearchPageData<>();
		final PaginationData pagination = new PaginationData();
		pagination.setTotalNumberOfResults(
				DefaultConfigurationServiceLayerHelper.PAGE_SIZE * DefaultConfigurationServiceLayerHelper.MAXIMUM_PAGES);
		pagination.setNumberOfPages(DefaultConfigurationServiceLayerHelper.MAXIMUM_PAGES);
		fullSearchPageData.setPagination(pagination);
		final List<Object> fullList = Mockito.mock(ArrayList.class);
		fullSearchPageData.setResults(fullList);
		given(fullList.size()).willReturn(DefaultConfigurationServiceLayerHelper.PAGE_SIZE);
		return fullSearchPageData;
	}

	protected SearchPageData<?> mockSearchResultWith10Items()
	{
		final SearchPageData<Object> fullSearchPageData = new SearchPageData<>();
		final PaginationData pagination = new PaginationData();
		pagination.setTotalNumberOfResults(10);
		pagination.setNumberOfPages(1);
		fullSearchPageData.setPagination(pagination);
		final List<Object> list = Mockito.mock(ArrayList.class);
		fullSearchPageData.setResults(list);
		given(list.size()).willReturn(10);
		return fullSearchPageData;
	}

	@Test
	public void testIsRelevantForImpersonation()
	{
		assertTrue(classUnderTest.isRelevantForImpersonation(orderEntry));
	}

	@Test
	public void testIsRelevantForImpersonation_Not_Relevant_For_Cart_Entry()
	{
		assertFalse(classUnderTest.isRelevantForImpersonation(cartEntry));
	}

	@Test
	public void testIsRelevantForImpersonation_Not_Relevant_For_Not_Anonymous_User()
	{
		user.setUid("not_anonymous");
		assertFalse(classUnderTest.isRelevantForImpersonation(orderEntry));
	}

	@Test
	public void testRetrieveUserForAbstractOrderEntry()
	{
		assertEquals(user, classUnderTest.retrieveUserForAbstractOrderEntry(orderEntry));
	}

	@Test
	public void testRetrieveUserForAbstractOrderEntryIfRelevant()
	{
		assertEquals(user, classUnderTest.retrieveUserForAbstractOrderEntryIfRelevant(orderEntry));
	}

	@Test
	public void testRetrieveUserForAbstractOrderEntryIfRelevant_Not_Relevant()
	{
		user.setUid("not_anonymous");
		assertNull(classUnderTest.retrieveUserForAbstractOrderEntryIfRelevant(orderEntry));
	}

	@Test
	public void testPre2211Constrcutor()
	{
		classUnderTest = new DefaultConfigurationServiceLayerHelper(baseSiteService);
		assertSame(baseSiteService, classUnderTest.getBaseSiteService());
		assertNull(classUnderTest.getUserService());
	}

}
