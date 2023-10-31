/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.ruleengineservices.jobs.events;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.PK;
import de.hybris.platform.ruleengineservices.jobs.RuleEngineJobExecutionSynchronizer;
import de.hybris.platform.ruleengineservices.model.RuleEngineCronJobModel;
import de.hybris.platform.ruleengineservices.model.RuleEngineJobModel;
import de.hybris.platform.servicelayer.event.events.AfterCronJobFinishedEvent;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import org.mockito.Mockito;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class RuleEngineAfterCronJobFinishedEventListenerUnitTest
{
	@InjectMocks
	private RuleEngineAfterCronJobFinishedEventListener listener;

	@Mock
	private ModelService modelService;

	@Mock
	private RuleEngineJobExecutionSynchronizer ruleEngineJobExecutionSynchronizer;

	@Mock
	private AfterCronJobFinishedEvent event;

	@Mock
	private RuleEngineCronJobModel cronJob;

	@Test
	public void shouldNotPerformForNonRuleEngineJobType()
	{
		Mockito.lenient().when(event.getCronJobType()).thenReturn("ArbitraryCronJob");

		listener.onEvent(event);

		verifyZeroInteractions(modelService, ruleEngineJobExecutionSynchronizer);
	}

	@Test
	public void shouldReleaseLockForRuleEngineJobType()
	{
		final PK cronJobPk = PK.NULL_PK;
		Mockito.lenient().when(event.getJobType()).thenReturn(RuleEngineJobModel._TYPECODE);
		Mockito.lenient().when(event.getCronJobPK()).thenReturn(cronJobPk);
		Mockito.lenient().when(modelService.get(cronJobPk)).thenReturn(cronJob);

		listener.onEvent(event);

		verify(modelService).get(cronJobPk);
		verify(ruleEngineJobExecutionSynchronizer).releaseLock(cronJob);
	}
}
