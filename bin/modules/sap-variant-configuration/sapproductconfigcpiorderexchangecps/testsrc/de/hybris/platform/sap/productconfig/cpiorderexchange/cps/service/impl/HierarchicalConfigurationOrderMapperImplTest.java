/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.cpiorderexchange.cps.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.sap.productconfig.cpiorderexchange.cps.service.MappingDecisionStrategy;
import de.hybris.platform.sap.productconfig.cpiorderexchange.model.SAPCpiOutboundOrderS4hcConfigHeaderModel;
import de.hybris.platform.sap.productconfig.cpiorderexchange.model.SAPCpiOutboundOrderS4hcConfigStructureNodeModel;
import de.hybris.platform.sap.productconfig.cpiorderexchange.model.SAPCpiOutboundOrderS4hcConfigValuationModel;
import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSExternalCharacteristic;
import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSExternalItem;
import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSExternalValue;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderItemModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


/**
 * Unit test for {@link HierarchicalConfigurationOrderMapperImpl}
 */
@UnitTest
public class HierarchicalConfigurationOrderMapperImplTest
{

	private static final String ITEM_ID = "itemId";
	private static final String EXTERNAL_CONFIGURATION = "{\"externalConfiguration\":{\"kbId\":0,\"kbKey\":{\"logsys\":\"string\",\"name\":\"string\",\"version\":\"string\"},\"consistent\":false,\"complete\":false,\"rootItem\":{\"id\":\"string\",\"objectKey\":{\"id\":\"string\",\"type\":\"string\",\"classType\":\"string\"},\"objectKeyAuthor\":\"string\",\"bomPosition\":\"string\",\"bomPositionObjectKey\":{\"id\":\"string\",\"type\":\"string\",\"classType\":\"string\"},\"bomPositionAuthor\":\"string\",\"quantity\":{\"value\":0,\"unit\":\"string\"},\"fixedQuantity\":false,\"salesRelevant\":false,\"consistent\":false,\"complete\":false,\"characteristics\":[{\"id\":\"string\",\"required\":false,\"visible\":false,\"values\":[{\"value\":\"string\",\"author\":\"\"}]}],\"variantConditions\":[{\"key\":\"string\",\"factor\":0}],\"subItems\":[]}},\"unitCodes\":{\"PCE\":\"PCE\"}}";
	private static final String EXTERNAL_CONFIGURATION_MULTILEVEL = "{\"externalConfiguration\":{\"kbId\":0,\"kbKey\":{\"logsys\":\"string\",\"name\":\"string\",\"version\":\"string\"},\"consistent\":false,\"complete\":false,\"rootItem\":{\"id\":\"string\",\"objectKey\":{\"id\":\"string\",\"type\":\"string\",\"classType\":\"string\"},\"objectKeyAuthor\":\"string\",\"bomPosition\":\"string\",\"bomPositionObjectKey\":{\"id\":\"string\",\"type\":\"string\",\"classType\":\"string\"},\"bomPositionAuthor\":\"string\",\"quantity\":{\"value\":0,\"unit\":\"string\"},\"fixedQuantity\":false,\"salesRelevant\":false,\"consistent\":false,\"complete\":false,\"characteristics\":[{\"id\":\"string\",\"required\":false,\"visible\":false,\"values\":[{\"value\":\"string\",\"author\":\"\"}]}],\"variantConditions\":[{\"key\":\"string\",\"factor\":0}],\"subItems\":[{\"id\":\"string\",\"objectKey\":{\"id\":\"string\",\"type\":\"string\",\"classType\":\"string\"},\"objectKeyAuthor\":\"string\",\"bomPosition\":\"string\",\"bomPositionObjectKey\":{\"id\":\"string\",\"type\":\"string\",\"classType\":\"string\"},\"bomPositionAuthor\":\"string\",\"quantity\":{\"value\":0,\"unit\":\"string\"},\"fixedQuantity\":false,\"salesRelevant\":false,\"consistent\":false,\"complete\":false,\"characteristics\":[{\"id\":\"string\",\"required\":false,\"visible\":false,\"values\":[{\"value\":\"string\",\"author\":\"\"}]}],\"variantConditions\":[{\"key\":\"string\",\"factor\":0}],\"subItems\":[]}]}},\"unitCodes\":{\"PCE\":\"PCE\"}}";
	private static final Integer ENTRY_NUMBER = 1;
	private static final String ORDER_ID = "123";
	private static final String CSTIC_NAME = "Cstic";
	private static final String VALUE_NAME = "Value";
	private static final String AUTHOR_DEPENDENCY = "6";

