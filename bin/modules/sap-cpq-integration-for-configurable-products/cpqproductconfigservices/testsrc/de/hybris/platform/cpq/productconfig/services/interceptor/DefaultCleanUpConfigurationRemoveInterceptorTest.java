/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.services.interceptor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.catalog.enums.ConfiguratorType;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.cpq.productconfig.services.CloudCPQOrderEntryProductInfoDao;
import de.hybris.platform.cpq.productconfig.services.ConfigurationService;
import de.hybris.platform.cpq.productconfig.services.ConfigurationServiceLayerHelper;
import de.hybris.platform.cpq.productconfig.services.impl.DefaultConfigurationServiceLayerHelper;
import de.hybris.platform.cpq.productconfig.services.model.CloudCPQOrderEntryProductInfoModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.Test.None;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;




/**
 * Unit test for {@link DefaultCleanUpConfigurationRemoveInterceptor}
 */
@UnitTest
public class DefaultCleanUpConfigurationRemoveInterceptorTest
{
	private static final String CONFIG_ID = "configId";

	private DefaultCleanUpConfigurationRemoveInterceptor classUnderTest;

	@Mock
	private ConfigurationService cpqService;
	@Mock
	private BaseSiteService baseSiteService;

	private ConfigurationServiceLayerHelper serviceLayerHelper;

	private AbstractOrderEntryModel entry;

	@Mock
	private InterceptorContext context;
	@Mock
	private CloudCPQOrderEntryProductInfoDao cpqOrderEntryProductInfoDao;

	@Mock
	private UserService userService;

	private AbstractOrderModel orderModel;

	private BaseSiteModel baseSiteModel;

	private BaseSiteModel anotherBaseSiteModel;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		baseSiteModel = new BaseSiteModel();
		anotherBaseSiteModel = new BaseSiteModel();
		orderModel = new AbstractOrderModel();
		orderModel.setSite(baseSiteModel);
		entry = new AbstractOrderEntryModel();
		final CloudCPQOrderEntryProductInfoModel cpqInfo = new CloudCPQOrderEntryProductInfoModel();
		cpqInfo.setConfigurationId(CONFIG_ID);
		cpqInfo.setConfiguratorType(ConfiguratorType.CLOUDCPQCONFIGURATOR);
		entry.setProductInfos(Collections.singletonList(cpqInfo));
		serviceLayerHelper = new DefaultConfigurationServiceLayerHelper(baseSiteService, userService);
		entry.setOrder(orderModel);

		classUnderTest = new DefaultCleanUpConfigurationRemoveInterceptor(cpqService, serviceLayerHelper,
				cpqOrderEntryProductInfoDao);
		given(cpqOrderEntryProductInfoDao.isOnlyRelatedToGivenEntry(entry, CONFIG_ID)).willReturn(true);
		given(cpqOrderEntryProductInfoDao.isOnlyRelatedToGivenEntry(entry, null)).willThrow(new IllegalArgumentException());
	}

	@Test
	public void testOnRemoveSuccessful() throws InterceptorException
	{
		classUnderTest.onRemove(entry, context);
		verify(cpqService).deleteConfiguration(CONFIG_ID);
	}

	@Test
	public void testOnRemoveConfigIdStillRelatedToOtherEntries() throws InterceptorException
	{
		given(cpqOrderEntryProductInfoDao.isOnlyRelatedToGivenEntry(entry, CONFIG_ID)).willReturn(false);
		classUnderTest.onRemove(entry, context);
		verifyNoInteractions(cpqService);
	}

	@Test
	public void testOnRemoveNoCPQEntry() throws InterceptorException
	{
		entry.getProductInfos().get(0).setConfiguratorType(null);
		classUnderTest.onRemove(entry, context);
		verifyNoInteractions(cpqService);
	}

	@Test
	public void testOnRemoveNoConfigId() throws InterceptorException
	{
		serviceLayerHelper.getCPQInfo(entry).setConfigurationId(null);
		classUnderTest.onRemove(entry, context);
		verifyNoInteractions(cpqService);
	}

	@Test(expected = None.class)
	public void testOnRemoveShouldCatchException() throws InterceptorException
	{
		given(cpqService.deleteConfiguration(CONFIG_ID)).willThrow(new RuntimeException());
		classUnderTest.onRemove(entry, context);
	}


	@Test
	public void testOnRemoveInjectsBaseSite() throws InterceptorException
	{
		given(baseSiteService.getCurrentBaseSite()).willReturn(anotherBaseSiteModel);
		classUnderTest.onRemove(entry, context);
		verify(baseSiteService).setCurrentBaseSite(baseSiteModel, false);
		verify(baseSiteService).setCurrentBaseSite(anotherBaseSiteModel, false);
	}

	@Test
	public void testOnRemoveInjectsBaseSiteNoOrder() throws InterceptorException
	{
		entry.setOrder(null);
		given(baseSiteService.getCurrentBaseSite()).willReturn(anotherBaseSiteModel);
		classUnderTest.onRemove(entry, context);
		verify(baseSiteService, times(0)).setCurrentBaseSite(any(BaseSiteModel.class), anyBoolean());
		verify(cpqService).deleteConfiguration(CONFIG_ID);
	}

}
