/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {
    CatalogHomepageDetailsStatus,
    HomepageService,
    HomepageType
} from 'cmssmarteditcontainer/services';
import {
    ICatalog,
    ICatalogService,
    ICatalogVersion,
    IUriContext,
    EventMessageData,
    SystemEventService,
    functionsUtils,
    ICMSPage
} from 'smarteditcommons';
import { homepageServiceTestData } from './homepageServiceTestData';

import 'jasmine';

describe('homepageService', () => {
    let eventService: jasmine.SpyObj<SystemEventService>;

    let catalogService: jasmine.SpyObj<ICatalogService>;
    let homepageService: HomepageService;

    const uriContexts = {
        electronicsStaged: {
            CURRENT_CONTEXT_SITE_ID: 'electronicsSite',
            CURRENT_CONTEXT_CATALOG: 'electronicsContentCatalog',
            CURRENT_CONTEXT_CATALOG_VERSION: 'Staged'
        },
        euStaged: {
            CURRENT_CONTEXT_SITE_ID: 'electronicsSite',
            CURRENT_CONTEXT_CATALOG: 'electronics-euContentCatalog',
            CURRENT_CONTEXT_CATALOG_VERSION: 'Staged'
        },
        ukStaged: {
            CURRENT_CONTEXT_SITE_ID: 'electronicsSite',
            CURRENT_CONTEXT_CATALOG: 'electronics-ukContentCatalog',
            CURRENT_CONTEXT_CATALOG_VERSION: 'Staged'
        },
        frStaged: {
            CURRENT_CONTEXT_SITE_ID: 'electronicsSite',
            CURRENT_CONTEXT_CATALOG: 'electronics-frContentCatalog',
            CURRENT_CONTEXT_CATALOG_VERSION: 'Staged'
        },
        noHomepageOnline: {
            CURRENT_CONTEXT_SITE_ID: 'electronicsSite',
            CURRENT_CONTEXT_CATALOG: 'electronics-noHomepage',
            CURRENT_CONTEXT_CATALOG_VERSION: 'Online'
        }
    };

    beforeEach(() => {
        eventService = jasmine.createSpyObj<SystemEventService>('eventService', ['publish']);
        catalogService = jasmine.createSpyObj<ICatalogService>('catalogService', [
            'getContentCatalogVersion',
            'getCatalogVersionByUuid'
        ]);
        catalogService.getContentCatalogVersion.and.callFake((urlContext: IUriContext) => {
            const matchingCatalog = homepageServiceTestData.catalogs.find(
                (catalog: ICatalog) => catalog.catalogId === urlContext.CURRENT_CONTEXT_CATALOG
            );
            if (!matchingCatalog) {
                throw Error(
                    `HomepageServiceTest - no catalog found with ID ${urlContext.CURRENT_CONTEXT_CATALOG}`
                );
            }
            const matchingCatalogVersion = matchingCatalog.versions.find(
                (catalogVersion: ICatalogVersion) =>
                    catalogVersion.version === urlContext.CURRENT_CONTEXT_CATALOG_VERSION
            );
            if (matchingCatalogVersion) {
                return Promise.resolve(matchingCatalogVersion);
            }
            throw Error(
                `HomepageServiceTest - version not found for getContentCatalogVersion(${urlContext})`
            );
        });
        catalogService.getCatalogVersionByUuid.and.callFake((uuid: string) => {
            let returnCatalogVersion;
            homepageServiceTestData.catalogs.forEach((catalog: ICatalog) => {
                const found = catalog.versions.find(
                    (catalogVersion: ICatalogVersion) => catalogVersion.uuid === uuid
                );
                if (found) {
                    returnCatalogVersion = found;
                }
            });
            if (returnCatalogVersion) {
                return Promise.resolve(returnCatalogVersion);
            }
            throw Error(
                `HomepageServiceTest - version not found for getCatalogVersionByUuid(${uuid})`
            );
        });
        homepageService = new HomepageService(catalogService, eventService);
    });

    it('Send show replace parent homepage info event', () => {
        const data: EventMessageData = {
            description: 'bla',
            title: 'bla'
        };

        homepageService.sendEventShowReplaceParentHomePageInfo(data);

        expect(eventService.publish).toHaveBeenCalledWith(
            'CMS_EVENT_SHOW_REPLACE_PARENT_HOMEPAGE_INFO',
            data
        );
    });

    it('Send hdie replace parent homepage info event', () => {
        const data: EventMessageData = {
            description: 'bla',
            title: 'bla'
        };

        homepageService.sendEventHideReplaceParentHomePageInfo(data);

        expect(eventService.publish).toHaveBeenCalledWith(
            'CMS_EVENT_HIDE_REPLACE_PARENT_HOMEPAGE_INFO',
            data
        );
    });

    it('getHomepageType - CURRENT', async () => {
        const page: ICMSPage = {
            catalogVersion: 'electronics-ukContentCatalog/Staged',
            uid: 'cmsitem_00003001'
        } as ICMSPage;

        const type = await homepageService.getHomepageType(page, uriContexts.ukStaged);

        expect(type).toBe(HomepageType.CURRENT);
    });

    it('getHomepageType - OLD', async () => {
        const page: ICMSPage = {
            catalogVersion: 'electronics-ukContentCatalog/Online',
            uid: 'homepage-uk'
        } as ICMSPage;

        const type = await homepageService.getHomepageType(page, uriContexts.ukStaged);

        expect(type).toBe(HomepageType.OLD);
    });

    it('getHomepageType - FALLBACKS', async () => {
        const page: ICMSPage = {
            catalogVersion: 'electronicsContentCatalog/Online',
            uid: 'homepage'
        } as ICMSPage;

        const type = await homepageService.getHomepageType(page, uriContexts.euStaged);

        expect(type).toBe(HomepageType.FALLBACK);
    });

    it('isCurrentHomepage - true', async () => {
        const page: ICMSPage = {
            catalogVersion: 'electronics-ukContentCatalog/Staged',
            uid: 'cmsitem_00003001'
        } as ICMSPage;
        spyOn(homepageService, 'getHomepageType').and.callThrough();

        const result = await homepageService.isCurrentHomepage(page, uriContexts.ukStaged);

        expect(homepageService.getHomepageType).toHaveBeenCalledWith(page, uriContexts.ukStaged);
        expect(result).toBe(true);
    });

    it('isOldHomepage - false', async () => {
        const page: ICMSPage = {
            catalogVersion: 'electronics-ukContentCatalog/Staged',
            uid: 'homepage'
        } as ICMSPage;
        spyOn(homepageService, 'getHomepageType').and.callThrough();

        const result = await homepageService.isOldHomepage(page, uriContexts.ukStaged);

        expect(homepageService.getHomepageType).toHaveBeenCalledWith(page, uriContexts.ukStaged);
        expect(result).toBe(false);
    });

    it('isOldHomepage - true', async () => {
        const page: ICMSPage = {
            catalogVersion: 'electronics-ukContentCatalog/Online',
            uid: 'homepage-uk'
        } as ICMSPage;
        spyOn(homepageService, 'getHomepageType').and.callThrough();

        const result = await homepageService.isOldHomepage(page, uriContexts.ukStaged);

        expect(homepageService.getHomepageType).toHaveBeenCalledWith(page, uriContexts.ukStaged);
        expect(result).toBe(true);
    });

    it('isCurrentHomepage - false', async () => {
        const page: ICMSPage = {
            catalogVersion: 'electronics-ukContentCatalog/Staged',
            uid: 'homepage'
        } as ICMSPage;
        spyOn(homepageService, 'getHomepageType').and.callThrough();

        const result = await homepageService.isCurrentHomepage(page, uriContexts.ukStaged);

        expect(homepageService.getHomepageType).toHaveBeenCalledWith(page, uriContexts.ukStaged);
        expect(result).toBe(false);
    });

    it('hasFallbackHomePage - true', async () => {
        const result = await homepageService.hasFallbackHomePage(uriContexts.ukStaged);

        expect(result).toBe(true);
    });

    it('hasFallbackHomePage - false', async () => {
        const result = await homepageService.hasFallbackHomePage(uriContexts.electronicsStaged);

        expect(result).toBe(false);
    });

    describe('getHomepageDetailsForContext', () => {
        const errMsg = 'some error message';

        it('Handles failure - getCatalogVersionByUuid', async () => {
            catalogService.getCatalogVersionByUuid.and.returnValue(Promise.reject(errMsg));

            try {
                await homepageService.getHomepageDetailsForContext(uriContexts.ukStaged);

                functionsUtils.assertFail();
            } catch (e) {
                expect(e).toEqual(errMsg);
            }
        });

        it('Handles failure - getContentCatalogVersion', async () => {
            catalogService.getContentCatalogVersion.and.returnValue(Promise.reject(errMsg));

            try {
                await homepageService.getHomepageDetailsForContext(uriContexts.ukStaged);

                functionsUtils.assertFail();
            } catch (e) {
                expect(e).toEqual(errMsg);
            }
        });

        it('NO homepage', async () => {
            const result = await homepageService.getHomepageDetailsForContext(
                uriContexts.noHomepageOnline
            );

            expect(result).toEqual({
                status: CatalogHomepageDetailsStatus.NO_HOMEPAGE
            });
        });

        it('Is a PARENT homepage', async () => {
            // TARGET IS FR STAGED, PARENT SHOULD BE EU ONLINE
            const result = await homepageService.getHomepageDetailsForContext(uriContexts.frStaged);

            expect(result).toEqual({
                status: 'PARENT' as CatalogHomepageDetailsStatus,
                parentCatalogName: {
                    en: 'Electronics Content Catalog EU',
                    de: 'Elektronikkatalog EU',
                    ja: 'エレクトロニクス コンテンツ カタログ EU',
                    zh: '电子产品内容目录 EU'
                },
                parentCatalogVersion: 'Online',
                targetCatalogName: {
                    en: 'Electronics Content Catalog FR'
                },
                targetCatalogVersion: 'Staged'
            });
        });

        it('Is a LOCAL homepage', async () => {
            // TARGET IS FR STAGED, PARENT SHOULD BE EU ONLINE
            const result = await homepageService.getHomepageDetailsForContext(uriContexts.ukStaged);

            expect(result).toEqual({
                status: 'LOCAL' as CatalogHomepageDetailsStatus,
                currentHomepageName: 'n1',
                currentHomepageUid: 'cmsitem_00003001',
                oldHomepageUid: 'homepage-uk'
            });
        });
    });
});
