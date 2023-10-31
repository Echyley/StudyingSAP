/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtcfgfacades.hook;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationService;
import de.hybris.platform.sap.sapproductconfigsomservices.prodconf.ProductConfigurationSomService;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CPQSapCartFacadeHookTest
{
	private static final String CONFIG_ID = "config id";
	private static final String PK = "PK";
	@Mock
	private ProductConfigurationSomService productConfigurationSomService;
	@Mock
	private ProductConfigurationService productConfigurationService;
	@InjectMocks
	private CPQSapCartFacadeHook classUnderTest;
	private List<OrderEntryData> entries;
	private OrderEntryData entry;

	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
		entries = new ArrayList();
		entry = new OrderEntryData();
		entries.add(entry);
		entry.setItemPK(PK);
		entry.setEntryNumber(Integer.valueOf(3));
		when(productConfigurationSomService.getGetConfigId(PK)).thenReturn(CONFIG_ID);
	}

	@Test
	public void testBeforeCartEntryUpdateNothingToBeDone()
	{
		classUnderTest.beforeCartEntryUpdate(1, 3, entries);
		verify(productConfigurationSomService, times(0)).getGetConfigId(PK);
		verify(productConfigurationService, times(0)).releaseSession(CONFIG_ID);
	}

	@Test
	public void testBeforeCartEntryUpdatItemNotFound()
	{
		classUnderTest.beforeCartEntryUpdate(1, 10, entries);
		verify(productConfigurationSomService, times(0)).getGetConfigId(any());
		verify(productConfigurationService, times(0)).releaseSession(any());
	}

	@Test
	public void testBeforeCartEntryUpdateRemoveConfigurable()
	{
		classUnderTest.beforeCartEntryUpdate(0, 3, entries);
		verify(productConfigurationSomService).getGetConfigId(PK);
		verify(productConfigurationService).releaseSession(CONFIG_ID);
	}

	@Test
	public void testBeforeCartEntryUpdateRemoveButNotConfigurable()
	{
		when(productConfigurationSomService.getGetConfigId(PK)).thenReturn(null);
		classUnderTest.beforeCartEntryUpdate(0, 3, entries);
		verify(productConfigurationSomService).getGetConfigId(PK);
		verify(productConfigurationService, times(0)).releaseSession(any());
	}

}
