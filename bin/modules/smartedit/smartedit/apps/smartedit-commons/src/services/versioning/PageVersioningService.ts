/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { IRestService, Page } from '@smart/utils';
import { PAGE_CONTEXT_SITE_ID } from '../../utils/smarteditconstants';
import { IRestServiceFactory, IPageVersion, PageVersionSearchPayload } from '../interfaces';

/**
 * Used to manage versions in a page.
 */
@Injectable()
export class PageVersioningService {
    private readonly pageVersionRESTService: IRestService<IPageVersion>;
    private readonly pageVersionsRollbackRESTService: IRestService<void>;
    private readonly pageVersionsServiceResourceURI = `/cmswebservices/v1/sites/${PAGE_CONTEXT_SITE_ID}/cmsitems/:pageUuid/versions`;
    private readonly pageVersionsRollbackServiceResourceURI = `/cmswebservices/v1/sites/${PAGE_CONTEXT_SITE_ID}/cmsitems/:pageUuid/versions/:versionId/rollbacks`;

    constructor(private readonly restServiceFactory: IRestServiceFactory) {
        this.pageVersionRESTService = this.restServiceFactory.get(
            this.pageVersionsServiceResourceURI
        );
        this.pageVersionsRollbackRESTService = this.restServiceFactory.get(
            this.pageVersionsRollbackServiceResourceURI
        );
    }

    /**
     * Retrieves the list of versions found for the page identified by the provided id. This method is paged.
     *
     * @param payload The payload containing search query params, including the pageable information.
     * @returns A promise that resolves to a paged list of versions.
     */
    public findPageVersions(payload: PageVersionSearchPayload): Promise<Page<IPageVersion>> {
        return this.pageVersionRESTService.page(payload);
    }

    /**
     * Retrieves the page version information for the provided versionId.
     */
    public getPageVersionForId(pageUuid: string, versionId: string): Promise<IPageVersion> {
        return this.pageVersionRESTService.get({
            pageUuid,
            identifier: versionId
        });
    }

    /**
     * Retrieves the resource URI to manage page versions.
     */
    public getResourceURI(): string {
        return this.pageVersionsServiceResourceURI;
    }

    public deletePageVersion(pageUuid: string, versionId: string): Promise<void> {
        return this.pageVersionRESTService.remove({
            pageUuid,
            identifier: versionId
        });
    }

    /**
     * Rollbacks the page to the provided version. This process will automatically create a version of the current page.
     */
    public rollbackPageVersion(pageUuid: string, versionId: string): Promise<void> {
        return this.pageVersionsRollbackRESTService.save({ pageUuid, versionId });
    }
}
