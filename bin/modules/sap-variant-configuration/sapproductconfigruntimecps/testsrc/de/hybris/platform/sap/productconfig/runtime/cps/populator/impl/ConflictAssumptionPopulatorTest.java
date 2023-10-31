/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.cps.populator.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSChoice;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSValue;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConflictingAssumptionModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConflictingAssumptionModelImpl;

import org.junit.Before;
import org.junit.Test;


/**
 * Unit test for {@link ConflictAssumptionPopulator}
 */
@UnitTest
public class ConflictAssumptionPopulatorTest
{
	private static final String ITEM_ID = "1";
	private static final String CSTIC_ID = "CSTIC";
	private static final String CSTIC_VALUE = "Value";

	private ConflictAssumptionPopulator classUnderTest;

	private CPSChoice source;
	private ConflictingAssumptionModel target;

	@Before
	public void setUp()
	{
		classUnderTest = new ConflictAssumptionPopulator();

		source = new CPSChoice();
		source.setItemId(ITEM_ID);
		source.setCharacteristicId(CSTIC_ID);
		final CPSValue value = new CPSValue();
		value.setValue(CSTIC_VALUE);
		source.setValue(value);

		target = new ConflictingAssumptionModelImpl();
	}

	@Test
	public void testPopulateConflictAssumptionModel()
	{
		classUnderTest.populate(source, target);

		assertNull(target.getId());
		assertEquals(ITEM_ID, target.getInstanceId());
		assertEquals(CSTIC_ID, target.getCsticName());
		assertEquals(CSTIC_VALUE, target.getValueName());
	}

}
