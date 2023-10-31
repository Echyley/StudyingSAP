/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.interf.services.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.UnitTest;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;


@UnitTest
public class ClassificationSystemCPQAttributesContainerTest
{
	private ClassificationSystemCPQAttributesContainer classUnderTest;

	@Before
	public void setUp()
	{
		classUnderTest = new ClassificationSystemCPQAttributesContainer("code", "name", "description", Collections.emptyMap(),
				Collections.emptyMap(), Collections.emptyList(), Collections.emptyMap());
	}

	@Test
	public void testEqualsHashCodeSameObject()
	{
		assertEquals(classUnderTest, classUnderTest);
		assertEquals(classUnderTest.hashCode(), classUnderTest.hashCode());
	}

	@Test
	public void testEqualsHashSameDate()
	{
		final ClassificationSystemCPQAttributesContainer other = new ClassificationSystemCPQAttributesContainer("code", "name",
				"description", Collections.emptyMap(), Collections.emptyMap(), Collections.emptyList(), Collections.emptyMap());
		assertEquals(classUnderTest, other);
		assertEquals(classUnderTest.hashCode(), other.hashCode());
	}


	@Test
	public void testEqualsHashNotSameCode()
	{
		final ClassificationSystemCPQAttributesContainer other = new ClassificationSystemCPQAttributesContainer("otherCode", "name",
				"description", Collections.emptyMap(), Collections.emptyMap(), Collections.emptyList(), Collections.emptyMap());
		assertNotEquals(classUnderTest, other);
		assertNotEquals(classUnderTest.hashCode(), other.hashCode());
	}

	@Test
	public void testEqualsHashNullObj()
	{
		assertNotEquals(ClassificationSystemCPQAttributesContainer.NULL_OBJ, classUnderTest);
		assertNotEquals(ClassificationSystemCPQAttributesContainer.NULL_OBJ.hashCode(), classUnderTest.hashCode());
	}

	@Test
	public void testNullEqualsCases()
	{
		assertNotNull(classUnderTest);
		assertNotEquals(new DummyClassificationSystemCPQAttributesContainer(), classUnderTest);
	}

	@Test
	public void testEqualsCodeCases()
	{
		classUnderTest = new ClassificationSystemCPQAttributesContainer(null, "name", "description", Collections.emptyMap(),
				Collections.emptyMap(), Collections.emptyList(), Collections.emptyMap());

		ClassificationSystemCPQAttributesContainer other = new ClassificationSystemCPQAttributesContainer("code", "name",
				"description", Collections.emptyMap(), Collections.emptyMap(), Collections.emptyList(), Collections.emptyMap());
		assertNotEquals(classUnderTest, other);

		other = new ClassificationSystemCPQAttributesContainer(null, "name", "description", Collections.emptyMap(),
				Collections.emptyMap(), Collections.emptyList(), Collections.emptyMap());
		assertEquals(classUnderTest, other);
	}


	private static class DummyClassificationSystemCPQAttributesContainer extends ClassificationSystemCPQAttributesContainer
	{
		public DummyClassificationSystemCPQAttributesContainer()
		{
			super("test1", "test2", "test3", Collections.emptyMap(), Collections.emptyMap(), Collections.emptyList(),
					Collections.emptyMap());
		}
	}
}
