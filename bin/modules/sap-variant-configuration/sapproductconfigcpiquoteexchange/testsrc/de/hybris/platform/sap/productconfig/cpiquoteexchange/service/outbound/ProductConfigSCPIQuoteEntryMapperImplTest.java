/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.cpiquoteexchange.service.outbound;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import com.sap.hybris.sapquoteintegration.model.SAPCpiOutboundQuoteItemModel;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.sap.productconfig.services.model.ProductConfigurationModel;

@UnitTest
public class ProductConfigSCPIQuoteEntryMapperImplTest
{

	private static final String CONFIG_ID = "c123";
	private ProductConfigSCPIQuoteEntryMapperImpl classUnderTest = new ProductConfigSCPIQuoteEntryMapperImpl();
	private SAPCpiOutboundQuoteItemModel scpiOutboundItemModel = new SAPCpiOutboundQuoteItemModel();
	private AbstractOrderEntryModel entryModel = new AbstractOrderEntryModel();
	private ProductConfigurationModel productConfigurationModel = new ProductConfigurationModel();
	

	@Before
	public void setUp() {		
		entryModel.setProductConfiguration(productConfigurationModel);
		productConfigurationModel.setConfigurationId(CONFIG_ID);
	}
	
	@Test
	public void testMapWithoutConfig() {
		entryModel.setProductConfiguration(null);
		classUnderTest.map(entryModel, scpiOutboundItemModel);
		assertNull(scpiOutboundItemModel.getConfigId());
	}
	
	@Test
	public void testMapWithConfig() {
		classUnderTest.map(entryModel, scpiOutboundItemModel);
		assertEquals(CONFIG_ID,scpiOutboundItemModel.getConfigId());
	}
}
