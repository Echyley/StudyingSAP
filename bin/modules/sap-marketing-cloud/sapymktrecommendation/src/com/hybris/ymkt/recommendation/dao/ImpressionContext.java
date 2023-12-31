/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hybris.ymkt.recommendation.dao;

import java.util.Date;


/**
 *
 * data structure that holds the interaction data context
 *
 */
public class ImpressionContext
{
	private final int impressionCount;
	private final int itemCount;
	private final String scenarioId;
	private final Date timeStamp;

	/**
	 * @param scenarioId
	 * @param itemCount
	 *
	 */
	public ImpressionContext(String scenarioId, int itemCount)
	{
		this(scenarioId, 1, itemCount, new Date());
	}

	/**
	 * @param scenarioId
	 * @param impressionCount
	 * @param itemCount
	 * @param timeStamp
	 */
	public ImpressionContext(final String scenarioId, final int impressionCount, final int itemCount, final Date timeStamp)
	{
		this.scenarioId = scenarioId;
		this.impressionCount = impressionCount;
		this.itemCount = itemCount;
		this.timeStamp = timeStamp;
	}

	public int getImpressionCount()
	{
		return impressionCount;
	}

	public int getItemCount()
	{
		return itemCount;
	}

	public String getScenarioId()
	{
		return scenarioId;
	}

	public Date getTimeStamp()
	{
		return timeStamp;
	}
}