	@InjectMocks
	private HierarchicalConfigurationOrderMapperImpl classUnderTest;

	private final CPSExternalCharacteristic cstic = new CPSExternalCharacteristic();
	private final List<AbstractOrderEntryModel> sourceEntryList = new ArrayList<>();
	private final Set<SAPCpiOutboundOrderItemModel> targetEntryList = new HashSet<>();
	private final SAPCpiOutboundOrderS4hcConfigHeaderModel configurationHeader = new SAPCpiOutboundOrderS4hcConfigHeaderModel();
	private final List<CPSExternalValue> characteristicValues = new ArrayList<>();
	private final CPSExternalValue characteristicValue = new CPSExternalValue();

	@Mock
	private OrderModel source;
	@Mock
	private SAPCpiOutboundOrderModel target;
	@Mock
	private AbstractOrderEntryModel sourceEntry;
	@Mock
	private SAPCpiOutboundOrderItemModel targetEntry;
	@Mock
	private MappingDecisionStrategy mappingDecisionStrategy;

	@Before
	public void initialize()
	{
		MockitoAnnotations.initMocks(this);
		sourceEntryList.add(sourceEntry);
		targetEntryList.add(targetEntry);
		Mockito.when(source.getEntries()).thenReturn(sourceEntryList);
		Mockito.when(sourceEntry.getExternalConfiguration()).thenReturn(EXTERNAL_CONFIGURATION);
		Mockito.when(target.getSapCpiOutboundOrderItems()).thenReturn(targetEntryList);
		Mockito.when(sourceEntry.getEntryNumber()).thenReturn(ENTRY_NUMBER);
		Mockito.when(targetEntry.getEntryNumber()).thenReturn(ENTRY_NUMBER.toString());
		Mockito.when(targetEntry.getOrderId()).thenReturn(ORDER_ID);
		Mockito.when(mappingDecisionStrategy.isA2AServiceMappingActive(Mockito.any())).thenReturn(true);

		configurationHeader.setSapCpiS4hcConfigValuations(new HashSet<SAPCpiOutboundOrderS4hcConfigValuationModel>());
		configurationHeader.setEntryNumber(ENTRY_NUMBER.toString());
		configurationHeader.setOrderId(ORDER_ID);

		cstic.setValues(characteristicValues);
		characteristicValue.setAuthor(HierarchicalConfigurationOrderMapperImpl.AUTHOR_EXTERNAL);
		characteristicValue.setValue(VALUE_NAME);
		characteristicValues.add(characteristicValue);
	}

	@Test
	public void testMappingDecisionStrategy()
	{
		assertEquals(mappingDecisionStrategy, classUnderTest.getMappingDecisionStrategy());
	}

	@Test
	public void testMapAuthorExternal()
	{
		assertEquals(HierarchicalConfigurationOrderMapperImpl.TARGET_AUTHOR_EXTERNAL,
				classUnderTest.mapAuthor(HierarchicalConfigurationOrderMapperImpl.AUTHOR_EXTERNAL));
	}

	@Test
	public void testMapAuthor()
	{
		assertEquals(HierarchicalConfigurationOrderMapperImpl.TARGET_AUTHOR_USER,
				classUnderTest.mapAuthor(HierarchicalConfigurationOrderMapperImpl.AUTHOR_USER));
		assertEquals(HierarchicalConfigurationOrderMapperImpl.TARGET_AUTHOR_USER, classUnderTest.mapAuthor(" "));
		assertEquals(HierarchicalConfigurationOrderMapperImpl.TARGET_AUTHOR_USER, classUnderTest.mapAuthor(null));
	}

