/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.sapppspricing.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.promotions.model.ProductPromotionModel;
import de.hybris.platform.promotions.result.PromotionOrderResults;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.sap.retail.sapppspricing.PPSConfigService;

@SuppressWarnings("javadoc")
@UnitTest
public class DefaultPPSPromotionsServiceTest {
	private DefaultPPSPromotionsService cut;

	@Mock
	private PPSConfigService configService;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		cut = new DefaultPPSPromotionsService();
		Mockito.when(configService.isPpsActive(Mockito.any(ProductModel.class))).thenReturn(Boolean.TRUE);
		Mockito.when(configService.isPpsActive(Mockito.any(AbstractOrderModel.class))).thenReturn(Boolean.TRUE);
		cut.setConfigService(configService);
	}

	@Test
	public void testSetGetBaseConfigService() {
		cut = new DefaultPPSPromotionsService();
		assertNull(cut.getConfigService());
		cut.setConfigService(configService);
		assertSame(configService, cut.getConfigService());
	}

	@Test
	public void testGetProductPromotions() {
		final List<ProductPromotionModel> result = cut.getProductPromotions(null, new ProductModel(), true, null);
		assertTrue(result.isEmpty());
	}

	@Test
	public void testUpdatePromotions() {
		final PromotionOrderResults promotionOrderResults = cut.updatePromotions(null, new AbstractOrderModel());
		Assert.assertNotNull(promotionOrderResults);
	}

	@Test
	public void testUpdatePromotions1() {
		assertNotNull(cut.updatePromotions(null, new AbstractOrderModel(), false, null, null, null));
	}
}
