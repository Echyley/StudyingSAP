/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.cmsitems.populators;

import de.hybris.platform.cms2.enums.CmsApprovalStatus;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.relateditems.RelatedItemsOnPageService;
import de.hybris.platform.cms2.relatedpages.service.RelatedPageRejectionService;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Map;


public class AbstractPagePopulator implements Populator<Map<String, Object>, ItemModel>
{
	private final RelatedPageRejectionService relatedPageRejectionService;
	private final RelatedItemsOnPageService relatedItemsOnPageService;

	public AbstractPagePopulator(final RelatedPageRejectionService relatedPageRejectionService,
			final RelatedItemsOnPageService relatedItemsOnPageService)
	{
		this.relatedPageRejectionService = relatedPageRejectionService;
		this.relatedItemsOnPageService = relatedItemsOnPageService;
	}

	@Override
	public void populate(final Map<String, Object> source, final ItemModel itemModel) throws ConversionException
	{
		if (itemModel == null)
		{
			throw new ConversionException("Item Model used in the populator should not be null.");
		}
		if (source == null)
		{
			throw new ConversionException("Source map used in the populator should not be null.");
		}

		final AbstractPageModel page = ((AbstractPageModel) itemModel);

		// when the approval status is manually updated approved
		if (getRelatedPageRejectionService().hasUserChangedApprovalStatus(page)
				&& page.getApprovalStatus() == CmsApprovalStatus.APPROVED)
		{

			getRelatedItemsOnPageService()
					.getRelatedItems(page)
					.stream()
					.filter(relatedModel -> relatedModel.isSynchronizationBlocked())
					.forEach(relatedModel -> relatedModel.setSynchronizationBlocked(false));
		}
	}

	protected RelatedPageRejectionService getRelatedPageRejectionService()
	{
		return relatedPageRejectionService;
	}

	protected RelatedItemsOnPageService getRelatedItemsOnPageService()
	{
		return relatedItemsOnPageService;
	}
}
