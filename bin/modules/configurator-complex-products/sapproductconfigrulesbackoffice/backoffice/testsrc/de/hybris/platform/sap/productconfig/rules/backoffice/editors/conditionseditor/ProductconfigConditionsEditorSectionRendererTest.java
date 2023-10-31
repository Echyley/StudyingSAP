/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.rules.backoffice.editors.conditionseditor;

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.UnitTest;

import org.junit.Before;
import org.junit.Test;


@UnitTest
public class ProductconfigConditionsEditorSectionRendererTest
{

	private ProductconfigConditionsEditorSectionRenderer classUnderTest;

	@Before
	public void setUp()
	{
		classUnderTest = new ProductconfigConditionsEditorSectionRenderer();
	}

	@Test
	public void testGetEditorId()
	{
		final String editorId = classUnderTest.getEditorId();
		assertEquals(ProductconfigConditionsEditorSectionRenderer.PRODUCTCONFIG_CONDITIONS_EDITOR_ID, editorId);
	}
}
