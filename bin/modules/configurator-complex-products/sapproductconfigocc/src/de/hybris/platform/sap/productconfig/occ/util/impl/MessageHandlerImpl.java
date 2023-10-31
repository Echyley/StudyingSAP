/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.occ.util.impl;

import de.hybris.platform.sap.productconfig.occ.ConfigurationOverviewWsDTO;
import de.hybris.platform.sap.productconfig.occ.ConfigurationWsDTO;
import de.hybris.platform.sap.productconfig.occ.CsticValueWsDTO;
import de.hybris.platform.sap.productconfig.occ.CsticWsDTO;
import de.hybris.platform.sap.productconfig.occ.GroupOverviewWsDTO;
import de.hybris.platform.sap.productconfig.occ.GroupWsDTO;
import de.hybris.platform.sap.productconfig.occ.OverviewCsticValueWsDTO;
import de.hybris.platform.sap.productconfig.occ.ProductConfigMessageWsDTO;
import de.hybris.platform.sap.productconfig.occ.util.MessageHandler;
import de.hybris.platform.util.Config;
import de.hybris.platform.webservicescommons.util.YSanitizer;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.util.HtmlUtils;


/**
 * Default implementation of the {@link MessageHandler}.
 */
public class MessageHandlerImpl implements MessageHandler
{

	@Override
	public void processConfiguration(final ConfigurationWsDTO configuration)
	{
		if (isSupportProductConfigMessages())
		{
			configuration.getMessages().forEach(this::processMessage);
		}
		else {
			configuration.setMessages(new ArrayList<>(0));
		}
		configuration.getGroups().forEach(this::processGroup);
	}

	protected void processGroup(final GroupWsDTO group)
	{
		final List<CsticWsDTO> attributes = group.getAttributes();
		if (attributes != null)
		{
			attributes.forEach(this::processAttribute);
		}
		final List<GroupWsDTO> subGroups = group.getSubGroups();
		if (subGroups != null)
		{
			subGroups.forEach(this::processGroup);
		}
	}

	protected void processAttribute(final CsticWsDTO attribute)
	{
		if (isSupportProductConfigMessages())
		{
			attribute.getMessages().forEach(this::processMessage);
		}
		else
		{
			attribute.setMessages(new ArrayList<>(0));
		}
		final List<CsticValueWsDTO> values = attribute.getDomainValues();
		if (values != null)
		{
			values.forEach(this::processValue);
		}
	}

	protected void processValue(final CsticValueWsDTO value)
	{
		if (isSupportProductConfigMessages())
		{
			value.getMessages().forEach(this::processMessage);
		}
		else
		{
			value.setMessages(new ArrayList<>(0));
		}
	}

	protected void processMessage(final ProductConfigMessageWsDTO message)
	{
		if (StringUtils.isNotEmpty(message.getMessage()))
		{
			final String processedMessage = YSanitizer.sanitize(HtmlUtils.htmlUnescape(message.getMessage()));
			message.setMessage(processedMessage);
		}
		if (StringUtils.isNotEmpty(message.getExtendedMessage()))
		{
			final String processedExtendedMessage = YSanitizer.sanitize(HtmlUtils.htmlUnescape(message.getExtendedMessage()));
			message.setExtendedMessage(processedExtendedMessage);
		}
	}

	@Override
	public void processConfigurationOverview(final ConfigurationOverviewWsDTO configurationOverview)
	{
		if (isSupportProductConfigMessages())
		{
			configurationOverview.getMessages().forEach(this::processMessage);
		}
		else
		{
			configurationOverview.setMessages(new ArrayList<>(0));
		}
		configurationOverview.getGroups().forEach(this::processOverviewGroup);
	}

	protected void processOverviewGroup(final GroupOverviewWsDTO overviewGroup)
	{
		final List<OverviewCsticValueWsDTO> overviewCharacteristicValues = overviewGroup.getCharacteristicValues();
		if (overviewCharacteristicValues != null)
		{
			overviewCharacteristicValues
					.forEach(this::processOverviewCharacteristicValue);
		}
		final List<GroupOverviewWsDTO> overviewSubGroups = overviewGroup.getSubGroups();
		if (overviewSubGroups != null)
		{
			overviewSubGroups.forEach(this::processOverviewGroup);
		}
	}

	protected void processOverviewCharacteristicValue(final OverviewCsticValueWsDTO overviewCharacteristicValue)
	{
		if (isSupportProductConfigMessages())
		{
			overviewCharacteristicValue.getMessages().forEach(this::processMessage);
		}
		else
		{
			overviewCharacteristicValue.setMessages(new ArrayList<>(0));
		}
	}

	protected boolean isSupportProductConfigMessages()
	{
		return Config.getBoolean("toggle.sapproductconfigservices.exposeProductConfigMessages.enabled", false);
	}

}
