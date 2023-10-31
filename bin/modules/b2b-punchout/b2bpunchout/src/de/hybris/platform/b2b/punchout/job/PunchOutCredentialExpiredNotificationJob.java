/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.punchout.job;

import de.hybris.platform.b2b.punchout.model.PunchOutCredentialExpiredNotificationJobModel;
import de.hybris.platform.b2b.punchout.model.PunchOutCredentialModel;
import de.hybris.platform.b2b.punchout.services.PunchOutCredentialService;
import de.hybris.platform.b2b.punchout.services.PunchOutEmailService;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PunchOutCredentialExpiredNotificationJob extends AbstractJobPerformable<CronJobModel>
{
	private static final Logger LOG = LoggerFactory.getLogger(PunchOutCredentialExpiredNotificationJob.class);
	private PunchOutEmailService punchOutEmailService;
	private PunchOutCredentialService punchOutCredentialService;


	@Override
	public PerformResult perform(final CronJobModel cronJob)
	{
		getExpiredPunchOutCredentials().stream().forEach(model -> {
			final String emailMessageId = getPunchOutEmailService()
					.sendEmail(model, ((PunchOutCredentialExpiredNotificationJobModel) cronJob.getJob()).getEmailToAddress());
			if (emailMessageId != null)
			{
				LOG.info(
						"PunchOut Credential Expiration Notification Email has been sent for {} with success email message id : {}. ",
						model.getCode(), emailMessageId);
			}
		});

		return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
	}

	protected List<PunchOutCredentialModel> getExpiredPunchOutCredentials()
	{
		final List<PunchOutCredentialModel> credentialModels = getPunchOutCredentialService().getExpiredPunchOutCredentials();
		return Optional.ofNullable(credentialModels).map(List::stream).orElseGet(Stream::empty).collect(Collectors.toList());
	}

	protected PunchOutEmailService getPunchOutEmailService()
	{
		return punchOutEmailService;
	}

	public void setPunchOutEmailService(final PunchOutEmailService punchOutEmailService)
	{
		this.punchOutEmailService = punchOutEmailService;
	}

	protected PunchOutCredentialService getPunchOutCredentialService()
	{
		return punchOutCredentialService;
	}

	public void setPunchOutCredentialService(final PunchOutCredentialService punchOutCredentialService)
	{
		this.punchOutCredentialService = punchOutCredentialService;
	}
}
