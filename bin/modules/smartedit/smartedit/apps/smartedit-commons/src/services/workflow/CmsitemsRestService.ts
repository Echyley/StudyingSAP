/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * @ngdoc overview
 * @name cmsitemsRestService
 *
 * @description
 * The cmsitemsRestService provides a service to CRUD operations on CMS Items API.
 */
import { Injectable } from '@angular/core';
import {
    EvictionTag,
    Cached,
    IRestService,
    OperationContextRegistered,
    InvalidateCache,
    IRestOptions
} from '@smart/utils';
import * as lodash from 'lodash';
import { CONTEXT_SITE_ID, CMSITEMS_UPDATE_EVENT } from '../../utils/smarteditconstants';
import { rarelyChangingContent, userEvictionTag } from '../cache';
import { IPage, ICatalogService, IRestServiceFactory, CMSItemSearch, CMSItem } from '../interfaces';

export const cmsitemsUri = `/cmswebservices/v1/sites/${CONTEXT_SITE_ID}/cmsitems`;
export const cmsitemsEvictionTag = new EvictionTag({ event: CMSITEMS_UPDATE_EVENT });

/**
 * @ngdoc service
 * @name cmsitemsRestService.cmsitemsRestService
 *
 * @description
 * Service to deal with CMS Items related CRUD operations.
 */
@OperationContextRegistered(cmsitemsUri.replace(/CONTEXT_SITE_ID/, ':CONTEXT_SITE_ID'), 'CMS')
@Injectable()
export class CmsitemsRestService {
    private readonly cmsitemsUuidsUri = `/cmswebservices/v1/sites/${CONTEXT_SITE_ID}/cmsitems/uuids`;

    private readonly resource: IRestService<CMSItem>;
    private readonly versionedResource: IRestService<CMSItem>;
    private readonly uuidsResource: IRestService<CMSItem[] | IPage<CMSItem>>;

    constructor(
        private readonly restServiceFactory: IRestServiceFactory,
        private readonly catalogService: ICatalogService
    ) {
        this.resource = restServiceFactory.get(cmsitemsUri);

        this.versionedResource = restServiceFactory.get(cmsitemsUri + '/:itemUuid');

        this.uuidsResource = restServiceFactory.get(this.cmsitemsUuidsUri);
    }

    /**
     * @ngdoc method
     * @name cmsitemsRestService.service:cmsitemsRestService#getByIdAndVersion
     * @methodOf cmsitemsRestService.cmsitemsRestService
     *
     * @description
     * Get the CMS Item that matches the given item uuid (Universally Unique Identifier) for a given version.
     *
     * @param {String} cmsitemUuid The CMS Item uuid
     * @param {String} versionId The uid of the version to be retrieved.
     *
     * @returns {Promise<CMSItem>} If request is successful, it returns a promise that resolves with the CMS Item object. If the
     * request fails, it resolves with errors from the backend.
     */
    @Cached({ actions: [rarelyChangingContent], tags: [userEvictionTag, cmsitemsEvictionTag] })
    public getByIdAndVersion<T extends CMSItem = CMSItem>(
        itemUuid: string,
        versionId: string
    ): Promise<T> {
        return this.versionedResource.get({
            itemUuid,
            versionId
        });
    }

    /**
     * @ngdoc method
     * @name cmsitemsRestService.service:cmsitemsRestService#get
     * @methodOf cmsitemsRestService.cmsitemsRestService
     *
     * @description
     * Fetch CMS Items search result by making a REST call to the CMS Items API.
     * A search can be performed by a typeCode (optionnaly in combination of a mask parameter), or by providing a list of cms items uuid.
     *
     * @param {Object} queryParams The object representing the query params
     * @param {String} queryParams.pageSize number of items in the page
     * @param {String} queryParams.currentPage current page number
     * @param {String =} queryParams.typeCode for filtering on the cms item typeCode
     * @param {String =} queryParams.mask for filtering the search
     * @param {String =} queryParams.itemSearchParams search on additional fields using a comma separated list of field name and value
     * pairs which are separated by a colon. Exact matches only.
     * @param {String =} queryParams.catalogId the catalog to search items in. If empty, the current context catalog will be used.
     * @param {String =} queryParams.catalogVersion the catalog version to search items in. If empty, the current context catalog version will be used.
     *
     * @returns {Promise<CMSItem>} If request is successful, it returns a promise that resolves with the paged search result. If the
     * request fails, it resolves with errors from the backend.
     */
    @Cached({ actions: [rarelyChangingContent], tags: [userEvictionTag, cmsitemsEvictionTag] })
    public get<T extends CMSItem = CMSItem>(queryParams: CMSItemSearch): Promise<IPage<T>> {
        return this.catalogService.retrieveUriContext().then((uriContext) => {
            const catalogDetailsParams = {
                catalogId: queryParams.catalogId || uriContext.CURRENT_CONTEXT_CATALOG,
                catalogVersion:
                    queryParams.catalogVersion || uriContext.CURRENT_CONTEXT_CATALOG_VERSION
            };

            queryParams = lodash.merge(catalogDetailsParams, queryParams);

            return this.restServiceFactory.get<IPage<T>>(cmsitemsUri).get(queryParams);
        });
    }

