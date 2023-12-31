/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapimpeximportadapter.tasks;

import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.impex.model.ImpExMediaModel;
import de.hybris.platform.task.TaskModel;
import de.hybris.platform.task.TaskService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.sap.hybris.sapimpeximportadapter.services.ImpexImportService;


/**
 * JUnit test suite for {@link SapImpexImportTaskRunnerTest}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SapImpexImportTaskRunnerTest
{
	@Mock
	private ImpexImportService impexImportService;


	@InjectMocks
	private SapImpexImportTaskRunner sapImpexImportTaskRunner;

	private TaskService taskService;
	private TaskModel taskModel;


	@Before
	public void setUp()
	{
		taskService = mock(TaskService.class);
		taskModel = new TaskModel();

	}

	@Test(expected = RuntimeException.class)
	public void ThrowExceptionOfInvaidPayload()
	{
		sapImpexImportTaskRunner.run(taskService, taskModel);
	}

	@Test(expected = RuntimeException.class)
	public void ThrowExceptionOnExecutionFailure()
	{
		final ImpExMediaModel imm = new ImpExMediaModel();
		imm.setRealFileName("dummy");
		taskModel.setContext(imm);
		Mockito.lenient().when(impexImportService.importMedia(imm)).thenReturn(null);
		sapImpexImportTaskRunner.run(taskService, taskModel);
	}





}
