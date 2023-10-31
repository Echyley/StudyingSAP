/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 *
 */
package de.hybris.platform.warehousing.atp.services;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.enums.StockLevelStatus;
import de.hybris.platform.commerceservices.stock.strategies.WarehouseSelectionStrategy;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.servicelayer.model.ItemModelInternalContext;
import de.hybris.platform.stock.impl.DefaultStockService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.storelocator.model.PointOfServiceModel;
import de.hybris.platform.warehousing.atp.services.impl.WarehousingCommerceStockService;
import de.hybris.platform.warehousing.atp.strategy.PickupWarehouseSelectionStrategy;
import de.hybris.platform.warehousing.atp.strategy.impl.WarehousingAvailabilityCalculationStrategy;
import de.hybris.platform.warehousing.model.AtpFormulaModel;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


// make the persistenceContext public to avoid referencing subscription related definitions
class MockProductData extends ProductModel
{
	public ItemModelInternalContext getPersistenceContextTesting()
	{
		return getPersistenceContext();
	}
}

@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class WarehousingCommerceStockServiceTest
{
	private static final Long ZERO = Long.valueOf(0);
	private static final Long TEN = Long.valueOf(10);

	@InjectMocks
	private final WarehousingCommerceStockService warehousingCommerceStockService = new WarehousingCommerceStockService();
	@Mock
	private DefaultStockService stockService;
	@Mock
	private WarehousingAvailabilityCalculationStrategy commerceAvailabilityCalculationStrategy;
	@Mock
	private PickupWarehouseSelectionStrategy pickupWarehouseSelectionStrategy;
	@Mock
	private WarehouseSelectionStrategy warehouseSelectionStrategy;
	private PointOfServiceModel pointOfService;
	private MockProductData product;
	private WarehouseModel warehouse;
	private List<WarehouseModel> warehouses;
	private StockLevelModel stockLevel;
	private List<StockLevelModel> stockLevels;
	private BaseStoreModel baseStore;
	private AtpFormulaModel atpFormula;
	private ItemModelInternalContext productContext;
	private boolean supportSubscriptionTerm;

	@Before
	public void setUp()
	{
		baseStore = new BaseStoreModel();
		warehouse = new WarehouseModel();
		warehouses = Arrays.asList(warehouse);
		atpFormula = new AtpFormulaModel();
		baseStore.setDefaultAtpFormula(atpFormula);

		pointOfService = new PointOfServiceModel();
		pointOfService.setWarehouses(warehouses);
		pointOfService.setBaseStore(baseStore);

		product = new MockProductData();
		product.setCode("TEST");

		stockLevel = new StockLevelModel();
		stockLevel.setAvailable(TEN.intValue());
		stockLevel.setProduct(product);
		stockLevel.setWarehouse(warehouse);
		stockLevels = Arrays.asList(stockLevel);
		productContext = product.getPersistenceContextTesting();
		try
		{
			Method m = (ProductModel.class).getDeclaredMethod("getSubscriptionTerm");
			supportSubscriptionTerm = m != null;
		}
		catch (ReflectiveOperationException ex)
		{
			supportSubscriptionTerm = false;
		}
	}

	private void setSubscriptionTermToProduct() throws ReflectiveOperationException
	{
		Object term = Class.forName("de.hybris.platform.subscriptionservices.model.SubscriptionTermModel").newInstance();
		productContext.setPropertyValue("subscriptionTerm", term);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFail_GetStockLevelForProductAndPointOfService_NullProduct()
	{
		warehousingCommerceStockService.getStockLevelForProductAndPointOfService(null, pointOfService);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFail_GetStockLevelForProductAndPointOfService_NullPos()
	{
		warehousingCommerceStockService.getStockLevelForProductAndPointOfService(product, null);
	}

	@Test
	public void shouldGetStockLevelForProductAndPointOfService_NoWarehouses()
	{
		when(pickupWarehouseSelectionStrategy.getWarehouses(pointOfService)).thenReturn(Collections.emptyList());

		final Long value = warehousingCommerceStockService.getStockLevelForProductAndPointOfService(product, pointOfService);

		assertEquals(ZERO, value);
	}

	@Test
	public void shouldGetStockLevelForProductAndPointOfService_NullStock()
	{
		when(pickupWarehouseSelectionStrategy.getWarehouses(pointOfService)).thenReturn(warehouses);
		when(stockService.getStockLevels(product, warehouses)).thenReturn(null);

		final Long value = warehousingCommerceStockService.getStockLevelForProductAndPointOfService(product, pointOfService);

		assertEquals(ZERO, value);
	}

	@Test
	public void shouldGetStockLevelForProductAndPointOfService_NoStock()
	{
		when(pickupWarehouseSelectionStrategy.getWarehouses(pointOfService)).thenReturn(warehouses);
		when(stockService.getStockLevels(product, warehouses)).thenReturn(Collections.emptyList());

		final Long value = warehousingCommerceStockService.getStockLevelForProductAndPointOfService(product, pointOfService);

		assertEquals(ZERO, value);
	}

	@Test
	public void shouldGetStockLevelForProductAndPointOfService_NullAvailability()
	{
		when(pickupWarehouseSelectionStrategy.getWarehouses(pointOfService)).thenReturn(warehouses);
		when(stockService.getStockLevels(product, warehouses)).thenReturn(stockLevels);
		when(commerceAvailabilityCalculationStrategy.calculateAvailability(stockLevels)).thenReturn(null);

		final Long value = warehousingCommerceStockService.getStockLevelForProductAndPointOfService(product, pointOfService);

		assertEquals(null, value);
	}

	@Test
	public void shouldGetStockLevelForProductAndPointOfService_Valid()
	{
		when(pickupWarehouseSelectionStrategy.getWarehouses(pointOfService)).thenReturn(warehouses);
		when(stockService.getStockLevels(product, warehouses)).thenReturn(stockLevels);
		when(commerceAvailabilityCalculationStrategy.calculateAvailability(stockLevels)).thenReturn(
				Long.valueOf(stockLevel.getAvailable()));

		final Long value = warehousingCommerceStockService.getStockLevelForProductAndPointOfService(product, pointOfService);

		assertEquals(TEN, value);
	}

	@Test
	public void shouldGetStockLevelStatusForSubscriptionProductWithBaseStore()
	{
		if (supportSubscriptionTerm)
		{
			try
			{
				setSubscriptionTermToProduct();
				final StockLevelStatus value = warehousingCommerceStockService.getStockLevelStatusForProductAndBaseStore(product,
						null);
				assertEquals(StockLevelStatus.INSTOCK, value);
			}
			catch (ReflectiveOperationException ex)
			{
				throw new RuntimeException("failed with unexpected ex" + ex);
			}
		}
	}

	@Test
	public void shouldGetStockLevelStatusForNonSubscriptionProductWithBaseStore()
	{
		warehousingCommerceStockService.getStockLevelStatusForProductAndBaseStore(product, new BaseStoreModel());
		verify(stockService).getProductStatus(Mockito.any(), (Collection) Mockito.any());
	}

	@Test
	public void shouldGetStockLevelStatusForSubscriptionProductWithPointOfService()
	{
		if (supportSubscriptionTerm)
		{
			try
			{
				setSubscriptionTermToProduct();
				final StockLevelStatus value = warehousingCommerceStockService.getStockLevelStatusForProductAndPointOfService(product,
						pointOfService);
				assertEquals(StockLevelStatus.INSTOCK, value);
			}
			catch (ReflectiveOperationException ex)
			{
				throw new RuntimeException("failed with unexpected ex: " + ex);
			}
		}
	}

	@Test
	public void shouldStockLevelStatusForNonSubscriptionProductWithPointOfService()
	{
		warehousingCommerceStockService.getStockLevelStatusForProductAndPointOfService(product, pointOfService);
		verify(stockService).getProductStatus(Mockito.any(), (Collection) Mockito.any());
	}

	@Test
	public void shouldGetStockLevelForSubscriptionProductWithBaseStore()
	{
		if (supportSubscriptionTerm)
		{
			try
			{
				setSubscriptionTermToProduct();
				final Long value = warehousingCommerceStockService.getStockLevelForProductAndBaseStore(product, null);
				assertNull(value);
			}
			catch (ReflectiveOperationException ex)
			{
				throw new RuntimeException("failed with unexpected ex: " + ex);
			}
		}
	}

	@Test
	public void shouldGetStockLevelForNonSubscriptionProductWithBaseStore()
	{
		warehousingCommerceStockService.getStockLevelForProductAndBaseStore(product, new BaseStoreModel());
		verify(commerceAvailabilityCalculationStrategy).calculateAvailability(Mockito.any());
	}

	@Test
	public void shouldGetStockLevelForSubscriptionProductWithPointOfService()
	{
		if (supportSubscriptionTerm)
		{
			try
			{
				setSubscriptionTermToProduct();
				final Long value = warehousingCommerceStockService.getStockLevelForProductAndPointOfService(product, pointOfService);
				assertNull(value);
			}
			catch (ReflectiveOperationException ex)
			{
				throw new RuntimeException("failed with unexpected ex: " + ex);
			}
		}
	}

	@Test
	public void shouldGetStockLevelForNonSubscriptionProductWithPointOfService()
	{
		warehousingCommerceStockService.getStockLevelForProductAndPointOfService(product, pointOfService);
		verify(commerceAvailabilityCalculationStrategy).calculateAvailability(Mockito.any());
	}
}
