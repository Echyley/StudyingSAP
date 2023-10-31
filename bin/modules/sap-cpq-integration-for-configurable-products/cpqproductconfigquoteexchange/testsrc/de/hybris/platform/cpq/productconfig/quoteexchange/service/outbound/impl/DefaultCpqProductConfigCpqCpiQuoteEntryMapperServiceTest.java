/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.quoteexchange.service.outbound.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;

import com.sap.hybris.sapcpqquoteintegration.model.SAPCPQOutboundQuoteItemModel;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.enums.ConfiguratorType;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.cpq.productconfig.services.ConfigurationServiceLayerHelper;
import de.hybris.platform.cpq.productconfig.services.impl.DefaultConfigurationServiceLayerHelper;
import de.hybris.platform.cpq.productconfig.services.model.CloudCPQOrderEntryProductInfoModel;

@UnitTest
public class DefaultCpqProductConfigCpqCpiQuoteEntryMapperServiceTest
{

	private static final String CPQ_CONFIG_ID = "cpqConfigId";
	private DefaultCpqProductConfigCpqCpiQuoteEntryMapperService classUnderTest;
	private SAPCPQOutboundQuoteItemModel target;

	private AbstractOrderEntryModel source;
	private ConfigurationServiceLayerHelper serviceHelper;

	@Before
	public void setUp() throws Exception
	{
		serviceHelper = new DefaultConfigurationServiceLayerHelper(null, null);
		classUnderTest = new DefaultCpqProductConfigCpqCpiQuoteEntryMapperService(serviceHelper);

		source = new AbstractOrderEntryModel();
		final CloudCPQOrderEntryProductInfoModel productInfo = new CloudCPQOrderEntryProductInfoModel();
		productInfo.setConfigurationId(CPQ_CONFIG_ID);
		productInfo.setConfiguratorType(ConfiguratorType.CLOUDCPQCONFIGURATOR);

		source.setProductInfos(Collections.singletonList(productInfo));
		target = new SAPCPQOutboundQuoteItemModel();
	}

	@Test
	public void testMapCPQConfig()
	{
		classUnderTest.map(source, target);
		assertEquals(CPQ_CONFIG_ID, target.getConfigurationId());
	}


	@Test
	public void testMapNoConfig()
	{
		source.setProductInfos(Collections.emptyList());
		classUnderTest.map(source, target);
		assertNull(target.getConfigurationId());
	}

}
