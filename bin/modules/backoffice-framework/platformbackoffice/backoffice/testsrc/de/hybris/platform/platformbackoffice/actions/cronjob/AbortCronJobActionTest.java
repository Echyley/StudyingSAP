/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package de.hybris.platform.platformbackoffice.actions.cronjob;

import com.hybris.cockpitng.actions.ActionContext;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.servicelayer.cronjob.CronJobService;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;



@RunWith(MockitoJUnitRunner.class)
public class AbortCronJobActionTest
{

	@Spy
	@InjectMocks
	private AbortCronJobAction abortCronJobAction;
	@Mock
	private CronJobService cronJobService;
	@Mock
	private ModelService modelService;
	@Mock
	private ActionContext actionContext;
	@Mock
	private CronJobModel cronJobModel;



	@Test
	public void shouldReturnFalseWhenCronJobModelIsNew()
	{
		//given
		when(actionContext.getData()).thenReturn(cronJobModel);
		when(modelService.isNew(cronJobModel)).thenReturn(true);

		doReturn(true).when(abortCronJobAction).isCurrentUserAllowedToRun(any());

		//when
		boolean canPerform = abortCronJobAction.canPerform(actionContext);

		//then
		assertThat(canPerform).isFalse();
	}

	@Test
	public void shouldReturnTrueWhenCronJobModelIsNotNew()
	{
		//given
		when(actionContext.getData()).thenReturn(cronJobModel);
		when(modelService.isNew(cronJobModel)).thenReturn(false);
		when(cronJobService.isAbortable(cronJobModel)).thenReturn(true);
		when(cronJobService.isRunning(cronJobModel)).thenReturn(true);

		doReturn(true).when(abortCronJobAction).isCurrentUserAllowedToRun(any());

		//when
		boolean canPerform = abortCronJobAction.canPerform(actionContext);

		//then
		assertThat(canPerform).isTrue();
	}

}
