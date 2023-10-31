/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { PagesFallbacksRestService } from 'cmssmarteditcontainer/dao/PagesFallbacksRestService';
import { PagesRestService } from 'cmssmarteditcontainer/dao/PagesRestService';
import { PagesVariationsRestService } from 'cmssmarteditcontainer/dao/PagesVariationsRestService';
import { PageService } from 'cmssmarteditcontainer/services/pages';
import * as lo from 'lodash';
import {
    annotationService,
    pageChangeEvictionTag,
    pageEvictionTag,
    rarelyChangingContent,
    Cached,
    GatewayProxied,
    IExperience,
    IExperienceService,
    IPageInfoService,
    IUriContext,
    IUrlService,
    CmsitemsRestService,
    ICMSPage,
    CMSPageTypes,
    CmsApprovalStatus
} from 'smarteditcommons';

describe('Page Service ->', () => {
    const PAGE_UID = 'SOME PAGE ID';
    const PAGE_UUID = 'SOME PAGE UUID';
    const PAGE_TYPE = CMSPageTypes.CategoryPage;
    const URI_CONTEXT: IUriContext = {
        siteUID: 'mySite',
        catalogId: 'myCatalog',
        catalogVersion: 'myCatalogVersion'
    };
    const DEFAULT_PAYLOAD = {
        typeCode: PAGE_TYPE,
        itemSearchParams: 'defaultPage:true,pageStatus:ACTIVE',
        currentPage: 0,
        pageSize: 10
    };

    let pagesRestService: jasmine.SpyObj<PagesRestService>;
    let pagesFallbacksRestService: jasmine.SpyObj<PagesFallbacksRestService>;
    let pagesVariationsRestService: jasmine.SpyObj<PagesVariationsRestService>;
    let cmsitemsRestService: jasmine.SpyObj<CmsitemsRestService>;
    let experienceService: jasmine.SpyObj<IExperienceService>;
    let pageInfoService: jasmine.SpyObj<IPageInfoService>;
    let urlService: jasmine.SpyObj<IUrlService>;

    let page: ICMSPage;
    let pageService: PageService;

    const siteId = 'someSiteId';
    const catalogId = 'someCatalogId';
    const catalogVersion = 'someCatalogVersion';

    beforeEach(() => {
        page = {
            name: 'some page name',
            uid: PAGE_UID,
            uuid: PAGE_UUID,
            title: { en: 'some title' },
            typeCode: CMSPageTypes.ContentPage,
            defaultPage: true,
            homepage: false,
            catalogVersion: null,
            restrictions: [],
            approvalStatus: CmsApprovalStatus.CHECK,
            pageStatus: null,
            displayStatus: null,
            masterTemplate: null,
            masterTemplateId: null,
            creationtime: null,
            modifiedtime: null
        };

        pagesRestService = jasmine.createSpyObj<PagesRestService>('pagesRestService', [
            'get',
            'getById',
            'update'
        ]);
        pagesFallbacksRestService = jasmine.createSpyObj<PagesFallbacksRestService>(
            'pagesFallbackRestService',
            ['getFallbacksForPageId', 'getFallbacksForPageIdAndContext']
        );
        pagesVariationsRestService = jasmine.createSpyObj<PagesVariationsRestService>(
            'pagesVariationsRestService',
            ['getVariationsForPrimaryPageId']
        );
        cmsitemsRestService = jasmine.createSpyObj<CmsitemsRestService>('cmsitemsRestService', [
            'get',
            'getById',
            'getByIdAndVersion',
            'update'
        ]);
        experienceService = jasmine.createSpyObj<IExperienceService>('experienceService', [
            'getCurrentExperience'
        ]);
        pageInfoService = jasmine.createSpyObj<IPageInfoService>('pageInfoService', [
            'getPageUUID'
        ]);
        urlService = jasmine.createSpyObj<IUrlService>('urlService', ['buildUriContext']);

        pageService = new PageService(
            pagesRestService,
            pagesFallbacksRestService,
            pagesVariationsRestService,
            pageInfoService,
            cmsitemsRestService,
            experienceService,
            urlService
        );
    });

    describe('initialization', () => {
        it('WHEN initialized THEN it must be GatewayProxied', () => {
            expect(annotationService.getClassAnnotation(PageService, GatewayProxied)).toEqual([]);
        });
    });

    describe('getPageById', () => {
        it('WHEN getPageById is called THEN it will retrieve the page matching that uid', async () => {
            // GIVEN
            pagesRestService.getById.and.returnValue(Promise.resolve(page));

            // WHEN
            const value = await pageService.getPageById(PAGE_UID);

            // THEN
            expect(value).toEqual(page);
        });
    });

    describe('getPageByUuid', () => {
        it('WHEN initialized THEN it is annotated with pageEvictionTag', () => {
            // GIVEN
            const decoratorObj = annotationService.getMethodAnnotation(
                PageService,
                'getPageByUuid',
                Cached
            );

            // WHEN/THEN
            expect(decoratorObj).toEqual(
                jasmine.objectContaining([
                    {
                        actions: [rarelyChangingContent],
                        tags: [pageEvictionTag]
                    }
                ])
            );
        });

        it('WHEN getPageByUuid is called THEN it will retrieve the page matching that uuid', async () => {
            // GIVEN
            cmsitemsRestService.getById.and.returnValue(Promise.resolve(page));

            // WHEN
            const value = await pageService.getPageByUuid(PAGE_UUID);

            // THEN
            expect(value).toEqual(page);
        });
    });

    describe('getCurrentPageInfo', () => {
        it('WHEN initialized THEN it is annotated with pageEvictionTag AND pageChangeEvictionTag', () => {
            // GIVEN
            const decoratorObj = annotationService.getMethodAnnotation(
                PageService,
                'getCurrentPageInfo',
                Cached
            );

            // WHEN/THEN
            expect(decoratorObj).toEqual(
                jasmine.objectContaining([
                    {
                        actions: [rarelyChangingContent],
                        tags: [pageEvictionTag, pageChangeEvictionTag]
                    }
                ])
            );
        });

        it('WHEN getCurrentPageInfo is called THEN it will return the page information of the current page loaded in the storefront', async () => {
            // GIVEN
            pageInfoService.getPageUUID.and.returnValue(Promise.resolve(PAGE_UUID));
            cmsitemsRestService.getById.and.returnValue(Promise.resolve(page));

            // WHEN
            const value = await pageService.getCurrentPageInfo();

            // THEN
            expect(cmsitemsRestService.getById).toHaveBeenCalledWith(PAGE_UUID);
            expect(value).toEqual(page);
        });
    });

    describe('getCurrentPageInfoByVersion', () => {
        it('WHEN getCurrentPageInfoByVersion is called it returns the information of the version of the current page loaded in the storefront', async () => {
            // GIVEN
            const versionId = 'some version Id';
            cmsitemsRestService.getByIdAndVersion.and.returnValue(Promise.resolve(page));
            pageInfoService.getPageUUID.and.returnValue(Promise.resolve(PAGE_UUID));

            // WHEN
            const value = await pageService.getCurrentPageInfoByVersion(versionId);

            // THEN
            expect(value).toEqual(page);
            expect(cmsitemsRestService.getByIdAndVersion).toHaveBeenCalledWith(
                PAGE_UUID,
                versionId
            );
        });
    });

    describe('isPagePrimary', () => {
        const MOCK_FALLBACK_PAGE_IDS = ['some primary page id'];

        it('GIVEN no fallbacks exist for the current page WHEN isPagePrimary is called THEN it should return true', async () => {
            // GIVEN
            pagesFallbacksRestService.getFallbacksForPageId.and.returnValue(Promise.resolve([]));

            // WHEN
            const value = await pageService.isPagePrimary(PAGE_UID);

            // THEN
            expect(value).toEqual(true);
            expect(pagesFallbacksRestService.getFallbacksForPageId).toHaveBeenCalledWith(PAGE_UID);
        });

        it('GIVEN there are fallbacks for the current page WHEN isPagePrimary is called THEN it should return false', async () => {
            // GIVEN
            pagesFallbacksRestService.getFallbacksForPageId.and.returnValue(
                Promise.resolve(MOCK_FALLBACK_PAGE_IDS)
            );

            // WHEN
            const value = await pageService.isPagePrimary(PAGE_UID);

            // THEN
            expect(value).toEqual(false);
            expect(pagesFallbacksRestService.getFallbacksForPageId).toHaveBeenCalledWith(PAGE_UID);
        });

        it('GIVEN no fallbacks exist for the current page WHEN isPagePrimaryWithContext is called THEN it should return true', async () => {
            // GIVEN
            pagesFallbacksRestService.getFallbacksForPageIdAndContext.and.returnValue(
                Promise.resolve([])
            );

            // WHEN
            const value = await pageService.isPagePrimaryWithContext(PAGE_UID, URI_CONTEXT);

            // THEN
            expect(value).toEqual(true);
            expect(pagesFallbacksRestService.getFallbacksForPageIdAndContext).toHaveBeenCalledWith(
                PAGE_UID,
                URI_CONTEXT
            );
        });

        it('GIVEN there are fallbacks for the current page WHEN isPagePrimaryWithContext is called THEN it should return false', async () => {
            // GIVEN
            pagesFallbacksRestService.getFallbacksForPageIdAndContext.and.returnValue(
                Promise.resolve(MOCK_FALLBACK_PAGE_IDS)
            );

            // WHEN
            const value = await pageService.isPagePrimaryWithContext(PAGE_UID, URI_CONTEXT);

            // THEN
            expect(value).toEqual(false);
            expect(pagesFallbacksRestService.getFallbacksForPageIdAndContext).toHaveBeenCalledWith(
                PAGE_UID,
                URI_CONTEXT
            );
        });
    });

    describe('getPrimaryPage', () => {
        const PRIMARY_PAGE_UID = 'some primary page uid';

        it('GIVEN uid corresponds to primary page WHEN getPrimaryPage is called THEN it should not return an empty promise', async () => {
            // GIVEN
            pagesFallbacksRestService.getFallbacksForPageId.and.returnValue(Promise.resolve([]));

            // WHEN
            const value = await pageService.getPrimaryPage(PAGE_UID);

            // THEN
            expect(value).toEqual(null);
        });

        it('GIVEN uid corresponds to page variation WHEN getPrimaryPage is called THEN it returns the primary page', async () => {
            // GIVEN
            pagesFallbacksRestService.getFallbacksForPageId.and.returnValue(
                Promise.resolve([PRIMARY_PAGE_UID])
            );
            pagesRestService.getById.and.returnValue(page as any);

            // WHEN
            const value = await pageService.getPrimaryPage(PAGE_UID);

            // THEN
            expect(value).toEqual(page);
            expect(pagesRestService.getById).toHaveBeenCalledWith(PRIMARY_PAGE_UID);
        });
    });

    describe('getVariationPages', () => {
        const VARIATION_PAGE_ID = 'some variation page ID';

        it('GIVEN there are no variations for primary page WHEN getVariationPages is called THEN it returns a promise with an empty list', async () => {
            // GIVEN
            pagesVariationsRestService.getVariationsForPrimaryPageId.and.returnValue(
                Promise.resolve([])
            );

            // WHEN
            const value = await pageService.getVariationPages(PAGE_UID);

            // THEN
            expect(value).toEqual([]);
        });

        it('GIVEN there are variations for primary page WHEN getVariationPages is called THEN it returns a promise with the list of variations', async () => {
            // GIVEN
            pagesVariationsRestService.getVariationsForPrimaryPageId.and.returnValue(
                Promise.resolve([VARIATION_PAGE_ID])
            );
            pagesRestService.get.and.returnValue(Promise.resolve([page]));

            // WHEN
            const value = await pageService.getVariationPages(PAGE_UID);

            // THEN
            expect(value).toEqual([page]);
            expect(pagesRestService.get).toHaveBeenCalledWith([VARIATION_PAGE_ID]);
        });
    });

    describe('updatePageById', () => {
        it('WHEN updatePageById is called THEN it updates the page with the provided information', async () => {
            // GIVEN
            const newPageName = 'Some new page name';
            const newPageInfo = lo.assign({}, page, {
                pageName: newPageName
            });

            pagesRestService.getById.and.returnValue(Promise.resolve(page));
            pagesRestService.update.and.returnValue(Promise.resolve(newPageInfo));

            // WHEN
            const value = await pageService.updatePageById(PAGE_UID, newPageInfo);

            // THEN
            expect(value).toEqual(newPageInfo);
            expect(pagesRestService.update).toHaveBeenCalledWith(PAGE_UID, newPageInfo);
        });
    });

    describe('forcePageApprovalStatus', () => {
        const NEW_APPROVAL_STATUS = CmsApprovalStatus.APPROVED;

        it('WHEN forcePageApprovalStatus is called THEN it retrieves the current page info AND updates it with the new status', async () => {
            // GIVEN
            const expectedUpdatePayload = lo.assign({}, page, {
                approvalStatus: NEW_APPROVAL_STATUS,
                identifier: PAGE_UUID
            });

            const expectedPageInfo = lo.assign({}, page, {
                approvalStatus: NEW_APPROVAL_STATUS
            });

            pageInfoService.getPageUUID.and.returnValue(Promise.resolve(PAGE_UUID));
            cmsitemsRestService.getById.and.returnValue(Promise.resolve(page));
            cmsitemsRestService.update.and.returnValue(Promise.resolve(expectedPageInfo));

            // WHEN
            const value = await pageService.forcePageApprovalStatus(NEW_APPROVAL_STATUS);

            // THEN
            expect(value).toEqual(expectedPageInfo);
            expect(cmsitemsRestService.update).toHaveBeenCalledWith(expectedUpdatePayload);
        });
    });

    describe('isPageApproved', () => {
        beforeEach(() => {
            cmsitemsRestService.getById.and.returnValue(Promise.resolve(page));
        });

        it('GIVEN the uuid of an unapproved page WHEN isPageApproved is called THEN it returns false', async () => {
            // GIVEN
            cmsitemsRestService.getById.calls.reset();

            // WHEN
            const value = await pageService.isPageApproved(PAGE_UUID);

            // THEN
            expect(value).toEqual(false);
            expect(cmsitemsRestService.getById).toHaveBeenCalledWith(PAGE_UUID);
        });

        it('GIVEN the uuid of an approved page WHEN isPageApproved is called THEN it returns true', async () => {
            // GIVEN
            cmsitemsRestService.getById.calls.reset();
            page.approvalStatus = CmsApprovalStatus.APPROVED;

            // WHEN
            const value = await pageService.isPageApproved(PAGE_UUID);

            // THEN
            expect(value).toEqual(true);
            expect(cmsitemsRestService.getById).toHaveBeenCalledWith(PAGE_UUID);
        });

        it('GIVEN an unapproved page WHEN isPageApproved is called THEN it returns false', async () => {
            // GIVEN
            cmsitemsRestService.getById.calls.reset();

            // WHEN
            const value = await pageService.isPageApproved(page);

            // THEN
            expect(value).toEqual(false);
            expect(cmsitemsRestService.getById).not.toHaveBeenCalledWith(PAGE_UUID);
        });

        it('GIVEN an approved page WHEN isPageApproved is called THEN it returns true', async () => {
            // GIVEN
            cmsitemsRestService.getById.calls.reset();
            page.approvalStatus = CmsApprovalStatus.APPROVED;

            // WHEN
            const value = await pageService.isPageApproved(page);

            // THEN
            expect(value).toEqual(true);
            expect(cmsitemsRestService.getById).not.toHaveBeenCalledWith(PAGE_UUID);
        });
    });

    describe('buildUriContextForCurrentPage', () => {
        beforeEach(() => {
            urlService.buildUriContext.and.returnValue({
                CURRENT_CONTEXT_SITE_ID: siteId,
                CURRENT_CONTEXT_CATALOG: catalogId,
                CURRENT_CONTEXT_CATALOG_VERSION: catalogVersion
            });
        });
        it('GIVEN siteId, catalogId and catalogVersion have been provided WHEN buildUriContextForCurrentPage is called THEN it returns the page context', async () => {
            // GIVEN
            experienceService.getCurrentExperience.calls.reset();

            // WHEN
            const value = await pageService.buildUriContextForCurrentPage(
                siteId,
                catalogId,
                catalogVersion
            );

            // THEN
            expect(experienceService.getCurrentExperience).not.toHaveBeenCalled();
            expect(value).toEqual({
                CURRENT_CONTEXT_SITE_ID: 'someSiteId',
                CURRENT_CONTEXT_CATALOG: 'someCatalogId',
                CURRENT_CONTEXT_CATALOG_VERSION: 'someCatalogVersion'
            });
        });

        it('GIVEN siteId, catalogId and catalogVersion have not been provided WHEN buildUriContextForCurrentPage is called THEN it returns the page context from the current experience', async () => {
            // GIVEN
            const experience: IExperience = {
                siteDescriptor: null,
                catalogDescriptor: null,
                productCatalogVersions: null,
                time: null,
                pageContext: {
                    siteId,
                    catalogName: null,
                    catalogId,
                    catalogVersion,
                    catalogVersionUuid: null,
                    active: true
                }
            };

            experienceService.getCurrentExperience.and.returnValue(Promise.resolve(experience));

            // WHEN
            const value = await pageService.buildUriContextForCurrentPage(null, null, null);

            // THEN
            expect(experienceService.getCurrentExperience).toHaveBeenCalled();
            expect(value).toEqual({
                CURRENT_CONTEXT_SITE_ID: siteId,
                CURRENT_CONTEXT_CATALOG: catalogId,
                CURRENT_CONTEXT_CATALOG_VERSION: catalogVersion
            });
        });
    });

    describe('primaryPageForPageTypeExists', () => {
        it('GIVEN primary page for page type exists THEN it should return true', async () => {
            // GIVEN
            cmsitemsRestService.get.and.returnValue(
                Promise.resolve<any>({
                    response: [page]
                })
            );

            // WHEN
            const actual = await pageService.primaryPageForPageTypeExists(PAGE_TYPE);

            // THEN
            expect(actual).toBe(true);
        });

        it('GIVEN primary page for page type does not exist THEN it should return false', async () => {
            // GIVEN
            cmsitemsRestService.get.and.returnValue(
                Promise.resolve<any>({
                    response: []
                })
            );

            // WHEN
            const actual = await pageService.primaryPageForPageTypeExists(PAGE_TYPE);

            // THEN
            expect(actual).toBe(false);
        });
    });

    describe('getPaginatedPrimaryPagesForPageType', () => {
        it(
            'GIVEN fetchPageParams parameter is not provided WHEN getPaginatedPrimaryPagesForPageType is called' +
                'THEN it calls the CMS Items API with the right parameters',
            async () => {
                await pageService.getPaginatedPrimaryPagesForPageType(PAGE_TYPE, URI_CONTEXT);

                const expectedPayload = lo.assign({}, URI_CONTEXT, DEFAULT_PAYLOAD);

                expect(cmsitemsRestService.get).toHaveBeenCalledWith(expectedPayload);
            }
        );

        it(
            'GIVEN fetchPageParams parameter is not provided WHEN getPaginatedPrimaryPagesForPageType is called' +
                'THEN it calls the CMS Items API with the right parameters',
            async () => {
                await pageService.getPaginatedPrimaryPagesForPageType(PAGE_TYPE, URI_CONTEXT, {
                    search: 'somelabel',
                    pageSize: 20,
                    currentPage: null
                });
                const expectedPayload1 = lo.assign({}, URI_CONTEXT, DEFAULT_PAYLOAD, {
                    mask: 'somelabel',
                    pageSize: 20,
                    currentPage: 0
                });
                expect(cmsitemsRestService.get).toHaveBeenCalledWith(expectedPayload1);

                await pageService.getPaginatedPrimaryPagesForPageType(PAGE_TYPE, URI_CONTEXT, {
                    search: 'somelabel',
                    pageSize: null,
                    currentPage: null
                });
                const expectedPayload2 = lo.assign({}, URI_CONTEXT, DEFAULT_PAYLOAD, {
                    mask: 'somelabel',
                    pageSize: 10,
                    currentPage: 0
                });
                expect(cmsitemsRestService.get).toHaveBeenCalledWith(expectedPayload2);
            }
        );

        it('WHEN is called THEN it returns the paginated list of primary pages for the given page type', async () => {
            cmsitemsRestService.get.and.returnValue(
                Promise.resolve<any>({
                    response: [page]
                })
            );

            // WHEN
            const value = await pageService.getPaginatedPrimaryPagesForPageType(
                PAGE_TYPE,
                URI_CONTEXT
            );

            // THEN
            expect(value.response).toEqual([page]);
        });
    });
});
