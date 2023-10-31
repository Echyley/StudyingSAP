/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.punchout.services;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.punchout.model.PunchOutCredentialModel;
import de.hybris.platform.commons.model.renderer.RendererTemplateModel;
import de.hybris.platform.commons.renderer.RendererService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.util.mail.MailUtils;
import org.apache.commons.configuration.Configuration;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PunchOutEmailServiceTest
{
	private static final String EMAIL_TO_ADDRESS = "punchoutAdminTest@saptest.com";
	private static final String EMAIL_SUCCESS_MESSAGE = "messageID";
	private static final String EMAIL_SUBJECT = "b2bpunchout.credential.expiration.email.subject";
	private static final String EMAIL_TEMPLATE_CODE = "b2bpunchout.credential.expiration.email.template.code";
	private static final String SUBJECT_CONFIG_STRING = "suject_config_string";
	private static final String TEMPLATE_CODE_CONFIG_STRING = "template_code_config_string";

	@Mock
	private RendererService rendererService;

	@InjectMocks
	private PunchOutEmailService punchOutEmailService;

	@Mock
	private HtmlEmail htmlEmail;

	@Mock
	private RendererTemplateModel rendererTemplateModel;
	@Mock
	private PunchOutCredentialModel punchOutCredentialModel;

	@Mock
	private ConfigurationService configurationService;

	@Mock
	private Configuration configuration;


	@Before
	public void setup()
	{
		doNothing().when(rendererService).render(any(), any(), any());
	}

	@Test
	public void testSendEmailWithNullSubject() throws EmailException
	{
		try (final MockedStatic mockMailUtils = mockStatic(MailUtils.class))
		{
			mockMailUtils.when(MailUtils::getPreConfiguredEmail).thenReturn(htmlEmail);
			when(configurationService.getConfiguration()).thenReturn(configuration);
			when(configuration.getString(EMAIL_SUBJECT)).thenReturn(null);
			final String messageID = punchOutEmailService.sendEmail(punchOutCredentialModel, EMAIL_TO_ADDRESS);
			assertThat(messageID).isNull();
			verify(htmlEmail, never()).send();
		}
	}

	@Test
	public void testSendEmailWithNullTemplateCode() throws EmailException
	{
		try (final MockedStatic mockMailUtils = mockStatic(MailUtils.class))
		{
			mockMailUtils.when(MailUtils::getPreConfiguredEmail).thenReturn(htmlEmail);
			when(configurationService.getConfiguration()).thenReturn(configuration);
			when(configuration.getString(EMAIL_SUBJECT)).thenReturn(SUBJECT_CONFIG_STRING);
			when(configuration.getString(EMAIL_TEMPLATE_CODE)).thenReturn(null);
			final String messageID = punchOutEmailService.sendEmail(punchOutCredentialModel, EMAIL_TO_ADDRESS);
			assertThat(messageID).isNull();
			verify(htmlEmail, never()).send();
		}
	}

	@Test
	public void testSendEmailWithNullHtmlTemplate() throws EmailException
	{
		try (final MockedStatic mockMailUtils = mockStatic(MailUtils.class))
		{
			mockMailUtils.when(MailUtils::getPreConfiguredEmail).thenReturn(htmlEmail);
			when(configurationService.getConfiguration()).thenReturn(configuration);
			when(configuration.getString(EMAIL_SUBJECT)).thenReturn(SUBJECT_CONFIG_STRING);
			when(configuration.getString(EMAIL_TEMPLATE_CODE)).thenReturn(TEMPLATE_CODE_CONFIG_STRING);
			when(rendererService.getRendererTemplateForCode(TEMPLATE_CODE_CONFIG_STRING)).thenReturn(null);
			final String messageID = punchOutEmailService.sendEmail(punchOutCredentialModel, EMAIL_TO_ADDRESS);
			assertThat(messageID).isNull();
			verify(htmlEmail, never()).send();
		}
	}

	@Test
	public void testSendEmailSuccess() throws EmailException
	{
		try (final MockedStatic mockMailUtils = mockStatic(MailUtils.class))
		{
			mockMailUtils.when(MailUtils::getPreConfiguredEmail).thenReturn(htmlEmail);
			when(configurationService.getConfiguration()).thenReturn(configuration);
			when(configuration.getString(EMAIL_SUBJECT)).thenReturn(SUBJECT_CONFIG_STRING);
			when(configuration.getString(EMAIL_TEMPLATE_CODE)).thenReturn(TEMPLATE_CODE_CONFIG_STRING);
			when(rendererService.getRendererTemplateForCode(anyString())).thenReturn(rendererTemplateModel);
			when(htmlEmail.send()).thenReturn(EMAIL_SUCCESS_MESSAGE);
			final String messageID = punchOutEmailService.sendEmail(punchOutCredentialModel, EMAIL_TO_ADDRESS);
			assertThat(messageID).isEqualTo(EMAIL_SUCCESS_MESSAGE);
			verify(htmlEmail).send();
		}

	}

}
