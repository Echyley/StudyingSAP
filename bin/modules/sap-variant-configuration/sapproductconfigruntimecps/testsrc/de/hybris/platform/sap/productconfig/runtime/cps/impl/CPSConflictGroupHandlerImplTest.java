/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.cps.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationEngineException;
import de.hybris.platform.sap.productconfig.runtime.interf.PricingEngineException;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConflictingAssumptionModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticGroupModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.SolvableConflictModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConfigModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConflictingAssumptionModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticGroupModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.InstanceModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.SolvableConflictModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.services.UniqueKeyGenerator;
import de.hybris.platform.sap.productconfig.runtime.interf.services.impl.UniqueKeyGeneratorImpl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for {@link CPSConflictGroupHandlerImpl}
 */
@UnitTest
public class CPSConflictGroupHandlerImplTest
{
	private static final String ROOT_INSTANCE_ID = "RootInstanceId";
	private static final String ROOT_INSTANCE_NAME = "RootInstanceName";
	private static final String SUB_INSTANCE_ID_1 = "SubInstanceId1";
	private static final String SUB_INSTANCE_NAME_1 = "SubInstanceName1";
	private static final String SUB_INSTANCE_ID_2 = "SubInstanceId2";
	private static final String SUB_INSTANCE_NAME_2 = "SubInstanceName2";
	private static final String GROUP_NAME_1 = "GroupName1";
	private static final String GROUP_NAME_2 = "GroupName2";
	private static final String GROUP_NAME_SI_1 = "GroupNameSI1";
	private static final String GROUP_NAME_SI_2 = "GroupNameSI2";
	private static final String UNIQUE_GROUP_ID_1 = "RootInstanceId-RootInstanceName@GroupName1";
	private static final String CSTIC_NAME_X = "CsticNameX";
	private static final String CSTIC_NAME_Y = "CsticNameY";
	private static final String CSTIC_NAME_Z = "CsticNameZ";
	private static final String CSTIC_NAME_1_1 = "CsticName11";
	private static final String CSTIC_NAME_1_2 = "CsticName12";
	private static final String CSTIC_NAME_2_1 = "CsticName21";
	private static final String CSTIC_NAME_2_2 = "CsticName22";

	private CPSConflictGroupHandlerImpl classUnderTest;

	UniqueKeyGenerator keyGenerator = new UniqueKeyGeneratorImpl();

	private ConfigModel configModel;
	private InstanceModel rootInstance;
	private InstanceModel subInstance1;
	private InstanceModel subInstance2;

	@Before
	public void setup() throws PricingEngineException, ConfigurationEngineException
	{
		prepareTestData();
		classUnderTest = new CPSConflictGroupHandlerImpl();
		classUnderTest.setKeyGenerator(keyGenerator);
	}

	@Test
	public void testFindInstanceRoot()
	{
		final InstanceModel foundIntance = classUnderTest.findInstance(ROOT_INSTANCE_ID, rootInstance);
		assertNotNull(foundIntance);
		assertEquals(ROOT_INSTANCE_ID, foundIntance.getId());
		assertEquals(ROOT_INSTANCE_NAME, foundIntance.getName());
	}

	@Test
	public void testFindInstanceSub()
	{
		final InstanceModel foundIntance = classUnderTest.findInstance(SUB_INSTANCE_ID_1, rootInstance);
		assertNotNull(foundIntance);
		assertEquals(SUB_INSTANCE_ID_1, foundIntance.getId());
		assertEquals(SUB_INSTANCE_NAME_1, foundIntance.getName());
	}

	@Test
	public void testFindInstanceNotFound()
	{
		assertNull(classUnderTest.findInstance("Wrong Id", rootInstance));
	}

	@Test
	public void testDetermineGroupsInvolvedInAssumtion()
	{
		final List<String> groupsInAssumption = classUnderTest.determineGroupsInvolvedInAssumption(rootInstance, CSTIC_NAME_X);
		assertNotNull(groupsInAssumption);
		assertEquals(2, groupsInAssumption.size());
		assertEquals(GROUP_NAME_1, groupsInAssumption.get(0));
		assertEquals(GROUP_NAME_2, groupsInAssumption.get(1));
	}

	@Test
	public void testAddGroupToResultMap()
	{
		final Map<String, List<String>> conflictGroups = new HashMap<>();
		classUnderTest.addGroupToResultMap(GROUP_NAME_1, rootInstance, configModel, conflictGroups);
		assertNotNull(conflictGroups);
		assertEquals(1, conflictGroups.size());
		assertTrue(conflictGroups.keySet().contains(ROOT_INSTANCE_ID));
		assertEquals(GROUP_NAME_1, conflictGroups.get(ROOT_INSTANCE_ID).get(0));
	}

	@Test
	public void testProcessConflictingAssumption()
	{
		final Map<String, List<String>> conflictGroups = new HashMap<>();
		final ConflictingAssumptionModel conflictingAssumption = new ConflictingAssumptionModelImpl();
		conflictingAssumption.setInstanceId(ROOT_INSTANCE_ID);
		conflictingAssumption.setCsticName(CSTIC_NAME_X);
		classUnderTest.processConflictingAssumption(conflictingAssumption, configModel, conflictGroups);
		assertNotNull(conflictGroups);
		assertEquals(1, conflictGroups.size());
		assertTrue(conflictGroups.keySet().contains(ROOT_INSTANCE_ID));
		assertEquals(2, conflictGroups.get(ROOT_INSTANCE_ID).size());
		assertEquals(GROUP_NAME_1, conflictGroups.get(ROOT_INSTANCE_ID).get(0));
		assertEquals(GROUP_NAME_2, conflictGroups.get(ROOT_INSTANCE_ID).get(1));
	}

