/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.cps.populator.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.cps.masterdata.service.MasterDataContainerResolver;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataKnowledgeBaseContainer;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSCharacteristic;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSConfiguration;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSItem;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSPossibleValue;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticValueModelImpl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


/**
 * Unit test for {@link PossibleValueMasterDataPopulator}
 */
@UnitTest
public class PossibleValueMasterDataPopulatorTest
{
	private static final String kbId = "99";
	private static final String characteristicId = "CSTIC_ID";
	private static final String valueString = "VALUE_ID";
	private static final String valueName = "Language dependent value name";

	@Mock
	private MasterDataContainerResolver resolver;

	private PossibleValueMasterDataPopulator classUnderTest;
	private CPSMasterDataKnowledgeBaseContainer kbContainer;
	private MasterDataContext ctxt;
	private CPSPossibleValue source;
	private CsticValueModel target;

	@Before
	public void initialize()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new PossibleValueMasterDataPopulator();

		final CPSConfiguration config = new CPSConfiguration();
		config.setKbId(kbId);
		final CPSItem item = new CPSItem();
		item.setParentConfiguration(config);
		final CPSCharacteristic cstic = new CPSCharacteristic();
		cstic.setId(characteristicId);
		cstic.setParentItem(item);

		source = new CPSPossibleValue();
		source.setValueLow(valueString);
		source.setParentCharacteristic(cstic);

		target = new CsticValueModelImpl();
		target.setNumeric(true);

		kbContainer = new CPSMasterDataKnowledgeBaseContainer();
		ctxt = new MasterDataContext();
		ctxt.setKbCacheContainer(kbContainer);
		classUnderTest.setMasterDataResolver(resolver);

		Mockito.when(resolver.getValueName(kbContainer, characteristicId, valueString)).thenReturn(valueName);
		Mockito.when(resolver.getValueNameWithFallback(ctxt, characteristicId, valueString)).thenReturn(valueName);
	}

	@Test
	public void testMasterDataService()
	{
		assertSame(resolver, classUnderTest.getMasterDataResolver());
	}

	@Test
	public void testPopulate()
	{
		classUnderTest.populate(source, target, ctxt);
		assertEquals(valueName, target.getLanguageDependentName());
	}

	@Test
	public void testPopulateNoValueIdProvided()
	{
		source.setValueLow(null);
		classUnderTest.populate(source, target, ctxt);
		assertNull(valueName, target.getLanguageDependentName());
	}

}
