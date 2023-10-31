/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.hybris.datahub.core.tasks;

import de.hybris.platform.servicelayer.exceptions.SystemException;
import de.hybris.platform.task.RetryLaterException;
import de.hybris.platform.task.TaskModel;
import de.hybris.platform.task.TaskRunner;
import de.hybris.platform.task.TaskService;
import de.hybris.platform.task.impl.DefaultTaskExecutionStrategy;

/**
 * A task execution strategy that does not run the task inside a transaction
 */
public class DefaultDataHubTaskExecutionStrategy extends DefaultTaskExecutionStrategy
{
	@Override
	public void run(final TaskService taskService, final TaskRunner<TaskModel> runner, final TaskModel model)
	{
		try
		{
			runner.run(taskService, model);
		}
		catch (final RetryLaterException e)
		{
			throw e;
		}
		catch (final Exception e)
		{
			throw new SystemException(e);
		}
	}
}
