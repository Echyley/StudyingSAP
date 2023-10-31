/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorservices.dataimport.batch.task.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.dataimport.batch.BatchHeader;
import de.hybris.platform.acceleratorservices.dataimport.batch.task.HeaderSetupTask;

import java.io.File;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * Test for {@link HeaderSetupTask}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class HeaderSetupTaskTest
{
	private static final String TEST_CATEGORY = "category";
	private static final boolean TEST_NET = true;

	@Mock
	private File file;
	private HeaderSetupTask task;

	@Before
	public void setUp()
	{
		task = new HeaderSetupTask();
		task.setCatalog(TEST_CATEGORY);
		task.setNet(TEST_NET);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testErr()
	{
		task.execute(null);
	}

	@Test
	public void testSuccess()
	{
		final BatchHeader header = task.execute(file);
		Assert.assertEquals(file, header.getFile());
		Assert.assertNull(header.getSequenceId());
		Assert.assertNotNull(header.getTransformedFiles());
		Assert.assertTrue(header.getTransformedFiles().isEmpty());
		Assert.assertEquals(TEST_CATEGORY, header.getCatalog());
		Assert.assertEquals(Boolean.valueOf(TEST_NET), Boolean.valueOf(header.isNet()));
	}
}
