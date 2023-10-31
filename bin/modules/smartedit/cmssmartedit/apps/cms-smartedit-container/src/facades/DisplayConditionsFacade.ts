/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import {
    IUriContext,
    Page as SePage,
    SelectItem,
    LogService,
    IPageService,
    ICMSPage
} from 'smarteditcommons';
import { PageRestrictionsService } from '../services/pageRestrictions/PageRestrictionsService';

export interface IDisplayConditionsPageVariation {
    pageName: string;
    creationDate: string | Date;
    restrictions: number;
}

export interface IDisplayConditionsPageInfo {
    pageName: string;
    pageType: string;
    isPrimary: boolean;
}

export interface IDisplayConditionsPrimaryPage {
    uid: string;
    uuid: string;
    name: string;
    label: string;
}

@Injectable()
export class DisplayConditionsFacade {
    constructor(
        private pageService: IPageService,
        private pageRestrictionsService: PageRestrictionsService,
        private logService: LogService
    ) {}

    public async getPageInfoForPageUid(pageId: string): Promise<IDisplayConditionsPageInfo> {
        const pagePromise = this.pageService.getPageById(pageId);
        const displayConditionsPromise = this.pageService.isPagePrimary(pageId);

        const [page, isPrimary] = await Promise.all([pagePromise, displayConditionsPromise]);

        return {
            pageName: page.name,
            pageType: page.typeCode,
            isPrimary
        };
    }

    public async getVariationsForPageUid(
        primaryPageId: string
    ): Promise<IDisplayConditionsPageVariation[]> {
        const variationPages = await this.pageService.getVariationPages(primaryPageId);

        if (variationPages.length === 0) {
            return [];
        }

        const restrictionCounts = await Promise.all(
            variationPages.map((variationPage) =>
                this.pageRestrictionsService.getPageRestrictionsCountForPageUID(variationPage.uid)
            )
        );

        return variationPages.map((variationPage, index) => ({
            pageName: variationPage.name,
            creationDate: variationPage.creationtime,
            restrictions: restrictionCounts[index]
        }));
    }

    public updatePage(pageId: string, pageData: ICMSPage): Promise<ICMSPage> {
        return this.pageService.updatePageById(pageId, pageData);
    }

    public isPagePrimary(pageId: string): Promise<boolean> {
        return this.pageService.isPagePrimary(pageId);
    }

    public getPrimaryPageForVariationPage(
        variationPageId: string
    ): Promise<Partial<IDisplayConditionsPrimaryPage>> {
        return this.pageService.getPrimaryPage(variationPageId).then((primaryPage: ICMSPage) => ({
            uid: primaryPage.uid,
            name: primaryPage.name,
            label: primaryPage.label
        }));
    }

    public async getPrimaryPagesForPageType(
        pageTypeCode: string,
        uriParams?: IUriContext,
        fetchPageParams?: {
            search: string;
            pageSize: number;
            currentPage: number;
        }
    ): Promise<SePage<SelectItem>> {
        try {
            const page = await this.pageService.getPaginatedPrimaryPagesForPageType(
                pageTypeCode,
                uriParams,
                fetchPageParams
            );

            return {
                pagination: page.pagination,
                results: page.response.map((rawPage) => ({
                    id: rawPage.uid,
                    label: rawPage.name || rawPage.label
                }))
            };
        } catch (error) {
            this.logService.warn(
                `[getPrimaryPagesForPageType] - Cannot retrieve list of primary pages. ${error}`
            );
            return Promise.reject();
        }
    }
}
