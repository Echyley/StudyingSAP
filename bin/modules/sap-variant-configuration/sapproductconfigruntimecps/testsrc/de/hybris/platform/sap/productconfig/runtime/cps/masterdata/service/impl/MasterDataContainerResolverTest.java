/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.cps.masterdata.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.cps.constants.SapproductconfigruntimecpsConstants;
import de.hybris.platform.sap.productconfig.runtime.cps.masterdata.service.ConfigurationMasterDataService;
import de.hybris.platform.sap.productconfig.runtime.cps.masterdata.service.MasterDataContainerResolver;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.CPSMasterDataCharacteristicGroup;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.CPSMasterDataPossibleValue;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.CPSMasterDataPossibleValueSpecific;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataCharacteristicContainer;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataCharacteristicSpecificContainer;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataClassContainer;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataKnowledgeBaseContainer;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataProductContainer;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.common.CPSMasterDataKBHeaderInfo;
import de.hybris.platform.sap.productconfig.runtime.cps.populator.impl.MasterDataContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


/**
 * Unit test for {@link MasterDataContainerResolver}
 */
@UnitTest
public class MasterDataContainerResolverTest
{
	private static final String KB_ID = "99";
	private static final String CSTIC_ID = "csticId";
	private static final String NUMERIC_CSTIC_ID = "numericCsticId";
	private static final String CSTIC_TYPE_STRING = "string";
	private static final String CLASS_ID = "classId";
	private static final String INVALID_CLASS_ID = "invalidClassId";
	private static final String CLASS_NAME = "The Class Name";
	private static final String CLASS_NAME_FALLBACK = "The Fallback Class Name";
	private static final String PRODUCT_ID = "productId";
	private static final String PRODUCT_NAME = "productName";
	private static final String PRODUCT_NAME_FALLBACK = "Fallback productName";
	private static final Integer KB_BULD_NUMBER = Integer.valueOf(10);
	private static final String VALUE_ID = "valueId";
	private static final String VALUE_NAME = "Value Name";
	private static final String VALUE_NAME_FALLBACK = "Fallback Value Name";
	private static final String NUMERIC_VALUE_ID = "2.0";
	private static final String GROUP_ID = "groupId";
	private static final String GROUP_NAME = "The Group Name";
	private static final String GROUP_NAME_FALLBACK = "The Fallback Group Name";
	private static final String CSTIC_NAME_FALLBACK = "The Fallback Cstic Name";
	private static final String CSTIC_NAME = "The Cstic Name";
	private static final String VAR_COND_KEY = "conditionKey";

	public MasterDataContainerResolverImpl classUnderTest;
	@Mock
	private ConfigurationMasterDataService configurationMasterDataService;

	private Map<String, CPSMasterDataCharacteristicContainer> characteristics;
	private Map<String, CPSMasterDataCharacteristicContainer> characteristicsFallback;
	private CPSMasterDataCharacteristicContainer csticContainer;
	private CPSMasterDataCharacteristicContainer csticContainer2;
	private CPSMasterDataCharacteristicContainer csticContainerFallback;
	private CPSMasterDataCharacteristicContainer csticContainer2Fallback;
	private CPSMasterDataKnowledgeBaseContainer kbContainer;
	private CPSMasterDataKnowledgeBaseContainer fallbackKbContainer;
	private CPSMasterDataProductContainer productContainer;
	private CPSMasterDataProductContainer productContainerFallback;
	private CPSMasterDataClassContainer classContainer;
	private CPSMasterDataClassContainer classContainerFallback;
	private CPSMasterDataKBHeaderInfo headerInfo;
	private Map<String, CPSMasterDataPossibleValue> possibleValues;
	private CPSMasterDataPossibleValue possibleValue;
	private Map<String, CPSMasterDataPossibleValue> possibleValuesFallback;
	private CPSMasterDataPossibleValue possibleValueFallback;
	private Map<String, CPSMasterDataPossibleValue> possibleValues2;
	private CPSMasterDataPossibleValue possibleValue2;
	private Map<String, CPSMasterDataCharacteristicGroup> groups;
	private Map<String, CPSMasterDataCharacteristicGroup> groupsFallback;
	private CPSMasterDataCharacteristicGroup group;
	private CPSMasterDataCharacteristicGroup groupFallback;
	private MasterDataContext ctxt;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new MasterDataContainerResolverImpl();
		classUnderTest.setMasterDataService(configurationMasterDataService);
		kbContainer = new CPSMasterDataKnowledgeBaseContainer();
		fillCstics();
		fillHeader();
		fillClasses();
		fillProducts();
		fillGroups();
		ctxt = new MasterDataContext();
		ctxt.setKbCacheContainer(kbContainer);

