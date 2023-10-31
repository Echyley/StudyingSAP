/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hybris.ymkt.recommendation.services;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;

import javax.annotation.Resource;

import org.junit.Test;
import static org.junit.Assert.assertNotNull;

import com.hybris.ymkt.recommendation.dao.InteractionContext;



@IntegrationTest
public class InteractionServiceIntegrationTest extends ServicelayerTransactionalTest
{
	@Resource
	InteractionService interactionService;

	@Test
	public void testSaveClickthrough()
	{		
		final InteractionContext interactionContext = new InteractionContext();
		interactionContext.setProductId("2054947");
		interactionContext.setProductType("SAP_HYBRIS_PRODUCT");
		interactionContext.setScenarioId("SAP_TOP_SELLERS_EMAIL_CAMPAIGN");
		interactionContext.setSourceObjectId("BDD3131C3D7BF6050B8D34965FB65B93");
		assertNotNull(interactionContext.getProductId());
		assertNotNull(interactionContext.getProductType());
		assertNotNull(interactionContext.getScenarioId());
		assertNotNull(interactionContext.getSourceObjectId());
		interactionService.saveClickthrough(interactionContext);
	}
}
