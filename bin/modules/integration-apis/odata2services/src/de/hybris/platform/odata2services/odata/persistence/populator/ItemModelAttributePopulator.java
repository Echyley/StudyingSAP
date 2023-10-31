/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.odata.persistence.populator;

import de.hybris.platform.inboundservices.persistence.populator.ContextReferencedItemModelService;
import de.hybris.platform.integrationservices.util.ApplicationBeans;

/**
 * @deprecated use {@link de.hybris.platform.inboundservices.persistence.populator.ItemModelAttributePopulator}
 */
@Deprecated(since = "21.05.0-RC1", forRemoval = true)
public class ItemModelAttributePopulator
		extends de.hybris.platform.inboundservices.persistence.populator.ItemModelAttributePopulator
{
	private ContextReferencedItemModelService contextReferencedItemModelService;

	protected ContextReferencedItemModelService getContextReferencedItemModelService()
	{
		if (contextReferencedItemModelService == null)
		{
			contextReferencedItemModelService =
					ApplicationBeans.getBean("contextReferenceItemModelService", ContextReferencedItemModelService.class);
		}
		return contextReferencedItemModelService;
	}

	/**
	 * Injects implementation of the item reference service to be used for looking up items referenced in the attribute value.
	 *
	 * @param service an implementation to use. If this method is not called or the {@code service} is {@code null}, then the
	 *                default implementation present in the application context and named 'contextReferenceItemModelService'
	 *                is going to be used.
	 */
	@Override
	public void setContextReferencedItemModelService(final ContextReferencedItemModelService service)
	{
		contextReferencedItemModelService = service;
		super.setContextReferencedItemModelService(service);
	}
}
