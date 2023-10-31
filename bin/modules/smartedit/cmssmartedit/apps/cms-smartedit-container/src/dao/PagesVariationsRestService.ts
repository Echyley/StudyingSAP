/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import {
    IRestService,
    PAGE_CONTEXT_CATALOG,
    PAGE_CONTEXT_CATALOG_VERSION,
    PAGE_CONTEXT_SITE_ID,
    RestServiceFactory
} from 'smarteditcommons';

interface Response {
    uids: string[];
}

/**
 * Provides REST API for the CMS variations endpoint.
 *
 * Used on: "Pages" page.
 */
@Injectable()
export class PagesVariationsRestService {
    private readonly URI = `/cmswebservices/v1/sites/${PAGE_CONTEXT_SITE_ID}/catalogs/${PAGE_CONTEXT_CATALOG}/versions/${PAGE_CONTEXT_CATALOG_VERSION}/pages/:pageId/variations`;
    private resource: IRestService<Response>;

    constructor(restServiceFactory: RestServiceFactory) {
        this.resource = restServiceFactory.get<Response>(this.URI);
    }

    /**
     * @returns A promise resolving to a list of variation page IDs.
     */
    public getVariationsForPrimaryPageId(pageId: string): Promise<string[]> {
        return this.resource.get({ pageId }).then((response) => response.uids);
    }
}
