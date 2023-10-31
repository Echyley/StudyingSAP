/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Inject } from '@angular/core';
import {
    Cached,
    GatewayProxied,
    IPageInfoService,
    IRestService,
    IRestServiceFactory,
    LogService,
    pageChangeEvictionTag,
    PageTemplateType,
    rarelyChangingContent,
    YJQUERY_TOKEN
} from 'smarteditcommons';

/** @internal */
@GatewayProxied(
    'getPageUID',
    'getPageUUID',
    'getCatalogVersionUUIDFromPage',
    'isSameCatalogVersionOfPageAndPageTemplate'
)
export class PageInfoService extends IPageInfoService {
    public static PATTERN_SMARTEDIT_CATALOG_VERSION_UUID = /smartedit-catalog-version-uuid\-(\S+)/;

    private static PATTERN_SMARTEDIT_PAGE_UID = /smartedit-page-uid\-(\S+)/;

    private static PATTERN_SMARTEDIT_PAGE_UUID = /smartedit-page-uuid\-(\S+)/;
    private readonly pageTemplateRestService: IRestService<PageTemplateType>;

    /* @internal */
    constructor(
        @Inject(YJQUERY_TOKEN) private readonly yjQuery: JQueryStatic,
        private readonly restServiceFactory: IRestServiceFactory,
        private readonly logService: LogService
    ) {
        super();
        this.pageTemplateRestService = restServiceFactory.get('/cmswebservices/v1/pagetemplate');
    }

    /**
     * When the time comes to deprecate these 3 functions from componentHandlerService in the inner app, we will need
     * to migrate their implementations to here.
     */

    getPageUID(): Promise<string> {
        return this.try(() =>
            this.getBodyClassAttributeByRegEx(PageInfoService.PATTERN_SMARTEDIT_PAGE_UID)
        );
    }

    getPageUUID(): Promise<string> {
        return this.try(() =>
            this.getBodyClassAttributeByRegEx(PageInfoService.PATTERN_SMARTEDIT_PAGE_UUID)
        );
    }

    getCatalogVersionUUIDFromPage(): Promise<string> {
        return this.try(() =>
            this.getBodyClassAttributeByRegEx(
                PageInfoService.PATTERN_SMARTEDIT_CATALOG_VERSION_UUID
            )
        );
    }

    // eslint-disable-next-line @typescript-eslint/member-ordering
    @Cached({ actions: [rarelyChangingContent], tags: [pageChangeEvictionTag] })
    async isSameCatalogVersionOfPageAndPageTemplate(): Promise<boolean> {
        const pageUuid = await this.getPageUUID();
        const pageCatalogVersion = await this.getCatalogVersionUUIDFromPage();
        const params = {
            pageUuid
        };

        const pageTemplate = await this.pageTemplateRestService.get(params);

        return pageCatalogVersion === pageTemplate.catalogVersion;
    }

    /**
     * @param pattern Pattern of class names to search for
     *
     * @returns  Class attributes from the body element of the storefront
     */
    getBodyClassAttributeByRegEx(pattern: RegExp): string {
        try {
            const bodyClass: string = this.yjQuery('body').attr('class');
            return pattern.exec(bodyClass)[1];
        } catch {
            throw {
                name: 'InvalidStorefrontPageError',
                message: 'Error: the page is not a valid storefront page.'
            };
        }
    }

    /** @internal */
    try(func: () => string): Promise<string> {
        try {
            return Promise.resolve(func());
        } catch (e) {
            this.logService.warn(
                'Missing SmartEdit attributes on body element of the storefront - SmartEdit will resume once the attributes are added'
            );
            return Promise.reject(e);
        }
    }
}
