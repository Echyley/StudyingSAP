/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmssmarteditwebservices.catalogs;

import de.hybris.platform.cms2.model.contents.ContentCatalogModel;
import de.hybris.platform.cmsfacades.data.SiteData;

import java.util.List;

/**
 * Service interface which deals with methods related to content catalog operations
 */
public interface ContentCatalogService
{
	List<SiteData> getSites(ContentCatalogModel contentCatalog);
}
