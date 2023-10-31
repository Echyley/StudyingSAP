/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import {
    CONTEXT_CATALOG,
    CONTEXT_CATALOG_VERSION,
    CONTEXT_SITE_ID,
    IRestService,
    IUriContext,
    PAGE_CONTEXT_CATALOG,
    PAGE_CONTEXT_CATALOG_VERSION,
    PAGE_CONTEXT_SITE_ID,
    RestServiceFactory
} from 'smarteditcommons';

interface FallbacksResponse {
    uids: string[];
}

/**
 * Provides REST API for the CMS fallbacks endpoint.
 *
 * Used to fetch Primary Pages IDs for Variation Pages.
 */
@Injectable()
export class PagesFallbacksRestService {
    private readonly fallbacksForPageIdResource: IRestService<FallbacksResponse>;

    constructor(private restServiceFactory: RestServiceFactory) {
        this.fallbacksForPageIdResource = restServiceFactory.get(this.getUri());
    }

    public getFallbacksForPageId(pageId: string): Promise<string[]> {
        return this.fallbacksForPageIdResource.get({ pageId }).then((response) => response.uids);
    }

    public getFallbacksForPageIdAndContext(
        pageId: string,
        uriContext: IUriContext
    ): Promise<string[]> {
        const uri = this.getUri(
            uriContext[CONTEXT_SITE_ID],
            uriContext[CONTEXT_CATALOG],
            uriContext[CONTEXT_CATALOG_VERSION]
        );
        const resource = this.restServiceFactory.get<FallbacksResponse>(uri);
        return resource.get({ pageId }).then((response) => response.uids);
    }

    private getUri(
        siteId = PAGE_CONTEXT_SITE_ID,
        catalogId = PAGE_CONTEXT_CATALOG,
        catalogVersionId = PAGE_CONTEXT_CATALOG_VERSION
    ): string {
        return `/cmswebservices/v1/sites/${siteId}/catalogs/${catalogId}/versions/${catalogVersionId}/pages/:pageId/fallbacks`;
    }
}
