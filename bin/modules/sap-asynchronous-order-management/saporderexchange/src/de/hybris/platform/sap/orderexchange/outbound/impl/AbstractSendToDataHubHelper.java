/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.orderexchange.outbound.impl;

import de.hybris.platform.sap.orderexchange.outbound.RawItemBuilder;
import de.hybris.platform.sap.orderexchange.outbound.SendToDataHubHelper;
import de.hybris.platform.sap.orderexchange.outbound.SendToDataHubResult;
import de.hybris.platform.servicelayer.model.AbstractItemModel;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.hybris.datahub.core.rest.DataHubOutboundException;
import com.hybris.datahub.core.services.DataHubOutboundService;


/**
 * Helper for creating a raw items and sending it to the Data Hub
 * 
 * @param <T>
 */
public class AbstractSendToDataHubHelper<T extends AbstractItemModel> implements SendToDataHubHelper<T>
{
	private static final Logger log = Logger.getLogger(AbstractSendToDataHubHelper.class);
	private static final String DEFAULT_FEED = "DEFAULT_FEED";
	private String feed = DEFAULT_FEED;
	private DataHubOutboundService dataHubOutboundService;
	private String rawItemType;

	private RawItemBuilder<T> rawItemBuilder;

	protected RawItemBuilder<T> getRawItemBuilder()
	{
		return rawItemBuilder;
	}

	
	@Required
	public void setRawItemBuilder(final RawItemBuilder<T> csvBuilder)
	{
		this.rawItemBuilder = csvBuilder;
	}

	
	public String getFeed()
	{
		return feed;
	}

	
	@Required
	public void setFeed(final String feed)
	{
		this.feed = feed;
	}

	
	public DataHubOutboundService getDataHubOutboundService()
	{
		return dataHubOutboundService;
	}

	
	@Required
	public void setDataHubOutboundService(final DataHubOutboundService dataHubOutboundService)
	{
		this.dataHubOutboundService = dataHubOutboundService;
	}

	
	public String getRawItemType()
	{
		return rawItemType;
	}

	
	@Required
	public void setRawItemType(final String rawItem)
	{
		this.rawItemType = rawItem;
	}

	@Override
	public SendToDataHubResult createAndSendRawItem(final T model)
	{
		try
		{
			getDataHubOutboundService().sendToDataHub(getFeed(), getRawItemType(), getRawItemBuilder().rowsAsNameValuePairs(model));
		}
		catch (DataHubOutboundException e)
		{
			log.warn(e);
			return new DefaultSendToDataHubResult(SendToDataHubResult.SENDING_FAILED_CODE, e.getMessage());
		}
		return DefaultSendToDataHubResult.OKAY;
	}

}
