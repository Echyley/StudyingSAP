/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.services.evaluator;

import static org.junit.Assert.assertFalse;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.servicelayer.data.RestrictionData;
import de.hybris.platform.sap.productconfig.services.model.CMSCartConfigurationRestrictionModel;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * Evaluator test. Our implementation always evaluates to true, introducing the evaluator is done to enable a more
 * dynamic behaviour in extensions depending on CPQ
 */
@UnitTest
public class CMSCartConfigurationRestrictionEvaluatorTest
{
	CMSCartConfigurationRestrictionEvaluator classUnderTest = new CMSCartConfigurationRestrictionEvaluator();

	@Mock
	RestrictionData restrictionData;

	/**
	 * Test of evaluate method
	 */
	@Test
	public void testEvaluate()
	{
		MockitoAnnotations.initMocks(this);
		final CMSCartConfigurationRestrictionModel model = new CMSCartConfigurationRestrictionModel();
		assertFalse("Evaluator should always return false", classUnderTest.evaluate(model, restrictionData));

	}
}
