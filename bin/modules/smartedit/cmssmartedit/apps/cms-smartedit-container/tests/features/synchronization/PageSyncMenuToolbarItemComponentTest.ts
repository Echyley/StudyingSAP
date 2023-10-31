/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { TranslateService } from '@ngx-translate/core';
import { PageSyncMenuToolbarItemComponent } from 'cmssmarteditcontainer/components/synchronize';
import { PageSyncConditions } from 'cmssmarteditcontainer/components/synchronize/types';
import {
    CrossFrameEventService,
    LogService,
    ICatalogService,
    IPageInfoService,
    SystemEventService,
    IPageService,
    ICMSPage,
    ISyncPollingService
} from 'smarteditcommons';

describe('PageSyncMenuToolbarItemComponent', () => {
    const mockCmsPage = {} as ICMSPage;
    const mockUriContext = {};

    let crossFrameEventService: jasmine.SpyObj<CrossFrameEventService>;
    let systemEventService: jasmine.SpyObj<SystemEventService>;
    let catalogService: jasmine.SpyObj<ICatalogService>;
    let pageService: jasmine.SpyObj<IPageService>;
    let pageInfoService: jasmine.SpyObj<IPageInfoService>;
    let syncPollingService: jasmine.SpyObj<ISyncPollingService>;
    let translateService: jasmine.SpyObj<TranslateService>;
    let logService: jasmine.SpyObj<LogService>;

    let component: PageSyncMenuToolbarItemComponent;
    beforeEach(() => {
        crossFrameEventService = jasmine.createSpyObj<CrossFrameEventService>(
            'crossFrameEventService',
            ['subscribe']
        );

        systemEventService = jasmine.createSpyObj<SystemEventService>('systemEventService', [
            'subscribe'
        ]);

        catalogService = jasmine.createSpyObj<ICatalogService>('catalogService', [
            'retrieveUriContext',
            'isContentCatalogVersionNonActive'
        ]);

        pageService = jasmine.createSpyObj<IPageService>('pageService', ['getCurrentPageInfo']);

        pageInfoService = jasmine.createSpyObj<IPageInfoService>('pageInfoService', [
            'getPageUUID'
        ]);

        syncPollingService = jasmine.createSpyObj<ISyncPollingService>('syncPollingService', [
            'getSyncStatus'
        ]);

        translateService = jasmine.createSpyObj<TranslateService>('translateService', ['instant']);

        logService = jasmine.createSpyObj<LogService>('logService', ['error']);

        component = new PageSyncMenuToolbarItemComponent(
            crossFrameEventService,
            systemEventService,
            catalogService,
            pageService,
            pageInfoService,
            syncPollingService,
            translateService,
            logService,
            null
        );
    });

    describe('initialize', () => {
        it('subscribes to Page Change event', async () => {
            await component.ngOnInit();

            expect(crossFrameEventService.subscribe).toHaveBeenCalledWith(
                'PAGE_CHANGE',
                jasmine.any(Function)
            );
        });

        it('subscribes to Page Sync Status Ready event', async () => {
            await component.ngOnInit();

            expect(systemEventService.subscribe).toHaveBeenCalledWith(
                'PAGE_SYNC_STATUS_READY',
                jasmine.any(Function)
            );
        });

        describe('setups component properly', () => {
            beforeEach(() => {
                pageService.getCurrentPageInfo.and.returnValue(Promise.resolve(mockCmsPage));
                catalogService.retrieveUriContext.and.returnValue(Promise.resolve(mockUriContext));
            });

            it('fetches synchronization panel info properly', async () => {
                await component.ngOnInit();

                expect(component.cmsPage).toBe(mockCmsPage);
                expect(component.uriContext).toBe(mockUriContext);
            });

            it('handles rejections properly', async () => {
                pageService.getCurrentPageInfo.and.returnValue(Promise.reject());

                await component.ngOnInit();

                expect(logService.error).toHaveBeenCalledTimes(2);
            });

            it('GIVEN Active Content Catalog THEN it sets only cmsPage AND uriContext', async () => {
                catalogService.isContentCatalogVersionNonActive.and.returnValue(
                    Promise.resolve(false)
                );

                await component.ngOnInit();

                expect(component.cmsPage).toBe(mockCmsPage);
                expect(component.uriContext).toBe(mockUriContext);
                expect(component.isReady).toBe(false);
            });

            it(`GIVEN Non Active Content Catalog THEN it resubscribes Sync Polling Event
              AND fetches Sync Status
              AND updates isNotInSync flag`, async () => {
                syncPollingService.getSyncStatus.and.returnValue(
                    Promise.resolve<any>({ status: 'IN_SYNC' })
                );
                catalogService.isContentCatalogVersionNonActive.and.returnValue(
                    Promise.resolve(true)
                );

                await component.ngOnInit();

                expect(crossFrameEventService.subscribe).toHaveBeenCalled();
                expect(pageInfoService.getPageUUID).toHaveBeenCalled();
                expect(syncPollingService.getSyncStatus).toHaveBeenCalled();
                expect(component.isNotInSync).toBe(false);
            });
        });
    });

    it('GIVEN Page Sync Status Ready event THEN it sets Sync Page Conditions AND Help Text', async () => {
        translateService.instant.and.returnValue('mockTranslation');
        await component.ngOnInit();
        const mockSyncPageConditions = {
            pageHasNoDepOrNoSyncStatus: true
        } as PageSyncConditions;
        const onPageSyncStatusReady = systemEventService.subscribe.calls.argsFor(0)[1];

        onPageSyncStatusReady('eventId', mockSyncPageConditions);

        expect(component.helpText).toBeDefined();
        expect(component.syncPageConditions).toBe(mockSyncPageConditions);
    });

    describe('destroy', () => {
        let unRegisterPageChange: jasmine.Spy;
        let unRegisterSyncPageConditions: jasmine.Spy;
        let unRegisterSyncPolling: jasmine.Spy;
        beforeEach(() => {
            unRegisterPageChange = jasmine.createSpy();
            unRegisterSyncPageConditions = jasmine.createSpy();
            (component as any).unRegisterSyncPolling = unRegisterSyncPolling = jasmine.createSpy();

            crossFrameEventService.subscribe.and.returnValues(
                unRegisterPageChange,
                unRegisterSyncPolling
            );
            systemEventService.subscribe.and.returnValue(unRegisterSyncPageConditions);
        });

        it('Unregisters from Page Change Event', async () => {
            await component.ngOnInit();

            component.ngOnDestroy();

            expect(unRegisterPageChange).toHaveBeenCalled();
        });

        it('Unregisters from Sync Page Conditions Event', async () => {
            await component.ngOnInit();

            component.ngOnDestroy();

            expect(unRegisterSyncPageConditions).toHaveBeenCalled();
        });

        it('Unregisters from Sync Polling Event', async () => {
            await component.ngOnInit();

            component.ngOnDestroy();

            expect(unRegisterSyncPolling).toHaveBeenCalled();
        });
    });
});
