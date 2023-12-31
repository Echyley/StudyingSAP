/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.configuration.populators;

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.store.BaseStoreModel;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;


/**
 * Test for GenericModel2DtoPopulator.
 */
@UnitTest
public class SAPBaseStoreConfigurationMappingPopulatorTest
{

	private final SAPBaseStoreConfigurationMappingPopulator classUnderTest = new SAPBaseStoreConfigurationMappingPopulator();

	private final SAPConfigurationModel sapConfigurationModel = new SAPConfigurationModel();
	private final BaseStoreModel baseStoreModel = new BaseStoreModel();
	private final Map<String, Object> propertyMap = new HashMap<String, Object>();

	/**
	 * Test setup.
	 */
	@Before
	public void setUp()
	{
		sapConfigurationModel.setCore_name("myBaseStoreConfigurationName");
		baseStoreModel.setUid("myBaseStoreUid");
		baseStoreModel.setSAPConfiguration(sapConfigurationModel);
	}

	/**
	 * Tests if all properties with same names are copied.
	 */
	@Test
	public void testPopulateMapping()
	{
		classUnderTest.populate(baseStoreModel, propertyMap);
		assertEquals("myBaseStoreUid", propertyMap.get("baseStoreUid"));
		assertEquals("myBaseStoreConfigurationName", propertyMap.get("baseStoreConfigurationName"));
	}

}
