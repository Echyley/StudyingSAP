/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.apiregistryservices.services.impl;

import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.apiregistryservices.services.ConsumedDestinationVerifyUsageService;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

/**
 * Default implementation of {@link ConsumedDestinationVerifyUsageService}
 */
public class DefaultConsumedDestinationVerifyUsageService implements ConsumedDestinationVerifyUsageService
{
	private static final String SELECT_PK_FROM_WHERE_QUERY = "SELECT {item.pk} FROM {%s as item} WHERE {item.%s}=?%s";
	private static final String CONSUMED_DESTINATION = "consumedDestination";
	private final FlexibleSearchService flexibleSearchService;
	private final TypeService typeService;

	private static final Logger LOG = LoggerFactory.getLogger(DefaultConsumedDestinationVerifyUsageService.class);

	/**
	 * constructor
	 *
	 * @param flexibleSearchService to search for the items that has consumed destination {@link ConsumedDestinationModel}.
	 * @param typeService           to verify item type and attribute
	 */
	DefaultConsumedDestinationVerifyUsageService(@NotNull final FlexibleSearchService flexibleSearchService,
	                                             @NotNull final TypeService typeService)
	{
		Preconditions.checkArgument(flexibleSearchService != null, "Non-null flexibleSearchService must be provided");
		Preconditions.checkArgument(typeService != null, "Non-null typeService must be provided");
		this.flexibleSearchService = flexibleSearchService;
		this.typeService = typeService;
	}

	@Override
	public Optional<List<ItemModel>> findModelsAssignedConsumedDestination(@NotNull final String typeCode,
	                                                                       @NotNull final String attributeName,
	                                                                       @NotNull final ConsumedDestinationModel consumedDestinationModel)
	{
		Preconditions.checkArgument(typeCode != null, "Non-null item type code must be provided");
		Preconditions.checkArgument(attributeName != null, "Non-null attribute name of item must be provided");
		Preconditions.checkArgument(consumedDestinationModel != null, "Non-null consumed destination must be provided");

		if (!isValidItemTypeAndAttribute(typeCode, attributeName))
		{
			throw new IllegalArgumentException(String.format(
					"The item model with type code: [%s] has not been found or the attribute: [%s] is not valid consumed destination attribute, Please check your configuration!", typeCode,
					attributeName));
		}

		final String queryString = String.format(SELECT_PK_FROM_WHERE_QUERY, typeCode, attributeName, CONSUMED_DESTINATION);
		final FlexibleSearchQuery query = new FlexibleSearchQuery(queryString);
		final Map<String, Object> queryParams = new HashMap<>();
		queryParams.put(CONSUMED_DESTINATION, consumedDestinationModel.getPk());
		query.addQueryParameters(queryParams);

		final SearchResult<ItemModel> queryResult = flexibleSearchService.search(query);
		return queryResult.getResult().isEmpty() ? Optional.empty() : Optional.of(queryResult.getResult());
	}

	private boolean isValidItemTypeAndAttribute(final String typeCode, final String attributeName)
	{
		try
		{
			return typeService.getAttributeDescriptor(typeCode, attributeName)
			                  .getAttributeType()
			                  .getCode()
			                  .equals(ConsumedDestinationModel._TYPECODE);
		}
		catch (final UnknownIdentifierException exception)
		{
			LOG.error(String.format("Does not have item model with type code:[%s] and attribute name:[%s]", typeCode,
					attributeName));
		}
		return false;
	}
}
