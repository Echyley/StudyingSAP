/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapimpeximportadapter.facade.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.impex.model.ImpExMediaModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.task.TaskModel;
import de.hybris.platform.task.TaskService;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.sap.hybris.sapimpeximportadapter.facades.impl.DefaultSapImpexImportFacade;
import com.sap.hybris.sapimpeximportadapter.services.ImpexImportService;


/**
 * JUnit test suite for {@link DefaultSapImpexImportFacadeTest}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultSapImpexImportFacadeTest
{
	@Mock
	private ImpexImportService impexImportService;
	@Mock
	private ModelService modelService;
	@Mock
	private TimeService timeService;
	@Mock
	private TaskService taskService;

	private ImpExMediaModel impexMediaModel;

	private InputStream inputStream;

	private Date date;



	@InjectMocks
	private DefaultSapImpexImportFacade defaultSapImpexImportFacade;

	@Before
	public void setUp()
	{
		inputStream = new ByteArrayInputStream("dummy".getBytes());
		impexMediaModel = new ImpExMediaModel();
		Mockito.lenient().when(impexImportService.createImpexMedia(any(InputStream.class))).thenReturn(impexMediaModel);
		Mockito.lenient().doNothing().when(modelService).save(any(InputStream.class));
		date = new Date();
		Mockito.lenient().when(timeService.getCurrentTime()).thenReturn(date);

	}

	@Test
	public void testcreateAndImportImpexMedia()
	{
		final DefaultSapImpexImportFacade defaultSapImpexImportFacadeSpy = spy(defaultSapImpexImportFacade);
		Mockito.lenient().doNothing().when(defaultSapImpexImportFacadeSpy).scheduleImportTask(impexMediaModel);
		defaultSapImpexImportFacadeSpy.createAndImportImpexMedia(inputStream);
		assertTrue(impexMediaModel.isRemoveOnSuccess());
	}

	@Test
	public void testScheduleImportTask()
	{

		final TaskModel tm = new TaskModel();
		Mockito.lenient().when(modelService.create(any(Class.class))).thenReturn(tm);
		Mockito.lenient().doNothing().when(taskService).scheduleTask(any(TaskModel.class));

		defaultSapImpexImportFacade.scheduleImportTask(impexMediaModel);
		assertEquals(tm.getRunnerBean(), "sapImpexImportTaskRunner");
		assertEquals(tm.getContext(), impexMediaModel);
		assertEquals(tm.getExecutionTimeMillis().longValue(), date.getTime());
	}

}
