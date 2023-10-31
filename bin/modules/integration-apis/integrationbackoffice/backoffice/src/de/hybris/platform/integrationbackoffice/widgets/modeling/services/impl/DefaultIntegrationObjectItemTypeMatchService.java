/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.modeling.services.impl;

import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.integrationbackoffice.utility.ItemTypeMatchSelector;
import de.hybris.platform.integrationbackoffice.widgets.modeling.services.IntegrationObjectItemTypeMatchService;
import de.hybris.platform.integrationservices.enums.ItemTypeMatchEnum;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;
import de.hybris.platform.integrationservices.search.ItemTypeMatch;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import com.google.common.base.Preconditions;

/**
 * Default implementation of item type match service
 */
public class DefaultIntegrationObjectItemTypeMatchService implements IntegrationObjectItemTypeMatchService
{
	private final ItemTypeMatchSelector itemTypeMatchSelector;

	/**
	 * Default constructor
	 *
	 * @param itemTypeMatchSelector used to get the item type match of an {@link IntegrationObjectItemModel}
	 */
	public DefaultIntegrationObjectItemTypeMatchService(@NotNull final ItemTypeMatchSelector itemTypeMatchSelector)
	{
		Preconditions.checkArgument(itemTypeMatchSelector != null, "ItemTypeMatchSelector must be provided");
		this.itemTypeMatchSelector = itemTypeMatchSelector;
	}

	@Override
	public void assignItemTypeMatchForIntegrationObject(final IntegrationObjectModel integrationObjectModel,
	                                                    final Map<ComposedTypeModel, ItemTypeMatchEnum> itemTypeMatchMap)
	{
		integrationObjectModel.getItems().forEach(item -> {
			if (item.getItemTypeMatch() == null || (ItemTypeMatch.DEFAULT.name()).equals(item.getItemTypeMatch().getCode()))
			{
				if (itemTypeMatchMap.containsKey(item.getType()))
				{
					item.setItemTypeMatch(itemTypeMatchMap.get(item.getType()));
				}
				else
				{
					item.setItemTypeMatch(getDefaultItemTypeMatchEnum(item));
				}
			}
		});
	}

	@Override
	public Map<ComposedTypeModel, ItemTypeMatchEnum> groupItemTypeMatchForIntegrationObject(
			final IntegrationObjectModel integrationObjectModel)
	{
		return integrationObjectModel.getItems().stream()
		                             .filter(it -> it.getItemTypeMatch() != null)
		                             .filter(distinctByKey(IntegrationObjectItemModel::getType))
		                             .collect(Collectors.toMap(IntegrationObjectItemModel::getType,
				                             IntegrationObjectItemModel::getItemTypeMatch));
	}

	private ItemTypeMatchEnum getDefaultItemTypeMatchEnum(final IntegrationObjectItemModel integrationObjectItemModel)
	{
		return ItemTypeMatchEnum.valueOf(itemTypeMatchSelector.getToSelectItemTypeMatch(integrationObjectItemModel).name());
	}

	private static <T> Predicate<T> distinctByKey(final Function<? super T, ?> keyExtractor)
	{
		final Map<Object, Boolean> seen = new ConcurrentHashMap<>();
		return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}
}
