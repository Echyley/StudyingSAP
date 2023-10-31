/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.punchout.job;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.punchout.model.PunchOutCredentialExpiredNotificationJobModel;
import de.hybris.platform.b2b.punchout.model.PunchOutCredentialModel;
import de.hybris.platform.b2b.punchout.services.PunchOutCredentialService;
import de.hybris.platform.b2b.punchout.services.PunchOutEmailService;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.servicelayer.cronjob.PerformResult;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PunchOutCredentialExpiredNotificationJobTest
{
	private static final String EMAIL_TO_ADDRESS = "punchoutTest@sap.com";
	private static final String EMAIL_SUCCESS_MESSAGE = "success_message";

	private final List<PunchOutCredentialModel> modelList = new ArrayList<>();

	@Mock
	private PunchOutEmailService punchOutEmailService;
	@Mock
	private PunchOutCredentialService punchOutCredentialService;

	@InjectMocks
	private PunchOutCredentialExpiredNotificationJob notificationJob;

	@Mock
	private CronJobModel cronJob;

	@Mock
	private PunchOutCredentialExpiredNotificationJobModel jobModel;

	@Before
	public void setup()
	{
		when(cronJob.getJob()).thenReturn(jobModel);
		modelList.add(new PunchOutCredentialModel());
	}

	@Test
	public void testSuccessExecuteJobWithoutExpiredCredentials()
	{
		when(punchOutCredentialService.getExpiredPunchOutCredentials()).thenReturn(Collections.emptyList());
		final PerformResult result = notificationJob.perform(cronJob);
		verify(punchOutEmailService, never()).sendEmail(any(), anyString());
		assertThat(result.getResult()).isEqualTo(CronJobResult.SUCCESS);
		assertThat(result.getStatus()).isEqualTo(CronJobStatus.FINISHED);
	}

	@Test
	public void testSuccessExecuteJobWithExpiredCredentials()
	{
		when(punchOutCredentialService.getExpiredPunchOutCredentials()).thenReturn(modelList);
		when(jobModel.getEmailToAddress()).thenReturn(EMAIL_TO_ADDRESS);
		when(punchOutEmailService.sendEmail(any(), anyString())).thenReturn(EMAIL_SUCCESS_MESSAGE);
		final PerformResult result = notificationJob.perform(cronJob);
		assertThat(result.getResult()).isEqualTo(CronJobResult.SUCCESS);
		assertThat(result.getStatus()).isEqualTo(CronJobStatus.FINISHED);
	}

}
