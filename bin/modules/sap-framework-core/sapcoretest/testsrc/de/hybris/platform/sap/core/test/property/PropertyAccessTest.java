/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.test.property;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import de.hybris.bootstrap.annotations.UnitTest;


/**
 * Test class for {@link PropertyAccess}.
 */
@UnitTest
public class PropertyAccessTest
{

	/**
	 * Test simple property access.
	 * 
	 * @throws IOException
	 *            IOException
	 */
	@Test
	public void testSimplePropertyAccess() throws IOException
	{

		final PropertyAccess testProperties = new PropertyAccessFactory().createPropertyAccess();
		testProperties.setPropertyPathPrefix(PropertyAccessTestUtil.getCanonicalPathOfExtensionSapCoreTest());
		testProperties.addPropertyFile("testsrc/color1.properties");
		testProperties.addPropertyFile("testsrc/color2.properties");
		testProperties.loadProperties();

		// foreground = red is defined in color1.properties.
		Assert.assertEquals("red", testProperties.getStringProperty("foreground"));
		// background = blue is defined in color1.properties, but background color=green is defined in color2.properties.
		Assert.assertEquals("green", testProperties.getStringProperty("background"));

	}


	/**
	 * Test language property access.
	 * 
	 * @throws IOException
	 *            IOException
	 */
	@Test
	public void testLanguagePropertyAccess() throws IOException
	{

		final PropertyAccess testProperties = new PropertyAccessFactory().createPropertyAccess();
		testProperties.setPropertyPathPrefix(PropertyAccessTestUtil.getCanonicalPathOfExtensionSapCoreTest());
		testProperties.addPropertyFile("testsrc/color.properties");
		testProperties.loadProperties();

		//color.properties defines env.locale=fr which loads property file color_fr.properties additionally.
		Assert.assertEquals("rouge", testProperties.getStringProperty("foreground"));
	}

	/**
	 * Test system specific property access.
	 * 
	 * @throws IOException
	 *            IOException
	 */
	@Test
	public void testSystemPropertyAccess() throws IOException
	{

		final PropertyAccess testProperties = new PropertyAccessFactory().createPropertyAccess();
		testProperties.setPropertyPathPrefix(PropertyAccessTestUtil.getCanonicalPathOfExtensionSapCoreTest());
		testProperties.addPropertyFile("testsrc/color.properties");
		testProperties.loadProperties();

		//color.properties defines env.additional.props.suffix=abc which loads property file color_abc.properties additionally.
		Assert.assertEquals("testvalue_abc", testProperties.getStringProperty("system_specific_setting"));
	}
}
