/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.interf.model.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConflictingAssumptionModel;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;


/**
 * Unit Test
 */
@UnitTest
public class SolvableConflictModelImplTest
{
	SolvableConflictModelImpl classUnderTest = new SolvableConflictModelImpl();

	@Test
	public void testDescription()
	{
		final String description = "A";
		classUnderTest.setDescription(description);
		assertEquals(description, classUnderTest.getDescription());
	}

	@Test
	public void testId()
	{
		final String id = "1";
		classUnderTest.setId(id);
		assertEquals(id, classUnderTest.getId());
	}

	@Test
	public void testConflictingAssumptions()
	{
		final List<ConflictingAssumptionModel> assumptions = new ArrayList<ConflictingAssumptionModel>();
		classUnderTest.setConflictingAssumptions(assumptions);
		assertEquals(assumptions, classUnderTest.getConflictingAssumptions());
	}

	@Test
	public void testToString()
	{
		final String description = "A long description";
		classUnderTest.setDescription(description);
		assertTrue("We expect the description printed through toString", classUnderTest.toString().indexOf(description) > -1);
	}
}
