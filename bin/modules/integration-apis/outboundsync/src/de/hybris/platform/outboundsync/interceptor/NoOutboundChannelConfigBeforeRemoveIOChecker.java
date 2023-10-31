/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.outboundsync.interceptor;

import de.hybris.platform.core.PK;
import de.hybris.platform.integrationservices.interceptor.IntegrationObjectRemoveInterceptor;
import de.hybris.platform.integrationservices.interceptor.interfaces.BeforeRemoveIntegrationObjectChecker;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;
import de.hybris.platform.integrationservices.util.Log;
import de.hybris.platform.outboundsync.exceptions.CannotDeleteIntegrationObjectLinkedWithOutboundChannelConfigException;
import de.hybris.platform.outboundsync.model.OutboundChannelConfigurationModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;

import com.google.common.base.Preconditions;

/**
 * This checker is dynamically added to the beforeRemoveIntegrationObjectCheckers of the IntegrationObjectRemoveInterceptor {@link IntegrationObjectRemoveInterceptor}.
 * Before an IntegrationObject is deleted, this checker will check if there is any OutboundChannelConfiguration that references that IntegrationObject.
 */
public class NoOutboundChannelConfigBeforeRemoveIOChecker implements BeforeRemoveIntegrationObjectChecker
{
	private static final Logger LOG = Log.getLogger(NoOutboundChannelConfigBeforeRemoveIOChecker.class);
	private static final String INTEGRATION_OBJECT = "integrationObject";
	private static final String GET_OCC_BY_INTEGRATION_OBJECT = "select {" + OutboundChannelConfigurationModel.PK + "} " +
			"from {" + OutboundChannelConfigurationModel._TYPECODE + " as occ} WHERE {occ.integrationObject} = ?" + INTEGRATION_OBJECT;

	private FlexibleSearchService flexibleSearchService;

	/**
	 * Constructor
	 *
	 * @param flexibleSearchService to search for the {@link OutboundChannelConfigurationModel}.
	 */
	public NoOutboundChannelConfigBeforeRemoveIOChecker(@NotNull final FlexibleSearchService flexibleSearchService)
	{
		Preconditions.checkArgument(flexibleSearchService != null, "Non-null flexibleSearchService must be provided");
		this.flexibleSearchService = flexibleSearchService;
	}

	@Override
	public void checkIfIntegrationObjectInUse(final IntegrationObjectModel integrationObject) throws InterceptorException
	{
		if (integrationObject != null)
		{
			LOG.debug("Finding OutboundChannelConfiguration by integration object {}", integrationObject.getCode());
			try
			{
				final Map<String, PK> paramsMap = Map.of(INTEGRATION_OBJECT, integrationObject.getPk());
				final SearchResult<OutboundChannelConfigurationModel> query = flexibleSearchService.search(
						GET_OCC_BY_INTEGRATION_OBJECT, paramsMap);
				final List<OutboundChannelConfigurationModel> occList = query.getResult();
				if (!occList.isEmpty())
				{
					final String occCode = occList.stream()
					                              .map(OutboundChannelConfigurationModel::getCode)
					                              .collect(Collectors.joining(","));
					throw new CannotDeleteIntegrationObjectLinkedWithOutboundChannelConfigException(integrationObject.getCode(),
							occCode);
				}
			}
			catch (final RuntimeException e)
			{
				LOG.error("Failed finding OutboundChannelConfiguration by integration object '{}'", integrationObject.getCode(),
						e);
				throw new InterceptorException(
						String.format("Failed finding OutboundChannelConfiguration by integration object: %s",
								integrationObject.getCode()), e);
			}
		}
	}
}
