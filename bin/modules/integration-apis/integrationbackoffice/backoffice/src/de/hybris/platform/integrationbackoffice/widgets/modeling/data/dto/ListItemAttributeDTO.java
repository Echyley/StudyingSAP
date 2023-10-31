/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto;

import de.hybris.platform.core.model.type.AtomicTypeModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.CollectionTypeModel;
import de.hybris.platform.core.model.type.MapTypeModel;
import de.hybris.platform.core.model.type.TypeModel;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.IntegrationObjectDefinition;
import de.hybris.platform.integrationbackoffice.widgets.modeling.services.ReadService;

import java.util.NoSuchElementException;
import java.util.Optional;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Preconditions;

/**
 * Represents a {@link de.hybris.platform.integrationservices.model.IntegrationObjectItemAttributeModel} stored as the
 * value of a {@link org.zkoss.zul.Listitem} in the modeling view.
 */
public class ListItemAttributeDTO extends AbstractListItemDTO
{
	private final AttributeTypeDTO attributeTypeDTO;
	private final AttributeDescriptorModel attributeDescriptor;
	private final ListItemStructureType structureType;
	private final boolean isSupported;
	private final TypeModel baseType;
	private TypeModel type;

	/**
	 * Represents a {@link de.hybris.platform.integrationservices.model.IntegrationObjectItemAttributeModel} stored as the
	 * value of a {@link org.zkoss.zul.Listitem} in the modeling view.
	 *
	 * @param selected         if the list item is selected.
	 * @param customUnique     if the unique checkbox is checked.
	 * @param autocreate       if the autocreate checkbox is checked.
	 * @param alias            the attribute name of the {@link de.hybris.platform.integrationservices.model.IntegrationObjectItemAttributeModel}.
	 * @param typeAlias        the attribute's {@link de.hybris.platform.integrationservices.model.IntegrationObjectItemModel}'s code
	 *                         if the attribute is of complex type.
	 * @param attributeTypeDTO the mandatory object containing the attribute's type information.
	 */
	ListItemAttributeDTO(final boolean selected, final boolean customUnique, final boolean autocreate, final String alias,
	                     final String typeAlias, @NotNull final AttributeTypeDTO attributeTypeDTO)
	{
		super(selected, customUnique, autocreate, typeAlias);
		Preconditions.checkArgument(attributeTypeDTO != null, "Attribute type DTO can't be null.");
		this.attributeTypeDTO = attributeTypeDTO;
		this.attributeDescriptor = attributeTypeDTO.getAttributeDescriptor();
		this.structureType = attributeTypeDTO.getStructureType();
		final TypeModel attributeDescriptorType = findBaseType(attributeTypeDTO.getAttributeDescriptor(),
				attributeTypeDTO.getStructureType());
		this.baseType = attributeDescriptorType;
		this.type = (attributeTypeDTO.getType() != null) ? attributeTypeDTO.getType() : attributeDescriptorType;
		this.alias = createAlias(alias);
		createDescription();
		this.isSupported = determineIsSupported();
	}

	/**
	 * Instantiates a builder for this class.
	 *
	 * @param attributeTypeDTO the mandatory object containing the attribute's type information.
	 * @return a builder for this class.
	 */
	public static ListItemAttributeDTOBuilder builder(@NotNull final AttributeTypeDTO attributeTypeDTO)
	{
		Preconditions.checkArgument(attributeTypeDTO != null, "Attribute type DTO can't be null.");
		return new ListItemAttributeDTOBuilder(attributeTypeDTO);
	}

	public AttributeTypeDTO getAttributeTypeDTO()
	{
		return attributeTypeDTO;
	}

	public boolean isRequired()
	{
		return attributeDescriptor.getUnique() && !attributeDescriptor.getOptional();
	}

	public AttributeDescriptorModel getAttributeDescriptor()
	{
		if (attributeDescriptor != null)
		{
			return attributeDescriptor;
		}
		else
		{
			throw new ListItemDTOMissingDescriptorModelException(alias);
		}
	}

	public TypeModel getBaseType()
	{
		return baseType;
	}

	@Override
	public TypeModel getType()
	{
		return type;
	}

