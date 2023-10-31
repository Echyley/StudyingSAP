/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.rules.action.strategy.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ruleengineservices.rao.AbstractRuleActionRAO;
import de.hybris.platform.sap.productconfig.rules.rao.CsticRAO;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.ConfigModelFactoryImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConfigModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticValueModelImpl;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ProductConfigRuleActionStrategyCheckerImplTest
{
	private ProductConfigRuleActionStrategyCheckerImpl classUnderTest;

	private final CsticModel cstic = new CsticModelImpl();
	private final CsticValueModel value = new CsticValueModelImpl();
	private final Map<String, CsticModel> csticMap = new HashMap<>();
	private final String cscticName = "csticName";
	private final String valueName = "valueName";

	@Mock
	private ConfigModelFactoryImpl mockedConfigModelFactory;
	@Mock
	private AbstractRuleActionRAO action;


	@Before
	public void setUp()
	{
		classUnderTest = new ProductConfigRuleActionStrategyCheckerImpl();
		classUnderTest.setConfigModelFactory(mockedConfigModelFactory);

		value.setName(valueName);
		final CsticValueModel value1 = new CsticValueModelImpl();
		value1.setName("Value1");
		final CsticValueModel value2 = new CsticValueModelImpl();
		value2.setName("Value2");
		final List<CsticValueModel> values = new ArrayList<>();
		values.add(value1);
		values.add(value2);
		cstic.setAssignableValues(values);
		cstic.setConstrained(true);
		cstic.setName(cscticName);
		final CsticRAO appliedToObject = new CsticRAO();
		appliedToObject.setCsticName(cscticName);
		csticMap.put(cscticName, cstic);

		when(mockedConfigModelFactory.createInstanceOfCsticValueModel(anyInt())).thenReturn(value);
		when(action.getAppliedToObject()).thenReturn(appliedToObject);
	}

	@Test
	public void testCheckValueAssignableDomainOnDemandTrueO()
	{
		classUnderTest.setReadDomainValuesOnDemand(true);
		assertTrue(
				classUnderTest.checkValueAssignable(new ConfigModelImpl(), action, valueName, "", csticMap));
	}

	@Test
	public void testCheckValueAssignableDomainOnDemandFalseO()
	{
		classUnderTest.setReadDomainValuesOnDemand(false);
		assertFalse(classUnderTest.checkValueAssignable(new ConfigModelImpl(), action, valueName, "", csticMap));
	}

	@Test
	public void testCheckValueAssignableCsticNotConstrained()
	{
		classUnderTest.setReadDomainValuesOnDemand(true);
		cstic.setConstrained(false);
		assertTrue(classUnderTest.checkValueAssignable(new ConfigModelImpl(), action, valueName, "", csticMap));
	}

}
