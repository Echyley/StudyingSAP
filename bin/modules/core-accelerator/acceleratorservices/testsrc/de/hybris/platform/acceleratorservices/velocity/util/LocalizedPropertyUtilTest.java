/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorservices.velocity.util;

import de.hybris.bootstrap.annotations.UnitTest;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/acceleratorservices/test/impexgenerator/test-generate-impex-spring.xml"})
@UnitTest
public class LocalizedPropertyUtilTest
{

	@Value("classpath:/acceleratorservices/test/impexgenerator/test-encoding/utf-data.test")
	private Resource res;

	@Test
	public void testLoadUtfData() throws Exception
	{

		if (res.exists() && res.isReadable())
		{
			final Map properties = LocalizedPropertyUtil.loadProperties(res);

			for (final Object row : properties.values())
			{
				Assert.assertEquals("äÄéöÖüÜß", row);
			}
		}
	}

	@Value("classpath:/acceleratorservices/test/impexgenerator/test-encoding/key-data-special-chars.test")
	private Resource resourceSpecialChars;

	@Test
	public void testLoadSpecialCharsData() throws Exception
	{

		if (resourceSpecialChars.exists() && resourceSpecialChars.isReadable())
		{
			final Map properties = LocalizedPropertyUtil.loadProperties(resourceSpecialChars);

			for (final Object row : properties.keySet())
			{
				Assert.assertTrue(((String) row).startsWith("type.attribute with space"));
			}

			for (final Object row : properties.values())
			{
				Assert.assertTrue(((String) row).startsWith("value with space"));
			}
		}
	}


	@Value("classpath:/acceleratorservices/test/impexgenerator/test-encoding/key-data-visible-chars.test")
	private Resource resourceVisibleChars;

	@Test
	public void testLoadVisibleCharsData() throws Exception
	{

		if (resourceVisibleChars.exists() && resourceVisibleChars.isReadable())
		{
			final Map properties = LocalizedPropertyUtil.loadProperties(resourceVisibleChars);

			for (final Object row : properties.keySet())
			{
				Assert.assertTrue(((String) row).startsWith("type.attribute.with.many.space"));
			}

			for (final Object row : properties.values())
			{
				Assert.assertTrue(((String) row).startsWith("value.with.space"));
			}
		}
	}


	@Value("classpath:/acceleratorservices/test/impexgenerator/test-encoding/key-data-specific.test")
	private Resource resourceSpecificData;

	@Test
	public void testLoadSpecificData() throws Exception
	{

		if (resourceSpecificData.exists() && resourceSpecificData.isReadable())
		{
			final Map properties = LocalizedPropertyUtil.loadProperties(resourceSpecificData);

			for (final Object row : properties.keySet())
			{
				Assert.assertTrue(((String) row).startsWith("ImageMapComponent.SunglassesImageMapComponent.imageMapHTML"));
				Assert.assertEquals(
						"\"<area shape=\"\"rect\"\" coords=\"\"10,51,256,231\"\" href=\"\"$storefrontContextRoot/Anon/Sonnenbrille-Anon-Convict-black-fade-gray-gradient/p/300044617\"\" alt=\"\"Sonnenbrille Anon Convict black fade gray gradient\"\" title=\"\"Sonnenbrille Anon Convict black fade gray gradient \"\"/>"
								+ "<area shape=\"\"rect\"\" coords=\"\"274,54,520,234\"\" href=\"\"$storefrontContextRoot/Fox/Sonnenbrille-Fox-The-Median-polished-black-warm-gray/p/300024964\"\" alt=\"\"Sonnenbrille Fox The Median polished black warm gray\"\" title=\"\"Sonnenbrille Fox The Median polished black warm gray\"\"/>"
								+ "<area shape=\"\"rect\"\" coords=\"\"539,50,763,234\"\" href=\"\"$storefrontContextRoot/Quiksilver/Sonnenbrille-Quiksilver-Dinero-black-white-red-gray/p/300045375\"\" alt=\"\"Sonnenbrille Quiksilver Dinero black white red gray\"\" title=\"\"Sonnenbrille Quiksilver Dinero black white red gray\"\"/>"
								+ "<area shape=\"\"rect\"\" coords=\"\"131,250,368,444\"\" href=\"\"$storefrontContextRoot/Roxy/Damen-Sonnenbrille-Roxy-Tee-Dee-Gee-dark-tortoise-brown-gray/p/300047195\"\" alt=\"\"Damen Sonnenbrille Roxy Tee Dee Gee dark tortoise brown gray \"\" title=\"\"Damen Sonnenbrille Roxy Tee Dee Gee dark tortoise brown gray\"\"/>"
								+ "<area shape=\"\"rect\"\" coords=\"\"400,265,649,444\"\" href=\"\"$storefrontContextRoot/Von-Zipper/Shades-Von-Zipper-Fernstein-gold-moss-gradient/p/300015407\"\" alt=\"\"Shades Von Zipper Fernstein gold moss gradient\"\" title=\"\"Shades Von Zipper Fernstein gold moss gradient\"\"/>\"",
						properties.get(row));
			}

		}
	}

	@Value("classpath:/acceleratorservices/test/impexgenerator/test-encoding/japanese-utf-data.test")
	private Resource resourceJapanesProperties;

	@Test
	public void testLoadJapaneseData() throws Exception
	{

		if (resourceJapanesProperties.exists() && resourceJapanesProperties.isReadable())
		{
			final Map properties = LocalizedPropertyUtil.loadProperties(resourceJapanesProperties);

			for (final Object row : properties.keySet())
			{
				Assert.assertTrue("Should  load only property keys ", ((String) row).startsWith("Product"));
			}
		}
	}
}
