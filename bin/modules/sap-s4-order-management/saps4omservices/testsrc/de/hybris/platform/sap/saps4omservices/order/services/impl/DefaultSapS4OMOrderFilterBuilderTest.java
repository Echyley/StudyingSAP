/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saps4omservices.order.services.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.sap.saps4omservices.constants.Saps4omservicesConstants;
import de.hybris.platform.sap.saps4omservices.filter.dto.FilterData;
import de.hybris.platform.sap.saps4omservices.order.services.SapS4OMOrderFilterBuilderHook;
import de.hybris.platform.sap.saps4omservices.utils.SapS4OrderUtil;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultSapS4OMOrderFilterBuilderTest {
	
	public static final String NUMBER_STRING = "12345";
	private static final String DETAULT_SORT= "byDate";
	public static final String PAGING_TOP_SUFFIX = "$top=";
	private PageableData pageableData;
	
	@Spy
	@InjectMocks
	private DefaultSapS4OMOrderFilterBuilder defaultSapS4OMOrderFilterBuilder;
	
	@Mock 
	private SapS4OrderUtil sapS4OrderUtil;
	@Mock
	private CustomerModel customerModel;
	@Mock
	private ConfigurationService configurationService;
	@Mock
	private Configuration configuration;
	@Mock
	BaseStoreService baseStoreService;
	

	@Before
	public void setUp()
	{
		when(configurationService.getConfiguration()).thenReturn(configuration);
		pageableData = new PageableData();
		pageableData.setSort(DETAULT_SORT);
		pageableData.setPageSize(1);
		final SapS4OMOrderFilterBuilderHook sapS4OMOrderFilterBuilderHook = Mockito.mock(SapS4OMOrderFilterBuilderHook.class);
		defaultSapS4OMOrderFilterBuilder.setSapS4OMOrderFilterBuilderHooks(Collections.<SapS4OMOrderFilterBuilderHook> singletonList(sapS4OMOrderFilterBuilderHook));
	}
	
	@Test
	public void getOrderHistoryFiltersTest() {
		SAPConfigurationModel sapConfig = spy(SAPConfigurationModel.class);
		BaseStoreModel baseStore = spy(BaseStoreModel.class);
		sapConfig.setSapcommon_distributionChannel(NUMBER_STRING);
		sapConfig.setSapcommon_division(NUMBER_STRING);
		sapConfig.setSapcommon_salesOrganization(NUMBER_STRING);

		lenient().when(configuration.getString(Saps4omservicesConstants.ORDER_TYPE)).thenReturn("SalesOrderType");
		lenient().when(configuration.getString(Saps4omservicesConstants.SOLD_TO)).thenReturn("SoldToParty");
		lenient().when(configuration.getString(Saps4omservicesConstants.SORT_ORDER)).thenReturn("desc");
		lenient().when(sapS4OrderUtil.getCommonTransactionType()).thenReturn("OR");
		lenient().when(sapS4OrderUtil.getSoldToParty(customerModel)).thenReturn(NUMBER_STRING);
		lenient().when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStore);
		lenient().when(baseStore.getSAPConfiguration()).thenReturn(sapConfig);

		final Map<String,List<FilterData>> filters=defaultSapS4OMOrderFilterBuilder.getOrderHistoryFilters(customerModel, null, pageableData, DETAULT_SORT);
		assertFalse(filters.isEmpty());
		assertEquals(5,filters.size());
	}
	
	@Test
	public void getOrderDetailFiltersTest() {
		SAPConfigurationModel sapConfig = spy(SAPConfigurationModel.class);
		BaseStoreModel baseStore = spy(BaseStoreModel.class);
		sapConfig.setSapcommon_distributionChannel(NUMBER_STRING);
		sapConfig.setSapcommon_division(NUMBER_STRING);
		sapConfig.setSapcommon_salesOrganization(NUMBER_STRING);
		lenient().when(sapS4OrderUtil.getSoldToParty(null)).thenReturn(NUMBER_STRING);
		lenient().when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStore);
		lenient().when(baseStore.getSAPConfiguration()).thenReturn(sapConfig);
		final Map<String,List<FilterData>> filters=defaultSapS4OMOrderFilterBuilder.getOrderDetailFilters();
		assertFalse(filters.isEmpty());
		assertEquals(filters.size(), 2);
	}
	
	@Test
	public void testAfterPoulateOrderHistoryFilters() {
		final Map<String,List<FilterData>> filters = new HashMap<>();
		defaultSapS4OMOrderFilterBuilder.afterPoulateOrderHistoryFilters(filters, customerModel, null, pageableData, DETAULT_SORT);
		assertEquals(filters.size(), 0);
	}
	
	@Test
	public void testAfterPoulateOrderDetailFilters() {
		final Map<String,List<FilterData>> filters = new HashMap<>();
		defaultSapS4OMOrderFilterBuilder.afterPoulateOrderDetailFilters(filters);
		assertEquals(filters.size(), 0);
	}

}
