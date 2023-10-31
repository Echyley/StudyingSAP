/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.ssc.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConflictingAssumptionModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueDelta;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.SolvableConflictModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ValueChangeType;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConfigModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConflictingAssumptionModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.InstanceModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.SolvableConflictModelImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.custdev.projects.fbs.slc.cfg.client.IConfigSessionClient;
import com.sap.custdev.projects.fbs.slc.cfg.client.ICsticData;
import com.sap.custdev.projects.fbs.slc.cfg.client.ICsticHeader;
import com.sap.custdev.projects.fbs.slc.cfg.client.ICsticValueData;
import com.sap.custdev.projects.fbs.slc.cfg.client.IDeltaBean;
import com.sap.custdev.projects.fbs.slc.cfg.client.IInstanceData;
import com.sap.custdev.projects.fbs.slc.cfg.command.beans.CsticData;
import com.sap.custdev.projects.fbs.slc.cfg.command.beans.CsticHeader;
import com.sap.custdev.projects.fbs.slc.cfg.command.beans.CsticValueData;
import com.sap.custdev.projects.fbs.slc.cfg.exception.IpcCommandException;


@UnitTest
public class ConfigurationUpdateAdapterImplTest
{
	private static final String VAL1 = "VAL1";
	private static final String VAL2 = "VAL2";
	private static final String VAL3 = "VAL3";
	private static final String VAL4 = "VAL4";
	private static final String CSTIC_NAME = "CsticName";
	private static final String CSTICNAME = "CSTIC1";
	private static final String CSTICNAME2 = "CSTIC2";
	private static final String INSTANCENAME = "INSTANCENAME";
	private static final String INSTANCEID = "INSTANCEID";
	private static final String VALUE = "Value";

	private final ConfigurationUpdateAdapterImpl classUnderTest = new ConfigurationUpdateAdapterImpl();

	private ConfigModel configModel;
	@Mock
	private IConfigSessionClient session;
	@Mock
	private IInstanceData instanceData;
	private String configId;
	private SolvableConflictModel solvableConflict;
	private ConflictingAssumptionModel conflictingAssumption;
	private final List<String> newValues = new ArrayList<>();
	private final List<String> oldValues = new ArrayList<>();
	private final InstanceModel rootInstance = new InstanceModelImpl();
	private final List<CsticModel> cstics = new ArrayList();
	private final CsticModel csticModel = new CsticModelImpl();
	@Mock
	private ICsticData csticData;
	@Mock
	private ICsticValueData csticValueData;
	private final ICsticValueData[] csticValueDatas = new ICsticValueData[1];
	@Mock
	private ICsticHeader csticHeaderData;
	@Mock
	private IDeltaBean deltaBean;
	@Mock
	private ObjectMapper objectMapper;

	@Before
	public void setup() throws IpcCommandException
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest.setObjectMapper(objectMapper);

