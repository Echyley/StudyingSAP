/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.punchout.process.email.context;

import de.hybris.platform.b2b.punchout.model.PunchOutCredentialModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
import org.apache.velocity.VelocityContext;


public class PunchOutEmailContext extends VelocityContext
{
	private static final int DEFAULT_EXPIRED_DAYS = 180;
	private static final String CREDENTIAL_EXPIRATION_DAYS = "b2bpunchout.credential.expiration.days";
	private PunchOutCredentialModel model;
	private transient ConfigurationService configurationService;

	protected PunchOutCredentialModel getModel()
	{
		return model;
	}

	public void setModel(final PunchOutCredentialModel model)
	{
		this.model = model;
	}

	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

	public Date getExpiredDate()
	{
		return DateUtils.addDays(model.getSharedSecretModifiedTime(), getConfigurationService().getConfiguration()
				.getInt(CREDENTIAL_EXPIRATION_DAYS, DEFAULT_EXPIRED_DAYS));
	}

	public String getCode()
	{
		return model.getCode();
	}

	public String getDomain()
	{
		return model.getDomain();
	}

}
