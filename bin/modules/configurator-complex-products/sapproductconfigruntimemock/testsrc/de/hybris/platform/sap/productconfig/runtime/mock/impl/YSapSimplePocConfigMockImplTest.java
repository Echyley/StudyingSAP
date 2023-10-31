/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.mock.impl;

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticGroupModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticValueModelImpl;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;


/**
 * Unit test for {@link YSapSimplePocConfigMockImpl}
 */
@UnitTest
public class YSapSimplePocConfigMockImplTest
{
	private YSapSimplePocConfigMockImpl classUnderTest;
	private ConfigModel model;
	private InstanceModel instance;
	private CsticModel cstic;

	@Before
	public void setUp()
	{
		classUnderTest = (YSapSimplePocConfigMockImpl) new RunTimeConfigMockFactory().createConfigMockForProductCode("ysap");
		model = classUnderTest.createDefaultConfiguration();
		instance = model.getRootInstance();
		cstic = instance.getCstic(YSapSimplePocConfigMockImpl.NUM_NAME);
	}

	private List<CsticValueModel> setAssignedValue(final String value)
	{
		final List<CsticValueModel> assignedValues = new ArrayList<>();
		final CsticValueModel csticValue = new CsticValueModelImpl();
		csticValue.setName(value);
		assignedValues.add(csticValue);

		return assignedValues;
	}

	private void checkCsticCheck(final String assignedValue)
	{
		final List<CsticValueModel> assignedValues = setAssignedValue(assignedValue);
		cstic.setAssignedValues(assignedValues);
		classUnderTest.checkCstic(model, instance, cstic);

		final CsticValueModel csticValue = classUnderTest.retrieveValue(instance, YSapSimplePocConfigMockImpl.NUM_NAME);
		assertEquals(assignedValue, csticValue.getName());
	}

	@Test
	public void testCheckCsticPassNullValue()
	{
		checkCsticCheck(null);
	}

	@Test
	public void testCheckCsticPassIntegerValue()
	{
		checkCsticCheck("12");
	}

	@Test
	public void testCheckCsticPassFloatValue()
	{
		checkCsticCheck("12.0");
	}

	@Test(expected = NumberFormatException.class)
	public void testCheckCsticPassStringValue()
	{
		final String assignedValue = "aaa";
		final List<CsticValueModel> assignedValues = setAssignedValue(assignedValue);
		cstic.setAssignedValues(assignedValues);
		classUnderTest.checkCstic(model, instance, cstic);
	}

	@Test
	public void testGeneralGroupCreated()
	{
		final List<CsticGroupModel> groups = instance.getCsticGroups();
		assertEquals(1, groups.size());

		final CsticGroupModel genGroup = groups.get(0);
		assertEquals("_GEN", genGroup.getName());
		assertEquals(4, genGroup.getCsticNames().size());
	}

}
