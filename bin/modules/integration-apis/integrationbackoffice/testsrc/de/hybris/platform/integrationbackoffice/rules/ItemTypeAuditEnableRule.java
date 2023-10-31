/*
 *  Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.rules;

import de.hybris.platform.core.Registry;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import java.util.ArrayList;
import java.util.HashSet;

import org.junit.rules.ExternalResource;

/**
 * A rule for enabling/disabling auditing on specified Item Types in integration tests.
 */
public class ItemTypeAuditEnableRule extends ExternalResource
{
	private static final String DESTINATION_TARGET_AUDIT_ENABLED_PROPERTY = "audit.destinationtarget.enabled";
	private static final String IO_AUDITING_ENABLED_PROPERTY = "audit.integrationobject.enabled";

	private final ConfigurationService configuration;
	private final HashSet<String> enabledAuditProperties = new HashSet();

	private ItemTypeAuditEnableRule()
	{
		configuration = getConfigurationService();
	}

	public static ItemTypeAuditEnableRule create()
	{
		return new ItemTypeAuditEnableRule();
	}

	/**
	 * Creates a rule that adds destinationTarget item type to the list of item types to be enabled for auditing.
	 *
	 * @return an ItemTypeAuditEnableRule with destinationTarget auditing added.
	 */
	public ItemTypeAuditEnableRule destinationTarget()
	{
		enabledAuditProperties.add(DESTINATION_TARGET_AUDIT_ENABLED_PROPERTY);
		return this;
	}

	/**
	 * Creates a rule that adds integrationObject item type to the list of item types to be enabled for auditing.
	 *
	 * @return an ItemTypeAuditEnableRule with integrationObject auditing added.
	 */
	public ItemTypeAuditEnableRule integrationObject()
	{
		enabledAuditProperties.add(IO_AUDITING_ENABLED_PROPERTY);
		return this;
	}

	public ItemTypeAuditEnableRule enable()
	{
		enabledAuditProperties.forEach(property -> {
			this.enableAuditing(property);
		});

		return this;
	}

	public ItemTypeAuditEnableRule disable()
	{
		enabledAuditProperties.forEach(property -> {
			this.disableAuditing(property);
		});

		return this;
	}

	@Override
	protected void after()
	{
		this.disable();
	}

	private static ConfigurationService getConfigurationService()
	{
		return Registry.getApplicationContext()
		               .getBean("configurationService", ConfigurationService.class);
	}

	private ItemTypeAuditEnableRule enableAuditing(final String property)
	{
		setAuditing(property, true);
		return this;
	}

	private ItemTypeAuditEnableRule disableAuditing(final String property)
	{
		setAuditing(property, false);
		return this;
	}

	private void setAuditing(final String property, final boolean value)
	{
		configuration.getConfiguration().setProperty(property, String.valueOf(value));
	}
}