	@Test(expected = IllegalStateException.class)
	public void testMapAuthorNotForeseen()
	{
		classUnderTest.mapAuthor(AUTHOR_DEPENDENCY);
	}

	@Test
	public void testMapAuthorNotForeseenButNoCheck()
	{
		classUnderTest.setDisabledAuthorCheck(true);
		assertEquals(AUTHOR_DEPENDENCY, classUnderTest.mapAuthor(AUTHOR_DEPENDENCY));
	}

	@Test
	public void testMap()
	{
		classUnderTest.map(source, target);
		Mockito.verify(targetEntry, times(1)).setS4hcConfigHeader(Mockito.any());
	}

	@Test
	public void testMapEntry()
	{
		classUnderTest.mapEntry(sourceEntry, target);
		Mockito.verify(targetEntry, times(1)).setS4hcConfigHeader(Mockito.any());
	}

	@Test
	public void testMapEntryNotConfigurable()
	{
		Mockito.when(sourceEntry.getExternalConfiguration()).thenReturn(null);
		classUnderTest.mapEntry(sourceEntry, target);
		Mockito.verify(targetEntry, times(0)).setS4hcConfigHeader(Mockito.any());
	}

	@Test
	public void testFindTargetEntry()
	{
		final SAPCpiOutboundOrderItemModel targetEntry = classUnderTest.findTargetEntry(sourceEntry, target);
		assertNotNull(targetEntry);
	}


	@Test
	public void testFindTargetEntryNoMatch()
	{
		Mockito.when(targetEntry.getEntryNumber()).thenReturn("3");
		final SAPCpiOutboundOrderItemModel targetEntry = classUnderTest.findTargetEntry(sourceEntry, target);
		assertNull(targetEntry);
	}

	@Test
	public void testCreateConfigurationSingleLevel()
	{
		final SAPCpiOutboundOrderS4hcConfigHeaderModel configuration = classUnderTest.createConfiguration(EXTERNAL_CONFIGURATION,
				targetEntry);
		assertNotNull(configuration);
		final Set<SAPCpiOutboundOrderS4hcConfigValuationModel> configValuations = configuration.getSapCpiS4hcConfigValuations();
		assertNotNull(configValuations);
		assertEquals(1, configValuations.size());
		assertEquals(ENTRY_NUMBER.toString(), configuration.getEntryNumber());
		assertEquals(ORDER_ID, configuration.getOrderId());
		assertTrue(configuration.getSapCpiS4hcConfigStructureNodes().isEmpty());
	}

	@Test(expected = IllegalStateException.class)
	public void testCreateConfigurationWrongFormat()
	{
		classUnderTest.createConfiguration("Huhu", targetEntry);
	}

	@Test
	public void testAddCharacteristicValues()
	{
		classUnderTest.addCharacteristicValues(cstic, configurationHeader, configurationHeader.getSapCpiS4hcConfigValuations(), "");
		final Set<SAPCpiOutboundOrderS4hcConfigValuationModel> sapCpiS4hcConfigValuations = configurationHeader
				.getSapCpiS4hcConfigValuations();
		assertEquals(1, sapCpiS4hcConfigValuations.size());
	}

	@Test
	public void testAddCharacteristicValuesForNullValues()
	{
		cstic.setValues(null);
		classUnderTest.addCharacteristicValues(cstic, configurationHeader, configurationHeader.getSapCpiS4hcConfigValuations(), "");
		final Set<SAPCpiOutboundOrderS4hcConfigValuationModel> sapCpiS4hcConfigValuations = configurationHeader
				.getSapCpiS4hcConfigValuations();
		assertEquals(0, sapCpiS4hcConfigValuations.size());
	}

