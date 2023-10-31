/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.inboundservices.persistence.populator;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.inboundservices.persistence.AttributePopulator;
import de.hybris.platform.inboundservices.persistence.PersistenceContext;
import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor;
import de.hybris.platform.integrationservices.util.ApplicationBeans;

/**
 * An implementation of an {@link AttributePopulator}, that handles populating a {@link ItemModel}s
 * attribute for a single items model reference {@link TypeAttributeDescriptor}.
 */
public class ItemModelAttributePopulator extends AbstractAttributePopulator
{
	private ContextReferencedItemModelService contextReferencedItemModelService;

	@Override
	protected boolean isApplicable(final TypeAttributeDescriptor attribute, final PersistenceContext context)
	{
		return !(attribute.isCollection()
				|| attribute.isPrimitive()
				|| attribute.isMap()
				|| attribute.getAttributeType().isEnumeration());
	}

	@Override
	protected void populateAttribute(final ItemModel item,
	                                 final TypeAttributeDescriptor attribute,
	                                 final PersistenceContext context)
	{
		final PersistenceContext referencedContext = context.getReferencedContext(attribute);
		final var value = getContextReferencedItemModelService()
				.deriveReferencedItemModel(attribute, referencedContext);
		attribute.accessor().setValue(item, value);
	}

	/**
	 * Injects implementation of the item reference service to be used for looking up items referenced in the attribute value.
	 *
	 * @param service an implementation to use. If this method is not called or the {@code service} is {@code null}, then the
	 *                default implementation present in the application context and named 'contextReferenceItemModelService'
	 *                is going to be used.
	 */
	public void setContextReferencedItemModelService(final ContextReferencedItemModelService service)
	{
		contextReferencedItemModelService = service;
	}

	ContextReferencedItemModelService getContextReferencedItemModelService()
	{
		if (contextReferencedItemModelService == null)
		{
			contextReferencedItemModelService =
					ApplicationBeans.getBean("contextReferenceItemModelService", ContextReferencedItemModelService.class);
		}
		return contextReferencedItemModelService;
	}
}
