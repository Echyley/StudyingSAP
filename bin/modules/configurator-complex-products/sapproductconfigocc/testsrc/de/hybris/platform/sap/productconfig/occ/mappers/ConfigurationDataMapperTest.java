/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.occ.mappers;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.facades.KBKeyData;
import de.hybris.platform.sap.productconfig.occ.ConfigurationWsDTO;
import de.hybris.platform.webservicescommons.mapping.FieldSelectionStrategy;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import ma.glasnost.orika.MappingContext;


@UnitTest
public class ConfigurationDataMapperTest
{
	private static final String PRODUCT_CODE = "CONF_LAPTOP";
	@Mock
	private MappingContext context;
	@Mock
	FieldSelectionStrategy fieldSelectionStrategy;
	@InjectMocks
	ConfigurationDataMapper classUnderTest;
	private final ConfigurationData configurationData = new ConfigurationData();
	private final ConfigurationWsDTO configurationWsDto = new ConfigurationWsDTO();
	private final KBKeyData kbKey = new KBKeyData();

	@Before
	public void initialize()
	{
		MockitoAnnotations.initMocks(this);
		when(fieldSelectionStrategy.shouldMap(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(true);
		configurationData.setKbKey(kbKey);
		kbKey.setProductCode(PRODUCT_CODE);
	}

	@Test
	public void testMapAtoB()
	{
		classUnderTest.mapAtoB(configurationData, configurationWsDto, context);
		assertEquals(PRODUCT_CODE, configurationWsDto.getRootProduct());
	}

	@Test
	public void testMapBtoA()
	{
		configurationData.setKbKey(null);
		configurationWsDto.setRootProduct(PRODUCT_CODE);
		classUnderTest.mapBtoA(configurationWsDto, configurationData, context);
		assertEquals(PRODUCT_CODE, configurationData.getKbKey().getProductCode());
	}


}
