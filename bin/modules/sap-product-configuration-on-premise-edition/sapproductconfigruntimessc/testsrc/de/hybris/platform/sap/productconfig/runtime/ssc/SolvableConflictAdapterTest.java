/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.ssc;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConfigModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticModelImpl;

import org.apache.commons.lang.NotImplementedException;
import org.junit.Test;

import com.sap.custdev.projects.fbs.slc.cfg.IConfigSession;


@UnitTest
public class SolvableConflictAdapterTest
{
	private static final String ASSUMPTION_ID = "123";

	@Test(expected = NotImplementedException.class)
	public void testReleaseSessionDefault()
	{
		final SolvableConflictAdapterImplForTest solvableConflictAdapter = new SolvableConflictAdapterImplForTest();
		solvableConflictAdapter.getAssumptionId(new CsticModelImpl(), new ConfigModelImpl());
	}

	private class SolvableConflictAdapterImplForTest implements SolvableConflictAdapter
	{
		@Override
		public void transferSolvableConflicts(final IConfigSession configSession, final String configId,
				final ConfigModel configModel)
		{
			// empty
		}

	}

}
