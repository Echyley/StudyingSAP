/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.integration.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeModel;
import de.hybris.platform.sap.productconfig.services.intf.ClassificationAttributeDescriptionAccess;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class ClassificationAttributeDescriptionAccessTest
{

	private static final String LONG_TEXT_DESCRIPTION = "Long Text Description";
	private ClassificationAttributeDescriptionAccess classUnderTest;

	@Mock
	private ClassificationAttributeModel classificationAttribute;

	@Before
	public void setUp()
	{
		classUnderTest = new ClassificationAttributeDescriptionAccessImpl();
		MockitoAnnotations.initMocks(this);
		Mockito.when(classificationAttribute.getDescription()).thenReturn(LONG_TEXT_DESCRIPTION);
	}

	@Test
	public void testGetDescription()
	{
		assertNotNull(classUnderTest.getDescription(classificationAttribute));
		assertEquals(LONG_TEXT_DESCRIPTION, classUnderTest.getDescription(classificationAttribute));
	}

	@Test
	public void testGetDescriptionWithNull()
	{
		assertNull(classUnderTest.getDescription(null));
	}
}
