/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { CONTEXT_SITE_ID } from 'cmscommons';
import { PageFacade } from 'cmssmarteditcontainer/facades/PageFacade';
import {
    CrossFrameEventService,
    ICatalogService,
    ISharedDataService,
    IUrlService,
    functionsUtils,
    CmsitemsRestService
} from 'smarteditcommons';

/* jshint unused:false, undef:false */
describe('PageFacade', () => {
    const MOCK_PAGE = {
        uid: 'somePageUid',
        name: 'Some Page Name',
        typeCode: 'somePageTypeCode',
        label: 'some-page-label',
        catalogVersion: 'someCatalogVersionUid'
    };

    const MOCK_CATALOG_VERSION = {
        uid: 'someCatalogVersionUid',
        catalogId: 'someCatalogId',
        version: 'someVersion'
    };

    const MOCK_RESULT_WITH_RESPONSE = {
        response: [MOCK_PAGE]
    };

    const MOCK_RESULT_WITH_NO_RESPONSE = {
        response: []
    };

    let facade: PageFacade;
    let cmsitemsRestService: jasmine.SpyObj<CmsitemsRestService>;
    let crossFrameEventService: jasmine.SpyObj<CrossFrameEventService>;
    let sharedDataService: jasmine.SpyObj<ISharedDataService>;
    let urlService: jasmine.SpyObj<IUrlService>;
    let catalogService: jasmine.SpyObj<ICatalogService>;

    beforeEach(() => {
        cmsitemsRestService = jasmine.createSpyObj<CmsitemsRestService>('cmsitemsRestService', [
            'get',
            'create'
        ]);
        crossFrameEventService = jasmine.createSpyObj<CrossFrameEventService>(
            'crossFrameEventService',
            ['publish']
        );
        sharedDataService = jasmine.createSpyObj<ISharedDataService>('sharedDataService', ['get']);
        urlService = jasmine.createSpyObj<IUrlService>('urlService', ['buildUriContext']);
        catalogService = jasmine.createSpyObj<ICatalogService>('catalogService', [
            'getCatalogVersionUUid'
        ]);

        facade = new PageFacade(
            cmsitemsRestService,
            crossFrameEventService,
            sharedDataService,
            urlService,
            catalogService
        );
    });

    describe('contentPageWithLabelExists', () => {
        it('will return a promise resolving to true if the content page with given label exists', async () => {
            cmsitemsRestService.get.and.returnValue(
                Promise.resolve<any>(MOCK_RESULT_WITH_RESPONSE)
            );

            const actual = await facade.contentPageWithLabelExists(
                MOCK_PAGE.label,
                MOCK_CATALOG_VERSION.catalogId,
                MOCK_CATALOG_VERSION.version
            );
            expect(actual).toEqual(true);
        });

        it('will return a promise resolving to false if the content page with given label does not exist', async () => {
            cmsitemsRestService.get.and.returnValue(
                Promise.resolve<any>(MOCK_RESULT_WITH_NO_RESPONSE)
            );

            const actual = await facade.contentPageWithLabelExists(
                'labelDoesNotExist',
                MOCK_CATALOG_VERSION.catalogId,
                MOCK_CATALOG_VERSION.version
            );
            expect(actual).toEqual(false);
        });
    });

    describe('createPage', () => {
        let mockCatVerId;
        let page;

        beforeEach(() => {
            mockCatVerId = 'mockCatVersionUuid';
            page = {
                catalogVersion: 'bla'
            };

            catalogService.getCatalogVersionUUid.and.returnValue(Promise.resolve(mockCatVerId));
        });

        it('will use the provided page catalogVersion, and default onlyOneRestrictionMustApply', async () => {
            await facade.createPage({ ...page, catalogVersion: null });
            expect(cmsitemsRestService.create).toHaveBeenCalledWith({
                catalogVersion: mockCatVerId,
                onlyOneRestrictionMustApply: false,
                restrictions: []
            } as any);
        });

        it('will use the current page catalogVersion, and accept the provided onlyOneRestrictionMustApply', async () => {
            page.onlyOneRestrictionMustApply = true;
            await facade.createPage(page);
            expect(cmsitemsRestService.create).toHaveBeenCalledWith({
                catalogVersion: page.catalogVersion,
                onlyOneRestrictionMustApply: true,
                restrictions: []
            } as any);
        });

        it('will send the EVENTS.PAGE_CREATED event on successful creation', async () => {
            page = {
                unique: '1'
            };

            // success
            cmsitemsRestService.create.and.returnValue(Promise.resolve(page));

            await facade.createPage(page);
            expect(crossFrameEventService.publish).toHaveBeenCalledWith('PAGE_CREATED_EVENT', page);
        });

        it('will NOT send the EVENTS.PAGE_CREATED event on failure to create page', async () => {
            // failure
            cmsitemsRestService.create.and.returnValue(Promise.reject({}));

            try {
                await facade.createPage(page);
                functionsUtils.assertFail();
            } catch {
                expect(crossFrameEventService.publish).not.toHaveBeenCalled();
            }
        });
    });

    describe('createPageForSite', () => {
        let mockCatVerId;
        let page;
        const siteUid = 'someSiteUid';

        beforeEach(() => {
            mockCatVerId = 'mockCatVersionUuid';
            page = {
                catalogVersion: 'bla'
            };

            catalogService.getCatalogVersionUUid.and.returnValue(Promise.resolve(mockCatVerId));
        });

        it('will use the provided page catalogVersion, and default onlyOneRestrictionMustApply', async () => {
            await facade.createPageForSite({ ...page, catalogVersion: null }, siteUid);
            expect(cmsitemsRestService.create).toHaveBeenCalledWith({
                catalogVersion: mockCatVerId,
                onlyOneRestrictionMustApply: false,
                [CONTEXT_SITE_ID]: 'someSiteUid'
            } as any);
        });

        it('will use the current page catalogVersion, and accept the provided onlyOneRestrictionMustApply', async () => {
            page.onlyOneRestrictionMustApply = true;
            await facade.createPageForSite(page, siteUid);
            expect(cmsitemsRestService.create).toHaveBeenCalledWith({
                catalogVersion: page.catalogVersion,
                onlyOneRestrictionMustApply: true,
                [CONTEXT_SITE_ID]: 'someSiteUid'
            } as any);
        });

        it('will send the EVENTS.PAGE_CREATED event on successful creation', async () => {
            page = {
                unique: '1'
            };

            // success
            cmsitemsRestService.create.and.returnValue(Promise.resolve(page));

            await facade.createPageForSite(page, siteUid);
            expect(crossFrameEventService.publish).toHaveBeenCalledWith('PAGE_CREATED_EVENT', {
                ...page,
                [CONTEXT_SITE_ID]: 'someSiteUid'
            });
        });

        it('will NOT send the EVENTS.PAGE_CREATED event on failure to create page', async () => {
            // failure
            cmsitemsRestService.create.and.returnValue(Promise.reject({}));
            try {
                await facade.createPageForSite(page, siteUid);
                functionsUtils.assertFail();
            } catch {
                expect(crossFrameEventService.publish).not.toHaveBeenCalled();
            }
        });
    });
});
