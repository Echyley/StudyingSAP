/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { assign } from 'lodash';

import {
    PAGE_CONTEXT_CATALOG,
    PAGE_CONTEXT_CATALOG_VERSION,
    PAGE_CONTEXT_SITE_ID,
    RestServiceFactory,
    URIBuilder,
    ICMSPage
} from 'smarteditcommons';

/** Provides REST services for the CMS pages rest endpoint. */
@Injectable()
export class PagesRestService {
    private readonly URI = `/cmswebservices/v1/sites/${PAGE_CONTEXT_SITE_ID}/catalogs/${PAGE_CONTEXT_CATALOG}/versions/${PAGE_CONTEXT_CATALOG_VERSION}/pages/:pageUid`;

    constructor(private restServiceFactory: RestServiceFactory) {}

    /**
     * Fetches a list of pages for a given array of UIDs.
     * It uses the current site, catalog and catalog version from the session.
     *
     * @returns A promise resolving to a list of pages, or an empty list.
     */
    public get(uids: string[]): Promise<ICMSPage[]> {
        return this.restServiceFactory
            .get<{ pages: ICMSPage[] }>(this.URI, 'pageUid')
            .get({ uids })
            .then((response) => response.pages);
    }

    /**
     * Fetches a page for a given UID.
     * It uses the current site, catalog and catalog version from the session.
     *
     * @param pageUid A page UID of the page to fetch
     */
    public getById(pageUid: string): Promise<ICMSPage> {
        return this.restServiceFactory.get(this.URI, 'pageUid').get({ pageUid });
    }

    /**
     * Updates a page for a given site, catalog, and catalog version.
     * It uses the current site, catalog and catalog version from the session.
     *
     * @param pageUid The page UID of the page to update.
     * @param payload The page object to be applied to the page resource as it exists on the backend.
     *
     * @returns A promise that resolves to a JSON object representing the updated page.
     */
    public update(pageUid: string, payload: ICMSPage): Promise<ICMSPage> {
        const uri = new URIBuilder(this.URI).replaceParams(payload).build();
        const extendedParams = assign(
            {
                pageUid
            },
            payload
        );
        return this.restServiceFactory.get(uri, 'pageUid').update(extendedParams);
    }
}
