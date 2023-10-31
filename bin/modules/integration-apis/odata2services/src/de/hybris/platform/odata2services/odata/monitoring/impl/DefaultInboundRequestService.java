/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.odata.monitoring.impl;

import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.inboundservices.model.InboundRequestModel;
import de.hybris.platform.integrationservices.util.Log;
import de.hybris.platform.odata2services.odata.monitoring.InboundRequestBuilder;
import de.hybris.platform.odata2services.odata.monitoring.InboundRequestService;
import de.hybris.platform.odata2services.odata.monitoring.InboundRequestServiceParameter;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Collection;
import java.util.LinkedList;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Required;

/**
 * The default implementation of the {@link InboundRequestService}
 */
public class DefaultInboundRequestService implements InboundRequestService
{
	private static final Logger LOG = Log.getLogger(DefaultInboundRequestService.class);

	private ModelService modelService;
	private UserService userService;

	@Override
	public void register(final InboundRequestServiceParameter parameter)
	{
		final Collection<InboundRequestModel> inboundRequests = new LinkedList<>();
		final InboundRequestPartsCoordinator iterator = new InboundRequestPartsCoordinator(parameter.getRequests(),
				parameter.getResponses(), parameter.getMedias());
		final UserModel user = findUser(parameter.getUserId());
		while (iterator.hasNext())
		{
			iterator.next();
			final InboundRequestModel inboundRequest = InboundRequestBuilder.builder()
					.withRequest(iterator.getBatch())
					.withResponse(iterator.getChangeSet())
					.withMedia(iterator.getMedia())
					.withHttpMethod(parameter.getHttpMethod())
					.withUser(user)
					.withSapPassport(parameter.getSapPassport())
					.build();
			inboundRequests.add(inboundRequest);
		}
		getModelService().saveAll(inboundRequests);
	}

	protected UserModel findUser(final String userId)
	{
		try
		{
			if (StringUtils.isNotBlank(userId))
			{
				return getUserService().getUserForUID(userId);
			}
		}
		catch(final UnknownIdentifierException e)
		{
			LOG.error("Failed to find user", e);
		}
		return null;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	protected UserService getUserService()
	{
		return userService;
	}

	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}
}
