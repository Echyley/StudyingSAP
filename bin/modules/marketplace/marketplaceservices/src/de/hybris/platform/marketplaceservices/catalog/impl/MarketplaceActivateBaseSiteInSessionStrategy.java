/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.marketplaceservices.catalog.impl;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.basecommerce.strategies.impl.DefaultActivateBaseSiteInSessionStrategy;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.marketplaceservices.vendor.VendorService;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;


/**
 * Override implementation for marketplace related activating attributes in session in
 * {@link DefaultActivateBaseSiteInSessionStrategy}
 */
public class MarketplaceActivateBaseSiteInSessionStrategy<T extends BaseSiteModel> extends
		DefaultActivateBaseSiteInSessionStrategy<T>
{
	private VendorService vendorService;

	@Override
	protected Collection<CatalogVersionModel> collectCatalogVersions(final T site)
	{
		final Collection<CatalogVersionModel> catalogVersions = super.collectCatalogVersions(site);
		catalogVersions.addAll(getVendorService().getActiveProductCatalogVersions());
		return catalogVersions;
	}

	/**
	 * Collects a {@link ContentCatalogModel}s using additionally a {@link CMSSiteModel#getContentCatalogs()} specific
	 * for CMS.
	 */
	@Override
	protected Set<CatalogModel> collectContentCatalogs(final T site)
	{
		final Set<CatalogModel> ret = new HashSet<>(super.collectContentCatalogs(site));
		if (site instanceof CMSSiteModel)
		{
			ret.addAll(((CMSSiteModel) site).getContentCatalogs());
		}
		return ret;
	}

	protected VendorService getVendorService()
	{
		return vendorService;
	}

	@Required
	public void setVendorService(final VendorService vendorService)
	{
		this.vendorService = vendorService;
	}


}
