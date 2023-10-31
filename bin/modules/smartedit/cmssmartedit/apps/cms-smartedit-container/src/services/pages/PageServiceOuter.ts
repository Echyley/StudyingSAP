/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { PagesFallbacksRestService } from 'cmssmarteditcontainer/dao/PagesFallbacksRestService';
import { PagesRestService } from 'cmssmarteditcontainer/dao/PagesRestService';
import { PagesVariationsRestService } from 'cmssmarteditcontainer/dao/PagesVariationsRestService';
import { assign, isEmpty, isNumber } from 'lodash';
import {
    pageChangeEvictionTag,
    pageEvictionTag,
    rarelyChangingContent,
    Cached,
    GatewayProxied,
    IExperienceService,
    IPageInfoService,
    IUriContext,
    IUrlService,
    Nullable,
    objectUtils,
    CmsitemsRestService,
    CMSItemSearch,
    IPage,
    IPageService,
    ICMSPage,
    CmsApprovalStatus
} from 'smarteditcommons';

@GatewayProxied()
@Injectable()
export class PageService extends IPageService {
    constructor(
        private pagesRestService: PagesRestService,
        private pagesFallbacksRestService: PagesFallbacksRestService,
        private pagesVariationsRestService: PagesVariationsRestService,
        private pageInfoService: IPageInfoService,
        private cmsitemsRestService: CmsitemsRestService,
        private experienceService: IExperienceService,
        private urlService: IUrlService
    ) {
        super();
    }

    @Cached({ actions: [rarelyChangingContent], tags: [pageEvictionTag] })
    public getPageByUuid(pageUuid: string): Promise<ICMSPage> {
        return this.cmsitemsRestService.getById<ICMSPage>(pageUuid);
    }

    @Cached({ actions: [rarelyChangingContent], tags: [pageEvictionTag, pageChangeEvictionTag] })
    public async getCurrentPageInfo(): Promise<ICMSPage> {
        const pageUUID = await this.pageInfoService.getPageUUID();
        return this.cmsitemsRestService.getById<ICMSPage>(pageUUID);
    }

    public getPageById(pageUid: string): Promise<ICMSPage> {
        return this.pagesRestService.getById(pageUid);
    }

    public async getCurrentPageInfoByVersion(versionId: string | null): Promise<ICMSPage> {
        const pageUUID = await this.pageInfoService.getPageUUID();
        return this.cmsitemsRestService.getByIdAndVersion<ICMSPage>(pageUUID, versionId);
    }

    public async primaryPageForPageTypeExists(
        pageTypeCode: string,
        uriParams?: IUriContext
    ): Promise<boolean> {
        const page = await this.getPaginatedPrimaryPagesForPageType(pageTypeCode, uriParams, {
            search: null,
            pageSize: 1,
            currentPage: 0
        });
        return page.response.length > 0;
    }

    public getPaginatedPrimaryPagesForPageType(
        pageTypeCode: string,
        uriParams?: IUriContext,
        fetchPageParams?: {
            search: string;
            pageSize: number;
            currentPage: number;
        }
    ): Promise<IPage<ICMSPage>> {
        const itemSearchParams = 'defaultPage:true,pageStatus:ACTIVE';

        const extendedParams = assign({}, uriParams || {}, fetchPageParams || {}, {
            typeCode: pageTypeCode,
            itemSearchParams
        }) as CMSItemSearch;

        if (extendedParams.search) {
            extendedParams.mask = extendedParams.search as string;
            delete extendedParams.search;
        }
        if (!isNumber(extendedParams.pageSize)) {
            extendedParams.pageSize = 10;
        }
        if (!isNumber(extendedParams.currentPage)) {
            extendedParams.currentPage = 0;
        }

        return this.cmsitemsRestService.get(extendedParams);
    }

    public async isPagePrimary(pageUid: string): Promise<boolean> {
        const fallbacks = await this.pagesFallbacksRestService.getFallbacksForPageId(pageUid);
        return fallbacks.length === 0;
    }

    public async isPagePrimaryWithContext(
        pageUid: string,
        uriContext: IUriContext
    ): Promise<boolean> {
        const fallbacks = await this.pagesFallbacksRestService.getFallbacksForPageIdAndContext(
            pageUid,
            uriContext
        );
        return fallbacks.length === 0;
    }

    public async getPrimaryPage(variationPageId: string): Promise<Nullable<ICMSPage>> {
        const fallbacks = await this.pagesFallbacksRestService.getFallbacksForPageId(
            variationPageId
        );
        return fallbacks[0]
            ? this.pagesRestService.getById(fallbacks[0])
            : Promise.resolve<ICMSPage>(null);
    }

    public async getVariationPages(primaryPageId: string): Promise<ICMSPage[]> {
        const variationPageIds =
            await this.pagesVariationsRestService.getVariationsForPrimaryPageId(primaryPageId);
        return variationPageIds.length > 0
            ? this.pagesRestService.get(variationPageIds)
            : Promise.resolve([] as ICMSPage[]);
    }

    public async updatePageById(pageUid: string, payload: ICMSPage): Promise<ICMSPage> {
        const originalPage = await this.pagesRestService.getById(pageUid);
        // This call is done to ensure that default promise properties are removed from the payload.
        const originalPagePayload = objectUtils.copy(originalPage);

        payload = { ...originalPagePayload, ...payload };
        return this.pagesRestService.update(pageUid, payload);
    }

    public async forcePageApprovalStatus(newPageStatus: CmsApprovalStatus): Promise<ICMSPage> {
        const pageInfo = await this.getCurrentPageInfo();
        const clonePageInfo = Object.assign({}, pageInfo);
        clonePageInfo.approvalStatus = newPageStatus;
        clonePageInfo.identifier = pageInfo.uuid;

        return this.cmsitemsRestService.update(clonePageInfo);
    }

    public async isPageApproved(pageParam: string | ICMSPage): Promise<boolean> {
        let page: ICMSPage;

        if (typeof pageParam === 'string') {
            page = await this.getPageByUuid(pageParam);
        } else {
            page = pageParam;
        }

        return page.approvalStatus === CmsApprovalStatus.APPROVED;
    }

    public async buildUriContextForCurrentPage(
        siteId: Nullable<string>,
        catalogId: Nullable<string>,
        catalogVersion: Nullable<string>
    ): Promise<IUriContext> {
        let uriContext = {} as IUriContext;

        if (siteId && catalogId && catalogVersion) {
            uriContext = this.urlService.buildUriContext(siteId, catalogId, catalogVersion);
        }

        if (!isEmpty(uriContext)) {
            return uriContext;
        }

        const experience = await this.experienceService.getCurrentExperience();

        return this.urlService.buildUriContext(
            experience.pageContext.siteId,
            experience.pageContext.catalogId,
            experience.pageContext.catalogVersion
        );
    }
}