	@Test
	public void testAddCharacteristicValue()
	{
		classUnderTest.addCharacteristicValue(characteristicValue, CSTIC_NAME, configurationHeader,
				configurationHeader.getSapCpiS4hcConfigValuations(), ITEM_ID);
		final Set<SAPCpiOutboundOrderS4hcConfigValuationModel> sapCpiS4hcConfigValuations = configurationHeader
				.getSapCpiS4hcConfigValuations();
		assertEquals(1, sapCpiS4hcConfigValuations.size());
		sapCpiS4hcConfigValuations.forEach(valuation -> checkValuation(valuation));
	}

	@Test
	public void testAddCharacteristicValueAuthorFilter()
	{
		characteristicValue.setAuthor(AUTHOR_DEPENDENCY);
		final Set<SAPCpiOutboundOrderS4hcConfigValuationModel> configValuations = configurationHeader
				.getSapCpiS4hcConfigValuations();
		classUnderTest.addCharacteristicValue(characteristicValue, CSTIC_NAME, configurationHeader, configValuations, ITEM_ID);
		assertTrue(configValuations.isEmpty());
	}

	@Test
	public void testIsPartOfMessage()
	{
		characteristicValue.setAuthor(AUTHOR_DEPENDENCY);
		assertFalse(classUnderTest.isPartOfMessage(characteristicValue));
		characteristicValue.setAuthor(null);
		assertTrue(classUnderTest.isPartOfMessage(characteristicValue));
	}

	@Test
	public void testIsPartOfMessageAuthorUser()
	{
		characteristicValue.setAuthor(HierarchicalConfigurationOrderMapperImpl.AUTHOR_USER);
		assertTrue(classUnderTest.isPartOfMessage(characteristicValue));
	}

	@Test
	public void testIsPartOfMessageAuthorCheckDisabled()
	{
		characteristicValue.setAuthor(AUTHOR_DEPENDENCY);
		classUnderTest.setDisabledAuthorCheck(true);
		assertTrue(classUnderTest.isPartOfMessage(characteristicValue));
	}

	@Test
	public void testGetObjectMapper()
	{
		// 2 calls will return the same instance
		assertEquals(classUnderTest.getObjectMapper(), classUnderTest.getObjectMapper());
	}

	protected void checkValuation(final SAPCpiOutboundOrderS4hcConfigValuationModel valuation)
	{
		assertEquals(HierarchicalConfigurationOrderMapperImpl.TARGET_AUTHOR_EXTERNAL, valuation.getAuthor());
		assertEquals(CSTIC_NAME, valuation.getCharacteristic());
		assertEquals(ENTRY_NUMBER.toString(), valuation.getEntryNumber());
		assertEquals(ORDER_ID, valuation.getOrderId());
		assertEquals(ITEM_ID, valuation.getNodeId());
		assertEquals(VALUE_NAME, valuation.getValue());
	}

	@Test
	public void testCreateConfigurationMultiLevel()
	{
		final SAPCpiOutboundOrderS4hcConfigHeaderModel configuration = classUnderTest
				.createConfiguration(EXTERNAL_CONFIGURATION_MULTILEVEL, targetEntry);
		assertNotNull(configuration);
		final Set<SAPCpiOutboundOrderS4hcConfigValuationModel> configValuations = configuration.getSapCpiS4hcConfigValuations();
		assertNotNull(configValuations);
		assertEquals(1, configValuations.size());
		assertEquals(ENTRY_NUMBER.toString(), configuration.getEntryNumber());
		assertEquals(ORDER_ID, configuration.getOrderId());
		assertEquals(1, configuration.getSapCpiS4hcConfigStructureNodes().size());
	}

