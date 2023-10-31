/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.ssc.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConflictingAssumptionModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.SolvableConflictModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConfigModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConflictingAssumptionModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticValueModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.SolvableConflictModelImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.sap.custdev.projects.fbs.slc.cfg.IConfigSession;
import com.sap.custdev.projects.fbs.slc.cfg.client.IAssumptions;
import com.sap.custdev.projects.fbs.slc.cfg.client.IConflictingAssumptionsContainer;
import com.sap.custdev.projects.fbs.slc.cfg.client.ITextDescription;
import com.sap.custdev.projects.fbs.slc.cfg.exception.IpcCommandException;


/**
 * Tests
 */
@UnitTest
public class SolvableConflictAdapterImplTest
{
	public static final String WE_EXPECT_CONFLICTS = "We expect conflicts";
	public static final String WE_EXPECT_ONE_CONFLICT = "We expect one conflict";
	public static final String WE_EXPECT_ONE_ASSUMPTION = "We expect one assumption";
	SolvableConflictAdapterImpl classUnderTest = new SolvableConflictAdapterImpl();

	String configId = "1";

	String instanceId = "2";

	@Mock
	IConfigSession configSession;

	@Mock
	IConflictingAssumptionsContainer solvableConflict;

	@Mock
	private IAssumptions assumption;


	private IConflictingAssumptionsContainer[] solvableConflictcontainer;
	private static final IConflictingAssumptionsContainer[] solvableConflictcontainerNull = null;
	private IConflictingAssumptionsContainer[] solvableConflictcontainerEmpty;

	private static final String CONFLICT_NAME = "Name";

	private IAssumptions[] assumptions;
	private IAssumptions[] assumptionsEmpty;

	private static final String CSTIC_NAME = "cstic";
	private static final String VALUE_NAME = "value";

	private ConfigModel configModel;

	private SolvableConflictModel conflict;

	private static String assumptionId = "123";

	private CsticModel csticModel;

	private final ITextDescription textDescription = new ITextDescription()
	{

		@Override
		public void setTextLineId(final Integer arg0)
		{//
		}

		@Override
		public void setTextLine(final String arg0)
		{
			//
		}

		@Override
		public void setTextFormat(final String arg0)
		{
			//
		}

		@Override
		public Integer getTextLineId()
		{
			return Integer.valueOf(7);
		}

		@Override
		public String getTextLine()
		{
			return CONFLICT_NAME;
		}

		@Override
		public String getTextFormat()
		{
			return "X";
		}
	};



	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
		solvableConflictcontainer = new IConflictingAssumptionsContainer[]
		{ solvableConflict };
		assumptions = new IAssumptions[]
		{ assumption };
		assumptionsEmpty = new IAssumptions[] {};
		solvableConflictcontainerEmpty = new IConflictingAssumptionsContainer[] {};
		final ITextDescription[] description =
		{ textDescription };
		Mockito.when(solvableConflict.getConflictLongText()).thenReturn(description);
		Mockito.when(assumption.getObservableName()).thenReturn(CSTIC_NAME);
		Mockito.when(assumption.getObservableValueName()).thenReturn(VALUE_NAME);
		Mockito.when(assumption.getInstanceId()).thenReturn(instanceId);
		Mockito.when(assumption.getAsumptionId()).thenReturn(assumptionId);

