/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commerceservices.util;

import de.hybris.bootstrap.annotations.UnitTest;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * The unit test for {@link ImpexDuplicatedImportHandleUtils}.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ImpexDuplicatedImportHandleUtilsTest
{
	String file = "/commerceservices/test/testImpexDuplicatedImportHandleUtils.impex";

	@Test
	public void testImpexImportedFirstTime()
	{
		//give
		Integer iExpectImportedTimes = 1;
		//when
		ImpexDuplicatedImportHandleUtils.logDuplicatedImportInfo(file);
		//then
		Assert.assertEquals("The import times should be 1", ImpexDuplicatedImportHandleUtils.getFileImportCounterMap().get(file),
				iExpectImportedTimes);
	}

	@Test
	public void testImpexImportedSecondTime()
	{
		//give
		Integer iExpectImportedTimes = 2;
		//when
		ImpexDuplicatedImportHandleUtils.logDuplicatedImportInfo(file);
		//then
		Assert.assertEquals("The import times should be 2", ImpexDuplicatedImportHandleUtils.getFileImportCounterMap().get(file),
				iExpectImportedTimes);
	}
}
