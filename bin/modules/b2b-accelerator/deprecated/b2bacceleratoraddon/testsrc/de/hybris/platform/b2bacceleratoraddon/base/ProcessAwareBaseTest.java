/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2bacceleratoraddon.base;

import de.hybris.platform.basecommerce.util.BaseCommerceBaseTest;
import de.hybris.platform.processengine.enums.ProcessState;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


@Ignore
public class ProcessAwareBaseTest extends BaseCommerceBaseTest
{
	private static final Logger LOG = Logger.getLogger(ProcessAwareBaseTest.class);

	protected boolean waitForProcessToBeCreated(final String processDefinitionName, final long maxWait)
			throws InterruptedException
	{
		final long start = System.currentTimeMillis();
		List processes = Collections.EMPTY_LIST;
		while (CollectionUtils.isEmpty(processes))
		{
			Thread.sleep(1L);
			processes = getProcesses(processDefinitionName, Arrays.asList(new ProcessState[]
			{ ProcessState.RUNNING, ProcessState.CREATED, ProcessState.WAITING }));

			if (System.currentTimeMillis() - start > maxWait)
			{
				try
				{
					List currentProcesses = getProcesses(processDefinitionName, Arrays.asList(
							new ProcessState[] { ProcessState.RUNNING, ProcessState.CREATED, ProcessState.WAITING,
									ProcessState.SUCCEEDED, ProcessState.FAILED, ProcessState.ERROR }));
					String processJsonList = new ObjectMapper().writeValueAsString(currentProcesses);
					//					throw new RuntimeException(
					//							"expected process object is not found in expected seconds, current process:" + processJsonList);
					LOG.error("expected process object is not found in expected seconds, current process:" + processJsonList);
					return false;
				}
				catch (JsonProcessingException e)
				{
					throw new RuntimeException(e);
				}
			}
		}
		if (LOG.isDebugEnabled())
		{
			LOG.debug(String.format("Process '%s' has been created in %d ms.", processDefinitionName, System.currentTimeMillis()
					- start));
		}
		return true;
	}
}
