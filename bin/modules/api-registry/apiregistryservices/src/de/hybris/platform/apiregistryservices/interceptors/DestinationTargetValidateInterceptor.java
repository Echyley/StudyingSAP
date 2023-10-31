/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.apiregistryservices.interceptors;

import static de.hybris.platform.apiregistryservices.constants.ApiregistryservicesConstants.ITEM_DESTINATION_ATTRIBUTE;
import static de.hybris.platform.apiregistryservices.constants.ApiregistryservicesConstants.ITEM_TYPE_CODE;

import de.hybris.platform.apiregistryservices.enums.DestinationChannel;
import de.hybris.platform.apiregistryservices.exceptions.DestinationTargetValidationException;
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.apiregistryservices.model.DestinationTargetModel;
import de.hybris.platform.apiregistryservices.services.ConsumedDestinationVerifyUsageService;
import de.hybris.platform.core.Registry;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang.BooleanUtils;

import com.google.common.base.Preconditions;

/**
 * DestinationTargetInterceptor prevents the users to change the template flag and/or some ConsumedDestination of the DestinationTarget
 */
public class DestinationTargetValidateInterceptor implements ValidateInterceptor<DestinationTargetModel>
{
	private static final String TEMPLATE_FLAG_ERROR_MESSAGE = "The value of the template flag can only be changed from 'null' to 'false'";
	private static final String CHANGING_CHANNEL_ERROR_MESSAGE = "Changing the destination channel value of the destination target is not allowed";
	private static final String CONSUMED_DESTINATION_RESET_ERROR_MESSAGE = "ConsumedDestinationModel [%s] cannot be assigned to Destination Target because it is used in one or more [%s]";

	private final ConsumedDestinationVerifyUsageService consumedDestinationVerifyUsageService;
	private final List<Map<String, String>> consumedDestinationPreventResetList;

	/**
	 * Default constructor to create DestinationTargetValidateInterceptor
	 */
	public DestinationTargetValidateInterceptor()
	{
		this.consumedDestinationVerifyUsageService = defaultConsumedDestinationVerifyUsageService();
		this.consumedDestinationPreventResetList = Collections.emptyList();
	}

	/**
	 * Constructor to create DestinationTargetValidateInterceptor
	 *
	 * @param consumedDestinationVerifyUsageService to search for item model that was assigned Consumed Destination
	 * @param consumedDestinationPreventResetList   to configure item model type code/destination attribute name
	 */
	public DestinationTargetValidateInterceptor(
			@NotNull final ConsumedDestinationVerifyUsageService consumedDestinationVerifyUsageService,
			@NotNull final List<Map<String, String>> consumedDestinationPreventResetList)
	{
		Preconditions.checkArgument(consumedDestinationVerifyUsageService != null,
				"ConsumedDestinationVerifyUsageService cannot be null");
		Preconditions.checkArgument(consumedDestinationPreventResetList != null,
				"List(consumedDestinationPreventResetList) cannot be null");

		this.consumedDestinationVerifyUsageService = consumedDestinationVerifyUsageService;
		this.consumedDestinationPreventResetList = consumedDestinationPreventResetList;
	}

	private static ConsumedDestinationVerifyUsageService defaultConsumedDestinationVerifyUsageService()
	{
		return Registry.getApplicationContext()
		               .getBean("consumedDestinationVerifyUsageService", ConsumedDestinationVerifyUsageService.class);
	}

	@Override
	public void onValidate(final DestinationTargetModel destinationTarget, final InterceptorContext interceptorContext)
			throws InterceptorException
	{
		if (!interceptorContext.isNew(destinationTarget))
		{
			validateTemplateFlag(destinationTarget);
			validateDestinationChannel(destinationTarget);
			validateDestinationTarget(destinationTarget);
		}
	}

	private void validateDestinationChannel(final DestinationTargetModel destinationTarget) throws DestinationTargetValidationException
	{
		final DestinationChannel originalValueOfDestinationChannel = destinationTarget.getItemModelContext()
		                                                                              .getOriginalValue(
				                                                                              DestinationTargetModel.DESTINATIONCHANNEL);
		if (originalValueOfDestinationChannel != destinationTarget.getDestinationChannel())
		{
			throw new DestinationTargetValidationException(CHANGING_CHANNEL_ERROR_MESSAGE);
		}
	}

	private void validateTemplateFlag(final DestinationTargetModel destinationTarget) throws DestinationTargetValidationException
	{
		final Boolean originalValueOfTemplateFlag = destinationTarget.getItemModelContext()
		                                                             .getOriginalValue(DestinationTargetModel.TEMPLATE);
		if (!Objects.equals(originalValueOfTemplateFlag, destinationTarget.getTemplate())
				&& !(originalValueOfTemplateFlag == null && BooleanUtils.isFalse(destinationTarget.getTemplate())))
		{
			throw new DestinationTargetValidationException(TEMPLATE_FLAG_ERROR_MESSAGE);
		}
	}

	private void validateDestinationTarget(final DestinationTargetModel destinationTarget) throws DestinationTargetValidationException
	{
		for (final Map<String, String> configuredConsumedDestinationAttributes : consumedDestinationPreventResetList)
		{
			raiseExceptionIfConsumedDestinationIsAssigned(getAssignedConsumedDestinations(destinationTarget),
					configuredConsumedDestinationAttributes);
		}
	}

	private List<ConsumedDestinationModel> getAssignedConsumedDestinations(final DestinationTargetModel destinationTarget)
	{
		return destinationTarget.getDestinationChannel() == DestinationChannel.DEFAULT ?
				destinationTarget.getDestinations()
				                 .stream()
				                 .filter(ConsumedDestinationModel.class::isInstance)
				                 .map(ConsumedDestinationModel.class::cast)
				                 .filter(this::isConsumedDestinationHasNonDefaultDestinationChannel)
				                 .collect(Collectors.toList()) :
				Collections.emptyList();
	}

	private boolean isConsumedDestinationHasNonDefaultDestinationChannel(final ConsumedDestinationModel destinationModel)
	{
		final var consumedDestinationTarget = destinationModel.getDestinationTarget();
		return consumedDestinationTarget != null && consumedDestinationTarget.getDestinationChannel() != DestinationChannel.DEFAULT;
	}

	private void raiseExceptionIfConsumedDestinationIsAssigned(final List<ConsumedDestinationModel> consumedDestinations,
	                                                           final Map<String, String> assignmentAttributes)
			throws DestinationTargetValidationException
	{
		for (final ConsumedDestinationModel consumedDestination : consumedDestinations)
		{
			final var itemType = assignmentAttributes.get(ITEM_TYPE_CODE);

			final var assignedItems = consumedDestinationVerifyUsageService.findModelsAssignedConsumedDestination(
					itemType, assignmentAttributes.get(ITEM_DESTINATION_ATTRIBUTE), consumedDestination);

			if (assignedItems.isPresent())
			{
				throw new DestinationTargetValidationException(
						String.format(CONSUMED_DESTINATION_RESET_ERROR_MESSAGE, consumedDestination.getId(), itemType));
			}
		}
	}
}
