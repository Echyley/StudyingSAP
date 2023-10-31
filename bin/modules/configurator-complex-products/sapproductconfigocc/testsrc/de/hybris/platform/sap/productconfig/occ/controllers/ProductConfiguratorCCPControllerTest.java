/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.occ.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.facades.ConfigurationExpertModeFacade;
import de.hybris.platform.sap.productconfig.facades.ConfigurationFacade;
import de.hybris.platform.sap.productconfig.facades.ConfigurationPricingFacade;
import de.hybris.platform.sap.productconfig.facades.CsticData;
import de.hybris.platform.sap.productconfig.facades.PriceDataPair;
import de.hybris.platform.sap.productconfig.facades.PriceValueUpdateData;
import de.hybris.platform.sap.productconfig.facades.PricingData;
import de.hybris.platform.sap.productconfig.facades.UiGroupData;
import de.hybris.platform.sap.productconfig.facades.overview.FilterEnum;
import de.hybris.platform.sap.productconfig.occ.ConfigurationSupplementsWsDTO;
import de.hybris.platform.sap.productconfig.occ.ConfigurationWsDTO;
import de.hybris.platform.sap.productconfig.occ.FilterDataWsDTO;
import de.hybris.platform.sap.productconfig.occ.util.CCPControllerHelper;
import de.hybris.platform.sap.productconfig.occ.util.ImageHandler;
import de.hybris.platform.webservicescommons.mapping.DataMapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class ProductConfiguratorCCPControllerTest
{
	private static final String CONFIG_ID = "config id";
	private static final String GROUP_ID = "group_id";
	private static final String CONFIG_ID_TEMPLATE = "configIdTemplate";
	private static final String PRODUCT_CODE = "CONF_LAPTOP";
	private static final String CONFLICT_GROUP_ID = "CONFLICTab5df12";
	protected final ConfigurationWsDTO updatedConfiguration = new ConfigurationWsDTO();
	protected final ConfigurationWsDTO backendUpdatedWsConfiguration = new ConfigurationWsDTO();
	@Mock
	protected ImageHandler imageHandler;

	@Mock
	protected CCPControllerHelper ccpControllerHelper;

	@Mock
	protected DataMapper dataMapper;

	@Mock
	protected ConfigurationFacade configurationFacade;

	@Mock
	private ConfigurationPricingFacade configPricingFacade;

	@Mock
	private ConfigurationExpertModeFacade configExpertModeFacade;

	@InjectMocks
	ProductConfiguratorCCPController classUnderTest;
	private final ConfigurationData configurationData = new ConfigurationData();
	private final ConfigurationData updatedConfigurationData = new ConfigurationData();
	private ConfigurationSupplementsWsDTO configurationSupplement;
	private ConfigurationSupplementsWsDTO configurationSupplementNoGroup;
	private PricingData priceSummary;
	private List<PriceValueUpdateData> valuePrices;
	private List<PriceValueUpdateData> valuePricesNoGroup;
	private final PriceValueUpdateData valuePrice = new PriceValueUpdateData();
	private final UiGroupData subGroup = new UiGroupData();
	private final UiGroupData subGroupConflict = new UiGroupData();
	private final List<String> valuePriceInput = new ArrayList<>();
	private final List<String> valuePriceInputConflict = new ArrayList<>();
	private List<FilterDataWsDTO> csticFilterListInput = new ArrayList<>();
	private final List<FilterEnum> csticFilterListOutput = new ArrayList<>();
	private final FilterDataWsDTO csticFilterDataVisible = new FilterDataWsDTO();
	private final FilterDataWsDTO csticFilterDataUserInput = new FilterDataWsDTO();
	private final FilterDataWsDTO csticFilterDataPriceRelevant = new FilterDataWsDTO();
	private List<FilterDataWsDTO> groupFilterListInput = new ArrayList<>();
	private final Set<String> groupFilterListOutput = new HashSet<>();
	final FilterDataWsDTO groupFilterDataGroup1 = new FilterDataWsDTO();
	final FilterDataWsDTO groupFilterDataGroup2 = new FilterDataWsDTO();
	final FilterDataWsDTO groupFilterDataGroup3 = new FilterDataWsDTO();



	@Before
	public void initialize()
	{
		configurationData.setGroups(Collections.emptyList());
		MockitoAnnotations.initMocks(this);
		when(dataMapper.map(updatedConfiguration, ConfigurationData.class)).thenReturn(configurationData);
		when(ccpControllerHelper.readDefaultConfiguration(CONFIG_ID, false)).thenReturn(configurationData);
		when(ccpControllerHelper.determineFirstGroupId(configurationData.getGroups())).thenReturn(GROUP_ID);
		when(configurationFacade.getConfiguration(configurationData)).thenReturn(updatedConfigurationData);
		when(ccpControllerHelper.mapDTOData(updatedConfigurationData)).thenReturn(backendUpdatedWsConfiguration);

		configurationSupplement = new ConfigurationSupplementsWsDTO();
		configurationSupplementNoGroup = new ConfigurationSupplementsWsDTO();
		configurationSupplement.setConfigId(CONFIG_ID);
		priceSummary = new PricingData();
		valuePrices = new ArrayList<>();
		valuePricesNoGroup = new ArrayList<>();
		valuePrices.add(valuePrice);
		valuePriceInput.add(GROUP_ID);
		valuePriceInputConflict.add(CONFLICT_GROUP_ID);
		when(configPricingFacade.getPriceSummary(CONFIG_ID)).thenReturn(priceSummary);
		when(ccpControllerHelper.getUiGroup(CONFIG_ID, GROUP_ID)).thenReturn(subGroup);
		when(ccpControllerHelper.getUiGroup(CONFIG_ID, CONFLICT_GROUP_ID)).thenReturn(subGroupConflict);
		when(ccpControllerHelper.compileValuePriceInput(subGroup)).thenReturn(valuePriceInput);
		when(ccpControllerHelper.compileValuePriceInput(subGroupConflict)).thenReturn(valuePriceInputConflict);
		when(configPricingFacade.getValuePrices(valuePriceInput, CONFIG_ID)).thenReturn(valuePrices);
		when(configPricingFacade.getValuePrices(valuePriceInputConflict, CONFIG_ID)).thenReturn(valuePrices);
		when(configPricingFacade.getValuePrices(Collections.emptyList(), CONFIG_ID)).thenReturn(valuePricesNoGroup);
		when(ccpControllerHelper.compilePricingResult(CONFIG_ID, priceSummary, valuePrices)).thenReturn(configurationSupplement);
		when(ccpControllerHelper.compilePricingResult(CONFIG_ID, priceSummary, valuePricesNoGroup))
				.thenReturn(configurationSupplementNoGroup);

		csticFilterDataVisible.setKey(FilterEnum.VISIBLE.toString());
		csticFilterDataUserInput.setKey(FilterEnum.USER_INPUT.toString());
		csticFilterDataPriceRelevant.setKey(FilterEnum.PRICE_RELEVANT.toString());

		groupFilterDataGroup1.setKey("Key Group 1");
		groupFilterDataGroup2.setKey("Key Group 2");
		groupFilterDataGroup3.setKey("Key Group 3");
	}

	@Test
	public void testCreateCsticFilterListInputIsNull()
	{
		prepareCsticLists();
		csticFilterListInput = null;
		assertEquals(csticFilterListOutput, classUnderTest.createCsticFilterList(csticFilterListInput));
	}

	@Test
	public void testCreateCsticFilterListAllSelected()
	{
		prepareCsticLists();

		csticFilterDataVisible.setSelected(true);
		csticFilterDataUserInput.setSelected(true);
		csticFilterDataPriceRelevant.setSelected(true);

		csticFilterListInput.add(csticFilterDataVisible);
		csticFilterListInput.add(csticFilterDataUserInput);
		csticFilterListInput.add(csticFilterDataPriceRelevant);

		csticFilterListOutput.add(FilterEnum.VISIBLE);
		csticFilterListOutput.add(FilterEnum.USER_INPUT);
		csticFilterListOutput.add(FilterEnum.PRICE_RELEVANT);

		assertEquals(csticFilterListOutput, classUnderTest.createCsticFilterList(csticFilterListInput));
	}

	@Test
	public void testCreateCsticFilterListNoneSelected()
	{
		prepareCsticLists();

		csticFilterDataVisible.setSelected(false);
		csticFilterDataUserInput.setSelected(false);
		csticFilterDataPriceRelevant.setSelected(false);

		csticFilterListInput.add(csticFilterDataVisible);
		csticFilterListInput.add(csticFilterDataUserInput);
		csticFilterListInput.add(csticFilterDataPriceRelevant);

		assertEquals(csticFilterListOutput, classUnderTest.createCsticFilterList(csticFilterListInput));
	}

	@Test(expected = IllegalStateException.class)
	public void testCreateCsticFilterUnknownKey()
	{
		final FilterDataWsDTO filterWithUnknownKey = new FilterDataWsDTO();
		filterWithUnknownKey.setKey("UNKNOWN");
		filterWithUnknownKey.setSelected(true);

		classUnderTest.createCsticFilterList(Collections.singletonList(filterWithUnknownKey));
	}

	@Test
	public void testCreateGroupFilterListInputIsNull()
	{
		prepareGroupLists();
		groupFilterListInput = null;
		assertEquals(groupFilterListOutput, classUnderTest.createGroupFilterList(groupFilterListInput));
	}

	@Test
	public void testCreateGroupFilterListAllSelected()
	{
		prepareGroupLists();

		groupFilterDataGroup1.setSelected(true);
		groupFilterDataGroup2.setSelected(true);
		groupFilterDataGroup3.setSelected(true);

		groupFilterListInput.add(groupFilterDataGroup1);
		groupFilterListInput.add(groupFilterDataGroup2);
		groupFilterListInput.add(groupFilterDataGroup3);

		groupFilterListOutput.add(groupFilterDataGroup1.getKey());
		groupFilterListOutput.add(groupFilterDataGroup2.getKey());
		groupFilterListOutput.add(groupFilterDataGroup3.getKey());

		assertEquals(groupFilterListOutput, classUnderTest.createGroupFilterList(groupFilterListInput));
	}

	@Test
	public void testCreateGroupFilterListTwoSelected()
	{
		prepareGroupLists();

		groupFilterDataGroup1.setSelected(true);
		groupFilterDataGroup2.setSelected(false);
		groupFilterDataGroup3.setSelected(true);

		groupFilterListInput.add(groupFilterDataGroup1);
		groupFilterListInput.add(groupFilterDataGroup2);
		groupFilterListInput.add(groupFilterDataGroup3);

		groupFilterListOutput.add(groupFilterDataGroup1.getKey());
		groupFilterListOutput.add(groupFilterDataGroup3.getKey());

		assertEquals(groupFilterListOutput, classUnderTest.createGroupFilterList(groupFilterListInput));
	}

	@Test
	public void testCreateGroupFilterListNoneSelected()
	{
		prepareGroupLists();

		groupFilterDataGroup1.setSelected(false);
		groupFilterDataGroup2.setSelected(false);
		groupFilterDataGroup3.setSelected(false);

		groupFilterListInput.add(groupFilterDataGroup1);
		groupFilterListInput.add(groupFilterDataGroup2);
		groupFilterListInput.add(groupFilterDataGroup3);

		assertEquals(groupFilterListOutput, classUnderTest.createGroupFilterList(groupFilterListInput));

	}

	@Test
	public void testUpdateConfiguration()
	{
		final ConfigurationWsDTO configurationAfterUpdate = classUnderTest.updateConfiguration(CONFIG_ID, updatedConfiguration,
				false);
		assertEquals(backendUpdatedWsConfiguration, configurationAfterUpdate);
	}


	@Test
	public void testPricing()
	{
		final ConfigurationSupplementsWsDTO supplementsWsDTO = classUnderTest.getPricing(CONFIG_ID, GROUP_ID);
		assertEquals(configurationSupplement, supplementsWsDTO);
	}

	@Test
	public void testPricingNoGroupSpecified()
	{
		final ConfigurationSupplementsWsDTO supplementsWsDTO = classUnderTest.getPricing(CONFIG_ID, null);
		assertEquals(configurationSupplementNoGroup, supplementsWsDTO);
	}

	@Test
	public void testPricingForConflictGroup()
	{
		final ConfigurationSupplementsWsDTO supplementsWsDTO = classUnderTest.getPricing(CONFIG_ID, CONFLICT_GROUP_ID);
		assertEquals(configurationSupplementNoGroup, supplementsWsDTO);
	}

	@Test
	public void testGetDefaultConfigurationWithoutExpMode()
	{
		classUnderTest.getDefaultConfiguration(CONFIG_ID, false, null, false, false);
		verify(configExpertModeFacade, times(0)).enableExpertMode();
	}

	@Test
	public void testGetDefaultConfigurationWithExpMode()
	{
		classUnderTest.getDefaultConfiguration(CONFIG_ID, false, null, true, false);
		verify(configExpertModeFacade, times(1)).enableExpertMode();
	}

	@Test
	public void testGetDefaultConfigurationWithTemplate()
	{
		classUnderTest.getDefaultConfiguration(PRODUCT_CODE, true, CONFIG_ID_TEMPLATE, false, false);
		verify(ccpControllerHelper).getConfigurationFromTemplate(PRODUCT_CODE, CONFIG_ID_TEMPLATE);
		verify(ccpControllerHelper, times(0)).readDefaultConfiguration(PRODUCT_CODE);
	}

	@Test
	public void testGetDefaultConfigurationWithTemplateSanitizesTemplateId()
	{
		classUnderTest.getDefaultConfiguration(PRODUCT_CODE, true, "cId<script>alert(evil)</script>123 ", false, false);
		verify(ccpControllerHelper).getConfigurationFromTemplate(PRODUCT_CODE, "cId&lt;script&gt;alert(evil)&lt;/script&gt;123");
		verify(ccpControllerHelper, times(0)).readDefaultConfiguration(PRODUCT_CODE);
	}

	@Test
	public void testGetDefaultConfigurationWithForceResetTrue()
	{
		classUnderTest.getDefaultConfiguration(PRODUCT_CODE, true, null, false, true);
		verify(ccpControllerHelper).readDefaultConfiguration(PRODUCT_CODE, true);
	}

	@Test
	public void testGetDefaultConfigurationWithForceResetFalse()
	{
		classUnderTest.getDefaultConfiguration(PRODUCT_CODE, true, null, false, false);
		verify(ccpControllerHelper).readDefaultConfiguration(PRODUCT_CODE, false);
	}

	@Test
	public void testGetDefaultConfigurationTemplateNotProvided()
	{
		classUnderTest.getDefaultConfiguration(PRODUCT_CODE, true, null, false, false);
		verify(ccpControllerHelper).readDefaultConfiguration(PRODUCT_CODE, false);
		verify(ccpControllerHelper, times(0)).getConfigurationFromTemplate(PRODUCT_CODE, CONFIG_ID_TEMPLATE);
	}

	@Test
	public void testGetDefaultConfigurationWithoutOptionalForceResetParameter()
	{
		classUnderTest.getDefaultConfiguration(PRODUCT_CODE, true, null, false);
		verify(ccpControllerHelper).readDefaultConfiguration(PRODUCT_CODE, false);
	}

	@Test
	public void testGetConfigurationWithoutExpMode()
	{
		classUnderTest.getConfiguration(CONFIG_ID, GROUP_ID, false);
		verify(configExpertModeFacade, times(0)).enableExpertMode();
	}

	@Test
	public void testGetConfigurationWithExpMode()
	{
		classUnderTest.getConfiguration(CONFIG_ID, GROUP_ID, true);
		verify(configExpertModeFacade, times(1)).enableExpertMode();
	}

	@Test
	public void testGetConfigurationForwardsGroup()
	{
		classUnderTest.getConfiguration(CONFIG_ID, GROUP_ID, true);
		final ArgumentCaptor<ConfigurationData> argument = ArgumentCaptor.forClass(ConfigurationData.class);
		verify(configurationFacade, times(1)).getConfiguration(argument.capture());
		assertEquals(GROUP_ID, argument.getValue().getGroupIdToDisplay());
	}

	@Test
	public void testGetConfigurationForwardsGroupAsNull()
	{
		classUnderTest.getConfiguration(CONFIG_ID, null, true);
		final ArgumentCaptor<ConfigurationData> argument = ArgumentCaptor.forClass(ConfigurationData.class);
		verify(configurationFacade, times(1)).getConfiguration(argument.capture());
		assertNull(argument.getValue().getGroupIdToDisplay());
	}

	@Test
	public void testUpdateConfigurationWithoutExpMode()
	{
		classUnderTest.updateConfiguration(CONFIG_ID, updatedConfiguration, false);
		verify(configExpertModeFacade, times(0)).enableExpertMode();
	}

	@Test
	public void testUpdateConfigurationWithExpMode()
	{
		classUnderTest.updateConfiguration(CONFIG_ID, updatedConfiguration, true);
		verify(configExpertModeFacade, times(1)).enableExpertMode();
	}

	@Test
	public void testUpdateConfigurationForwardingGroup()
	{
		assertNull(configurationData.getGroupIdToDisplay());
		classUnderTest.updateConfiguration(CONFIG_ID, updatedConfiguration, true);

		final ArgumentCaptor<ConfigurationData> argumentForUpdate = ArgumentCaptor.forClass(ConfigurationData.class);
		verify(configurationFacade, times(1)).updateConfiguration(argumentForUpdate.capture());
		assertEquals(GROUP_ID, argumentForUpdate.getValue().getGroupIdToDisplay());

		assertEquals(GROUP_ID, configurationData.getGroupIdToDisplay());
	}

	@Test
	public void testDeprecatedMethods()
	{
		final CsticData cstic = new CsticData();
		final UiGroupData uiGroup = new UiGroupData();
		final Entry<String, PriceDataPair> entryPriceDataPair = null;
		final Map<String, PriceDataPair> mapStringPriceDataPair = null;
		final List<UiGroupData> uiGroupList = null;
		final String productCode = null;

		classUnderTest.compileCsticKey(cstic, uiGroup);
		Mockito.verify(ccpControllerHelper).compileCsticKey(cstic, uiGroup);

		classUnderTest.compilePricingResult(CONFIG_ID, priceSummary, valuePrices);
		Mockito.verify(ccpControllerHelper).compilePricingResult(CONFIG_ID, priceSummary, valuePrices);

		classUnderTest.compileValuePriceInput(uiGroup);
		Mockito.verify(ccpControllerHelper).compileValuePriceInput(uiGroup);

		classUnderTest.convertEntrytoWsDTO(entryPriceDataPair);
		Mockito.verify(ccpControllerHelper).convertEntrytoWsDTO(entryPriceDataPair);

		classUnderTest.createAttributeSupplementDTO(valuePrice);
		Mockito.verify(ccpControllerHelper).createAttributeSupplementDTO(valuePrice);

		classUnderTest.createPriceSupplements(mapStringPriceDataPair);
		Mockito.verify(ccpControllerHelper).createPriceSupplements(mapStringPriceDataPair);

		classUnderTest.deleteCstics(uiGroup);
		Mockito.verify(ccpControllerHelper).deleteCstics(uiGroup);

		classUnderTest.determineFirstGroupId(uiGroupList);
		Mockito.verify(ccpControllerHelper).determineFirstGroupId(uiGroupList);

		classUnderTest.filterGroups(configurationData, GROUP_ID);
		Mockito.verify(ccpControllerHelper).filterGroups(configurationData, GROUP_ID);

		classUnderTest.filterGroups(uiGroupList, GROUP_ID);
		Mockito.verify(ccpControllerHelper).filterGroups(uiGroupList, GROUP_ID);

		classUnderTest.getFlattened(uiGroup);
		Mockito.verify(ccpControllerHelper).getFlattened(uiGroup);

		classUnderTest.getImageHandler();
		Mockito.verify(ccpControllerHelper).getImageHandler();

		classUnderTest.getUiGroup(uiGroupList, GROUP_ID);
		Mockito.verify(ccpControllerHelper).getUiGroup(uiGroupList, GROUP_ID);

		classUnderTest.getUiGroup(CONFIG_ID, GROUP_ID);
		Mockito.verify(ccpControllerHelper).getUiGroup(CONFIG_ID, GROUP_ID);

		classUnderTest.getUniqueUIKeyGenerator();
		Mockito.verify(ccpControllerHelper).getUniqueUIKeyGenerator();

		classUnderTest.hasSubGroups(uiGroup);
		Mockito.verify(ccpControllerHelper).hasSubGroups(uiGroup);

		classUnderTest.isNotRequestedGroup(uiGroup, GROUP_ID);
		Mockito.verify(ccpControllerHelper).isNotRequestedGroup(uiGroup, GROUP_ID);

		classUnderTest.mapDTOData(configurationData);
		Mockito.verify(ccpControllerHelper).mapDTOData(configurationData);

		classUnderTest.readDefaultConfiguration(productCode);
		Mockito.verify(ccpControllerHelper).readDefaultConfiguration(productCode);
	}

	private void prepareCsticLists()
	{
		csticFilterListInput.clear();
		csticFilterListOutput.clear();
	}


	private void prepareGroupLists()
	{
		groupFilterListInput.clear();
		groupFilterListOutput.clear();
	}


}
