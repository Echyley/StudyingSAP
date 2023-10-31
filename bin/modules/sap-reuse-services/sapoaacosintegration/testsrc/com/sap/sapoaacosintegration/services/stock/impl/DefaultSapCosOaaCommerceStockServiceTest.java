/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacosintegration.services.stock.impl;

import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.storelocator.PointOfServiceDao;
import de.hybris.platform.storelocator.model.PointOfServiceModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import com.sap.retail.oaa.commerce.services.atp.ATPService;
import com.sap.retail.oaa.commerce.services.atp.pojos.ATPAvailability;
import com.sap.retail.oaa.commerce.services.atp.strategy.ATPAggregationStrategy;
import com.sap.retail.oaa.commerce.services.common.util.CommonUtils;
import com.sap.retail.oaa.commerce.services.stock.impl.DefaultSapOaaCommerceStockService;


/**
 *
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultSapCosOaaCommerceStockServiceTest
{
	@Mock
	ProductModel product;

	@Mock
	BaseStoreModel baseStore;

	@Mock
	DefaultSapOaaCommerceStockService defaultSapOaaCommerceStockService;

	@Mock
	private ATPService atpService;

	@Mock
	private PointOfServiceDao pointOfServiceDao;

	@Mock
	private ATPAggregationStrategy atpAggregationStrategy;

	@Mock
	private CommonUtils commonUtils;

	@Mock
	private PointOfServiceModel pointOfService;

	@Spy
	@InjectMocks
	private DefaultSapCosOaaCommerceStockService defaultSapCosOaaCommerceStockService;

	private final List<ATPAvailability> productAvailabilityList = new ArrayList<ATPAvailability>();
	private final ATPAvailability atpAvailability = new ATPAvailability();

	@Before
	public void setUp()
	{
		defaultSapCosOaaCommerceStockService.setCommonUtils(commonUtils);

		//setup commonutils
		Mockito.lenient().when(commonUtils.isCAREnabled()).thenReturn(false);
		when(commonUtils.isCOSEnabled()).thenReturn(true);

		//ATPAvailability
		atpAvailability.setAtpDate(new Date());
		atpAvailability.setQuantity(2d);

		//ProductAvailabilityList
		productAvailabilityList.add(atpAvailability);


	}


	@Test
	public void getStockLevelForProductAndBaseStoreInstockTest()
	{
		//SetUp
		final long result=2;

		final Long value = new Long(result);

		Long finalResult = null;

		doReturn(value).when(defaultSapCosOaaCommerceStockService).getAvailableStockLevel(null, null, product, null);

		//Execute
		finalResult = defaultSapCosOaaCommerceStockService.getStockLevelForProductAndBaseStore(product, baseStore);

		//Verify
		Assert.assertNull(finalResult);
	}

	@Test
	public void getStockLevelForProductAndPointOfServiceTest()
	{
		
		Long result;

		// Mock ATPService
		//final ATPService atpServiceMock = EasyMock.createNiceMock(ATPService.class);

		Mockito.lenient()
				.when(defaultSapOaaCommerceStockService.retrieveStockLevelForProduct(product, pointOfService))
				.thenReturn(Long.valueOf("0"));

		result=defaultSapCosOaaCommerceStockService.getStockLevelForProductAndPointOfService(product,pointOfService);
		//Verify
		Assert.assertEquals((long) 0, (long) result);

	}

	@Test
	public void getStockLevelForProductAndBaseStoreOutOfStockTest()
	{
		//SetUp
		Long result;
		Mockito.lenient().when(atpService.callRestAvailabilityServiceForProduct(anyString(), anyString(), anyObject()))
				.thenReturn(productAvailabilityList);

		Mockito.lenient().when(atpAggregationStrategy.aggregateAvailability(anyString(), anyObject(), anyObject(), anyList()))
				.thenReturn(Long.valueOf("0"));



		//Execute
		result = defaultSapCosOaaCommerceStockService.getStockLevelForProductAndBaseStore(product, baseStore);

		//Verify
		Assert.assertEquals((long) 0, (long) result);
	}

}
