/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.occ.controllers;

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.facades.ConfigurationVariantData;
import de.hybris.platform.sap.productconfig.facades.ConfigurationVariantFacade;
import de.hybris.platform.sap.productconfig.occ.ConfigurationVariantWsDTO;
import de.hybris.platform.webservicescommons.mapping.DataMapper;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class ProductConfiguratorCCPVariantSearchControllerTest
{
	private static final String CONFIG_ID = "A";

	@Mock
	protected ConfigurationVariantFacade configurationVariantFacade;

	@Mock
	protected DataMapper dataMapper;

	@InjectMocks
	ProductConfiguratorCCPVariantSearchController classUnderTest;

	private List<ConfigurationVariantData> variantDataList;

	private final ConfigurationVariantData variantData1 = new ConfigurationVariantData();
	private final ConfigurationVariantData variantData2 = new ConfigurationVariantData();

	private List<ConfigurationVariantWsDTO> variantWsDTOList;

	private final ConfigurationVariantWsDTO variantWsDTO1 = new ConfigurationVariantWsDTO();
	private final ConfigurationVariantWsDTO variantWsDTO2 = new ConfigurationVariantWsDTO();


	@Before
	public void initialize()
	{
		variantDataList = Arrays.asList(variantData1, variantData2);
		variantWsDTOList = Arrays.asList(variantWsDTO1, variantWsDTO2);

		MockitoAnnotations.openMocks(this);
		Mockito.when(configurationVariantFacade.searchForSimilarVariants(CONFIG_ID)).thenReturn(variantDataList);
		Mockito.when(dataMapper.mapAsList(variantDataList, ConfigurationVariantWsDTO.class, null)).thenReturn(variantWsDTOList);
	}

	@Test
	public void testConfigurationVariantFacade()
	{
		assertEquals(configurationVariantFacade, classUnderTest.getConfigurationVariantFacade());
	}

	@Test
	public void testDataMapper()
	{
		assertEquals(dataMapper, classUnderTest.getDataMapper());
	}

	@Test
	public void testGetVariants()
	{
		assertEquals(variantWsDTOList, classUnderTest.getVariants(CONFIG_ID));
	}






}
