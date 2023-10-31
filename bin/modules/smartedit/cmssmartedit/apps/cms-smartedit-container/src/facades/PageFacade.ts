/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { CONTEXT_SITE_ID } from 'cmscommons';
import { isEmpty, cloneDeep } from 'lodash';
import {
    CrossFrameEventService,
    EVENTS,
    ICatalogService,
    IExperience,
    ISharedDataService,
    IUriContext,
    IUrlService,
    EXPERIENCE_STORAGE_KEY,
    CmsitemsRestService,
    ICMSPage
} from 'smarteditcommons';

@Injectable()
export class PageFacade {
    constructor(
        private cmsitemsRestService: CmsitemsRestService,
        private crossFrameEventService: CrossFrameEventService,
        private sharedDataService: ISharedDataService,
        private urlService: IUrlService,
        private catalogService: ICatalogService
    ) {}

    /**
     * Determines if a ContentPage with a given label exists in the given catalog and catalog version
     *
     * @param label The label to search for
     * @param catalogId The catalog ID to search in for the ContentPage
     * @param catalogVersion The catalog version to search in for the ContentPage
     * @return Promise resolving to a boolean determining if the ContentPage exists
     */
    public async contentPageWithLabelExists(
        label: string,
        catalogId: string,
        catalogVersion: string
    ): Promise<boolean> {
        const requestParams = {
            pageSize: 10,
            currentPage: 0,
            typeCode: 'ContentPage',
            itemSearchParams: 'label:' + label,
            catalogId,
            catalogVersion
        };

        const result = await this.cmsitemsRestService.get(requestParams);
        return result && !isEmpty(result.response);
    }

    /**
     * Retrieves the experience and builds a uri context based on its page context
     *
     * @returns the page uriContext
     */
    public async retrievePageUriContext(): Promise<IUriContext> {
        const experience = (await this.sharedDataService.get(
            EXPERIENCE_STORAGE_KEY
        )) as IExperience;
        if (!experience) {
            throw new Error('pageFacade - could not retrieve an experience from sharedDataService');
        }
        if (!experience.pageContext) {
            return null;
        }
        return this.urlService.buildUriContext(
            experience.pageContext.siteId,
            experience.pageContext.catalogId,
            experience.pageContext.catalogVersion
        );
    }

    /**
     * @param page The object representing the CMS page item to create
     * @returns If request is successful, it returns a promise that resolves with the CMS page item object. If
     * the request fails, it resolves with errors from the backend.
     */
    public async createPage(page: ICMSPage): Promise<ICMSPage> {
        if (!page.catalogVersion) {
            page.catalogVersion = await this.catalogService.getCatalogVersionUUid();
        }
        if (page.onlyOneRestrictionMustApply === undefined) {
            page.onlyOneRestrictionMustApply = false;
        }
        if (page.restrictions === undefined) {
            page.restrictions = [];
        }
        const newlyCreatedPage = await this.cmsitemsRestService.create(page);
        this.crossFrameEventService.publish(EVENTS.PAGE_CREATED, page);

        return newlyCreatedPage;
    }

    /**
     * Creates a new CMS page item for a given site.
     *
     * @param page The object representing the CMS page item to create
     * @param siteUid The uid of the target site.
     * @returns If request is successful, it returns a promise that resolves with the CMS page item object. If
     * the request fails, it resolves with errors from the backend.
     */
    public async createPageForSite(page: ICMSPage, siteUid: string): Promise<ICMSPage> {
        const catalogVersionUUid = await this.catalogService.getCatalogVersionUUid();
        page.catalogVersion = page.catalogVersion || catalogVersionUUid;
        if (page.onlyOneRestrictionMustApply === undefined) {
            page.onlyOneRestrictionMustApply = false;
        }

        page = cloneDeep(page);
        page[CONTEXT_SITE_ID] = siteUid;
        const newlyCreatedPage = await this.cmsitemsRestService.create(page);
        this.crossFrameEventService.publish(EVENTS.PAGE_CREATED, page);

        return newlyCreatedPage;
    }
}
