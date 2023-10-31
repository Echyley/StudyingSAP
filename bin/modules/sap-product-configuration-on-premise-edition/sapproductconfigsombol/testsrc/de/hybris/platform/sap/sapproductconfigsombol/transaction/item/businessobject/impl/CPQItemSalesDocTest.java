/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapproductconfigsombol.transaction.item.businessobject.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.productconfig.runtime.interf.external.Configuration;
import de.hybris.platform.sap.productconfig.runtime.interf.external.impl.ConfigurationImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConfigModelImpl;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;


@UnitTest
public class CPQItemSalesDocTest
{
	private static final String ANOTHER_ID = "another id";

	private static final String TECH_KEY_ID = "tech key id";

	private final CPQItemSalesDoc classUnderTest = new CPQItemSalesDoc();

	private final ConfigModel productConfiguration = new ConfigModelImpl();
	private final Configuration externalConfiguration = new ConfigurationImpl();
	private final Date kbDate = new Date();

	private final boolean productConfigurationDirty = false;

	@Before
	public void setup()
	{
		fillDefaultValues(classUnderTest);
	}

	protected void fillDefaultValues(final CPQItemSalesDoc item)
	{
		item.setProductConfiguration(productConfiguration);
		item.setExternalConfiguration(externalConfiguration);
		item.setProductConfigurationDirty(productConfigurationDirty);
		item.setKbDate(kbDate);
		item.setTechKey(new TechKey(TECH_KEY_ID));
	}

	@Test
	public void testDefaultConstructor()
	{
		final CPQItemSalesDoc result = new CPQItemSalesDoc();
		assertNotNull(result);
		assertNull(result.getProductConfiguration());
		assertNull(result.getExternalConfiguration());
		assertTrue(result.isProductConfigurationDirty());
		assertNull(result.getKbDate());
	}

	@Test
	public void testGetters()
	{
		assertEquals(productConfiguration, classUnderTest.getProductConfiguration());
		assertEquals(externalConfiguration, classUnderTest.getExternalConfiguration());
		assertEquals(productConfigurationDirty, classUnderTest.isProductConfigurationDirty());
		assertEquals(kbDate, classUnderTest.getKbDate());
	}

	@Test
	public void testEquals()
	{
		final CPQItemSalesDoc anotherItemDefault = new CPQItemSalesDoc();
		fillDefaultValues(anotherItemDefault);
		assertEquals(anotherItemDefault, classUnderTest);

		assertNotNull(classUnderTest);
		assertNotEquals(new String(), classUnderTest);

		final CPQItemSalesDoc anotherItem = new CPQItemSalesDoc();
		fillDefaultValues(anotherItem);
		anotherItem.setTechKey(new TechKey(ANOTHER_ID));
		assertNotEquals(anotherItem, classUnderTest);

		final CPQItemSalesDoc anotherItem2 = new CPQItemSalesDoc();
		fillDefaultValues(anotherItem2);
		anotherItem2.setProductConfigurationDirty(true);
		assertNotEquals(anotherItem2, classUnderTest);

		final CPQItemSalesDoc anotherItem3 = new CPQItemSalesDoc();
		fillDefaultValues(anotherItem3);
		final ConfigModelImpl configModel = new ConfigModelImpl();
		configModel.setId("config id");
		anotherItem3.setProductConfiguration(configModel);
		assertNotEquals(anotherItem3, classUnderTest);

		final CPQItemSalesDoc anotherItem4 = new CPQItemSalesDoc();
		fillDefaultValues(anotherItem4);
		anotherItem4.setExternalConfiguration(new ConfigurationImpl());
		assertNotEquals(anotherItem4, classUnderTest);

		final CPQItemSalesDoc anotherItem5 = new CPQItemSalesDoc();
		fillDefaultValues(anotherItem5);
		anotherItem5.setKbDate(new Date(0));
		assertNotEquals(anotherItem5, classUnderTest);
	}

	@Test
	public void testHashCode()
	{
		assertEquals(classUnderTest.hashCode(), classUnderTest.hashCode());

		final CPQItemSalesDoc anotherItemDefault = new CPQItemSalesDoc();
		fillDefaultValues(anotherItemDefault);
		assertEquals(classUnderTest.hashCode(), anotherItemDefault.hashCode());

		final CPQItemSalesDoc anotherItem = new CPQItemSalesDoc();
		fillDefaultValues(anotherItem);
		anotherItem.setTechKey(new TechKey(ANOTHER_ID));
		assertNotEquals(classUnderTest.hashCode(), anotherItem.hashCode());

		final CPQItemSalesDoc anotherItem2 = new CPQItemSalesDoc();
		fillDefaultValues(anotherItem2);
		anotherItem2.setProductConfigurationDirty(true);
		assertNotEquals(classUnderTest.hashCode(), anotherItem2.hashCode());

		final CPQItemSalesDoc anotherItem3 = new CPQItemSalesDoc();
		fillDefaultValues(anotherItem3);
		final ConfigModelImpl configModel = new ConfigModelImpl();
		configModel.setId("config id");
		anotherItem3.setProductConfiguration(configModel);
		assertNotEquals(classUnderTest.hashCode(), anotherItem3.hashCode());

		final CPQItemSalesDoc anotherItem4 = new CPQItemSalesDoc();
		fillDefaultValues(anotherItem4);
		anotherItem4.setExternalConfiguration(new ConfigurationImpl());
		assertNotEquals(classUnderTest.hashCode(), anotherItem4.hashCode());

		final CPQItemSalesDoc anotherItem5 = new CPQItemSalesDoc();
		fillDefaultValues(anotherItem5);
		anotherItem5.setKbDate(new Date(0));
		assertNotEquals(classUnderTest.hashCode(), anotherItem5.hashCode());
	}
}
