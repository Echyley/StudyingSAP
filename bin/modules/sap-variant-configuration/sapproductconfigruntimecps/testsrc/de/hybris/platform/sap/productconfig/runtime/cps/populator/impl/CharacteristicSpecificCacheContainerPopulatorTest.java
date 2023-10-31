/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.cps.populator.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.CPSMasterDataCharacteristicSpecific;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.CPSMasterDataPossibleValueSpecific;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataCharacteristicSpecificContainer;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;


/**
 * Unit test for {@link CharacteristicSpecificCacheContainerPopulator}
 */
@UnitTest
public class CharacteristicSpecificCacheContainerPopulatorTest
{
	private CharacteristicSpecificCacheContainerPopulator classUnderTest;
	private CPSMasterDataCharacteristicSpecific source;
	private CPSMasterDataCharacteristicSpecificContainer target;

	@Before
	public void setup()
	{
		classUnderTest = new CharacteristicSpecificCacheContainerPopulator();
		source = new CPSMasterDataCharacteristicSpecific();
		source.setId("id");
		source.setPossibleValueSpecifics(new ArrayList<>());
		target = new CPSMasterDataCharacteristicSpecificContainer();
	}

	@Test
	public void testPopulateCoreAttributes()
	{
		classUnderTest.populate(source, target);
		assertNotNull(target.getId());
		assertEquals(source.getId(), target.getId());
	}

	@Test
	public void testPopulatePossibleValuesEmpty()
	{
		source.setPossibleValueSpecifics(new ArrayList<>());
		classUnderTest.populate(source, target);
		assertTrue(target.getPossibleValueSpecifics().isEmpty());
	}

	@Test
	public void testPopulatePossibleValuesNull()
	{
		source.setPossibleValueSpecifics(null);
		classUnderTest.populate(source, target);
		assertTrue(target.getPossibleValueSpecifics().isEmpty());
	}

	@Test
	public void testPopulatePossibleValues()
	{
		final List<CPSMasterDataPossibleValueSpecific> possibleValues = new ArrayList<>();
		final CPSMasterDataPossibleValueSpecific possibleValue = new CPSMasterDataPossibleValueSpecific();
		possibleValue.setId("valueId");
		possibleValues.add(possibleValue);
		source.setPossibleValueSpecifics(possibleValues);

		classUnderTest.populate(source, target);
		assertNotNull(target.getPossibleValueSpecifics());
		assertFalse(target.getPossibleValueSpecifics().isEmpty());
		assertEquals(1, target.getPossibleValueSpecifics().size());
		final CPSMasterDataPossibleValueSpecific result = target.getPossibleValueSpecifics().get("valueId");
		assertNotNull(result);
		assertEquals(possibleValue, result);
	}

}
