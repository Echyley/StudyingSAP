/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import {
    AbstractCachedRestService,
    CacheConfig,
    OperationContextRegistered,
    RestServiceFactory
} from '@smart/utils';
import { IBaseCatalogs } from 'smarteditcommons/dtos';
import {
    contentCatalogUpdateEvictionTag,
    pageEvictionTag,
    rarelyChangingContent,
    userEvictionTag
} from 'smarteditcommons/services/cache';

const CONTENT_CATALOG_VERSION_DETAILS_RESOURCE_API =
    '/cmssmarteditwebservices/v1/sites/:siteUID/contentcatalogs';

@CacheConfig({
    actions: [rarelyChangingContent],
    tags: [userEvictionTag, pageEvictionTag, contentCatalogUpdateEvictionTag]
})
@OperationContextRegistered(CONTENT_CATALOG_VERSION_DETAILS_RESOURCE_API, ['CMS', 'INTERACTIVE'])
@Injectable()
export class ContentCatalogRestService extends AbstractCachedRestService<IBaseCatalogs> {
    constructor(restServiceFactory: RestServiceFactory) {
        super(restServiceFactory, CONTENT_CATALOG_VERSION_DETAILS_RESOURCE_API);
    }
}
