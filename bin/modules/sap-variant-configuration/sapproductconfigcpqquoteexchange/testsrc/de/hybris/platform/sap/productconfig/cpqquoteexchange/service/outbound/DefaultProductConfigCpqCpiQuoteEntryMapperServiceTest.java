/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.cpqquoteexchange.service.outbound;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import com.sap.hybris.sapcpqquoteintegration.model.SAPCPQOutboundQuoteItemModel;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.sap.productconfig.services.model.ProductConfigurationModel;

@UnitTest
public class DefaultProductConfigCpqCpiQuoteEntryMapperServiceTest
{

	private static final String CONFIG_ID = "c123";
	private final DefaultProductConfigCpqCpiQuoteEntryMapperService classUnderTest = new DefaultProductConfigCpqCpiQuoteEntryMapperService();
	private final SAPCPQOutboundQuoteItemModel cpqOutboundQuoteItemModel = new SAPCPQOutboundQuoteItemModel();
	private final AbstractOrderEntryModel entryModel = new AbstractOrderEntryModel();
	private final ProductConfigurationModel productConfigurationModel = new ProductConfigurationModel();


	@Before
	public void setUp() {
		entryModel.setProductConfiguration(productConfigurationModel);
		productConfigurationModel.setConfigurationId(CONFIG_ID);
	}

	@Test
	public void testMapWithoutConfig() {
		entryModel.setProductConfiguration(null);
		classUnderTest.map(entryModel, cpqOutboundQuoteItemModel);
		assertNull(cpqOutboundQuoteItemModel.getExternalItemID());
	}

	@Test
	public void testMapWithConfig()
	{
		classUnderTest.map(entryModel, cpqOutboundQuoteItemModel);
		assertEquals(CONFIG_ID, cpqOutboundQuoteItemModel.getExternalConfigurationId());
	}
}
