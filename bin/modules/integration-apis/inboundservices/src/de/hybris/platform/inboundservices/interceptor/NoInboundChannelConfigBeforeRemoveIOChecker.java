/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.inboundservices.interceptor;

import de.hybris.platform.core.PK;
import de.hybris.platform.inboundservices.exceptions.CannotDeleteIntegrationObjectLinkedWithInboundChannelConfigException;
import de.hybris.platform.integrationservices.model.InboundChannelConfigurationModel;
import de.hybris.platform.integrationservices.interceptor.IntegrationObjectRemoveInterceptor;
import de.hybris.platform.integrationservices.interceptor.interfaces.BeforeRemoveIntegrationObjectChecker;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;
import de.hybris.platform.integrationservices.util.Log;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.Map;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;

import com.google.common.base.Preconditions;

/**
 * This checker is dynamically added to the beforeRemoveIntegrationObjectCheckers of the IntegrationObjectRemoveInterceptor {@link IntegrationObjectRemoveInterceptor}.
 * Before an IntegrationObject is deleted, this checker will check if there is any InboundChannelConfiguration that references that IntegrationObject.
 */
public class NoInboundChannelConfigBeforeRemoveIOChecker implements BeforeRemoveIntegrationObjectChecker
{
	private static final Logger LOG = Log.getLogger(NoInboundChannelConfigBeforeRemoveIOChecker.class);

	private static final String INTEGRATION_OBJECT = "integrationObject";
	private static final String GET_ICC_BY_INTEGRATION_OBJECT = "select {" + InboundChannelConfigurationModel.PK + "} " +
			"from {" + InboundChannelConfigurationModel._TYPECODE + " as icc} WHERE {icc.integrationObject} = ?" + INTEGRATION_OBJECT;

	private FlexibleSearchService flexibleSearchService;

	/**
	 * Constructor
	 *
	 * @param flexibleSearchService to search for the {@link InboundChannelConfigurationModel}.
	 */
	public NoInboundChannelConfigBeforeRemoveIOChecker(@NotNull final FlexibleSearchService flexibleSearchService)
	{
		Preconditions.checkArgument(flexibleSearchService != null, "Non-null flexibleSearchService must be provided");
		this.flexibleSearchService = flexibleSearchService;
	}

	@Override
	public void checkIfIntegrationObjectInUse(final IntegrationObjectModel integrationObject) throws InterceptorException
	{
		if (integrationObject != null)
		{
			LOG.debug("Finding InboundChannelConfiguration by integration object {}", integrationObject.getCode());
			try
			{
				final Map<String, PK> paramsMap = Map.of(INTEGRATION_OBJECT, integrationObject.getPk());
				final SearchResult<InboundChannelConfigurationModel> query = flexibleSearchService.search(
						GET_ICC_BY_INTEGRATION_OBJECT, paramsMap);
				if (!query.getResult().isEmpty())
				{
					throw new CannotDeleteIntegrationObjectLinkedWithInboundChannelConfigException(integrationObject.getCode());
				}
			}
			catch (final RuntimeException e)
			{
				LOG.error("Failed finding InboundChannelConfiguration by integration object '{}'", integrationObject.getCode(),
						e);
				throw new InterceptorException(
						String.format("Failed finding InboundChannelConfiguration by integration object: %s",
								integrationObject.getCode()), e);
			}
		}
	}
}
