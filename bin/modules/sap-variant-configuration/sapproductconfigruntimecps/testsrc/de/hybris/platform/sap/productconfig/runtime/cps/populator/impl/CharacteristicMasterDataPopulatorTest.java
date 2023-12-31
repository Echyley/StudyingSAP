/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.cps.populator.impl;

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.cps.masterdata.service.ConfigurationMasterDataService;
import de.hybris.platform.sap.productconfig.runtime.cps.masterdata.service.MasterDataContainerResolver;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataCharacteristicContainer;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataKnowledgeBaseContainer;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.common.CPSMasterDataKBHeaderInfo;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSCharacteristic;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSConfiguration;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSItem;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.TextConverterBaseImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticModelImpl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;



/**
 * Unit test for {@link CharacteristicMasterDataPopulator}
 */
@UnitTest
public class CharacteristicMasterDataPopulatorTest
{
	private static final String KB_ID = "99";
	private static final String CSTIC_ID = "CSTIC_ID";
	private static final String CSTIC_ID_NUMERIC = "CSTIC_ID_NUMERIC";
	private static final String LANG_DEP_DESCRIPTION = "Language dependent LANG_DEP_DESCRIPTION";
	private static final String LANG_DEP_DESCRIPTION_FALLBACK = "Fallback Language dependent LANG_DEP_DESCRIPTION";
	private static final String LONG_TEXT = "Long text TEXT";
	private static final String DATE_TYPE = "date";
	private static final String STRING_TYPE = "string";
	private static final String INT_TYPE = "int";
	private static final String FLOAT_TYPE = "float";
	private static final int LENGTH = 3;
	private static final int NUMBER_DECIMALS = 1;
	private static final int MAX_LENGTH = 3;

	CharacteristicMasterDataPopulator classUnderTest = new CharacteristicMasterDataPopulator();
	@Mock
	private MasterDataContainerResolver resolver;
	@Mock
	private ConfigurationMasterDataService configurationMasterDataService;

	private CPSMasterDataKnowledgeBaseContainer kbContainer;
	private CPSMasterDataKnowledgeBaseContainer fallbackKbContainer;
	private MasterDataContext ctxt;
	private CPSCharacteristic source;
	private CsticModel target;

	private CPSMasterDataCharacteristicContainer characteristicMasterData;
	private CPSMasterDataCharacteristicContainer characteristicMasterDataNumeric;
	private CPSMasterDataCharacteristicContainer characteristicMasterDataFallback;
	private CPSMasterDataKBHeaderInfo headerInfo;
	private CPSCharacteristic sourceNumeric;

	@Before
	public void initialize()
	{
		characteristicMasterData = new CPSMasterDataCharacteristicContainer();
		characteristicMasterData.setName(LANG_DEP_DESCRIPTION);
		characteristicMasterData.setDescription(LONG_TEXT);
		characteristicMasterDataNumeric = new CPSMasterDataCharacteristicContainer();
		characteristicMasterDataNumeric.setType(FLOAT_TYPE);
		characteristicMasterDataNumeric.setNumberDecimals(Integer.valueOf(NUMBER_DECIMALS));
		characteristicMasterDataNumeric.setLength(Integer.valueOf(MAX_LENGTH));

		characteristicMasterDataFallback = new CPSMasterDataCharacteristicContainer();
		characteristicMasterDataFallback.setName(LANG_DEP_DESCRIPTION_FALLBACK);
		fallbackKbContainer = new CPSMasterDataKnowledgeBaseContainer();

		MockitoAnnotations.initMocks(this);

		kbContainer = new CPSMasterDataKnowledgeBaseContainer();
		fillHeader();
		ctxt = new MasterDataContext();
		ctxt.setKbCacheContainer(kbContainer);
		classUnderTest.setMasterDataResolver(resolver);
		classUnderTest.setConfigurationMasterDataService(configurationMasterDataService);
		classUnderTest.setTextConverterBase(new TextConverterBaseImpl());

		Mockito.when(resolver.getCharacteristic(kbContainer, CSTIC_ID)).thenReturn(characteristicMasterData);
		Mockito.when(resolver.getCharacteristic(fallbackKbContainer, CSTIC_ID)).thenReturn(characteristicMasterDataFallback);
		Mockito.when(resolver.getCharacteristicNameWithFallback(ctxt, CSTIC_ID)).thenReturn(LANG_DEP_DESCRIPTION);
		Mockito.when(configurationMasterDataService.getFallbackMasterData(KB_ID)).thenReturn(fallbackKbContainer);
		Mockito.when(resolver.getFallbackKbCacheContainer(ctxt)).thenReturn(fallbackKbContainer);



		final CPSConfiguration config = new CPSConfiguration();
		config.setKbId(KB_ID);
		final CPSItem item = new CPSItem();
		item.setParentConfiguration(config);
		source = new CPSCharacteristic();
		source.setId(CSTIC_ID);
		source.setParentItem(item);

		target = new CsticModelImpl();

		final CPSConfiguration configNum = new CPSConfiguration();
		configNum.setKbId(KB_ID);
		final CPSItem itemNum = new CPSItem();
		itemNum.setParentConfiguration(configNum);
		sourceNumeric = new CPSCharacteristic();
		sourceNumeric.setId(CSTIC_ID_NUMERIC);
		sourceNumeric.setParentItem(itemNum);

		Mockito.when(resolver.getCharacteristic(kbContainer, CSTIC_ID_NUMERIC)).thenReturn(characteristicMasterDataNumeric);
	}


