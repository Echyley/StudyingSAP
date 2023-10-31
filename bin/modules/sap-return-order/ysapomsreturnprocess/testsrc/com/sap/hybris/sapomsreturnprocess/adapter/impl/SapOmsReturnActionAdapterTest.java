/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapomsreturnprocess.adapter.impl;


import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.processengine.BusinessProcessEvent;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.processengine.model.ProcessTaskModel;
import de.hybris.platform.returns.model.ReturnProcessModel;
import de.hybris.platform.returns.model.ReturnRequestModel;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SapOmsReturnActionAdapterTest
{
	private static final String RETURNREQUEST_CODE = "1234";
	private static final String RETURN_PROCESS_CODE = "oms-return-process";

	@InjectMocks
	private final SapOmsReturnActionAdapter omsReturnActionAdapter = new SapOmsReturnActionAdapter();
	@Mock
	private ReturnRequestModel returnRequest;
	@Mock
	private ReturnProcessModel returnProcess;
	@Mock
	private BusinessProcessService businessProcessService;

	private ProcessTaskModel currentTask;

	@Before
	public void setUp()
	{
		currentTask = new ProcessTaskModel();
		currentTask.setAction(SapOmsReturnActionAdapter.WAIT_FOR_NOTIFICATION_FROM_BACKEND);
	   Mockito.lenient().when(returnProcess.getCurrentTasks()).thenReturn(Collections.singletonList(currentTask));
		Mockito.lenient().when(returnRequest.getCode()).thenReturn(RETURNREQUEST_CODE);
		Mockito.lenient().when(returnProcess.getCode()).thenReturn(RETURN_PROCESS_CODE);
	}

	@Test
	public void testCancelReturnRequest()
	{
		omsReturnActionAdapter.cancelReturnRequest(returnRequest, returnProcess);
		verify(businessProcessService).triggerEvent(any(BusinessProcessEvent.class));
	}

}