	@Test
	public void testAddSubItem()
	{
		final CPSExternalItem subItem = new CPSExternalItem();
		subItem.setId("sub item id");
		subItem.setBomPositionIdentifier("sub item bom position identifier");
		subItem.setCharacteristics(Arrays.asList(cstic));
		final CPSExternalItem parentItem = new CPSExternalItem();
		parentItem.setId("parent item id");
		configurationHeader.setSapCpiS4hcConfigStructureNodes(new HashSet());
		classUnderTest.addSubItem(configurationHeader, subItem, parentItem, "");
		assertEquals(1, configurationHeader.getSapCpiS4hcConfigStructureNodes().size());
		final SAPCpiOutboundOrderS4hcConfigStructureNodeModel result = configurationHeader.getSapCpiS4hcConfigStructureNodes()
				.iterator().next();
		assertEquals("sub item bom position identifier", result.getBomPositionIdentifier());
		assertEquals("sub item id", result.getNodeId());
		assertEquals("parent item id", result.getParentNodeId());
		assertEquals(ENTRY_NUMBER.toString(), result.getEntryNumber());
		assertEquals(ORDER_ID, result.getOrderId());
	}

	@Test
	public void testAddSubItemIsLeaf()
	{
		final CPSExternalItem subItem = new CPSExternalItem();
		subItem.setId("sub item id");
		subItem.setBomPositionIdentifier("sub item bom position identifier");
		final CPSExternalItem parentItem = new CPSExternalItem();
		parentItem.setId("parent item id");
		configurationHeader.setSapCpiS4hcConfigStructureNodes(new HashSet());
		classUnderTest.addSubItem(configurationHeader, subItem, parentItem, "");
		assertTrue(configurationHeader.getSapCpiS4hcConfigStructureNodes().isEmpty());
	}

	@Test
	public void testAddSubItemNoRootAsParent()
	{
		final CPSExternalItem subItem = new CPSExternalItem();
		subItem.setId("sub item id");
		subItem.setBomPositionIdentifier("sub item bom position identifier");
		subItem.setCharacteristics(Arrays.asList(cstic));
		final CPSExternalItem parentItem = new CPSExternalItem();
		parentItem.setId("parent item id");
		configurationHeader.setSapCpiS4hcConfigStructureNodes(new HashSet());
		classUnderTest.addSubItem(configurationHeader, subItem, parentItem, "parent item id");
		assertEquals(1, configurationHeader.getSapCpiS4hcConfigStructureNodes().size());
		final SAPCpiOutboundOrderS4hcConfigStructureNodeModel result = configurationHeader.getSapCpiS4hcConfigStructureNodes()
				.iterator().next();
		assertEquals("sub item bom position identifier", result.getBomPositionIdentifier());
		assertEquals("sub item id", result.getNodeId());
		assertNull(result.getParentNodeId());
		assertEquals(ENTRY_NUMBER.toString(), result.getEntryNumber());
		assertEquals(ORDER_ID, result.getOrderId());
	}

	@Test
	public void testIsLeafWithoutCharacteristicsNullCsticsAndSubItems()
	{
		final CPSExternalItem subItem = new CPSExternalItem();
		assertTrue(classUnderTest.isLeafWithoutCharacteristics(subItem));
	}

	@Test
	public void testIsLeafWithoutCharacteristicsEmptyCstics()
	{
		final CPSExternalItem subItem = new CPSExternalItem();
		subItem.setCharacteristics(Collections.emptyList());
		assertTrue(classUnderTest.isLeafWithoutCharacteristics(subItem));
	}

	@Test
	public void testIsLeafWithoutCharacteristicsHasSubItems()
	{
		final CPSExternalItem subItem = new CPSExternalItem();
		final CPSExternalItem subItemSecondLevel = new CPSExternalItem();
		subItem.setSubItems(Arrays.asList(subItemSecondLevel));
		assertFalse(classUnderTest.isLeafWithoutCharacteristics(subItem));
	}

	@Test
	public void testIsLeafWithoutCharacteristicsHasCstics()
	{
		final CPSExternalItem subItem = new CPSExternalItem();
		subItem.setCharacteristics(Arrays.asList(cstic));
		assertFalse(classUnderTest.isLeafWithoutCharacteristics(subItem));
	}
}
