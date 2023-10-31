/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.facades.impl;

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.interf.CsticGroup;
import de.hybris.platform.sap.productconfig.runtime.interf.CsticQualifier;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.CsticGroupImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticValueModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.InstanceModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.services.UniqueKeyGenerator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


/**
 * Unit test for {@link UniqueUIKeyGeneratorImpl}
 */
@UnitTest
public class UniqueUIKeyGeneratorImplTest
{

	private static final String GROUP_ID_FOR_INSTANCE = "groupIdForInstance";
	private static final String GROUP_ID_FOR_GROUP = "groupIdForGroup";
	private static final String UI_GROUP_ID = "uiGroupId";
	private static final String INSTANCE_ID = "instanceId";
	private static final String INSTANCE_NAME = "instanceName";
	private static final String GROUP_NAME = "groupName";
	private static final String PREFIX = "prefix";
	private static final String CSTIC_ID = "csticId";
	private static final String CSTIC_UI_KEY = "csticUiKey";
	private static final String INSTANCE_SEPARATOR = "instanceSeparator";
	private static final String KEY_SEPARATOR = "keySeparator";
	private static final String KEY_SEPARATOR_SPLIT = "keySeparatorSplit";

	private UniqueUIKeyGeneratorImpl classUnderTest;

	@Mock
	private UniqueKeyGenerator keyGenerator;

	private final InstanceModel instance = new InstanceModelImpl();
	private final CsticGroup csticGroup = new CsticGroupImpl();
	private final CsticModel csticModel = new CsticModelImpl();
	private final CsticValueModel csticValueModel = new CsticValueModelImpl();
	private final CsticQualifier csticQualifier = new CsticQualifier();


	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new UniqueUIKeyGeneratorImpl();
		classUnderTest.setKeyGenerator(keyGenerator);
	}

	@Test
	public void testGenerateGroupIdForInstance()
	{
		Mockito.when(keyGenerator.generateGroupIdForInstance(instance)).thenReturn(GROUP_ID_FOR_INSTANCE);
		assertEquals(GROUP_ID_FOR_INSTANCE, classUnderTest.generateGroupIdForInstance(instance));
	}

	@Test
	public void testGenerateGroupIdForGroup()
	{
		Mockito.when(keyGenerator.generateGroupIdForGroup(instance, csticGroup)).thenReturn(GROUP_ID_FOR_GROUP);
		assertEquals(GROUP_ID_FOR_GROUP, classUnderTest.generateGroupIdForGroup(instance, csticGroup));
	}

	@Test
	public void testRetrieveInstanceId()
	{
		Mockito.when(keyGenerator.retrieveInstanceId(UI_GROUP_ID)).thenReturn(INSTANCE_ID);
		assertEquals(INSTANCE_ID, classUnderTest.retrieveInstanceId(UI_GROUP_ID));
	}

	@Test
	public void testRetrieveGroupName()
	{
		Mockito.when(keyGenerator.retrieveGroupName(UI_GROUP_ID)).thenReturn(GROUP_NAME);
		assertEquals(GROUP_NAME, classUnderTest.retrieveGroupName(UI_GROUP_ID));
	}

	@Test
	public void testGenerateCsticId()
	{
		Mockito.when(keyGenerator.generateCsticId(csticModel, csticValueModel, PREFIX)).thenReturn(CSTIC_ID);
		assertEquals(CSTIC_ID, classUnderTest.generateCsticId(csticModel, csticValueModel, PREFIX));
	}

	@Test
	public void testSplitId()
	{
		Mockito.when(keyGenerator.splitId(CSTIC_UI_KEY)).thenReturn(csticQualifier);
		assertEquals(csticQualifier, classUnderTest.splitId(CSTIC_UI_KEY));
	}

	@Test
	public void testGenerateId()
	{
		Mockito.when(keyGenerator.generateId(csticQualifier)).thenReturn(CSTIC_UI_KEY);
		assertEquals(CSTIC_UI_KEY, classUnderTest.generateId(csticQualifier));
	}

	@Test
	public void testExtractInstanceNameFromGroupId()
	{
		Mockito.when(keyGenerator.extractInstanceNameFromGroupId(UI_GROUP_ID)).thenReturn(INSTANCE_NAME);
		assertEquals(INSTANCE_NAME, classUnderTest.extractInstanceNameFromGroupId(UI_GROUP_ID));
	}

	@Test
	public void testGetInstanceSeparator()
	{
		Mockito.when(keyGenerator.getInstanceSeparator()).thenReturn(INSTANCE_SEPARATOR);
		assertEquals(INSTANCE_SEPARATOR, classUnderTest.getInstanceSeparator());
	}

	@Test
	public void testGetKeySeparator()
	{
		Mockito.when(keyGenerator.getKeySeparator()).thenReturn(KEY_SEPARATOR);
		assertEquals(KEY_SEPARATOR, classUnderTest.getKeySeparator());
	}

	@Test
	public void testGetKeySeparatorSplit()
	{
		Mockito.when(keyGenerator.getKeySeparatorSplit()).thenReturn(KEY_SEPARATOR_SPLIT);
		assertEquals(KEY_SEPARATOR_SPLIT, classUnderTest.getKeySeparatorSplit());
	}

}