	protected void fillHeader()
	{
		headerInfo = new CPSMasterDataKBHeaderInfo();
		headerInfo.setId(Integer.valueOf(KB_ID));
		kbContainer.setHeaderInfo(headerInfo);
	}

	@Test
	public void testresolver()
	{
		assertEquals(resolver, classUnderTest.getMasterDataResolver());
	}

	@Test
	public void testPopulate()
	{
		classUnderTest.populate(source, target, ctxt);
		assertEquals(LANG_DEP_DESCRIPTION, target.getLanguageDependentName());
		assertEquals(LONG_TEXT, target.getLongText());
	}


	@Test
	public void testPopulateLengthNothingAssigned()
	{
		characteristicMasterData.setLength(null);
		classUnderTest.populate(source, target, ctxt);
		assertEquals(0, target.getStaticDomainLength());
	}

	@Test
	public void testPopulateDecimals()
	{
		characteristicMasterData.setNumberDecimals(Integer.valueOf(LENGTH));
		classUnderTest.populate(source, target, ctxt);
		assertEquals(LENGTH, target.getNumberScale());
	}

	@Test
	public void testPopulateValueType()
	{
		classUnderTest.populate(sourceNumeric, target, ctxt);
		assertEquals(CsticModel.TYPE_FLOAT, target.getValueType());
	}

	@Test
	public void testPopulateValueTypeNothingInMasterData()
	{
		classUnderTest.populate(source, target, ctxt);
		assertEquals(CsticModel.TYPE_STRING, target.getValueType());
	}

	@Test
	public void testPopulateValueTypeInt()
	{
		characteristicMasterDataNumeric.setType("int");
		classUnderTest.populate(sourceNumeric, target, ctxt);
		assertEquals(CsticModel.TYPE_INTEGER, target.getValueType());
	}

	@Test
	public void testPopulateDecimalsNothingAssigned()
	{
		characteristicMasterData.setNumberDecimals(null);
		classUnderTest.populate(source, target, ctxt);
		assertEquals(0, target.getNumberScale());
	}

	@Test
	public void testPopulateMaxFractions()
	{
		classUnderTest.populate(sourceNumeric, target, ctxt);
		assertEquals(NUMBER_DECIMALS, target.getNumberScale());
	}

	@Test
	public void testPopulateMaxLength()
	{
		classUnderTest.populate(sourceNumeric, target, ctxt);
		assertEquals(MAX_LENGTH, target.getTypeLength());
	}

	@Test
	public void testNumberWithExponentEntryFieldMask()
	{
		final String exponentFieldMask = "___.____________E+SS";
		characteristicMasterDataNumeric.setEntryFieldMask(exponentFieldMask);
		classUnderTest.populate(sourceNumeric, target, ctxt);
		assertEquals(exponentFieldMask, target.getEntryFieldMask());
	}

	@Test
	public void testGetValueType()
	{
		assertEquals(CsticModel.TYPE_DATE, classUnderTest.getValueType(DATE_TYPE));
		assertEquals(CsticModel.TYPE_FLOAT, classUnderTest.getValueType(FLOAT_TYPE));
		assertEquals(CsticModel.TYPE_STRING, classUnderTest.getValueType(STRING_TYPE));
		assertEquals(CsticModel.TYPE_INTEGER, classUnderTest.getValueType(INT_TYPE));
		assertEquals(CsticModel.TYPE_STRING, classUnderTest.getValueType("UNKNOWN"));
	}

	@Test
	public void testDateValueType()
	{
		characteristicMasterData.setType(DATE_TYPE);
		classUnderTest.populate(source, target, ctxt);
		assertEquals(CsticModel.TYPE_DATE, target.getValueType());
	}

}