		configModel = new ConfigModelImpl();
		configId = "1";
		classUnderTest.setConflictAdapter(new SolvableConflictAdapterImpl());
		solvableConflict = new SolvableConflictModelImpl();
		conflictingAssumption = new ConflictingAssumptionModelImpl();
		configModel.setRootInstance(rootInstance);
		rootInstance.setId(INSTANCEID);
		rootInstance.setName(INSTANCENAME);
		cstics.add(csticModel);
		csticModel.setName(CSTIC_NAME);
		csticModel.setChangedByFrontend(true);
		rootInstance.setCstics(cstics);
		when(csticValueData.getValueAssigned()).thenReturn(Boolean.TRUE);
		when(csticHeaderData.getCsticMulti()).thenReturn(Boolean.TRUE);
		when(csticData.getCsticHeader()).thenReturn(csticHeaderData);
		when(csticData.getCsticValues()).thenReturn(csticValueDatas);
		csticValueDatas[0] = csticValueData;
		when(session.getCstic(INSTANCEID, CSTIC_NAME, ConfigurationUpdateAdapterImpl.WITHOUT_DESCRIPTION, configId))
				.thenReturn(csticData);
		when(session.getInstance(configId, INSTANCEID)).thenReturn(instanceData);
		when(session.setCsticsValues(eq(INSTANCEID), eq(configId), eq(false), any())).thenReturn(deltaBean);
	}

	@Test
	public void testHasBeenRetractedNoRetraction() throws IpcCommandException
	{
		assertFalse(classUnderTest.hasBeenRetracted(csticModel, configModel, session, configId));
	}

	@Test
	public void testHasBeenRetractedAssumptions() throws IpcCommandException
	{
		csticModel.setRetractTriggered(true);
		csticModel.setInstanceId(INSTANCEID);
		configModel.setSolvableConflicts(Arrays.asList(solvableConflict));
		solvableConflict.setConflictingAssumptions(Arrays.asList(conflictingAssumption));
		conflictingAssumption.setCsticName(CSTIC_NAME);
		conflictingAssumption.setId("A");
		conflictingAssumption.setInstanceId(INSTANCEID);
		assertTrue("Retraction expected", classUnderTest.hasBeenRetracted(csticModel, configModel, session, configId));
	}

	@Test
	public void testHasBeenRetractedWithBlankAssumption() throws IpcCommandException
	{
		csticModel.setRetractTriggered(true);
		csticModel.setInstanceId(INSTANCEID);
		configModel.setSolvableConflicts(Arrays.asList(solvableConflict));
		solvableConflict.setConflictingAssumptions(Arrays.asList(conflictingAssumption));
		conflictingAssumption.setCsticName(CSTIC_NAME);
		conflictingAssumption.setId("  ");
		conflictingAssumption.setInstanceId(INSTANCEID);
		assertFalse("Retraction expected", classUnderTest.hasBeenRetracted(csticModel, configModel, session, configId));
		assertNull("Single value should be null: ", csticModel.getSingleValue());
	}

	@Test
	public void testHasBeenRetractedWithoutAssumption() throws IpcCommandException
	{
		csticModel.setRetractTriggered(true);
		assertFalse("Retraction expected", classUnderTest.hasBeenRetracted(csticModel, configModel, session, configId));
		assertNull("Single value should be null: ", csticModel.getSingleValue());
	}

	@Test
	public void testDetermineValuesToDeleteEmptyLists()
	{
		final ICsticValueData[] determineValuesToDelete = classUnderTest.determineValuesToDelete(newValues, oldValues);
		assertEquals(0, determineValuesToDelete.length);
	}

	@Test
	public void testDetermineValuesToDeleteOnlyNew()
	{
		newValues.add(VALUE);
		final ICsticValueData[] determineValuesToDelete = classUnderTest.determineValuesToDelete(newValues, oldValues);
		assertEquals(0, determineValuesToDelete.length);
	}

	@Test
	public void testDetermineValuesToDeleteOld()
	{
		final String oldValue = VALUE;
		oldValues.add(oldValue);
		final ICsticValueData[] determineValuesToDelete = classUnderTest.determineValuesToDelete(newValues, oldValues);
		assertEquals(1, determineValuesToDelete.length);
		assertEquals(oldValue, determineValuesToDelete[0].getValueName());
	}

	@Test
	public void testDetermineValuesToSetEmptyList()
	{
		final ICsticValueData[] valuesToSet = classUnderTest.determineValuesToSet(newValues, oldValues);
		assertEquals(0, valuesToSet.length);
	}

	@Test
	public void testDetermineValuesToSetOnlyOldValue()
	{
		final String oldValue = VALUE;
		oldValues.add(oldValue);
		final ICsticValueData[] valuesToSet = classUnderTest.determineValuesToSet(newValues, oldValues);
		assertEquals(0, valuesToSet.length);
	}

	@Test
	public void testDetermineValuesToSetNew()
	{
		final String newValue = VALUE;
		newValues.add(newValue);
		final ICsticValueData[] valuesToSet = classUnderTest.determineValuesToSet(newValues, oldValues);
		assertEquals(1, valuesToSet.length);
		assertEquals(newValue, valuesToSet[0].getValueName());
	}


	@Test
	public void testmapICsticDataToCsticValueDeltaEmptyValues()
	{
		final ICsticData icd = new CsticData();
		final List<CsticValueDelta> csticValueDeltas = new ArrayList();
		classUnderTest.mapICsticDataToCsticValueDelta(csticValueDeltas, null, null, null, icd);

		assertTrue(csticValueDeltas.isEmpty());
	}

	@Test
	public void testmapICsticDataToCsticValueDelta()
	{

		final ICsticData icd = createCsticData(CSTICNAME, VAL1, VAL2);

		final List<CsticValueDelta> csticValueDeltas = new ArrayList();
		classUnderTest.mapICsticDataToCsticValueDelta(csticValueDeltas, INSTANCENAME, INSTANCEID, ValueChangeType.DELETE, icd);

		assertEquals(1, csticValueDeltas.size());
		final CsticValueDelta result = csticValueDeltas.get(0);
		assertEquals(INSTANCENAME, result.getInstanceName());
		assertEquals(INSTANCEID, result.getInstanceId());
		assertEquals(CSTICNAME, result.getCsticName());
		assertEquals(ValueChangeType.DELETE, result.getChangeType());
		assertEquals(2, result.getValueNames().size());
		switch (result.getValueNames().get(0))
		{
			case VAL1:
				assertEquals(VAL2, result.getValueNames().get(1));
				break;
			case VAL2:
				assertEquals(VAL1, result.getValueNames().get(1));
				break;
			default:
				fail();
		}
	}

	private ICsticData createCsticData(final String csticName, final String... values)
	{
		final ICsticData icd = new CsticData();
		createCsticHeader(icd, csticName);
		createCsticValueData(icd, values);
		return icd;
	}

	private void createCsticHeader(final ICsticData icd, final String csticName)
	{
		final ICsticHeader ichd = new CsticHeader();

		ichd.setCsticName(csticName);
		icd.setCsticHeader(ichd);
	}

	private void createCsticValueData(final ICsticData icd, final String... values)
	{
		final ICsticValueData[] values2 = new ICsticValueData[values.length];
		for (int i = 0; i < values.length; i++)
		{
			final ICsticValueData icvd = new CsticValueData();
			icvd.setValueName(values[i]);
			values2[i] = icvd;

		}
		icd.setCsticValues(values2);
	}

	@Test
	public void fillCsticValueDeltasTest() throws IpcCommandException
	{

		final ICsticData icd = createCsticData(CSTICNAME, VAL1, VAL2);
		final ICsticData icd2 = createCsticData(CSTICNAME2, VAL4, VAL3);
		final List<ICsticData> csticDataListToClear = new ArrayList<>();
		csticDataListToClear.add(icd);
		final List<ICsticData> csticDataList = new ArrayList<>();
		csticDataList.add(icd2);
		configModel = new ConfigModelImpl();

		Mockito.when(session.getInstance("123", "S1")).thenReturn(instanceData);
		Mockito.when(instanceData.getInstName()).thenReturn(INSTANCENAME);

		classUnderTest.fillCsticValueDeltas(session, "123", configModel, "S1", csticDataListToClear, csticDataList);

		final List<CsticValueDelta> resultValueDeltas = configModel.getCsticValueDeltas();
		assertEquals(2, resultValueDeltas.size());
		assertEquals(ValueChangeType.DELETE, resultValueDeltas.get(0).getChangeType());
		assertEquals(ValueChangeType.SET, resultValueDeltas.get(1).getChangeType());

		final CsticValueDelta result = resultValueDeltas.get(1);
		assertEquals(INSTANCENAME, result.getInstanceName());
		assertEquals(CSTICNAME2, result.getCsticName());
		assertEquals(ValueChangeType.SET, result.getChangeType());
		assertEquals(2, result.getValueNames().size());
		switch (result.getValueNames().get(0))
		{
			case VAL3:
				assertEquals(VAL4, result.getValueNames().get(1));
				break;
			case VAL4:
				assertEquals(VAL3, result.getValueNames().get(1));
				break;
			default:
				fail();
		}
	}

	@Test
	public void testInitCsticValueDeltaList()
	{
		classUnderTest.initCsticValueDeltaList(configModel);
		assertEquals(ArrayList.class, configModel.getCsticValueDeltas().getClass());
	}

	@Test
	public void testInitCsticValueDeltaListDisbaled()
	{
		classUnderTest.setTrackingEnabled(false);
		classUnderTest.initCsticValueDeltaList(configModel);
		assertEquals(Collections.emptyList(), configModel.getCsticValueDeltas());
	}

	@Test
	public void testUpdateConfiguration() throws IpcCommandException
	{
		when(session.deleteCsticValues(eq(INSTANCEID), eq("false"), any(), eq(configId))).thenReturn(deltaBean);
		final boolean result = classUnderTest.updateConfiguration(configModel, configId, session);
		assertTrue(result);
		verify(session).deleteCsticValues(eq(INSTANCEID), eq("false"), any(), eq(configId));
	}

	@Test(expected = IllegalStateException.class)
	public void testUpdateConfigurationException() throws IpcCommandException
	{
		when(session.getCstic(INSTANCEID, CSTIC_NAME, ConfigurationUpdateAdapterImpl.WITHOUT_DESCRIPTION, configId))
				.thenThrow(new IpcCommandException());
		classUnderTest.updateConfiguration(configModel, configId, session);
	}

	@Test
	public void testSetCsticValues() throws IpcCommandException
	{
		final List<ICsticData> csticDataList = new ArrayList<ICsticData>();
		csticDataList.add(csticData);
		classUnderTest.setCsticValues(configId, session, INSTANCEID, csticDataList);
		verify(session).setCsticsValues(eq(INSTANCEID), eq(configId), eq(false), any());
	}

	@Test
	public void testIsTrackingEnabled()
	{
		assertTrue(classUnderTest.isTrackingEnabled());

		classUnderTest.setTrackingEnabled(false);
		assertFalse(classUnderTest.isTrackingEnabled());

	}
}