		fallbackKbContainer = new CPSMasterDataKnowledgeBaseContainer();
		fillFallbackCstics();
		fillFallbackClasses();
		fillFallbackProducts();
		fillFallbackGroups();
		Mockito.when(configurationMasterDataService.getFallbackMasterData(KB_ID)).thenReturn(fallbackKbContainer);
	}

	protected void fillClasses()
	{
		classContainer = new CPSMasterDataClassContainer();
		classContainer.setId(CLASS_ID);
		classContainer.setName(CLASS_NAME);
		kbContainer.setClasses(Collections.singletonMap(CLASS_ID, classContainer));
	}

	protected void fillFallbackClasses()
	{
		classContainerFallback = new CPSMasterDataClassContainer();
		classContainerFallback.setId(CLASS_ID);
		classContainerFallback.setName(CLASS_NAME_FALLBACK);
		fallbackKbContainer.setClasses(Collections.singletonMap(CLASS_ID, classContainerFallback));
	}

	protected void fillProducts()
	{
		productContainer = new CPSMasterDataProductContainer();
		productContainer.setId(PRODUCT_ID);
		productContainer.setName(PRODUCT_NAME);
		productContainer.setMultilevel(true);
		kbContainer.setProducts(Collections.singletonMap(PRODUCT_ID, productContainer));
	}

	protected void fillFallbackProducts()
	{
		productContainerFallback = new CPSMasterDataProductContainer();
		productContainerFallback.setId(PRODUCT_ID);
		productContainerFallback.setName(PRODUCT_NAME_FALLBACK);
		productContainerFallback.setMultilevel(true);
		fallbackKbContainer.setProducts(Collections.singletonMap(PRODUCT_ID, productContainerFallback));
	}

	protected void fillFallbackGroups()
	{
		groupFallback = new CPSMasterDataCharacteristicGroup();
		groupFallback.setId(GROUP_ID);
		groupFallback.setName(GROUP_NAME_FALLBACK);
		groupFallback.setCharacteristicIDs(new ArrayList<>());
		groupsFallback = new HashMap<>();
		groupsFallback.put(GROUP_ID, groupFallback);
		productContainerFallback.setGroups(groupsFallback);
	}

	protected void fillHeader()
	{
		headerInfo = new CPSMasterDataKBHeaderInfo();
		headerInfo.setId(Integer.valueOf(KB_ID));
		headerInfo.setBuild(KB_BULD_NUMBER);
		kbContainer.setHeaderInfo(headerInfo);
	}

	protected void fillFallbackCstics()
	{
		csticContainerFallback = new CPSMasterDataCharacteristicContainer();
		csticContainerFallback.setId(CSTIC_ID);
		csticContainerFallback.setName(CSTIC_NAME_FALLBACK);
		csticContainerFallback.setType(CSTIC_TYPE_STRING);
		characteristicsFallback = new HashMap<>();
		characteristicsFallback.put(CSTIC_ID, csticContainerFallback);
		fillPossibleValuesFallback();
		csticContainer2Fallback = new CPSMasterDataCharacteristicContainer();
		csticContainer2Fallback.setId(NUMERIC_CSTIC_ID);
		csticContainer2Fallback.setType(MasterDataContainerResolverImpl.CSTIC_TYPE_FLOAT);
		characteristicsFallback.put(NUMERIC_CSTIC_ID, csticContainer2Fallback);
		fillPossibleNumericValues();
		fallbackKbContainer.setCharacteristics(characteristicsFallback);
	}

	private void fillPossibleValuesFallback()
	{
		possibleValueFallback = new CPSMasterDataPossibleValue();
		possibleValueFallback.setId(VALUE_ID);
		possibleValueFallback.setName(VALUE_NAME_FALLBACK);
		possibleValuesFallback = new HashMap<>();
		possibleValuesFallback.put(VALUE_ID, possibleValueFallback);
		csticContainerFallback.setPossibleValueGlobals(possibleValuesFallback);
	}

	protected void fillCstics()
	{
		csticContainer = new CPSMasterDataCharacteristicContainer();
		csticContainer.setId(CSTIC_ID);
		csticContainer.setName(CSTIC_NAME);
		csticContainer.setType(CSTIC_TYPE_STRING);
		characteristics = new HashMap<>();
		characteristics.put(CSTIC_ID, csticContainer);
		fillPossibleValues();
		csticContainer2 = new CPSMasterDataCharacteristicContainer();
		csticContainer2.setId(NUMERIC_CSTIC_ID);
		csticContainer2.setType(MasterDataContainerResolverImpl.CSTIC_TYPE_FLOAT);
		characteristics.put(NUMERIC_CSTIC_ID, csticContainer2);
		fillPossibleNumericValues();
		kbContainer.setCharacteristics(characteristics);
	}

	protected void fillCsticsAndValueSpecific()
	{
		final CPSMasterDataCharacteristicSpecificContainer csticSpecificContainer = new CPSMasterDataCharacteristicSpecificContainer();
		csticSpecificContainer.setId(CSTIC_ID);
		final CPSMasterDataPossibleValueSpecific csticValueSpecificContainer = new CPSMasterDataPossibleValueSpecific();
		csticValueSpecificContainer.setId(VALUE_ID);
		csticValueSpecificContainer.setVariantConditionKey(VAR_COND_KEY);
		csticSpecificContainer.setPossibleValueSpecifics(Collections.singletonMap(VALUE_ID, csticValueSpecificContainer));
		productContainer.setCstics(Collections.singletonMap(CSTIC_ID, csticSpecificContainer));
	}

	private void fillPossibleValues()
	{
		possibleValue = new CPSMasterDataPossibleValue();
		possibleValue.setId(VALUE_ID);
		possibleValue.setName(VALUE_NAME);
		possibleValues = new HashMap<>();
		possibleValues.put(VALUE_ID, possibleValue);
		csticContainer.setPossibleValueGlobals(possibleValues);
	}

	private void fillPossibleNumericValues()
	{
		possibleValue2 = new CPSMasterDataPossibleValue();
		possibleValue2.setId(NUMERIC_VALUE_ID);
		possibleValue2.setName(NUMERIC_VALUE_ID);
		possibleValues2 = new HashMap<>();
		possibleValues2.put(NUMERIC_VALUE_ID, possibleValue2);
		csticContainer2.setPossibleValueGlobals(possibleValues2);
	}

	protected void fillGroups()
	{
		group = new CPSMasterDataCharacteristicGroup();
		group.setId(GROUP_ID);
		group.setName(GROUP_NAME);
		group.setCharacteristicIDs(new ArrayList<>());
		groups = new HashMap<>();
		groups.put(GROUP_ID, group);
		productContainer.setGroups(groups);
	}

	@Test
	public void testGetValueName()
	{
		final String result = classUnderTest.getValueName(kbContainer, CSTIC_ID, VALUE_ID);
		assertNotNull(result);
		assertEquals(VALUE_NAME, result);
	}

	@Test
	public void testGetValueNameForNumeric()
	{
		final String result = classUnderTest.getValueName(kbContainer, NUMERIC_CSTIC_ID, NUMERIC_VALUE_ID);
		assertNull(result);
	}

	@Test(expected = IllegalStateException.class)
	public void testGetValueNameNoValidCsticId()
	{
		classUnderTest.getValueName(kbContainer, "invalidCsticId", VALUE_ID);
	}

	@Test
	public void testGetValueNameNoValidValueId()
	{
		assertEquals("invalidValueId", classUnderTest.getValueName(kbContainer, CSTIC_ID, "invalidValueId"));
	}

	@Test
	public void testGetValueNameWithFallbackNoFallback()
	{
		final String result = classUnderTest.getValueNameWithFallback(ctxt, CSTIC_ID, VALUE_ID);
		assertNotNull(result);
		assertEquals(VALUE_NAME, result);
	}

	@Test
	public void testGetValueNameWithFallbackNoFallbackForNumeric()
	{
		final String result = classUnderTest.getValueNameWithFallback(ctxt, NUMERIC_CSTIC_ID, NUMERIC_VALUE_ID);
		assertNull(result);
	}

	@Test(expected = IllegalStateException.class)
	public void testGetValueNameWithFallbackNoValidCsticId()
	{
		classUnderTest.getValueNameWithFallback(ctxt, "invalidCsticId", VALUE_ID);
	}

	@Test
	public void testGetValueNameWithFallbackNoValidValueId()
	{
		assertEquals("invalidValueId", classUnderTest.getValueNameWithFallback(ctxt, CSTIC_ID, "invalidValueId"));
	}

	@Test
	public void testHandleValueNameFallbackNoFallback()
	{
		final String result = classUnderTest.handleValueNameFallback(ctxt, CSTIC_ID, VALUE_ID, possibleValue);
		assertNotNull(result);
		assertEquals(VALUE_NAME, result);
	}

	@Test
	public void testHandleValueNameFallback()
	{
		possibleValue.setName(null);
		assertNull(ctxt.getFallbackKbCacheContainer());
		final String result = classUnderTest.handleValueNameFallback(ctxt, CSTIC_ID, VALUE_ID, possibleValue);
		assertNotNull(result);
		assertEquals(VALUE_NAME_FALLBACK, result);
		assertNotNull(ctxt.getFallbackKbCacheContainer());
		verify(configurationMasterDataService).getFallbackMasterData(KB_ID);
	}

	@Test
	public void testIsCsticStringType()
	{
		assertTrue(classUnderTest.isCsticStringType(characteristics.get(CSTIC_ID)));
		assertFalse(classUnderTest.isCsticStringType(characteristics.get(NUMERIC_CSTIC_ID)));
	}

	@Test
	public void testIsCsticTypeNumericFloat()
	{
		csticContainer.setType(MasterDataContainerResolverImpl.CSTIC_TYPE_FLOAT);
		assertTrue(classUnderTest.isCsticNumericType(csticContainer));
	}

	@Test
	public void testIsCsticTypeNumericInteger()
	{
		csticContainer.setType(MasterDataContainerResolverImpl.CSTIC_TYPE_INTEGER);
		assertTrue(classUnderTest.isCsticNumericType(csticContainer));
	}

	@Test
	public void testGetProductName()
	{
		final String result = classUnderTest.getProductName(kbContainer, PRODUCT_ID);
		assertNotNull(result);
		assertEquals(PRODUCT_NAME, result);
	}

	@Test
	public void testIsProductMultilevel()
	{
		assertTrue(classUnderTest.isProductMultilevel(kbContainer, PRODUCT_ID));
	}

	@Test(expected = IllegalStateException.class)
	public void testGetProductName_NoValidProductId()
	{
		classUnderTest.getProductName(kbContainer, "invalidProductId");
	}

	@Test
	public void testGetClassName()
	{
		final String result = classUnderTest.getClassName(kbContainer, CLASS_ID);
		assertNotNull(result);
		assertEquals(CLASS_NAME, result);
	}

	@Test
	public void testGetClass()
	{
		final CPSMasterDataClassContainer classContainer = classUnderTest.getClass(kbContainer, CLASS_ID);
		assertNotNull(classContainer);
	}

	@Test(expected = IllegalStateException.class)
	public void testGetClass_NoValidClassId()
	{
		classUnderTest.getClass(kbContainer, INVALID_CLASS_ID);
	}

	@Test(expected = IllegalStateException.class)
	public void testGetClassName_NoValidClassId()
	{
		classUnderTest.getClassName(kbContainer, INVALID_CLASS_ID);
	}

	@Test
	public void testGetKbBuildNumber()
	{
		final Integer result = classUnderTest.getKbBuildNumber(kbContainer);
		assertNotNull(result);
	}

	@Test
	public void testGetFallbackKbCacheContainer()
	{
		assertNull(ctxt.getFallbackKbCacheContainer());
		classUnderTest.getFallbackKbCacheContainer(ctxt);
		assertEquals(fallbackKbContainer, ctxt.getFallbackKbCacheContainer());
		verify(configurationMasterDataService).getFallbackMasterData(KB_ID);
	}

	@Test
	public void testGetFallbackKbCacheContainerFromCtxt()
	{
		ctxt.setFallbackKbCacheContainer(fallbackKbContainer);
		classUnderTest.getFallbackKbCacheContainer(ctxt);
		verify(configurationMasterDataService, times(0)).getFallbackMasterData(KB_ID);
	}

	@Test
	public void testGetValueNameWithFallback()
	{
		possibleValue.setName(null);
		assertNull(ctxt.getFallbackKbCacheContainer());
		final String result = classUnderTest.getValueNameWithFallback(ctxt, CSTIC_ID, VALUE_ID);
		assertNotNull(result);
		assertEquals(VALUE_NAME_FALLBACK, result);
		assertNotNull(ctxt.getFallbackKbCacheContainer());
		verify(configurationMasterDataService).getFallbackMasterData(KB_ID);
	}

	@Test
	public void testGetValueNameWithFallbackFromCtxt()
	{
		possibleValue.setName(null);
		ctxt.setFallbackKbCacheContainer(fallbackKbContainer);
		final String result = classUnderTest.getValueNameWithFallback(ctxt, CSTIC_ID, VALUE_ID);
		assertNotNull(result);
		assertEquals(VALUE_NAME_FALLBACK, result);
		verify(configurationMasterDataService, times(0)).getFallbackMasterData(KB_ID);
	}

	@Test
	public void testGetValueNameWithFallbackButNotAvailable()
	{
		possibleValue.setName(null);
		Mockito.when(configurationMasterDataService.getFallbackMasterData(KB_ID)).thenReturn(null);
		final String result = classUnderTest.getValueNameWithFallback(ctxt, CSTIC_ID, VALUE_ID);
		assertNull(result);
		assertNull(ctxt.getFallbackKbCacheContainer());
	}

	@Test
	public void testGetValueNameFromFallbackKbContainer()
	{
		possibleValue.setName(null);
		assertNull(ctxt.getFallbackKbCacheContainer());
		final String result = classUnderTest.getValueNameFromFallbackKbContainer(ctxt, CSTIC_ID, VALUE_ID);
		assertNotNull(result);
		assertEquals(VALUE_NAME_FALLBACK, result);
		assertNotNull(ctxt.getFallbackKbCacheContainer());
		verify(configurationMasterDataService).getFallbackMasterData(KB_ID);
	}

	@Test
	public void testGetValueNameFromFallbackKbContainerButNotAvailable()
	{
		possibleValue.setName(null);
		Mockito.when(configurationMasterDataService.getFallbackMasterData(KB_ID)).thenReturn(null);
		final String result = classUnderTest.getValueNameFromFallbackKbContainer(ctxt, CSTIC_ID, VALUE_ID);
		assertNull(result);
		assertNull(ctxt.getFallbackKbCacheContainer());
	}

	@Test
	public void testGetGroupNameFromFallbackKbContainer()
	{
		group.setName(null);
		assertNull(ctxt.getFallbackKbCacheContainer());
		final String result = classUnderTest.getGroupNameFromFallbackKbContainer(ctxt, PRODUCT_ID, GROUP_ID);
		assertNotNull(result);
		assertEquals(GROUP_NAME_FALLBACK, result);
		assertNotNull(ctxt.getFallbackKbCacheContainer());
		verify(configurationMasterDataService).getFallbackMasterData(KB_ID);
	}

	@Test
	public void testGetGroupNameFromFallbackKbContainerButNotAvailable()
	{
		group.setName(null);
		Mockito.when(configurationMasterDataService.getFallbackMasterData(KB_ID)).thenReturn(null);
		final String result = classUnderTest.getGroupNameFromFallbackKbContainer(ctxt, PRODUCT_ID, GROUP_ID);
		assertNull(result);
		assertNull(ctxt.getFallbackKbCacheContainer());
	}

	@Test
	public void testHandleGroupNameFallbackNoFallback()
	{
		final String result = classUnderTest.handleGroupNameFallback(ctxt, PRODUCT_ID, GROUP_ID);
		assertNotNull(result);
		assertEquals(GROUP_NAME, result);
	}

	@Test
	public void testHandleGroupNameFallback()
	{
		group.setName(null);
		assertNull(ctxt.getFallbackKbCacheContainer());
		final String result = classUnderTest.handleGroupNameFallback(ctxt, PRODUCT_ID, GROUP_ID);
		assertNotNull(result);
		assertEquals(GROUP_NAME_FALLBACK, result);
		assertNotNull(ctxt.getFallbackKbCacheContainer());
		verify(configurationMasterDataService).getFallbackMasterData(KB_ID);
	}

	@Test
	public void testGetGroupNameWithFallbackNoFallback()
	{
		final String result = classUnderTest.getGroupNameWithFallback(ctxt, PRODUCT_ID,
				SapproductconfigruntimecpsConstants.ITEM_TYPE_MARA, GROUP_ID);
		assertNotNull(result);
		assertEquals(GROUP_NAME, result);
	}

	@Test(expected = IllegalStateException.class)
	public void testGetGroupNameWithFallbackNoValidProductId()
	{
		classUnderTest.getGroupNameWithFallback(ctxt, "invalidProductId", SapproductconfigruntimecpsConstants.ITEM_TYPE_MARA,
				GROUP_ID);
	}

	@Test(expected = IllegalStateException.class)
	public void testGetGroupNameWithFallbackNoValidGroupId()
	{
		classUnderTest.getGroupNameWithFallback(ctxt, PRODUCT_ID, SapproductconfigruntimecpsConstants.ITEM_TYPE_MARA,
				"invalidGroupId");
	}

	@Test
	public void testGetGroupNameWithFallback()
	{
		group.setName(null);
		assertNull(ctxt.getFallbackKbCacheContainer());
		final String result = classUnderTest.getGroupNameWithFallback(ctxt, PRODUCT_ID,
				SapproductconfigruntimecpsConstants.ITEM_TYPE_MARA, GROUP_ID);
		assertNotNull(result);
		assertEquals(GROUP_NAME_FALLBACK, result);
		assertNotNull(ctxt.getFallbackKbCacheContainer());
		verify(configurationMasterDataService).getFallbackMasterData(KB_ID);
	}

	@Test
	public void testGetGroupNameWithFallbackFromCtxt()
	{
		group.setName(null);
		ctxt.setFallbackKbCacheContainer(fallbackKbContainer);
		final String result = classUnderTest.getGroupNameWithFallback(ctxt, PRODUCT_ID,
				SapproductconfigruntimecpsConstants.ITEM_TYPE_MARA, GROUP_ID);
		assertNotNull(result);
		assertEquals(GROUP_NAME_FALLBACK, result);
		verify(configurationMasterDataService, times(0)).getFallbackMasterData(KB_ID);
	}

	@Test
	public void testGetGroupNameWithFallbackButNotAvailable()
	{
		group.setName(null);
		Mockito.when(configurationMasterDataService.getFallbackMasterData(KB_ID)).thenReturn(null);
		final String result = classUnderTest.getGroupNameWithFallback(ctxt, PRODUCT_ID,
				SapproductconfigruntimecpsConstants.ITEM_TYPE_MARA, GROUP_ID);
		assertNull(result);
		assertNull(ctxt.getFallbackKbCacheContainer());
	}

	@Test
	public void testGetClassNameFromFallbackKbContainer()
	{
		classContainer.setName(null);
		assertNull(ctxt.getFallbackKbCacheContainer());
		final String result = classUnderTest.getClassNameFromFallbackKbContainer(ctxt, CLASS_ID);
		assertNotNull(result);
		assertEquals(CLASS_NAME_FALLBACK, result);
		assertNotNull(ctxt.getFallbackKbCacheContainer());
		verify(configurationMasterDataService).getFallbackMasterData(KB_ID);
	}

	@Test
	public void testGetClassNameFromFallbackKbContainerButNotAvailable()
	{
		classContainer.setName(null);
		Mockito.when(configurationMasterDataService.getFallbackMasterData(KB_ID)).thenReturn(null);
		final String result = classUnderTest.getClassNameFromFallbackKbContainer(ctxt, CLASS_ID);
		assertNull(result);
		assertNull(ctxt.getFallbackKbCacheContainer());
	}

	@Test
	public void testHandleClassNameFallbackNoFallback()
	{
		final String result = classUnderTest.handleClassNameFallback(ctxt, CLASS_ID);
		assertNotNull(result);
		assertEquals(CLASS_NAME, result);
	}

	@Test
	public void testHandleClassNameFallback()
	{
		classContainer.setName(null);
		assertNull(ctxt.getFallbackKbCacheContainer());
		final String result = classUnderTest.handleClassNameFallback(ctxt, CLASS_ID);
		assertNotNull(result);
		assertEquals(CLASS_NAME_FALLBACK, result);
		assertNotNull(ctxt.getFallbackKbCacheContainer());
		verify(configurationMasterDataService).getFallbackMasterData(KB_ID);
	}

	@Test
	public void testGetGroupNameWithFallbackNoFallbackForClass()
	{
		final String result = classUnderTest.getGroupNameWithFallback(ctxt, CLASS_ID,
				SapproductconfigruntimecpsConstants.ITEM_TYPE_KLAH, GROUP_ID);
		assertNotNull(result);
		assertEquals(CLASS_NAME, result);
	}

	@Test(expected = IllegalStateException.class)
	public void testGetGroupNameWithFallbackNoValidClassIdForClass()
	{
		classUnderTest.getGroupNameWithFallback(ctxt, "noValidClassId", SapproductconfigruntimecpsConstants.ITEM_TYPE_KLAH,
				GROUP_ID);
	}

	@Test
	public void testGetGroupNameWithFallbackForClass()
	{
		classContainer.setName(null);
		assertNull(ctxt.getFallbackKbCacheContainer());
		final String result = classUnderTest.getGroupNameWithFallback(ctxt, CLASS_ID,
				SapproductconfigruntimecpsConstants.ITEM_TYPE_KLAH, GROUP_ID);
		assertNotNull(result);
		assertEquals(CLASS_NAME_FALLBACK, result);
		assertNotNull(ctxt.getFallbackKbCacheContainer());
		verify(configurationMasterDataService).getFallbackMasterData(KB_ID);
	}

	@Test
	public void testGetGroupNameWithFallbackFromCtxtForClass()
	{
		classContainer.setName(null);
		ctxt.setFallbackKbCacheContainer(fallbackKbContainer);
		final String result = classUnderTest.getGroupNameWithFallback(ctxt, CLASS_ID,
				SapproductconfigruntimecpsConstants.ITEM_TYPE_KLAH, GROUP_ID);
		assertNotNull(result);
		assertEquals(CLASS_NAME_FALLBACK, result);
		verify(configurationMasterDataService, times(0)).getFallbackMasterData(KB_ID);
	}

	@Test
	public void testGetGroupNameWithFallbackButNotAvailableForClass()
	{
		classContainer.setName(null);
		Mockito.when(configurationMasterDataService.getFallbackMasterData(KB_ID)).thenReturn(null);
		final String result = classUnderTest.getGroupNameWithFallback(ctxt, CLASS_ID,
				SapproductconfigruntimecpsConstants.ITEM_TYPE_KLAH, GROUP_ID);
		assertNull(result);
		assertNull(ctxt.getFallbackKbCacheContainer());
	}

	@Test
	public void testGetProductNameFromFallbackKbContainer()
	{
		productContainer.setName(null);
		assertNull(ctxt.getFallbackKbCacheContainer());
		final String result = classUnderTest.getProductNameFromFallbackKbContainer(ctxt, PRODUCT_ID);
		assertNotNull(result);
		assertEquals(PRODUCT_NAME_FALLBACK, result);
		assertNotNull(ctxt.getFallbackKbCacheContainer());
		verify(configurationMasterDataService).getFallbackMasterData(KB_ID);
	}

	@Test
	public void testGetProductNameFromFallbackKbContainerButNotAvailable()
	{
		productContainer.setName(null);
		Mockito.when(configurationMasterDataService.getFallbackMasterData(KB_ID)).thenReturn(null);
		final String result = classUnderTest.getProductNameFromFallbackKbContainer(ctxt, PRODUCT_ID);
		assertNull(result);
		assertNull(ctxt.getFallbackKbCacheContainer());
	}

	@Test
	public void testHandleProductNameFallbackNoFallback()
	{
		final String result = classUnderTest.handleProductNameFallback(ctxt, PRODUCT_ID);
		assertNotNull(result);
		assertEquals(PRODUCT_NAME, result);
	}

	@Test
	public void testHandleProductNameFallback()
	{
		productContainer.setName(null);
		assertNull(ctxt.getFallbackKbCacheContainer());
		final String result = classUnderTest.handleProductNameFallback(ctxt, PRODUCT_ID);
		assertNotNull(result);
		assertEquals(PRODUCT_NAME_FALLBACK, result);
		assertNotNull(ctxt.getFallbackKbCacheContainer());
		verify(configurationMasterDataService).getFallbackMasterData(KB_ID);
	}

	@Test
	public void testGetItemNameWithFallbackNoFallback()
	{
		final String result = classUnderTest.getItemNameWithFallback(ctxt, PRODUCT_ID,
				SapproductconfigruntimecpsConstants.ITEM_TYPE_MARA);
		assertNotNull(result);
		assertEquals(PRODUCT_NAME, result);
	}


	@Test(expected = IllegalStateException.class)
	public void testGetItemNameWithFallbackNoValidProductId()
	{
		classUnderTest.getItemNameWithFallback(ctxt, "invalidProductId", SapproductconfigruntimecpsConstants.ITEM_TYPE_MARA);
	}

	@Test
	public void testGetItemNameWithFallback()
	{
		productContainer.setName(null);
		assertNull(ctxt.getFallbackKbCacheContainer());
		final String result = classUnderTest.getItemNameWithFallback(ctxt, PRODUCT_ID,
				SapproductconfigruntimecpsConstants.ITEM_TYPE_MARA);
		assertNotNull(result);
		assertEquals(PRODUCT_NAME_FALLBACK, result);
		assertNotNull(ctxt.getFallbackKbCacheContainer());
		verify(configurationMasterDataService).getFallbackMasterData(KB_ID);
	}

	@Test
	public void testGetProductNameWithFallbackFromCtxt()
	{
		productContainer.setName(null);
		ctxt.setFallbackKbCacheContainer(fallbackKbContainer);
		final String result = classUnderTest.getItemNameWithFallback(ctxt, PRODUCT_ID,
				SapproductconfigruntimecpsConstants.ITEM_TYPE_MARA);
		assertNotNull(result);
		assertEquals(PRODUCT_NAME_FALLBACK, result);
		verify(configurationMasterDataService, times(0)).getFallbackMasterData(KB_ID);
	}

	@Test
	public void testGetItenNameWithFallbackButNotAvailable()
	{
		productContainer.setName(null);
		Mockito.when(configurationMasterDataService.getFallbackMasterData(KB_ID)).thenReturn(null);
		final String result = classUnderTest.getItemNameWithFallback(ctxt, PRODUCT_ID,
				SapproductconfigruntimecpsConstants.ITEM_TYPE_MARA);
		assertNull(result);
		assertNull(ctxt.getFallbackKbCacheContainer());
	}

	@Test(expected = IllegalStateException.class)
	public void testGetItemNameWithFallbackUnknownType()
	{
		classUnderTest.getItemNameWithFallback(ctxt, PRODUCT_ID, "unknownItemType");
	}

	@Test(expected = IllegalStateException.class)
	public void testGetItemNameWithFallbackTypeNull()
	{
		classUnderTest.getItemNameWithFallback(ctxt, PRODUCT_ID, null);
	}

	@Test
	public void testGetItemNameWithFallbackForClass()
	{
		classContainer.setName(null);
		assertNull(ctxt.getFallbackKbCacheContainer());
		final String result = classUnderTest.getItemNameWithFallback(ctxt, CLASS_ID,
				SapproductconfigruntimecpsConstants.ITEM_TYPE_KLAH);
		assertNotNull(result);
		assertEquals(CLASS_NAME_FALLBACK, result);
		assertNotNull(ctxt.getFallbackKbCacheContainer());
		verify(configurationMasterDataService).getFallbackMasterData(KB_ID);
	}

	@Test
	public void testGetItemNameWithFallbackNoFallbackForClass()
	{
		final String result = classUnderTest.getItemNameWithFallback(ctxt, CLASS_ID,
				SapproductconfigruntimecpsConstants.ITEM_TYPE_KLAH);
		assertNotNull(result);
		assertEquals(CLASS_NAME, result);
	}

	@Test
	public void testGetCharacteristicNameWithFallback()
	{
		final String result = classUnderTest.getCharacteristicNameWithFallback(ctxt, CSTIC_ID);
		assertEquals(CSTIC_NAME, result);
		assertNull(ctxt.getFallbackKbCacheContainer());
	}

	@Test
	public void testGetCharacteristicNameWithFallbackUsingFallback()
	{
		csticContainer.setName(null);
		final String result = classUnderTest.getCharacteristicNameWithFallback(ctxt, CSTIC_ID);
		assertEquals(CSTIC_NAME_FALLBACK, result);
		verify(configurationMasterDataService).getFallbackMasterData(KB_ID);
	}

	@Test
	public void testGetCharacteristicNameWithFallbackUsingFallbackFromCtxt()
	{
		csticContainer.setName(null);
		ctxt.setFallbackKbCacheContainer(fallbackKbContainer);
		final String result = classUnderTest.getCharacteristicNameWithFallback(ctxt, CSTIC_ID);
		assertEquals(CSTIC_NAME_FALLBACK, result);
		assertEquals(fallbackKbContainer, ctxt.getFallbackKbCacheContainer());
		verify(configurationMasterDataService, times(0)).getFallbackMasterData(KB_ID);
	}

	@Test
	public void testGetCharacteristicNameWithFallbackUsingFallbackButNotAvailable()
	{
		csticContainer.setName(null);
		Mockito.when(configurationMasterDataService.getFallbackMasterData(KB_ID)).thenReturn(null);
		final String result = classUnderTest.getCharacteristicNameWithFallback(ctxt, CSTIC_ID);
		assertNull(result);
		assertNull(ctxt.getFallbackKbCacheContainer());
	}

	@Test
	public void testGetValuePricingKey()
	{
		fillProducts();
		fillCsticsAndValueSpecific();
		assertEquals(VAR_COND_KEY, classUnderTest.getValuePricingKey(kbContainer, PRODUCT_ID, CSTIC_ID, VALUE_ID));
	}

	@Test
	public void testGetValuePricingKeyNoProduct()
	{
		fillProducts();
		assertNull(VAR_COND_KEY, classUnderTest.getValuePricingKey(kbContainer, "doesNoExist", CSTIC_ID, VALUE_ID));
	}

	@Test
	public void testGetValuePricingKeyNoCstic()
	{
		fillProducts();
		fillCsticsAndValueSpecific();
		assertNull(VAR_COND_KEY, classUnderTest.getValuePricingKey(kbContainer, PRODUCT_ID, "doesNoExist", VALUE_ID));
	}

	@Test
	public void testGetValuePricingKeyNoValue()
	{
		fillProducts();
		fillCsticsAndValueSpecific();
		assertNull(VAR_COND_KEY, classUnderTest.getValuePricingKey(kbContainer, PRODUCT_ID, CSTIC_ID, "doesNoExist"));
	}


}
