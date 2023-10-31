/*
 * [y] hybris Platform
 *
 * Copyright (c) 2019 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */

package de.hybris.platform.outboundsync.config.impl;

import de.hybris.platform.integrationservices.config.BaseIntegrationServicesConfiguration;
import de.hybris.platform.outboundsync.model.OutboundSyncStreamConfigurationModel;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

/**
 * Provides access methods to configurations related to the Inbound Services
 */
public class DefaultOutboundSyncConfiguration extends BaseIntegrationServicesConfiguration implements OutboundSyncConfiguration
{
	private static final String AUTOGENERATE_EXCLUDED_STREAM_IDS = "outboundsync.disabled.info.expression.auto.generation.stream.ids";
	private static final String AUTOGENERATE_EXCLUDED_STREAM_IDS_FALLBACK = "";
	private static final String OUTBOUNDSYNC_MAX_RETRIES = "outboundsync.max.retries";
	private static final String ITEM_GROUP_SIZE_MAX = "outboundsync.item.group.size.max";
	private static final String ITEM_GROUPING_TIMEOUT = "outboundsync.item.grouping.timeout";
	private static final String OUTBOUNDSYNC_BATCH_LIMIT = "outboundsync.batch.limit";
	private static final String OUTBOUNDSYNC_BATCH_RELEASE_TIMEOUT = "outboundsync.batch.release.timeout";
	private static final String OUTBOUNDSYNC_CRONJOBMODEL_SEARCH_SLEEP = "outboundsync.cronjob.search.sleep.milliseconds";
	private static final int OUTBOUNDSYNC_CRONJOBMODEL_SEARCH_SLEEP_MILLIS_FALLBACK = 1000;
	private static final int OUTBOUNDSYNC_BATCH_LIMIT_FALLBACK = 200;
	private static final int OUTBOUNDSYNC_BATCH_RELEASE_TIMEOUT_FALLBACK = 5000;

	@Override
	public boolean isInfoGenerationEnabledForStream(final OutboundSyncStreamConfigurationModel streamConfig)
	{
		return !getAutogenerateExcludedStreamIds().contains(streamConfig.getStreamId());
	}

	private Set<String> getAutogenerateExcludedStreamIds()
	{
		final String ids = getStringProperty(AUTOGENERATE_EXCLUDED_STREAM_IDS, AUTOGENERATE_EXCLUDED_STREAM_IDS_FALLBACK);
		return Stream.of(StringUtils.split(ids, ","))
		             .map(String::trim)
		             .collect(Collectors.toSet());
	}

	@Override
	public int getMaxOutboundSyncRetries()
	{
		return getIntegerProperty(OUTBOUNDSYNC_MAX_RETRIES, 0);
	}

	@Override
	public int getItemGroupSizeMax()
	{
		return getIntegerProperty(ITEM_GROUP_SIZE_MAX, 0);
	}

	@Override
	public int getItemGroupingTimeout()
	{
		return getIntegerProperty(ITEM_GROUPING_TIMEOUT, 0);
	}

	@Override
	public int getOutboundSyncCronjobModelSearchSleep()
	{
		return getIntegerProperty(OUTBOUNDSYNC_CRONJOBMODEL_SEARCH_SLEEP, OUTBOUNDSYNC_CRONJOBMODEL_SEARCH_SLEEP_MILLIS_FALLBACK);
	}

	@Override
	public int getOutboundBatchLimit()
	{
		return getIntegerProperty(OUTBOUNDSYNC_BATCH_LIMIT, OUTBOUNDSYNC_BATCH_LIMIT_FALLBACK);
	}

	@Override
	public int getOutboundBatchReleaseTimeout()
	{
		return getIntegerProperty(OUTBOUNDSYNC_BATCH_RELEASE_TIMEOUT, OUTBOUNDSYNC_BATCH_RELEASE_TIMEOUT_FALLBACK);
	}

	public void setOutboundBatchLimit(final int limit)
	{
		setProperty(OUTBOUNDSYNC_BATCH_LIMIT, limit);
	}
}
