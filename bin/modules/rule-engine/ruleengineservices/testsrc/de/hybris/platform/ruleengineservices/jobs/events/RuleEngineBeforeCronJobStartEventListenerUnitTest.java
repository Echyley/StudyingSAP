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
import de.hybris.platform.cronjob.model.TriggerModel;
import de.hybris.platform.ruleengineservices.model.RuleEngineCronJobModel;
import de.hybris.platform.ruleengineservices.model.RuleEngineJobModel;
import de.hybris.platform.servicelayer.event.events.BeforeCronJobStartEvent;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import org.mockito.Mockito;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class RuleEngineBeforeCronJobStartEventListenerUnitTest
{
	@InjectMocks
	private RuleEngineBeforeCronJobStartEventListener listener;

	@Mock
	private ModelService modelService;

	@Mock
	private BeforeCronJobStartEvent event;

	@Mock
	private RuleEngineCronJobModel cronJob;

	@Test
	public void shouldNotPerformForNonRuleEngineJobType()
	{
		Mockito.lenient().when(event.getJobType()).thenReturn("ArbitraryJob");

		listener.onEvent(event);

		verifyZeroInteractions(modelService);
	}

	@Test
	public void shouldRemoveTriggersForRuleEngineJobType()
	{
		final PK cronJobPk = PK.NULL_PK;
		Mockito.lenient().when(event.getJobType()).thenReturn(RuleEngineJobModel._TYPECODE);
		Mockito.lenient().when(event.getCronJobPK()).thenReturn(cronJobPk);
		Mockito.lenient().when(modelService.get(cronJobPk)).thenReturn(cronJob);

		final List<TriggerModel> triggerModels = Arrays.asList(mock(TriggerModel.class), mock(TriggerModel.class));
		Mockito.lenient().when(cronJob.getTriggers()).thenReturn(triggerModels);

		listener.onEvent(event);

		verify(modelService).get(cronJobPk);
		verify(modelService).removeAll(triggerModels);
	}
}
