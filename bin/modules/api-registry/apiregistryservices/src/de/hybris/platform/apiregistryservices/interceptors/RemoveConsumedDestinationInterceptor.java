/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.apiregistryservices.interceptors;

import static de.hybris.platform.apiregistryservices.constants.ApiregistryservicesConstants.ITEM_DESTINATION_ATTRIBUTE;
import static de.hybris.platform.apiregistryservices.constants.ApiregistryservicesConstants.ITEM_NAME_ATTRIBUTE;
import static de.hybris.platform.apiregistryservices.constants.ApiregistryservicesConstants.ITEM_TYPE_CODE;

import de.hybris.platform.apiregistryservices.exceptions.ConsumedDestinationRemoveException;
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.apiregistryservices.services.ConsumedDestinationVerifyUsageService;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.RemoveInterceptor;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

/**
 * Interceptor to prevent Consumed Destination deletion {@link de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel}
 * if it was assigned to WebhookConfiguration or OutboundChannelConfiguration.
 */
public class RemoveConsumedDestinationInterceptor implements RemoveInterceptor<ConsumedDestinationModel>
{
	private static final Logger LOGGER = LoggerFactory.getLogger(RemoveConsumedDestinationInterceptor.class);

	private final ConsumedDestinationVerifyUsageService consumedDestinationVerifyUsageService;
	private final List<Map<String, String>> consumedDestinationPreventRemoveList;

	/**
	 * Constructor to create a RemoveConsumedDestinationInterceptor
	 *
	 * @param consumedDestinationVerifyUsageService to search for item model that was assigned Consumed Destination
	 * @param consumedDestinationPreventRemoveList  to configure item model type code/destination attribute name and so on
	 */
	public RemoveConsumedDestinationInterceptor(
			@NotNull final ConsumedDestinationVerifyUsageService consumedDestinationVerifyUsageService,
			@NotNull final List<Map<String, String>> consumedDestinationPreventRemoveList)
	{
		Preconditions.checkArgument(consumedDestinationVerifyUsageService != null,
				"Non-null consumedDestinationVerifyUsageService must be provided");
		Preconditions.checkArgument(consumedDestinationPreventRemoveList != null,
				"Non-null consumedDestinationPreventRemoveList must be provided");
		this.consumedDestinationVerifyUsageService = consumedDestinationVerifyUsageService;
		this.consumedDestinationPreventRemoveList = consumedDestinationPreventRemoveList;
	}

	@Override
	public void onRemove(final ConsumedDestinationModel consumedDestinationModel, final InterceptorContext ctx)
			throws InterceptorException
	{

		for (final Map<String, String> itemAttributeMap : consumedDestinationPreventRemoveList)
		{
			if (!itemAttributeMap.isEmpty())
			{
				raiseExceptionIfConsumedDestinationIsAssigned(consumedDestinationModel, itemAttributeMap);
			}
		}
	}

	private void raiseExceptionIfConsumedDestinationIsAssigned(final ConsumedDestinationModel consumedDestination,
	                                                           final Map<String, String> assignmentAttributes)
			throws ConsumedDestinationRemoveException
	{
		final Optional<List<ItemModel>> assignedItems = consumedDestinationVerifyUsageService.findModelsAssignedConsumedDestination(
				assignmentAttributes.get(ITEM_TYPE_CODE), assignmentAttributes.get(ITEM_DESTINATION_ATTRIBUTE),
				consumedDestination);

		if (assignedItems.isPresent())
		{
			LOGGER.debug("Assigned items are present for type {}", assignmentAttributes.get(ITEM_TYPE_CODE));
			final String list = assignmentAttributes.get(ITEM_NAME_ATTRIBUTE) != null ?
					(" - " + assignedItems.get().stream()
					                      .map(item -> item.getProperty(assignmentAttributes.get(ITEM_NAME_ATTRIBUTE)).toString())
					                      .collect(Collectors.joining(",")))
					: "";
			throw new ConsumedDestinationRemoveException(consumedDestination.getId(),
					assignmentAttributes.get(ITEM_TYPE_CODE) + list);
		}
		else
		{
			LOGGER.debug("No assigned items are present for type {}", assignmentAttributes.get(ITEM_TYPE_CODE));
		}
	}
}