	public boolean isBaseType()
	{
		return type.equals(baseType);
	}

	public boolean isSubType()
	{
		return !isBaseType();
	}

	public void setType(final TypeModel type)
	{
		this.type = type;
	}

	public ListItemStructureType getStructureType()
	{
		return structureType;
	}

	@Override
	public void setAlias(final String alias)
	{
		this.alias = createAlias(alias);
	}

	public boolean isSupported()
	{
		return isSupported;
	}

	@Override
	public AbstractListItemDTO findMatch(final IntegrationObjectDefinition currentAttributesMap,
	                                     final IntegrationMapKeyDTO parentKey)
	{
		final ListItemAttributeDTO match;
		final Optional<ListItemAttributeDTO> optionalListItemAttributeDTO = currentAttributesMap.getAttributesByKey(parentKey)
		                                                                                        .stream()
		                                                                                        .filter(ListItemAttributeDTO.class::isInstance)
		                                                                                        .map(ListItemAttributeDTO.class::cast)
		                                                                                        .filter(listItemDTO -> listItemDTO.getAttributeDescriptor()
		                                                                                                                          .equals(getAttributeDescriptor()))
		                                                                                        .findFirst();

		match = optionalListItemAttributeDTO.orElseThrow(() -> new NoSuchElementException("No AttributeDescriptor was found"));

		return match;
	}

	@Override
	public boolean isComplexType(final ReadService readService)
	{
		return readService.isComplexType(type);
	}

	@Override
	public String getQualifier()
	{
		return getAttributeDescriptor().getQualifier();
	}

	@Override
	public final void createDescription()
	{
		final String typeLabel = !StringUtils.isEmpty(getTypeAlias()) ? getTypeAlias() : getType().getCode();
		if (structureType == ListItemStructureType.COLLECTION)
		{
			description = String.format("Collection [%s]", typeLabel);
		}
		else if (structureType == ListItemStructureType.MAP)
		{
			description = String.format("Map [%s]", typeLabel);
		}
		else
		{
			description = typeLabel;
		}
	}

	@Override
	public boolean isStructureType()
	{
		return structureType != ListItemStructureType.NONE;
	}

	private boolean determineIsSupported()
	{
		final AttributeDescriptorModel descriptor = this.getAttributeDescriptor();
		final boolean isMap = descriptor.getAttributeType() instanceof MapTypeModel;
		if (isMap)
		{
			final MapTypeModel mapTypeModel = (MapTypeModel) descriptor.getAttributeType();
			final Boolean localized = descriptor.getLocalized();
			final boolean isPrimitiveMap = mapTypeModel.getReturntype() instanceof AtomicTypeModel && mapTypeModel.getArgumentType() instanceof AtomicTypeModel;
			final boolean isLocalized = Boolean.TRUE.equals(localized) && mapTypeModel.getReturntype() instanceof AtomicTypeModel;
			return isPrimitiveMap || isLocalized;
		}
		else
		{
			return true;
		}
	}

	private TypeModel findBaseType(final AttributeDescriptorModel attributeDescriptor, final ListItemStructureType structureType)
	{
		TypeModel typeModel;
		if (structureType == ListItemStructureType.COLLECTION)
		{
			final CollectionTypeModel collectionType = (CollectionTypeModel) attributeDescriptor.getAttributeType();
			typeModel = collectionType.getElementType();
		}
		else if (structureType == ListItemStructureType.MAP)
		{
			typeModel = attributeDescriptor.getAttributeType();
			final MapTypeModel mapTypeModel = (MapTypeModel) attributeDescriptor.getAttributeType();
			if (mapTypeModel.getReturntype() instanceof CollectionTypeModel)
			{
				final CollectionTypeModel collectionTypeModel = (CollectionTypeModel) mapTypeModel.getReturntype();
				typeModel = collectionTypeModel.getElementType();
			}
		}
		else
		{
			typeModel = attributeDescriptor.getAttributeType();
		}
		return typeModel;
	}

	private String createAlias(final String alias)
	{
		return "".equals(alias) ? getAttributeDescriptor().getQualifier() : alias;
	}
}
