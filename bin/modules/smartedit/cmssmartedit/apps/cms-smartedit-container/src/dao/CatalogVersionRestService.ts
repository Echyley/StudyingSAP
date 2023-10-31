/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { ICloneableCatalogVersion } from 'cmscommons';
import {
    CONTEXT_CATALOG,
    CONTEXT_CATALOG_VERSION,
    CONTEXT_SITE_ID,
    IUriContext,
    RestServiceFactory
} from 'smarteditcommons';

/**
 * @ngdoc service
 * @name CatalogVersionRestService
 * @description
 *
 * Provides REST services for the CMS catalog version endpoint
 */
@Injectable()
export class CatalogVersionRestService {
    private readonly URI: string = `/cmswebservices/v1/sites/:${CONTEXT_SITE_ID}/catalogs/:${CONTEXT_CATALOG}/versions/:${CONTEXT_CATALOG_VERSION}/targets?mode=cloneableTo`;

    constructor(private restServiceFactory: RestServiceFactory) {}

    /**
     * @ngdoc method
     * @name CatalogVersionRestService#getCloneableTargets
     * @methodOf CatalogVersionRestService
     *
     * @description
     * Fetches all cloneable target catalog versions for a given site+catalog+catalogversion
     *
     * @param {Object} uriContext A {@link resourceLocationsModule.object:UriContext UriContext}
     *
     * @returns {Object} A JSON object with a single field 'versions' containing a list of catalog versions, or an empty list.
     */
    public getCloneableTargets(uriContext: IUriContext): Promise<ICloneableCatalogVersion> {
        const rest = this.restServiceFactory.get<ICloneableCatalogVersion>(this.URI);
        return rest.get(uriContext);
    }
}