		configModel = new ConfigModelImpl();
		classUnderTest.setTextConverter(new TextConverterImpl());
	}


	private List<SolvableConflictModel> performTransfer(final ConfigModel configModel)
	{
		classUnderTest.transferSolvableConflicts(configSession, configId, configModel);
		return configModel.getSolvableConflicts();
	}

	@Test
	public void testTransferSolvableConflictsNullConflicts() throws IpcCommandException
	{
		configModel = new ConfigModelImpl();
		Mockito.when(configSession.getConflictingAssumptions(configId)).thenReturn(solvableConflictcontainerNull);
		final List<SolvableConflictModel> solvableConflicts = performTransfer(configModel);
		assertEquals("We expect no conflicts as no conflicts are available at config session", 0, solvableConflicts.size());
	}

	@Test
	public void testTransferSolvableConflictsEmptyConflicts() throws IpcCommandException
	{
		configModel = new ConfigModelImpl();
		Mockito.when(configSession.getConflictingAssumptions(configId)).thenReturn(solvableConflictcontainerEmpty);
		final List<SolvableConflictModel> solvableConflicts = performTransfer(configModel);
		assertNotNull("We expect an empty conflict list", solvableConflicts);
		assertEquals("We expect no entries", 0, solvableConflicts.size());
	}

	@Test
	public void testTransferSolvableConflictsNoAssumptions() throws IpcCommandException
	{
		configModel = prepareSolvableConflictFromSSC();
		classUnderTest.transferSolvableConflicts(configSession, configId, configModel);
		final List<SolvableConflictModel> solvableConflicts = configModel.getSolvableConflicts();
		assertNotNull(WE_EXPECT_CONFLICTS, solvableConflicts);
		assertEquals(WE_EXPECT_ONE_CONFLICT, 1, solvableConflicts.size());
		final SolvableConflictModel solvableConflictModel = solvableConflicts.get(0);
		assertEquals("We expect conflict description", CONFLICT_NAME, solvableConflictModel.getDescription());
		assertEquals(0, solvableConflictModel.getConflictingAssumptions().size());
	}


	@Test
	public void testTransferSolvableConflictsEmptyAssumptions() throws IpcCommandException
	{
		Mockito.when(solvableConflict.getAssumptions()).thenReturn(assumptionsEmpty);
		configModel = prepareSolvableConflictFromSSC();
		classUnderTest.transferSolvableConflicts(configSession, configId, configModel);
		final List<SolvableConflictModel> solvableConflicts = configModel.getSolvableConflicts();
		assertNotNull(WE_EXPECT_CONFLICTS, solvableConflicts);
		assertEquals(WE_EXPECT_ONE_CONFLICT, 1, solvableConflicts.size());
		final SolvableConflictModel solvableConflictModel = solvableConflicts.get(0);
		assertNotNull(solvableConflictModel.getConflictingAssumptions());
		assertEquals(0, solvableConflictModel.getConflictingAssumptions().size());
	}

	@Test
	public void testTransferSolvableConflictsCheckAssumptions() throws IpcCommandException
	{
		Mockito.when(solvableConflict.getAssumptions()).thenReturn(assumptions);
		configModel = prepareSolvableConflictFromSSC();
		classUnderTest.transferSolvableConflicts(configSession, configId, configModel);
		final List<SolvableConflictModel> solvableConflicts = configModel.getSolvableConflicts();
		assertNotNull(WE_EXPECT_CONFLICTS, solvableConflicts);
		assertEquals(WE_EXPECT_ONE_CONFLICT, 1, solvableConflicts.size());
		final SolvableConflictModel solvableConflictModel = solvableConflicts.get(0);
		assertNotNull(solvableConflictModel.getConflictingAssumptions());
		assertEquals(1, solvableConflictModel.getConflictingAssumptions().size());
	}

	@Test
	public void testCreateSolvableConflictModelNoAssumptions()
	{
		final SolvableConflictModel solvableConflictModel = classUnderTest.createSolvableConflictModel(solvableConflict);
		assertNotNull("We expect conflict", solvableConflictModel);
		assertEquals("No entries", 0, solvableConflictModel.getConflictingAssumptions().size());
	}

	@Test
	public void testCreateSolvableConflictModelGroupId()
	{
		Mockito.when(solvableConflict.getAssumptions()).thenReturn(assumptions);
		final SolvableConflictModel solvableConflictModel = classUnderTest.createSolvableConflictModel(solvableConflict);
		assertNotNull("We expect a conflict", solvableConflictModel);
		assertEquals("We expect a group ID", assumptionId, solvableConflictModel.getId());
	}

	@Test
	public void testCreateSolvableConflictModelAssumptions()
	{
		Mockito.when(solvableConflict.getAssumptions()).thenReturn(assumptions);
		final SolvableConflictModel solvableConflictModel = classUnderTest.createSolvableConflictModel(solvableConflict);
		assertNotNull("We expect conflict", solvableConflictModel);
		final List<ConflictingAssumptionModel> conflictingAssumptions = solvableConflictModel.getConflictingAssumptions();
		assertNotNull("We expect assumptions", conflictingAssumptions);
		final ConflictingAssumptionModel conflictingAssumptionModel = conflictingAssumptions.get(0);
		assertNotNull(WE_EXPECT_ONE_ASSUMPTION, conflictingAssumptionModel);
		assertEquals("Cstic name must be matching", CSTIC_NAME, conflictingAssumptionModel.getCsticName());
		assertEquals("Value name must be matching", VALUE_NAME, conflictingAssumptionModel.getValueName());
	}

	@Test
	public void testCreateConflictingAssumptionsModel()
	{
		final ConflictingAssumptionModel conflictingAssumptionModel = SolvableConflictAdapterImpl
				.createConflictingAssumptionsModel(assumption);
		assertNotNull(WE_EXPECT_ONE_ASSUMPTION, conflictingAssumptionModel);
		assertEquals("Cstic name must be matching", CSTIC_NAME, conflictingAssumptionModel.getCsticName());
		assertEquals("Value name must be matching", VALUE_NAME, conflictingAssumptionModel.getValueName());
	}

	@Test
	public void testCreateConflictingAssumptionsModelInstanceId()
	{
		final ConflictingAssumptionModel conflictingAssumptionModel = SolvableConflictAdapterImpl
				.createConflictingAssumptionsModel(assumption);
		assertNotNull(WE_EXPECT_ONE_ASSUMPTION, conflictingAssumptionModel);
		assertEquals("Instance must be matching", instanceId, conflictingAssumptionModel.getInstanceId());
	}

	@Test
	public void testGetAssumptionIdForCsticAndValueNoConflicts()
	{
		prepareCsticModel();
		final String conflictAssumptionID = classUnderTest.getAssumptionId(csticModel, configModel);
		assertNull("Conflict assumption ID should be 'null': ", conflictAssumptionID);
	}

	@Test
	public void testGetAssumptionIdForCsticAndValueNoAssumption()
	{
		prepareCsticModel();
		createConflictAtConfigModel();
		final String conflictAssumptionID = classUnderTest.getAssumptionId(csticModel, configModel);
		assertNull("Conflict assumption ID should be 'null': ", conflictAssumptionID);
	}

	@Test
	public void testGetAssumptionForCsticAndValueMatchForCsticAndValue()
	{
		prepareCsticModel();
		prepareConflictsWithAssumptions();
		final String conflictAssumptionID = classUnderTest.getAssumptionId(csticModel, configModel);
		assertEquals("4", conflictAssumptionID);
	}

	@Test
	public void testGetAssumptionForCsticAndValueMatchForCsticOnly()
	{
		prepareCsticModel();
		csticModel.getAssignedValues().get(0).setName("ANOTHER_VALUE_NAME_1");
		prepareConflictsWithAssumptions();
		final String conflictAssumptionID = classUnderTest.getAssumptionId(csticModel, configModel);
		assertEquals("3", conflictAssumptionID);
	}

	@Test
	public void testGetAssumptionForCsticAndValueNoMatchWrongInstance()
	{
		prepareCsticModel();
		csticModel.setInstanceId("2");
		prepareConflictsWithAssumptions();
		final String conflictAssumptionID = classUnderTest.getAssumptionId(csticModel, configModel);
		assertNull("Conflict assumption ID should be 'null': ", conflictAssumptionID);
	}

	@Test
	public void testGetAssumptionForCsticAndValueNoMatchWrongCsticName()
	{
		prepareCsticModel();
		csticModel.setName("ANOTHER_CSTIC_NAME");
		prepareConflictsWithAssumptions();
		final String conflictAssumptionID = classUnderTest.getAssumptionId(csticModel, configModel);
		assertNull("Conflict assumption ID should be 'null': ", conflictAssumptionID);
	}

	@Test
	public void testIsAssumptionForAssignedValue()
	{
		prepareCsticModel();
		assertTrue(classUnderTest.isAssumptionForAssignedValue(VALUE_NAME, csticModel));
	}

	@Test
	public void testIsAssumptionForAssignedValueWrongValue()
	{
		prepareCsticModel();
		assertFalse(classUnderTest.isAssumptionForAssignedValue("WRONG_VALUE", csticModel));
	}

	@Test
	public void testIsAssumptionForAssignedValueWNoValues()
	{
		prepareCsticModel();
		assertFalse(classUnderTest.isAssumptionForAssignedValue("VALUE_NAME", new CsticModelImpl()));
	}

	private void createConflictAtConfigModel()
	{
		conflict = new SolvableConflictModelImpl();
		configModel.setSolvableConflicts(Arrays.asList(conflict));
	}

	private ConfigModel prepareSolvableConflictFromSSC() throws IpcCommandException
	{
		Mockito.when(configSession.getConflictingAssumptions(configId)).thenReturn(solvableConflictcontainer);
		return new ConfigModelImpl();
	}

	private void prepareCsticModel()
	{
		csticModel = new CsticModelImpl();
		csticModel.setName(CSTIC_NAME);
		csticModel.setInstanceId("1");
		final List<CsticValueModel> assignedValues = new ArrayList<>();
		final CsticValueModel assignedValue1 = new CsticValueModelImpl();
		assignedValue1.setName(VALUE_NAME);
		assignedValues.add(assignedValue1);
		final CsticValueModel assignedValue2 = new CsticValueModelImpl();
		assignedValue2.setName("ANOTHER_VALUE_NAME_2");
		assignedValues.add(assignedValue2);
		csticModel.setAssignedValues(assignedValues);
	}

	private void prepareConflictsWithAssumptions()
	{
		final List<SolvableConflictModel> solvableConflicts = new ArrayList<>();

		final SolvableConflictModel conflict1 = new SolvableConflictModelImpl();
		final List<ConflictingAssumptionModel> conflictingAssumptions1 = new ArrayList<>();
		final ConflictingAssumptionModel assumption11 = new ConflictingAssumptionModelImpl();
		assumption11.setId("1");
		assumption11.setCsticName("X");
		assumption11.setValueName("XV");
		assumption11.setInstanceId("1");
		conflictingAssumptions1.add(assumption11);
		final ConflictingAssumptionModel assumption12 = new ConflictingAssumptionModelImpl();
		assumption12.setId("2");
		assumption12.setCsticName("Y");
		assumption12.setValueName("YV");
		assumption12.setInstanceId("1");
		conflictingAssumptions1.add(assumption12);
		conflict1.setConflictingAssumptions(conflictingAssumptions1);
		solvableConflicts.add(conflict1);

		final SolvableConflictModel conflict2 = new SolvableConflictModelImpl();
		final List<ConflictingAssumptionModel> conflictingAssumptions2 = new ArrayList<>();
		final ConflictingAssumptionModel assumption21 = new ConflictingAssumptionModelImpl();
		assumption21.setId("3");
		assumption21.setCsticName(CSTIC_NAME);
		assumption21.setValueName("XX");
		assumption21.setInstanceId("1");
		conflictingAssumptions2.add(assumption21);
		final ConflictingAssumptionModel assumption22 = new ConflictingAssumptionModelImpl();
		assumption22.setId("4");
		assumption22.setCsticName(CSTIC_NAME);
		assumption22.setValueName(VALUE_NAME);
		assumption22.setInstanceId("1");
		conflictingAssumptions2.add(assumption22);
		conflict2.setConflictingAssumptions(conflictingAssumptions2);
		solvableConflicts.add(conflict2);

		configModel.setSolvableConflicts(solvableConflicts);
	}

}
