/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ICloneableCatalogVersion } from 'cmscommons';
import { CatalogVersionRestService } from 'cmssmarteditcontainer/dao';
import { PagesVariationsRestService } from 'cmssmarteditcontainer/dao/PagesVariationsRestService';
import { HomepageService, HomepageType } from 'cmssmarteditcontainer/services';
import { PageRestoredAlertService } from 'cmssmarteditcontainer/services/actionableAlert';
import { PageRestoreModalService } from 'cmssmarteditcontainer/services/pages';
import {
    CMSPageOperationPayload,
    ManagePageService
} from 'cmssmarteditcontainer/services/pages/ManagePageService';
import {
    annotationService,
    pageDeletionEvictionTag,
    pageRestoredEvictionTag,
    rarelyChangingContent,
    Cached,
    CrossFrameEventService,
    IAlertService,
    ICatalogService,
    IPageInfoService,
    IRestService,
    IRestServiceFactory,
    SmarteditRoutingService,
    LogService,
    SystemEventService,
    IConfirmationModalService,
    IWaitDialogService,
    EVENTS,
    EVENT_CONTENT_CATALOG_UPDATE,
    ISharedDataService,
    IExperience,
    IBaseCatalog,
    CmsitemsRestService,
    ICMSPage,
    CMSPageStatus,
    CMSPageTypes,
    WorkflowService
} from 'smarteditcommons';

