/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto;

import de.hybris.platform.core.model.type.TypeModel;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.IntegrationObjectDefinition;
import de.hybris.platform.integrationbackoffice.widgets.modeling.services.ReadService;
import de.hybris.platform.integrationservices.model.IntegrationObjectVirtualAttributeDescriptorModel;

import java.util.NoSuchElementException;
import java.util.Optional;

import javax.validation.constraints.NotNull;

import com.google.common.base.Preconditions;

/**
 * Represents a {@link de.hybris.platform.integrationservices.model.IntegrationObjectItemVirtualAttributeModel} stored as the
 * value of a {@link org.zkoss.zul.Listitem} in the modeling view.
 */
public class ListItemVirtualAttributeDTO extends AbstractListItemDTO
{
	private final IntegrationObjectVirtualAttributeDescriptorModel retrievalDescriptor;

	/**
	 * Represents a {@link de.hybris.platform.integrationservices.model.IntegrationObjectItemVirtualAttributeModel} stored as the
	 * value of a {@link org.zkoss.zul.Listitem} in the modeling view.
	 *
	 * @param selected            if the list item is selected.
	 * @param customUnique        if the unique checkbox is checked.
	 * @param autocreate          if the autocreate checkbox is checked.
	 * @param retrievalDescriptor the object containing the virtual attribute's type information.
	 * @param alias               the attribute name of the {@link de.hybris.platform.integrationservices.model.IntegrationObjectItemVirtualAttributeModel}.
	 */
	ListItemVirtualAttributeDTO(final boolean selected, final boolean customUnique, final boolean autocreate,
	                            @NotNull final IntegrationObjectVirtualAttributeDescriptorModel retrievalDescriptor,
	                            final String alias)
	{
		super(selected, customUnique, autocreate);
		Preconditions.checkArgument(retrievalDescriptor != null, "Retrieval descriptor can't be null.");
		this.retrievalDescriptor = retrievalDescriptor;
		this.alias = createAlias(alias);
		createDescription();
	}

	/**
	 * Instantiates a builder for this class.
	 *
	 * @param retrievalDescriptor the object containing the type information of the virtual attribute the list item represents.
	 * @return a builder for this class.
	 */
	public static ListItemVirtualAttributeDTOBuilder builder(
			@NotNull final IntegrationObjectVirtualAttributeDescriptorModel retrievalDescriptor)
	{
		Preconditions.checkArgument(retrievalDescriptor != null, "Retrieval descriptor can't be null.");
		return new ListItemVirtualAttributeDTOBuilder(retrievalDescriptor);
	}

	@Override
	public void setAlias(final String alias)
	{
		this.alias = createAlias(alias);
	}

	public IntegrationObjectVirtualAttributeDescriptorModel getRetrievalDescriptor()
	{
		if (retrievalDescriptor != null)
		{
			return retrievalDescriptor;
		}
		else
		{
			throw new ListItemDTOMissingDescriptorModelException(alias);
		}
	}

	@Override
	public final void createDescription()
	{
		description = getType().getCode();
	}

	@Override
	public AbstractListItemDTO findMatch(final IntegrationObjectDefinition currentAttributesMap,
	                                     final IntegrationMapKeyDTO parentKey)
	{
		final ListItemVirtualAttributeDTO match;
		final Optional<ListItemVirtualAttributeDTO> optionalListItemVirtualAttributeDTO = currentAttributesMap
				.getAttributesByKey(parentKey)
				.stream()
				.filter(ListItemVirtualAttributeDTO.class::isInstance)
				.map(ListItemVirtualAttributeDTO.class::cast)
				.filter(listItemDTO -> listItemDTO
						.getAlias()
						.equals(this.alias))
				.findFirst();
		match = optionalListItemVirtualAttributeDTO
				.orElseThrow(() -> new NoSuchElementException("No matching VirtualAttribute was found."));

		return match;
	}

	@Override
	public boolean isComplexType(final ReadService readService)
	{
		// Only hybris primitives are currently supported
		return false;
	}

	@Override
	public String getQualifier()
	{
		return getRetrievalDescriptor().getCode();
	}

	@Override
	public TypeModel getType()
	{
		return getRetrievalDescriptor().getType();
	}

	@Override
	public boolean isStructureType()
	{
		return false;
	}

	private String createAlias(final String alias)
	{
		return "".equals(alias) ? getRetrievalDescriptor().getCode() : alias;
	}

}
