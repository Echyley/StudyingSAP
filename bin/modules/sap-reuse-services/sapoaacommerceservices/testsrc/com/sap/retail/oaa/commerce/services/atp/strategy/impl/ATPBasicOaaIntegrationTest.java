/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.oaa.commerce.services.atp.strategy.impl;

import de.hybris.platform.commerceservices.stock.CommerceStockService;
import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.warehousing.util.models.AllocationEvents;
import de.hybris.platform.warehousing.util.models.PointsOfService;
import de.hybris.platform.warehousing.util.models.Products;
import de.hybris.platform.warehousing.util.models.StockLevels;
import de.hybris.platform.warehousing.util.models.Warehouses;
import de.hybris.platform.warehousing.atp.ATPBasicIntegrationTest;
import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.basecommerce.enums.StockLevelStatus;

import org.junit.Before;
import org.junit.Test;

import javax.annotation.Resource;

import static org.junit.Assert.assertEquals;

@IntegrationTest(replaces = ATPBasicIntegrationTest.class)
public class ATPBasicOaaIntegrationTest extends ATPBasicIntegrationTest {

    private static final Long ALLOCATED_CAMERA_QTY = Long.valueOf(10);
    private static final Long MONTREAL_CAMERA_QTY = Long.valueOf(50);

    private StockLevelModel montrealStockLevel;

    @Resource
    private ModelService modelService;
    @Resource
    private AllocationEvents allocationEvents;
    @Resource
    private CommerceStockService commerceStockService;
    @Resource
    private Products products;
    @Resource
    private PointsOfService pointsOfService;
    @Resource
    private StockLevels stockLevels;
    @Resource
    private Warehouses warehouses;

    @Before
    public void setUp() {
        final WarehouseModel warehouseMontreal = warehouses.Montreal();
        montrealStockLevel = stockLevels.Camera(warehouseMontreal, MONTREAL_CAMERA_QTY.intValue());
        super.setUp();
    }

    @Override
    @Test
    public void shouldReturnOutOfStockWhenAvailabilityForPoSIsLessThan0() {
        //Given
        montrealStockLevel.setAvailable(0);
        modelService.save(montrealStockLevel);
        allocationEvents.Camera_ShippedFromMontrealToMontrealNancyHome(ALLOCATED_CAMERA_QTY, montrealStockLevel);

        //When
        final StockLevelStatus result = commerceStockService
                .getStockLevelStatusForProductAndPointOfService(products.Camera(), pointsOfService.Montreal_Downtown());

        //Then
        assertEquals(StockLevelStatus.OUTOFSTOCK, result);
    }
}
