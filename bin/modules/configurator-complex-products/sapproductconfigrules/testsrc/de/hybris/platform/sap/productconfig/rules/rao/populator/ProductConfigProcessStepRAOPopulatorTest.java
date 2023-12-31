/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.rules.rao.populator;

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ruleengineservices.rao.ProcessStep;
import de.hybris.platform.sap.productconfig.rules.model.ProductConfigProcessStepModel;
import de.hybris.platform.sap.productconfig.rules.rao.ProductConfigProcessStepRAO;

import org.junit.Before;
import org.junit.Test;


@UnitTest
public class ProductConfigProcessStepRAOPopulatorTest
{
	private static final ProcessStep PROCESS_STEP = ProcessStep.CREATE_DEFAULT_CONFIGURATION;

	private ProductConfigProcessStepRAOPopulator classUnderTset;

	@Before
	public void setUp()
	{
		classUnderTset = new ProductConfigProcessStepRAOPopulator();
	}

	@Test
	public void testPopulate()
	{
		final ProductConfigProcessStepModel processStepModel = new ProductConfigProcessStepModel();
		processStepModel.setProcessStep(PROCESS_STEP);
		final ProductConfigProcessStepRAO processStepRAO = new ProductConfigProcessStepRAO();

		classUnderTset.populate(processStepModel, processStepRAO);
		assertEquals(PROCESS_STEP, processStepRAO.getProcessStep());
	}
}
