/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtcfgfacades.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.sap.productconfig.facades.ConfigurationOrderIntegrationFacade;
import de.hybris.platform.sap.productconfig.facades.overview.ConfigurationOverviewData;
import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.sap.productconfig.runtime.interf.external.Configuration;
import de.hybris.platform.sap.productconfig.runtime.interf.external.impl.ConfigurationImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConfigModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.InstanceModelImpl;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationService;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;
import de.hybris.platform.sap.sapordermgmtservices.order.OrderService;
import de.hybris.platform.sap.sapproductconfigsombol.transaction.item.businessobject.impl.CPQItem;
import de.hybris.platform.sap.sapproductconfigsombol.transaction.item.businessobject.impl.CPQItemSalesDoc;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultConfigurationOrderIntegrationFacadeTest
{
	@InjectMocks
	private final DefaultConfigurationOrderIntegrationFacade classUnderTest = new DefaultConfigurationOrderIntegrationFacade();
	@Mock
	private ConfigurationOrderIntegrationFacade sapProductConfigCartIntegrationFacade;
	@Mock
	private ProductConfigurationService productConfigurationService;
	private static final String code = "1234";
	private static final int entryNumber = 0;
	@Mock
	private OrderService orderService;
	@Mock
	private BaseStoreService baseStoreService;
	private final ConfigModel configModel = new ConfigModelImpl();
	private InstanceModel rootInstance;
	@Mock
	private BaseStoreModel baseStore;
	@Mock
	private SAPConfigurationModel sapConfiguration;
	private static final String productCode = "Product";
	private static final String configId = "1";
	private final Item item = new CPQItemSalesDoc();
	private final Configuration externalConfiguration = new ConfigurationImpl();
	private Date date;

	@Before
	public void initialize()
	{
		MockitoAnnotations.initMocks(this);
		rootInstance = new InstanceModelImpl();
		rootInstance.setName(productCode);
		configModel.setRootInstance(rootInstance);
		configModel.setId(configId);
		when(orderService.getItemFromOrder(code, String.valueOf(entryNumber))).thenReturn(item);
		when(productConfigurationService.createConfigurationFromExternalSource(externalConfiguration)).thenReturn(configModel);
		((CPQItem) item).setExternalConfiguration(externalConfiguration);
		date = new Date();
		((CPQItem) item).setKbDate(date);
		item.setProductId(code);
		when(sapConfiguration.isSapordermgmt_enabled()).thenReturn(Boolean.TRUE);
		when(baseStore.getSAPConfiguration()).thenReturn(sapConfiguration);
		when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStore);
	}


	@Test
	public void testGetConfiguration()
	{
		final ConfigurationOverviewData configurationOverviewData = classUnderTest.getConfiguration(code, entryNumber);
		assertNotNull(configurationOverviewData);
		assertEquals(productCode, configurationOverviewData.getProductCode());
		assertEquals(configId, configurationOverviewData.getId());
	}

	@Test
	public void testGetConfiguration2()
	{
		final ConfigModel configModel = classUnderTest.getConfiguration(item);
		assertNotNull(configModel);
		assertEquals(this.configModel, configModel);
	}

	@Test
	public void testGetConfigurationNoSOM()
	{
		when(sapConfiguration.isSapordermgmt_enabled()).thenReturn(Boolean.FALSE);
		classUnderTest.getConfiguration(code, entryNumber);
		verify(sapProductConfigCartIntegrationFacade).getConfiguration(code, entryNumber);
	}

	@Test
	public void testEnrichConfigurationFromItem()
	{
		classUnderTest.enrichConfigurationFromItem(externalConfiguration, (CPQItem) item);
		final KBKey kbKey = externalConfiguration.getKbKey();
		assertNotNull(kbKey);
		assertEquals(code, kbKey.getProductCode());
		assertEquals(date, kbKey.getDate());
	}

	@Test
	public void testOrderService()
	{
		classUnderTest.setOrderService(orderService);
		assertEquals(orderService, classUnderTest.getOrderService());
	}

	@Test
	public void testBaseStoreService()
	{
		assertEquals(baseStoreService, classUnderTest.getBaseStoreService());
	}

	@Test
	public void testIsReorderable()
	{
		assertFalse(classUnderTest.isReorderable(code));
	}

	@Test
	public void testIsReorderableNoSOM()
	{
		when(baseStore.getSAPConfiguration()).thenReturn(null);
		classUnderTest.isReorderable(code);
		verify(sapProductConfigCartIntegrationFacade).isReorderable(code);
	}


}
