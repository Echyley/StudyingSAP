/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.outboundservices.update;

import static de.hybris.platform.integrationservices.model.MonitoredRequestModel.HTTPMETHOD;

import de.hybris.platform.core.initialization.SystemSetup;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.integrationservices.enums.HttpMethod;
import de.hybris.platform.integrationservices.util.Log;
import de.hybris.platform.outboundservices.constants.OutboundservicesConstants;
import de.hybris.platform.outboundservices.model.OutboundRequestModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import java.util.List;

import org.slf4j.Logger;

@SystemSetup(extension = OutboundservicesConstants.EXTENSIONNAME)
public class OutboundRequestSystemUpdater
{
	private static final Logger LOG = Log.getLogger(OutboundRequestSystemUpdater.class);
	private static final HttpMethod HTTP_METHOD_DEFAULT_VALUE = HttpMethod.POST;
	private static final FlexibleSearchQuery NULL_HTTP_METHOD_SEARCH_QUERY = new FlexibleSearchQuery(
			"SELECT {orm." + ItemModel.PK + "} FROM {" + OutboundRequestModel._TYPECODE + " AS orm} WHERE ({orm." + HTTPMETHOD + "} is null)");
	private final ModelService modelService;
	private final FlexibleSearchService flexibleSearchService;

	public OutboundRequestSystemUpdater(final ModelService modelService,
	                                    final FlexibleSearchService flexibleSearchService)
	{
		this.modelService = modelService;
		this.flexibleSearchService = flexibleSearchService;
	}

	@SystemSetup(type = SystemSetup.Type.ESSENTIAL, process = SystemSetup.Process.UPDATE)
	public void populateNullHttpMethodWithDefaultValues()
	{
		final List<OutboundRequestModel> nullHttpMethodOutboundRequests = findOutboundRequestsWithNullHttpMethod();
		if (!nullHttpMethodOutboundRequests.isEmpty())
		{
			LOG.info("Setting default values for httpMethod for OutboundRequests.");
			nullHttpMethodOutboundRequests.forEach(this::updateHttpMethod);
			LOG.info("Finished setting default values for OutboundRequests.");
		}
	}

	private void updateHttpMethod(final OutboundRequestModel occ)
	{
		LOG.debug("Setting default value for httpMethod for OutboundRequest with integrationKey: {}",
				occ.getIntegrationKey());
		occ.setHttpMethod(HTTP_METHOD_DEFAULT_VALUE);
		modelService.save(occ);
	}

	private List<OutboundRequestModel> findOutboundRequestsWithNullHttpMethod()
	{
		return flexibleSearchService.<OutboundRequestModel>search(NULL_HTTP_METHOD_SEARCH_QUERY).getResult();
	}
}
