/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.modeling.services.impl;

import static org.apache.commons.lang.StringUtils.isBlank;

import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.core.model.type.TypeModel;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.IntegrationObjectDefinition;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.AbstractListItemDTO;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.IntegrationMapKeyDTO;
import de.hybris.platform.integrationbackoffice.widgets.modeling.services.ListItemDTOTypeService;
import de.hybris.platform.integrationbackoffice.widgets.modeling.services.ReturnIntegrationObjectItemService;
import de.hybris.platform.integrationservices.model.AbstractIntegrationObjectItemAttributeModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.validation.constraints.NotNull;

import com.google.common.base.Preconditions;

/**
 * Default implementation of {@link ReturnIntegrationObjectItemService}.
 */
public class DefaultReturnIntegrationObjectItemService implements ReturnIntegrationObjectItemService
{
	private final ListItemDTOTypeService listItemDTOTypeService;

	/**
	 * Default constructor for {@link DefaultReturnIntegrationObjectItemService}.
	 *
	 * @param listItemDTOTypeService the service to obtain the type of a {@link AbstractListItemDTO}.
	 */
	public DefaultReturnIntegrationObjectItemService(@NotNull final ListItemDTOTypeService listItemDTOTypeService)
	{
		Preconditions.checkNotNull(listItemDTOTypeService, "ListItemDTOTypeService can't be null.");
		this.listItemDTOTypeService = listItemDTOTypeService;
	}

	@Override
	public void setReturnIntegrationObjectItems(final IntegrationObjectModel integrationObject,
	                                            final IntegrationObjectDefinition integrationObjectDefinition)
	{
		final Set<IntegrationObjectItemModel> integrationObjectItems = integrationObject.getItems();
		for (final IntegrationObjectItemModel item : integrationObjectItems)
		{
			final Set<AbstractIntegrationObjectItemAttributeModel> integrationObjectItemAttributes = new HashSet<>();
			integrationObjectItemAttributes.addAll(item.getAttributes());
			integrationObjectItemAttributes.addAll(item.getClassificationAttributes());

			final IntegrationMapKeyDTO mapKeyDTO = new IntegrationMapKeyDTO(item.getType(), item.getCode());
			final List<AbstractListItemDTO> relatedDTOs = integrationObjectDefinition.getAttributesByKey(mapKeyDTO);

			for (final AbstractIntegrationObjectItemAttributeModel attribute : integrationObjectItemAttributes)
			{
				final AbstractListItemDTO attributeDTO = getDTOByAlias(attribute.getAttributeName(), relatedDTOs);
				attribute.setReturnIntegrationObjectItem(determineReturnIOIForAttribute(integrationObjectItems, attributeDTO));
			}
		}
	}

	private IntegrationObjectItemModel determineReturnIOIForAttribute(
			final Set<IntegrationObjectItemModel> integrationObjectItems,
			final AbstractListItemDTO attributeDTO)
	{
		final TypeModel attributeType = listItemDTOTypeService.getDTOType(attributeDTO);
		if (attributeType instanceof ComposedTypeModel)
		{
			final String attributeTypeCode = attributeType.getCode();
			final String attributeTypeAlias = attributeDTO.getTypeAlias();
			return determineReturnIOIForAttribute(integrationObjectItems, attributeTypeCode, attributeTypeAlias);
		}
		return null;
	}

	/**
	 * Find the {@link IntegrationObjectItemModel} to set as {@link AbstractIntegrationObjectItemAttributeModel#setReturnIntegrationObjectItem(IntegrationObjectItemModel)}.
	 * If both the item and the attribute have aliases, then the item is the returnIntegrationObjectItem of the attribute if
	 * the item's code is the attribute's type alias.
	 * If neither the item nor the attribute have aliases, then the item is the returnIntegrationObjectItem of the attribute if
	 * the item type's code is the attribute type's code.
	 * In any other case (such as when the item has an alias and the attribute does not have an alias), the item is not the attribute's returnIntegrationObjectItem.
	 *
	 * @param candidateItems     potential {@link IntegrationObjectItemModel} to set as {@link AbstractIntegrationObjectItemAttributeModel#setReturnIntegrationObjectItem}.
	 * @param attributeTypeCode  the code of the type of the attribute.
	 * @param attributeTypeAlias the alias of the type of the attribute.
	 */
	private IntegrationObjectItemModel determineReturnIOIForAttribute(final Set<IntegrationObjectItemModel> candidateItems,
	                                                                  final String attributeTypeCode,
	                                                                  final String attributeTypeAlias)
	{
		final boolean attributeHasTypeAlias = !isBlank(attributeTypeAlias);
		for (final IntegrationObjectItemModel item : candidateItems)
		{
			final String itemAlias = item.getCode();
			final String itemTypeCode = item.getType().getCode();
			final boolean itemHasAlias = !itemAlias.equals(itemTypeCode);
			if (itemHasAlias && attributeHasTypeAlias && itemAlias.equals(attributeTypeAlias))
			{
				return item;
			}
			else if (!itemHasAlias && !attributeHasTypeAlias && itemTypeCode.equals(attributeTypeCode))
			{
				return item;
			}
		}
		return null;
	}

	/**
	 * Search for a {@link AbstractListItemDTO} by its alias.
	 *
	 * @param alias   the alias of the {@link AbstractListItemDTO}.
	 * @param dtoList a list of {@link AbstractListItemDTO} to search in.
	 * @return the first {@link AbstractListItemDTO} that has the given alias. Null if none found.
	 */
	private AbstractListItemDTO getDTOByAlias(final String alias, final List<AbstractListItemDTO> dtoList)
	{
		return dtoList.stream().filter(dto -> dto.getAlias().equals(alias)).findFirst().orElseThrow(NullPointerException::new);
	}
}
