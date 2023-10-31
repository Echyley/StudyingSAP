/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.punchout.services;

import de.hybris.platform.b2b.punchout.model.PunchOutCredentialModel;
import de.hybris.platform.b2b.punchout.process.email.context.PunchOutEmailContext;
import de.hybris.platform.commons.model.renderer.RendererTemplateModel;
import de.hybris.platform.commons.renderer.RendererService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.util.mail.MailUtils;

import java.io.StringWriter;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;


/**
 * This is the service to handle the logic to send email when punch out credential expired.
 */

public class PunchOutEmailService
{
	private static final Logger LOG = LoggerFactory.getLogger(PunchOutEmailService.class);
	private static final String EMAIL_SUBJECT = "b2bpunchout.credential.expiration.email.subject";
	private static final String EMAIL_TEMPLATE_CODE = "b2bpunchout.credential.expiration.email.template.code";

	private RendererService rendererService;
	private ConfigurationService configurationService;

	/**
	 *
	 * @param model
	 * Model is used to set to PunchOutEmailContext, so that corresponding expired punch out credential info could be rendered into email.
	 * @param emailToAddress
	 * It is the recipient's email address
	 * @return the success email sent message ID.
	 *
	 */
	public String sendEmail(final PunchOutCredentialModel model, final String emailToAddress)
	{
		try
		{
			final HtmlEmail email = (HtmlEmail) MailUtils.getPreConfiguredEmail();
			email.setCharset("UTF-8");
			email.addTo(emailToAddress);
			email.setSubject(getEmailSubject());

			final StringWriter htmlVersion = new StringWriter();
			getRendererService().render(getHtmlTemplate(), createContext(model), htmlVersion);
			email.setHtmlMsg(htmlVersion.toString());

			return email.send();
		}
		catch (EmailException | IllegalArgumentException exception)
		{
			LOG.warn("Could not send e-mail cause: {}", exception.getMessage());
		}

		return null;
	}

	protected PunchOutEmailContext createContext(final PunchOutCredentialModel model)
	{
		final PunchOutEmailContext punchOutEmailContext = new PunchOutEmailContext();
		punchOutEmailContext.setConfigurationService(getConfigurationService());
		punchOutEmailContext.setModel(model);
		return punchOutEmailContext;
	}

	protected String getEmailSubject()
	{
		final String subject = getConfigurationService().getConfiguration().getString(EMAIL_SUBJECT);
		Assert.notNull(subject,
				"Subject to send punchOut credential expiration notification email cannot be null");
		return subject;
	}

	protected RendererTemplateModel getHtmlTemplate()
	{
		final String templateCode = getConfigurationService().getConfiguration().getString(EMAIL_TEMPLATE_CODE);
		Assert.notNull(templateCode,
				"Template code to send punchOut credential expiration notification email cannot be null");
		final RendererTemplateModel bodyRenderTemplate = getRendererService().getRendererTemplateForCode(templateCode);
		Assert.notNull(bodyRenderTemplate,
				"HtmlTemplate to render punchOut credential expiration notification email cannot be null");
		return bodyRenderTemplate;
	}

	protected RendererService getRendererService()
	{
		return rendererService;
	}

	public void setRendererService(final RendererService rendererService)
	{
		this.rendererService = rendererService;
	}

	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}
}
