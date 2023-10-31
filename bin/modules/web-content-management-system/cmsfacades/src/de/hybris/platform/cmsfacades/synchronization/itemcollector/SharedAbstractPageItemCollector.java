/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.synchronization.itemcollector;

import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.servicelayer.data.ContentSlotData;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminContentSlotService;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminSiteService;
import de.hybris.platform.cmsfacades.common.itemcollector.ItemCollector;
import de.hybris.platform.cmsfacades.synchronization.cache.SynchronizationCacheService;
import de.hybris.platform.core.model.ItemModel;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Required;


/**
 * Collects the shared content slots of a given {@link AbstractPageModel}.
 * Returns only those content slots that are from catalog version in the active session.
 */
public class SharedAbstractPageItemCollector implements ItemCollector<AbstractPageModel>
{
	private CMSAdminContentSlotService contentSlotService;
	/**
	 * @deprecated (since = "2105", forRemoval = true)
	 */
	@Deprecated(since = "2105", forRemoval = true)
	private Predicate<String> contentSlotExistsPredicate;
	private SynchronizationCacheService synchronizationCacheService;
	private CMSAdminSiteService cmsAdminSiteService;

	@Override
	public List<ItemModel> collect(final AbstractPageModel item)
	{
	 	return getSynchronizationCacheService().getOrSetItemListCache(() -> {
			return getContentSlotService().getContentSlotsForPage(item) //
					.stream() //
					.filter(ContentSlotData::isFromMaster) //
					.filter(this::contentSlotAndVersionExist) //
					.filter(contentSlotData -> slotFromActiveCatalogVersion(contentSlotData.getContentSlot())) //
					.map(ContentSlotData::getContentSlot) //
					.collect(Collectors.toList());
		}, "SharedAbstractPageItemCollector", item);
	}

	/**
	 * Verifies that the content slot is from active catalog version.
	 * @param slot the slot to verify
	 * @return true if slot from active catalog version, false otherwise.
	 */
	protected boolean slotFromActiveCatalogVersion(final ContentSlotModel slot)
	{
		return slot.getCatalogVersion().equals(getCmsAdminSiteService().getActiveCatalogVersion());
	}

	/**
	 * Verifies that the content slot is available in {@link ContentSlotData} object.
	 * @param contentSlotData the object to verify
	 * @return true if content slot available, false otherwise.
	 */
	protected boolean contentSlotAndVersionExist(final ContentSlotData contentSlotData)
	{
		return Objects.nonNull(contentSlotData.getContentSlot()) && Objects.nonNull(contentSlotData.getContentSlot().getCatalogVersion());
	}

	protected CMSAdminContentSlotService getContentSlotService()
	{
		return contentSlotService;
	}

	@Required
	public void setContentSlotService(final CMSAdminContentSlotService contentSlotService)
	{
		this.contentSlotService = contentSlotService;
	}

	/**
	 * @deprecated (since = "2105", forRemoval = true)
	 */
	@Deprecated(since = "2105", forRemoval = true)
	protected Predicate<String> getContentSlotExistsPredicate()
	{
		return contentSlotExistsPredicate;
	}

	/**
	 * @deprecated (since = "2105", forRemoval = true)
	 */
	@Required
	@Deprecated(since = "2105", forRemoval = true)
	public void setContentSlotExistsPredicate(final Predicate<String> contentSlotExistsPredicate)
	{
		this.contentSlotExistsPredicate = contentSlotExistsPredicate;
	}

	public SynchronizationCacheService getSynchronizationCacheService()
	{
		return synchronizationCacheService;
	}

	@Required
	public void setSynchronizationCacheService(final SynchronizationCacheService synchronizationCacheService)
	{
		this.synchronizationCacheService = synchronizationCacheService;
	}

	public CMSAdminSiteService getCmsAdminSiteService()
	{
		return cmsAdminSiteService;
	}

	@Required
	public void setCmsAdminSiteService(final CMSAdminSiteService cmsAdminSiteService)
	{
		this.cmsAdminSiteService = cmsAdminSiteService;
	}
}