describe('ManagePageService', () => {
    let logService: jasmine.SpyObj<LogService>;
    let routingService: jasmine.SpyObj<SmarteditRoutingService>;
    let alertService: jasmine.SpyObj<IAlertService>;
    let cmsitemsRestService: jasmine.SpyObj<CmsitemsRestService>;
    let systemEventService: jasmine.SpyObj<SystemEventService>;
    let crossFrameEventService: jasmine.SpyObj<CrossFrameEventService>;
    let pageInfoService: jasmine.SpyObj<IPageInfoService>;
    let confirmationModalService: jasmine.SpyObj<IConfirmationModalService>;
    let pagesVariationsRestService: jasmine.SpyObj<PagesVariationsRestService>;
    let waitDialogService: jasmine.SpyObj<IWaitDialogService>;
    let pageRestoreModalService: jasmine.SpyObj<PageRestoreModalService>;
    let pageRestoredAlertService: jasmine.SpyObj<PageRestoredAlertService>;
    let homepageService: jasmine.SpyObj<HomepageService>;
    let workflowService: jasmine.SpyObj<WorkflowService>;
    let catalogService: jasmine.SpyObj<ICatalogService>;
    let restServiceFactory: jasmine.SpyObj<IRestServiceFactory>;
    let pageOperationsRESTService: jasmine.SpyObj<IRestService<CMSPageOperationPayload>>;
    let catalogVersionRestService: jasmine.SpyObj<CatalogVersionRestService>;
    let sharedDataService: jasmine.SpyObj<ISharedDataService>;

    // Service being tested
    let managePageService: ManagePageService;

    // Mocked Data
    const MOCKED_URI_CONTEXT = {
        CURRENT_CONTEXT_CATALOG: 'MOCKED_CURRENT_CONTEXT_CATALOG',
        CURRENT_CONTEXT_CATALOG_VERSION: 'MOCKED_CURRENT_CONTEXT_CATALOG_VERSION',
        CURRENT_CONTEXT_SITE_ID: 'MOCKED_CURRENT_SITE_ID'
    };

    const MOCKED_PAGE_INFO: ICMSPage = {
        name: 'MOCKED_PAGE_NAME',
        uid: 'MOCKED_PAGE_UID',
        uuid: 'MOCKED_PAGE_UUID',
        typeCode: CMSPageTypes.ContentPage,
        pageStatus: CMSPageStatus.ACTIVE,
        homepage: false,
        catalogVersion: 'MOCKED_CATALOG_VERSION'
    } as ICMSPage;

    const MOCKED_HOMEPAGE_INFO: ICMSPage = {
        name: 'MOCKED_PAGE_NAME',
        uid: 'MOCKED_PAGE_UID',
        uuid: 'MOCKED_PAGE_UUID',
        typeCode: CMSPageTypes.ContentPage,
        pageStatus: CMSPageStatus.ACTIVE,
        catalogVersion: 'MOCKED_CATALOG_VERSION',
        homepage: true
    } as ICMSPage;

    const MOCKED_PAGE_INFO_FOR_UPDATE: any = {
        name: 'MOCKED_PAGE_NAME',
        uid: 'MOCKED_PAGE_UID',
        uuid: 'MOCKED_PAGE_UUID',
        typeCode: CMSPageTypes.ContentPage,
        pageStatus: CMSPageStatus.DELETED,
        identifier: 'MOCKED_PAGE_UUID',
        homepage: false,
        catalogVersion: 'MOCKED_CATALOG_VERSION'
    };

    const MOCKED_HOMEPAGE_INFO_FOR_UPDATE: any = {
        name: 'MOCKED_PAGE_NAME',
        uid: 'MOCKED_PAGE_UID',
        uuid: 'MOCKED_PAGE_UUID',
        typeCode: CMSPageTypes.ContentPage,
        pageStatus: CMSPageStatus.DELETED,
        catalogVersion: 'MOCKED_CATALOG_VERSION',
        homepage: true,
        identifier: 'MOCKED_PAGE_UUID'
    };

    const MOCKED_CONFIRMATION_PAYLOAD_STOREFRONT = {
        description: 'se.cms.actionitem.page.trash.confirmation.description.storefront',
        descriptionPlaceholders: {
            pageName: MOCKED_PAGE_INFO.name
        },
        title: 'se.cms.actionitem.page.trash.confirmation.title'
    };

    const MOCKED_CONFIRMATION_PAYLOAD_STOREFRONT_HOMEPAGE = {
        description: 'se.cms.actionitem.page.trash.confirmation.description.storefront.homepage',
        descriptionPlaceholders: {
            pageName: MOCKED_PAGE_INFO.name
        },
        title: 'se.cms.actionitem.page.trash.confirmation.title'
    };

    const MOCKED_RESPONSE = 'MOCKED_RESPONSE';

    const MOCKED_ALERT_PARAMETERS = {
        message: 'se.cms.actionitem.page.trash.alert.success.description',
        messagePlaceholders: {
            pageName: MOCKED_PAGE_INFO.name
        },
        minWidth: '',
        mousePersist: true,
        duration: 1000,
        dismissible: true,
        width: '300px'
    };

    const MOCKED_CONFIRMATION_PAYLOAD_PAGELIST = {
        description: 'se.cms.actionitem.page.trash.confirmation.description.pagelist',
        descriptionPlaceholders: {
            pageName: MOCKED_PAGE_INFO.name
        },
        title: 'se.cms.actionitem.page.trash.confirmation.title'
    };

    const MOCKED_PAGE_UID = 'MOCKED_PAGE_UID';

    const MOCKED_DELETE_ALERT_SUCCESS_KEY = 'se.cms.page.permanently.delete.alert.success';

    beforeEach(() => {
        routingService = jasmine.createSpyObj<SmarteditRoutingService>('smarteditRoutingService', [
            'go'
        ]);
        logService = jasmine.createSpyObj<LogService>('logService', ['error', 'warn']);
        alertService = jasmine.createSpyObj<IAlertService>('alertService', [
            'showSuccess',
            'showDanger'
        ]);
        cmsitemsRestService = jasmine.createSpyObj<CmsitemsRestService>('cmsitemsRestService', [
            'get',
            'update',
            'delete',
            'getById'
        ]);
        systemEventService = jasmine.createSpyObj<SystemEventService>('systemEventService', [
            'publishAsync'
        ]);
        crossFrameEventService = jasmine.createSpyObj<CrossFrameEventService>(
            'crossFrameEventService',
            ['publish']
        );
        pageInfoService = jasmine.createSpyObj<IPageInfoService>('pageInfoService', [
            'getPageUUID'
        ]);
        confirmationModalService = jasmine.createSpyObj<IConfirmationModalService>(
            'confirmationModalService',
            ['confirm']
        );
        pagesVariationsRestService = jasmine.createSpyObj<PagesVariationsRestService>(
            'pagesVariationsRestService',
            ['getVariationsForPrimaryPageId']
        );
        waitDialogService = jasmine.createSpyObj<IWaitDialogService>('waitDialogService', [
            'showWaitModal',
            'hideWaitModal'
        ]);
        pageRestoreModalService = jasmine.createSpyObj<PageRestoreModalService>(
            'pageRestoreModalService',
            ['handleRestoreValidationErrors']
        );
        pageRestoredAlertService = jasmine.createSpyObj<PageRestoredAlertService>(
            'pageRestoredAlertService',
            ['displayPageRestoredSuccessAlert']
        );
        homepageService = jasmine.createSpyObj<HomepageService>('homepageService', [
            'getHomepageType',
            'hasFallbackHomePage'
        ]);
        workflowService = jasmine.createSpyObj<WorkflowService>('workflowService', [
            'isPageInWorkflow'
        ]);
        restServiceFactory = jasmine.createSpyObj<IRestServiceFactory>('restServiceFactory', [
            'get'
        ]);
        catalogService = jasmine.createSpyObj<ICatalogService>('catalogService', [
            'retrieveUriContext',
            'getContentCatalogActiveVersion',
            'getContentCatalogsForSite'
        ]);
        pageOperationsRESTService = jasmine.createSpyObj<IRestService<CMSPageOperationPayload>>(
            'pageOperationsRESTService',
            ['save']
        );
        catalogVersionRestService = jasmine.createSpyObj<CatalogVersionRestService>(
            'catalogVersionRestService',
            ['getCloneableTargets']
        );
        sharedDataService = jasmine.createSpyObj<ISharedDataService>('sharedDataService', ['get']);

        managePageService = new ManagePageService(
            logService,
            routingService,
            alertService,
            cmsitemsRestService,
            systemEventService,
            crossFrameEventService,
            pageInfoService,
            confirmationModalService,
            pagesVariationsRestService,
            waitDialogService,
            pageRestoreModalService,
            pageRestoredAlertService,
            homepageService,
            workflowService,
            catalogService,
            restServiceFactory,
            sharedDataService,
            catalogVersionRestService
        );

        workflowService.isPageInWorkflow.and.returnValue(Promise.resolve(false));
        pagesVariationsRestService.getVariationsForPrimaryPageId.and.returnValue(
            Promise.resolve([])
        );
    });

    describe(' - softDeletePage - ', () => {
        beforeEach(() => {
            pageInfoService.getPageUUID.and.returnValue(Promise.resolve('homepage'));
        });

        it('WHEN confirmation is cancelled THEN softDeletePage does not trigger cmsItem update', async () => {
            // Given
            homepageService.getHomepageType.and.returnValue(Promise.resolve(null));
            confirmationModalService.confirm.and.returnValue(Promise.reject());

            // When
            await managePageService.softDeletePage(MOCKED_PAGE_INFO, MOCKED_URI_CONTEXT);

            // Assert
            expect(confirmationModalService.confirm).toHaveBeenCalledWith(
                MOCKED_CONFIRMATION_PAYLOAD_STOREFRONT
            );
            expect(cmsitemsRestService.update).not.toHaveBeenCalled();
            expect(crossFrameEventService.publish).not.toHaveBeenCalled();
            expect(alertService.showSuccess).not.toHaveBeenCalled();
        });

        it('WHEN cmsItem update is failing THEN softDeletePage is rejected', async () => {
            // Given
            homepageService.getHomepageType.and.returnValue(Promise.resolve(null));
            confirmationModalService.confirm.and.returnValue(Promise.resolve() as any);
            cmsitemsRestService.update.and.returnValue(Promise.reject());
            // When
            await managePageService.softDeletePage(MOCKED_PAGE_INFO, MOCKED_URI_CONTEXT);

            // Assert
            expect(confirmationModalService.confirm).toHaveBeenCalledWith(
                MOCKED_CONFIRMATION_PAYLOAD_STOREFRONT
            );
            expect(cmsitemsRestService.update).toHaveBeenCalledWith(MOCKED_PAGE_INFO_FOR_UPDATE);
            expect(logService.warn).toHaveBeenCalled();
            expect(crossFrameEventService.publish).not.toHaveBeenCalled();
            expect(alertService.showSuccess).not.toHaveBeenCalled();
        });

        it('WHEN cmsItem update is successful THEN call to softDeletePage displays a success alert', async () => {
            // Given
            confirmationModalService.confirm.and.returnValue(Promise.resolve() as any);
            cmsitemsRestService.update.and.returnValue(Promise.resolve() as any);
            alertService.showSuccess.and.returnValue(Promise.resolve(MOCKED_RESPONSE) as any);
            homepageService.getHomepageType.and.returnValue(Promise.resolve(null));

            // When
            await managePageService.softDeletePage(MOCKED_PAGE_INFO, MOCKED_URI_CONTEXT);

            // Assert
            expect(confirmationModalService.confirm).toHaveBeenCalledWith(
                MOCKED_CONFIRMATION_PAYLOAD_STOREFRONT
            );
            expect(cmsitemsRestService.update).toHaveBeenCalledWith(MOCKED_PAGE_INFO_FOR_UPDATE);
            expect(alertService.showSuccess).toHaveBeenCalledWith(MOCKED_ALERT_PARAMETERS);
        });

        it('WHEN cmsItem update is successful THEN call to softDeletePage should publish a page delete event', async () => {
            // Given
            confirmationModalService.confirm.and.returnValue(Promise.resolve() as any);
            cmsitemsRestService.update.and.returnValue(Promise.resolve() as any);
            alertService.showSuccess.and.returnValue(Promise.resolve(MOCKED_RESPONSE) as any);
            homepageService.getHomepageType.and.returnValue(Promise.resolve(null));

            // When
            await managePageService.softDeletePage(MOCKED_PAGE_INFO, MOCKED_URI_CONTEXT);

            // Assert
            expect(crossFrameEventService.publish).toHaveBeenCalledWith(EVENTS.PAGE_DELETED);
        });

        it('WHEN triggered from storefront THEN call to softDeletePage displays specific confirmation text', async () => {
            // Given
            pageInfoService.getPageUUID.and.returnValue(
                Promise.reject({
                    name: 'InvalidStorefrontPageError'
                })
            );

            confirmationModalService.confirm.and.returnValue(Promise.resolve() as any);
            cmsitemsRestService.update.and.returnValue(Promise.resolve() as any);
            alertService.showSuccess.and.returnValue(Promise.resolve(MOCKED_RESPONSE) as any);
            homepageService.getHomepageType.and.returnValue(Promise.resolve(null));

            // When
            await managePageService.softDeletePage(MOCKED_PAGE_INFO, MOCKED_URI_CONTEXT);

            // Assert
            expect(confirmationModalService.confirm).toHaveBeenCalledWith(
                MOCKED_CONFIRMATION_PAYLOAD_PAGELIST
            );
            expect(cmsitemsRestService.update).toHaveBeenCalledWith(MOCKED_PAGE_INFO_FOR_UPDATE);
            expect(alertService.showSuccess).toHaveBeenCalledWith(MOCKED_ALERT_PARAMETERS);
        });

        it('WHEN soft deleting an homePage that has a fallback THEN call to softDeletePage displays a success alert', async () => {
            // Given
            confirmationModalService.confirm.and.returnValue(Promise.resolve() as any);
            cmsitemsRestService.update.and.returnValue(Promise.resolve() as any);
            alertService.showSuccess.and.returnValue(Promise.resolve(MOCKED_RESPONSE) as any);
            homepageService.getHomepageType.and.returnValue(Promise.resolve(HomepageType.CURRENT));
            homepageService.hasFallbackHomePage.and.returnValue(Promise.resolve(true));

            // When
            await managePageService.softDeletePage(MOCKED_HOMEPAGE_INFO, MOCKED_URI_CONTEXT);

            // Assert
            expect(confirmationModalService.confirm).toHaveBeenCalledWith(
                MOCKED_CONFIRMATION_PAYLOAD_STOREFRONT_HOMEPAGE
            );
            expect(cmsitemsRestService.update).toHaveBeenCalledWith(
                MOCKED_HOMEPAGE_INFO_FOR_UPDATE
            );
            expect(alertService.showSuccess).toHaveBeenCalledWith(MOCKED_ALERT_PARAMETERS);
        });
    });

    describe(' - isPageTrashable - ', () => {
        const MOCK_URI_CONTEXT = {
            CURRENT_CONTEXT_CATALOG: 'MOCKED_CONTEXT_CATALOG',
            CURRENT_CONTEXT_CATALOG_VERSION: 'MOCKED_CONTEXT_CATALOG_VERSION',
            CURRENT_CONTEXT_SITE_ID: 'MOCKED_SITE_ID'
        };

        it('WHEN at least one variation page is associated to the indicated pageUid THEN isPageTrashable returns false', async () => {
            // Given
            homepageService.getHomepageType.and.returnValue(Promise.resolve(null));
            pagesVariationsRestService.getVariationsForPrimaryPageId.and.returnValue(
                Promise.resolve(['MOCKED_VARIATION_PAGE_UID'])
            );

            // When
            const actual = await managePageService.isPageTrashable(
                MOCKED_PAGE_INFO,
                MOCK_URI_CONTEXT
            );

            // Assert
            expect(pagesVariationsRestService.getVariationsForPrimaryPageId).toHaveBeenCalledWith(
                MOCKED_PAGE_UID
            );
            expect(actual).toEqual(false);
        });

        it('WHEN no variation pages are associated to the indicated pageUid THEN isPageTrashable returns true', async () => {
            // Given
            homepageService.getHomepageType.and.returnValue(Promise.resolve(null));
            pagesVariationsRestService.getVariationsForPrimaryPageId.and.returnValue(
                Promise.resolve([])
            );

            // When
            const actual = await managePageService.isPageTrashable(
                MOCKED_PAGE_INFO,
                MOCK_URI_CONTEXT
            );

            // Assert
            expect(pagesVariationsRestService.getVariationsForPrimaryPageId).toHaveBeenCalledWith(
                MOCKED_PAGE_UID
            );
            expect(actual).toEqual(true);
        });

        it('WHEN the page is the current homePage and it has a fallback homePage THEN isPageTrashable returns true', async () => {
            // GIVEN
            homepageService.getHomepageType.and.returnValue(Promise.resolve(HomepageType.CURRENT));
            homepageService.hasFallbackHomePage.and.returnValue(Promise.resolve(true));

            // WHEN
            const actual = await managePageService.isPageTrashable(
                MOCKED_PAGE_INFO,
                MOCK_URI_CONTEXT
            );

            // ASSERT
            expect(homepageService.hasFallbackHomePage).toHaveBeenCalledWith(MOCK_URI_CONTEXT);
            expect(actual).toEqual(true);
        });

        it('WHEN the page is the old homePage and it has a fallback homePage THEN isPageTrashable returns true', async () => {
            // GIVEN
            homepageService.getHomepageType.and.returnValue(Promise.resolve(HomepageType.OLD));
            homepageService.hasFallbackHomePage.and.returnValue(Promise.resolve(true));

            // WHEN
            const actual = await managePageService.isPageTrashable(
                MOCKED_PAGE_INFO,
                MOCK_URI_CONTEXT
            );

            // ASSERT
            expect(homepageService.hasFallbackHomePage).toHaveBeenCalledWith(MOCK_URI_CONTEXT);
            expect(actual).toEqual(true);
        });

        it('WHEN the page is the current homePage and it has no fallback homePage THEN isPageTrashable returns false', async () => {
            // GIVEN
            homepageService.getHomepageType.and.returnValue(Promise.resolve(HomepageType.CURRENT));
            homepageService.hasFallbackHomePage.and.returnValue(Promise.resolve(false));

            // WHEN
            const actual = await managePageService.isPageTrashable(
                MOCKED_PAGE_INFO,
                MOCK_URI_CONTEXT
            );

            // ASSERT
            expect(homepageService.hasFallbackHomePage).toHaveBeenCalledWith(MOCK_URI_CONTEXT);
            expect(actual).toEqual(false);
        });

        it('WHEN the page is an homePage (neither current nor old) and it has no fallback homePage THEN isPageTrashable returns false', async () => {
            // GIVEN
            homepageService.getHomepageType.and.returnValue(Promise.resolve(HomepageType.CURRENT));
            homepageService.hasFallbackHomePage.and.returnValue(Promise.resolve(false));

            // WHEN
            const actual = await managePageService.isPageTrashable(
                MOCKED_HOMEPAGE_INFO,
                MOCK_URI_CONTEXT
            );

            // ASSERT
            expect(homepageService.hasFallbackHomePage).toHaveBeenCalledWith(MOCK_URI_CONTEXT);
            expect(actual).toEqual(false);
        });

        it('WHEN the page is an homePage (neither current nor old) and it has a fallback homePage THEN isPageTrashable returns true', async () => {
            // GIVEN
            homepageService.getHomepageType.and.returnValue(Promise.resolve(HomepageType.CURRENT));
            homepageService.hasFallbackHomePage.and.returnValue(Promise.resolve(true));

            // WHEN
            const actual = await managePageService.isPageTrashable(
                MOCKED_HOMEPAGE_INFO,
                MOCK_URI_CONTEXT
            );

            // ASSERT
            expect(homepageService.hasFallbackHomePage).toHaveBeenCalledWith(MOCK_URI_CONTEXT);
            expect(actual).toEqual(true);
        });

        it('GIVEN the page is in a workflow WHEN isPageTrashable is called THEN it returns false', async () => {
            // GIVEN
            homepageService.getHomepageType.and.returnValue(Promise.resolve(HomepageType.CURRENT));
            homepageService.hasFallbackHomePage.and.returnValue(Promise.resolve(true));
            workflowService.isPageInWorkflow.and.returnValue(Promise.resolve(true));

            // WHEN
            const actual = await managePageService.isPageTrashable(
                MOCKED_HOMEPAGE_INFO,
                MOCK_URI_CONTEXT
            );

            // THEN
            expect(actual).toEqual(false);
        });
    });

    describe('- isPageCloneable -', () => {
        const mockExperience = {
            catalogDescriptor: {
                siteId: 'siteId',
                catalogId: 'catalogId',
                catalogVersion: 'catalogVersion'
            }
        } as unknown as IExperience;
        const mockPageInfo = {
            defaultPage: true,
            copyToCatalogsDisabled: false
        } as unknown as ICMSPage;
        const mockTargets = {
            versions: [{ uuid: 'uuid1' }, { uuid: 'uuid2' }]
        } as unknown as ICloneableCatalogVersion[];
        const mockCatalogs = [
            {
                catalogId: 'catalogUuid1',
                parents: [{ uuid: 'parentUuid1', versions: [] }],
                versions: [
                    { uuid: 'versionUuid1', active: true },
                    { uuid: 'versionUuid2', active: false }
                ]
            },
            {
                catalogId: 'catalogUuid2',
                parents: [{ uuid: 'parentUuid2', versions: [] }],
                versions: [
                    { uuid: 'versionUuid3', active: true },
                    { uuid: 'versionUuid4', active: false }
                ]
            }
        ] as unknown as IBaseCatalog[];

        beforeEach(() => {
            sharedDataService.get.and.returnValue(Promise.resolve(mockExperience));
            catalogService.getContentCatalogsForSite.and.returnValue(Promise.resolve(mockCatalogs));
            cmsitemsRestService.getById.and.returnValue(Promise.resolve(mockPageInfo));
            catalogVersionRestService.getCloneableTargets.and.returnValue(
                Promise.resolve(mockTargets) as any
            );
        });
        describe('GIVEN there is pageContext WHEN it is active AND there are cloneable target catalogs', () => {
            it('page is a default page and page cloning is not disable THEN it should return true', async () => {
                const actual = await managePageService.isPageCloneable(
                    'pageUuid',
                    mockCatalogs[0].versions[0].uuid
                );

                expect(actual).toEqual(true);
            });

            it('page is a default page but page cloning is disable THEN it should return false', async () => {
                cmsitemsRestService.getById.and.returnValue(
                    Promise.resolve({ ...mockPageInfo, copyToCatalogsDisabled: true })
                );

                const actual = await managePageService.isPageCloneable(
                    'pageUuid',
                    mockCatalogs[0].versions[0].uuid
                );

                expect(actual).toEqual(false);
            });
        });
        it('GIVEN there is pageContext WHEN it is active AND there are no cloneable targets THEN it should return false', async () => {
            catalogVersionRestService.getCloneableTargets.and.returnValue(
                Promise.resolve({ ...mockTargets, versions: [] })
            );

            const actual = await managePageService.isPageCloneable(
                'pageUuid',
                mockCatalogs[0].versions[0].uuid
            );

            expect(actual).toEqual(false);
        });

        it('GIVEN there is NO pageContext WHEN there are cloneable target catalogs THEN it should return true', async () => {
            const actual = await managePageService.isPageCloneable(
                'pageUuid',
                'noPageContextForThisCatalogVersion'
            );

            expect(actual).toEqual(true);
        });
    });

    describe(' - hardDeletePage - ', () => {
        it('WHEN cmsitemsRestService.delete is a successful call THEN a success alert is displayed', async () => {
            // Given
            confirmationModalService.confirm.and.returnValue(Promise.resolve() as any);
            cmsitemsRestService.delete.and.returnValue(Promise.resolve());

            // When
            await managePageService.hardDeletePage(MOCKED_PAGE_INFO);

            // Then
            expect(alertService.showSuccess).toHaveBeenCalledWith(MOCKED_DELETE_ALERT_SUCCESS_KEY);
        });
    });

    describe(' - restorePage - ', () => {
        it('GIVEN page can be restored WHEN restore is called THEN a success alert is displayed', async () => {
            // GIVEN
            const response = { a: 'some property' };
            cmsitemsRestService.update.and.returnValue(Promise.resolve(response) as any);

            // WHEN
            await managePageService.restorePage(MOCKED_PAGE_INFO);

            // THEN
            expect(pageRestoredAlertService.displayPageRestoredSuccessAlert).toHaveBeenCalledWith(
                jasmine.any(Object)
            );
            expect(systemEventService.publishAsync).toHaveBeenCalledWith(
                EVENT_CONTENT_CATALOG_UPDATE,
                response
            );
            expect(crossFrameEventService.publish).toHaveBeenCalledWith(EVENTS.PAGE_RESTORED);
            expect(waitDialogService.hideWaitModal).toHaveBeenCalled();
        });

        it('GIVEN page cannot be restored WHEN restore is called THEN the call is delegated to pageRestoreModalService', async () => {
            // GIVEN
            const errors: any = 'some errors';
            const rejectedResponse = {
                error: {
                    errors
                }
            };
            cmsitemsRestService.update.and.returnValue(Promise.reject(rejectedResponse));

            // WHEN
            await managePageService.restorePage(MOCKED_PAGE_INFO);

            // THEN
            expect(waitDialogService.hideWaitModal).toHaveBeenCalled();
            expect(pageRestoreModalService.handleRestoreValidationErrors).toHaveBeenCalledWith(
                jasmine.any(Object),
                errors
            );
        });
    });

    describe(' - getSoftDeletedPagesCount - ', () => {
        const MOCK_URI_CONTEXT = {
            CURRENT_CONTEXT_CATALOG: 'MOCKED_CONTEXT_CATALOG',
            CURRENT_CONTEXT_CATALOG_VERSION: 'MOCKED_CONTEXT_CATALOG_VERSION',
            CURRENT_CONTEXT_SITE_ID: 'MOCKED_SITE_ID'
        };

        beforeEach(() => {
            cmsitemsRestService.get.and.returnValue(
                Promise.resolve({
                    pagination: {
                        totalCount: 3
                    }
                }) as any
            );
        });

        it('getTrashedPagesCount should call cmsitemsapi with the right parameters', async () => {
            // WHEN
            const actual = await managePageService.getSoftDeletedPagesCount(MOCK_URI_CONTEXT);

            // ASSERT
            expect(cmsitemsRestService.get).toHaveBeenCalledWith({
                pageSize: 10,
                currentPage: 0,
                typeCode: 'AbstractPage',
                itemSearchParams: 'pageStatus:deleted',
                catalogId: MOCK_URI_CONTEXT.CURRENT_CONTEXT_CATALOG,
                catalogVersion: MOCK_URI_CONTEXT.CURRENT_CONTEXT_CATALOG_VERSION,
                fields: 'BASIC'
            });
        });

        it('getTrashedPagesCount should return the total number of trashed pages', async () => {
            // WHEN
            const actual = await managePageService.getSoftDeletedPagesCount(MOCK_URI_CONTEXT);

            // ASSERT
            expect(actual).toEqual(3);
        });

        it('checks Cached annotation on getTrashedPagesCount() method ', async () => {
            // WHEN
            await managePageService.getSoftDeletedPagesCount(MOCK_URI_CONTEXT);

            // ASSERT
            const decoratorObj: any = annotationService.getMethodAnnotation(
                ManagePageService,
                'getSoftDeletedPagesCount',
                Cached
            );
            expect(decoratorObj).toEqual(
                jasmine.objectContaining([
                    {
                        actions: [rarelyChangingContent],
                        tags: [pageDeletionEvictionTag, pageRestoredEvictionTag]
                    }
                ])
            );
        });
    });

    describe('getDisabledTrashTooltipMessage', () => {
        const MOCK_URI_CONTEXT = {
            CURRENT_CONTEXT_CATALOG: 'MOCKED_CONTEXT_CATALOG',
            CURRENT_CONTEXT_CATALOG_VERSION: 'MOCKED_CONTEXT_CATALOG_VERSION',
            CURRENT_CONTEXT_SITE_ID: 'MOCKED_SITE_ID'
        };

        it('GIVEN the homepage is CURRENT THEN return the expected disabled trash tooltip message', async () => {
            // GIVEN
            homepageService.getHomepageType.and.returnValue(Promise.resolve(HomepageType.CURRENT));

            // WHEN
            const actual = await managePageService.getDisabledTrashTooltipMessage(
                MOCKED_HOMEPAGE_INFO,
                MOCK_URI_CONTEXT
            );

            // ASSERT
            expect(actual).toEqual('se.cms.tooltip.current.homepage.movetotrash');
        });

        it('GIVEN the homepage is OLD THEN return the expected disabled trash tooltip message', async () => {
            // GIVEN
            homepageService.getHomepageType.and.returnValue(Promise.resolve(HomepageType.OLD));

            // WHEN
            const actual = await managePageService.getDisabledTrashTooltipMessage(
                MOCKED_HOMEPAGE_INFO,
                MOCK_URI_CONTEXT
            );

            // ASSERT
            expect(actual).toEqual('se.cms.tooltip.old.homepage.movetotrash');
        });

        it('GIVEN the homepage is not defined THEN return the expected disabled trash tooltip message', async () => {
            // GIVEN
            homepageService.getHomepageType.and.returnValue(Promise.resolve(null));

            // WHEN
            const actual = await managePageService.getDisabledTrashTooltipMessage(
                MOCKED_HOMEPAGE_INFO,
                MOCK_URI_CONTEXT
            );

            // ASSERT
            expect(actual).toEqual('se.cms.tooltip.movetotrash');
        });

        it('GIVEN the page in a workflow WHEN getDisabledTrashTooltipMessage is called THEN returns expected disabled trash tooltip message', async () => {
            // GIVEN
            homepageService.getHomepageType.and.returnValue(Promise.resolve(null));
            workflowService.isPageInWorkflow.and.returnValue(Promise.resolve(true));

            // WHEN
            const actual = await managePageService.getDisabledTrashTooltipMessage(
                MOCKED_HOMEPAGE_INFO,
                MOCK_URI_CONTEXT
            );

            // THEN
            expect(actual).toEqual('se.cms.tooltip.page.in.workflow.movetotrash');
        });
    });

    describe('trashPageInActiveCatalogVersion', () => {
        const MOCK_URI_CONTEXT = {
            CURRENT_CONTEXT_CATALOG: 'MOCKED_CONTEXT_CATALOG',
            CURRENT_CONTEXT_CATALOG_VERSION: 'MOCKED_CONTEXT_CATALOG_VERSION',
            CURRENT_CONTEXT_SITE_ID: 'MOCKED_SITE_ID'
        };

        const ACTIVE_CATALOG_VERSION = 'ACTIVE_CATALOG_VERSION';
        const MOCKED_CONFIRM_PARAMS = {
            title: 'se.cms.sync.page.status.confirm.title',
            description: 'se.cms.sync.page.status.confirm.description',
            descriptionPlaceholders: {
                catalogVersion: ACTIVE_CATALOG_VERSION
            }
        };
        const MOCKED_ALERT_PARAMS = {
            message: 'se.cms.sync.page.status.success.alert',
            messagePlaceholders: {
                pageId: MOCKED_PAGE_UID,
                catalogVersion: ACTIVE_CATALOG_VERSION
            },
            minWidth: '',
            mousePersist: true,
            duration: 1000,
            dismissible: true,
            width: '300px'
        };

        it('GIVEN a page WHEN trashPageInActiveCatalogVersion is called THEN it will tash the page in the active catalog version', async () => {
            // GIVEN
            catalogService.retrieveUriContext.and.returnValue(Promise.resolve(MOCK_URI_CONTEXT));
            catalogService.getContentCatalogActiveVersion.and.returnValue(
                Promise.resolve(ACTIVE_CATALOG_VERSION)
            );
            confirmationModalService.confirm.and.returnValue(Promise.resolve() as any);
            restServiceFactory.get.and.returnValue(pageOperationsRESTService);
            pageOperationsRESTService.save.and.returnValue(Promise.resolve({}) as any);
            alertService.showSuccess.and.returnValue(Promise.resolve(MOCKED_RESPONSE) as any);

            // WHEN
            await managePageService.trashPageInActiveCatalogVersion(MOCKED_PAGE_UID);

            // THEN
            expect(catalogService.retrieveUriContext).toHaveBeenCalled();
            expect(catalogService.getContentCatalogActiveVersion).toHaveBeenCalledWith(
                MOCK_URI_CONTEXT
            );
            expect(confirmationModalService.confirm).toHaveBeenCalledWith(MOCKED_CONFIRM_PARAMS);
            expect(pageOperationsRESTService.save).toHaveBeenCalledWith({
                operation: 'TRASH_PAGE',
                sourceCatalogVersion: MOCK_URI_CONTEXT.CURRENT_CONTEXT_CATALOG_VERSION,
                targetCatalogVersion: ACTIVE_CATALOG_VERSION
            });
            expect(alertService.showSuccess).toHaveBeenCalledWith(MOCKED_ALERT_PARAMS);
        });
    });
});