    /**
     * @ngdoc method
     * @name cmsitemsRestService.service:cmsitemsRestService#getByIds
     * @methodOf cmsitemsRestService.cmsitemsRestService
     *
     * @description
     * Fetch CMS Items by uuids, making a POST call to the CMS Items API.
     * A search can be performed by providing a list of cms items uuid.
     *
     * @param {string[] =} uuids list of cms item uuids
     *
     * @returns {Promise<CMSItem[]>} If request is successful, it returns a promise that resolves to the result. If the
     * request fails, it resolves with errors from the backend. Be mindful that the response payload size could
     * increase dramatically depending on the number of uuids that you send on the request.
     */
    @Cached({ actions: [rarelyChangingContent], tags: [userEvictionTag, cmsitemsEvictionTag] })
    public getByIds<T extends CMSItem = CMSItem>(
        uuids: string[],
        fields?: string
    ): Promise<IPage<T>> {
        return this.getByIdsNoCache(uuids, fields);
    }

    /**
     * @ngdoc method
     * @name cmsitemsRestService.service:cmsitemsRestService#update
     * @methodOf cmsitemsRestService.cmsitemsRestService
     *
     * @description
     * Update a CMS Item.
     *
     * @param {Object} cmsitem The object representing the CMS Item to update
     * @param {String} cmsitem.identifier The cms item identifier (uuid)
     *
     * @returns {Promise<CMSItem>} If request is successful, it returns a promise that resolves with the updated CMS Item object. If the
     * request fails, it resolves with errors from the backend.
     */
    @InvalidateCache(cmsitemsEvictionTag)
    public update<T extends CMSItem = CMSItem>(cmsitem: T, options?: IRestOptions): Promise<T> {
        return this.resource.update(cmsitem, options);
    }

    /**
     * @ngdoc method
     * @name cmsitemsRestService.service:cmsitemsRestService#delete
     * @methodOf cmsitemsRestService.cmsitemsRestService
     *
     * @description
     * Remove a CMS Item.
     *
     * @param {Number} cmsitemUuid The CMS Item uuid
     */
    @InvalidateCache(cmsitemsEvictionTag)
    public delete(identifier: string): Promise<void> {
        return this.resource.remove({
            identifier
        });
    }

    /**
     * @ngdoc method
     * @name cmsitemsRestService.service:cmsitemsRestService#create
     * @methodOf cmsitemsRestService.cmsitemsRestService
     *
     * @description
     * Create a new CMS Item.
     *
     * @param {Object} cmsitem The object representing the CMS Item to create
     *
     * @returns {Promise<CMSItem>} If request is successful, it returns a promise that resolves with the CMS Item object. If the
     * request fails, it resolves with errors from the backend.
     */
    public create<T extends CMSItem = CMSItem>(cmsitem: T): Promise<T> {
        return this.catalogService.getCatalogVersionUUid().then((catalogVersionUUid) => {
            cmsitem.catalogVersion = cmsitem.catalogVersion || catalogVersionUUid;
            if (cmsitem.onlyOneRestrictionMustApply === undefined) {
                cmsitem.onlyOneRestrictionMustApply = false;
            }
            return this.resource.save(cmsitem);
        });
    }

    /**
     * The function is same as getByIds but it doesn't use caching, it will request data from backend every time.
     *
     * If request is successful, it returns a promise that resolves to the result. If the
     * request fails, it resolves with errors from the backend. Be mindful that the response payload size could
     * increase dramatically depending on the number of uuids that you send on the request.
     */
    public getByIdsNoCache<T extends CMSItem = CMSItem>(
        uuids: string[],
        fields?: string
    ): Promise<IPage<T>> {
        uuids = Array.from(new Set(uuids)); // removing duplicates
        return this.catalogService.getCatalogVersionUUid().then((catalogVersion) => {
            const payload: any = {
                catalogVersion,
                uuids,
                fields
            };
            return this.uuidsResource.save(payload);
        });
    }

    /**
     * @ngdoc method
     * @name cmsitemsRestService.service:cmsitemsRestService#getById
     * @methodOf cmsitemsRestService.cmsitemsRestService
     *
     * @description
     * Get the CMS Item that matches the given item uuid (Universally Unique Identifier).
     *
     * @param {Number} cmsitemUuid The CMS Item uuid
     *
     * @returns {Promise<CMSItem>} If request is successful, it returns a promise that resolves with the CMS Item object. If the
     * request fails, it resolves with errors from the backend.
     */
    public getById<T extends CMSItem = CMSItem>(cmsitemUuid: string): Promise<T> {
        return this.resource.getById<T>(cmsitemUuid);
    }
}