	@Test
	public void testRetrieveNotCachedUniqueGroupIds()
	{
		final Map<String, List<String>> conflictGroups = classUnderTest.retrieveNotCachedUniqueGroupIds(configModel);
		assertNotNull(conflictGroups);
		assertEquals(2, conflictGroups.size());
		assertTrue(conflictGroups.keySet().contains(ROOT_INSTANCE_ID));
		assertTrue(conflictGroups.keySet().contains(SUB_INSTANCE_ID_1));
		assertFalse(conflictGroups.keySet().contains(SUB_INSTANCE_ID_2));
		assertEquals(2, conflictGroups.get(ROOT_INSTANCE_ID).size());
		assertEquals(GROUP_NAME_1, conflictGroups.get(ROOT_INSTANCE_ID).get(0));
		assertEquals(GROUP_NAME_2, conflictGroups.get(ROOT_INSTANCE_ID).get(1));
		assertEquals(1, conflictGroups.get(SUB_INSTANCE_ID_1).size());
		assertEquals(GROUP_NAME_SI_1, conflictGroups.get(SUB_INSTANCE_ID_1).get(0));
	}

	@Test
	public void testGenerateUniqueGroupName()
	{
		assertEquals(UNIQUE_GROUP_ID_1, classUnderTest.generateUniqueGroupName(ROOT_INSTANCE_ID, GROUP_NAME_1, configModel));
	}

	protected void prepareTestData()
	{
		configModel = new ConfigModelImpl();
		rootInstance = new InstanceModelImpl();
		configModel.setRootInstance(rootInstance);
		rootInstance.setId(ROOT_INSTANCE_ID);
		rootInstance.setName(ROOT_INSTANCE_NAME);

		subInstance1 = new InstanceModelImpl();
		subInstance1.setId(SUB_INSTANCE_ID_1);
		subInstance1.setName(SUB_INSTANCE_NAME_1);
		subInstance2 = new InstanceModelImpl();
		subInstance2.setId(SUB_INSTANCE_ID_2);
		subInstance2.setName(SUB_INSTANCE_NAME_2);
		rootInstance.setSubInstances(Arrays.asList(subInstance1, subInstance2));

		final CsticGroupModel group1 = new CsticGroupModelImpl();
		group1.setName(GROUP_NAME_1);
		group1.setCsticNames(Arrays.asList(CSTIC_NAME_X, CSTIC_NAME_1_1, CSTIC_NAME_1_2));
		final CsticGroupModel group2 = new CsticGroupModelImpl();
		group2.setName(GROUP_NAME_2);
		group2.setCsticNames(Arrays.asList(CSTIC_NAME_X, CSTIC_NAME_2_1, CSTIC_NAME_2_2));
		rootInstance.setCsticGroups(Arrays.asList(group1, group2));

		final ConflictingAssumptionModel conflictingAssumption11 = new ConflictingAssumptionModelImpl();
		conflictingAssumption11.setInstanceId(ROOT_INSTANCE_ID);
		conflictingAssumption11.setCsticName(CSTIC_NAME_X);
		final SolvableConflictModel solvableConflict1 = new SolvableConflictModelImpl();
		solvableConflict1.setConflictingAssumptions(Arrays.asList(conflictingAssumption11));

		final ConflictingAssumptionModel conflictingAssumption21 = new ConflictingAssumptionModelImpl();
		conflictingAssumption21.setInstanceId(SUB_INSTANCE_ID_1);
		conflictingAssumption21.setCsticName(CSTIC_NAME_Y);
		final ConflictingAssumptionModel conflictingAssumption22 = new ConflictingAssumptionModelImpl();
		conflictingAssumption22.setInstanceId(SUB_INSTANCE_ID_2);
		conflictingAssumption22.setCsticName(CSTIC_NAME_Z);
		final SolvableConflictModel solvableConflict2 = new SolvableConflictModelImpl();
		solvableConflict2.setConflictingAssumptions(Arrays.asList(conflictingAssumption21, conflictingAssumption22));
		configModel.setSolvableConflicts(Arrays.asList(solvableConflict1, solvableConflict2));

		final CsticGroupModel groupSI1 = new CsticGroupModelImpl();
		groupSI1.setName(GROUP_NAME_SI_1);
		groupSI1.setCsticNames(Arrays.asList(CSTIC_NAME_Y));
		final CsticGroupModel groupSI2 = new CsticGroupModelImpl();
		groupSI2.setName(GROUP_NAME_SI_2);
		groupSI2.setCsticNames(Arrays.asList(CSTIC_NAME_Z));
		subInstance1.setCsticGroups(Arrays.asList(groupSI1));
		subInstance2.setCsticGroups(Arrays.asList(groupSI2));
		configModel.getGroupsReadCompletely().add("SubInstanceId2-SubInstanceName2@GroupNameSI2");
	}

}
