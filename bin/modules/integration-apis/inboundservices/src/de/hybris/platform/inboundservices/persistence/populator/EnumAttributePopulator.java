/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.inboundservices.persistence.populator;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.inboundservices.persistence.AttributePopulator;
import de.hybris.platform.inboundservices.persistence.PersistenceContext;
import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor;
import de.hybris.platform.servicelayer.model.ModelService;

import javax.validation.constraints.NotNull;

import com.google.common.base.Preconditions;

/**
 * An implementation of an {@link AttributePopulator} that handles population of Hybris enum type attributes.
 */
public class EnumAttributePopulator extends AbstractAttributePopulator
{
	private final ContextReferencedItemModelService referencedItemService;

	/**
	 * Instantiates this populator with required dependencies injected.
	 *
	 * @param refService   an implementation of the service to look up item models referenced by the attribute value
	 * @param modelService an implementation of the service to persist the item models
	 */
	public EnumAttributePopulator(@NotNull final ContextReferencedItemModelService refService,
	                              @NotNull final ModelService modelService)
	{
		super(modelService);
		Preconditions.checkArgument(refService != null, "ContextReferencedItemModelService cannot be null");
		referencedItemService = refService;
	}

	@Override
	protected boolean isApplicable(final TypeAttributeDescriptor attribute, final PersistenceContext context)
	{
		return attribute.getAttributeType().isEnumeration() && !attribute.isCollection();
	}

	@Override
	protected void populateAttribute(final ItemModel item,
	                                 final TypeAttributeDescriptor attribute,
	                                 final PersistenceContext context)
	{
		final PersistenceContext referencedContext = context.getReferencedContext(attribute);
		final var referencedValue = referencedItemService.deriveReferencedItemModel(attribute, referencedContext);
		if (isNew(referencedValue))
		{
			getModelService().save(referencedValue);
		}
		final Object enumValue = getModelService().get(referencedValue.getPk());
		attribute.accessor().setValue(item, enumValue);
	}
}
