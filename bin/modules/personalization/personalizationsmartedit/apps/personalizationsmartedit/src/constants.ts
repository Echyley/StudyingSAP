/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { PAGE_CONTEXT_SITE_ID } from 'smarteditcommons';
export const PAGE_CONTEXT_CATALOG = 'CURRENT_PAGE_CONTEXT_CATALOG';
export const PAGE_CONTEXT_CATALOG_VERSION = 'CURRENT_PAGE_CONTEXT_CATALOG_VERSION';
export const ACTIONS_DETAILS =
    '/personalizationwebservices/v1/catalogs/:catalogId/catalogVersions/:catalogVersion/actions';
export const PAGES_CONTENT_SLOT_RESOURCE_URI = `/cmswebservices/v1/sites/${PAGE_CONTEXT_SITE_ID}/catalogs/${PAGE_CONTEXT_CATALOG}/versions/${PAGE_CONTEXT_CATALOG_VERSION}/pagescontentslots`;
