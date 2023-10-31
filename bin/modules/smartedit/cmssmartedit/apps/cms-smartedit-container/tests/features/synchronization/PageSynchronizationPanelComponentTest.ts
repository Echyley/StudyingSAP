/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { TranslateService } from '@ngx-translate/core';
import { ISynchronizationPanelApi, ISyncStatus, SynchronizationPanelComponent } from 'cmscommons';
import { PageSynchronizationPanelComponent } from 'cmssmarteditcontainer/components/synchronize';
import { HomepageService } from 'cmssmarteditcontainer/services';
import {
    CrossFrameEventService,
    IPageService,
    ICMSPage,
    ISyncPollingService
} from 'smarteditcommons';

describe('PageSynchronizationPanelComponent', () => {
    let pageService: jasmine.SpyObj<IPageService>;
    let homepageService: jasmine.SpyObj<HomepageService>;
    let crossFrameEventService: jasmine.SpyObj<CrossFrameEventService>;
    let syncPollingService: jasmine.SpyObj<ISyncPollingService>;
    let translateService: jasmine.SpyObj<TranslateService>;
    let synchronizationPanel: jasmine.SpyObj<SynchronizationPanelComponent>;

    let component: PageSynchronizationPanelComponent;
    beforeEach(() => {
        pageService = jasmine.createSpyObj<IPageService>('pageService', ['isPageApproved']);

        syncPollingService = jasmine.createSpyObj<ISyncPollingService>('syncPollingService', [
            'performSync'
        ]);

        crossFrameEventService = jasmine.createSpyObj<CrossFrameEventService>(
            'crossFrameEventService',
            ['subscribe', 'publish']
        );

        translateService = jasmine.createSpyObj<TranslateService>('translateService', ['instant']);

        synchronizationPanel = jasmine.createSpyObj<SynchronizationPanelComponent>(
            'synchronizationPanel',
            ['syncItems']
        );

        component = new PageSynchronizationPanelComponent(
            pageService,
            homepageService,
            crossFrameEventService,
            syncPollingService,
            translateService
        );

        (component as any).synchronizationPanel = synchronizationPanel;
    });

    describe('initialize', () => {
        it('should subscribe to Page Updated Event', () => {
            component.ngOnInit();

            expect(crossFrameEventService.subscribe).toHaveBeenCalledWith(
                'PAGE_UPDATED_EVENT',
                jasmine.any(Function)
            );
        });
    });

    describe('destroy', () => {
        it('should unsubscribe from Page Updated Event', () => {
            const unSubPageUpdatedEventSpy = jasmine.createSpy();
            crossFrameEventService.subscribe.and.returnValues(unSubPageUpdatedEventSpy);
            component.ngOnInit();

            component.ngOnDestroy();

            expect(unSubPageUpdatedEventSpy).toHaveBeenCalled();
        });
    });

    it('evaluateIfSyncIsApproved GIVEN page is not approved THEN it sets appropriate message AND disables item list', async () => {
        component.cmsPage = {
            uuid: '1'
        } as ICMSPage;
        pageService.isPageApproved.and.returnValue(Promise.resolve(false));
        const setMessageSpy = jasmine.createSpy('setMessage');
        const disableItemListSpy = jasmine.createSpy('disableItemList');
        (component as any).synchronizationPanelApi = {
            setMessage: setMessageSpy,
            disableItemList: disableItemListSpy
        };

        await (component as any).evaluateIfSyncIsApproved();

        expect(pageService.isPageApproved).toHaveBeenCalledWith(component.cmsPage.uuid);

        expect(setMessageSpy).toHaveBeenCalled();
        expect(disableItemListSpy).toHaveBeenCalled();
    });

    it('performSync delegates a call to the service properly', () => {
        const mockItems = [];
        component.performSync(mockItems);

        expect(syncPollingService.performSync).toHaveBeenCalledWith(mockItems, undefined);
    });

    it('performSyncItems delegates a call to Synchronization Panel Component', () => {
        component.syncItems();

        expect(synchronizationPanel.syncItems).toHaveBeenCalled();
    });

    it('onGetApi sets synchronizationPanelApi and disableItem method', () => {
        const mockApi = {} as ISynchronizationPanelApi;
        component.onGetApi(mockApi);

        expect((component as any).synchronizationPanelApi).toBe(mockApi);
        expect((component as any).synchronizationPanelApi.disableItem).toBeDefined();
    });

    describe('onSyncStatusReady', () => {
        let mockApi: ISynchronizationPanelApi;
        beforeEach(() => {
            mockApi = {
                displayItemList: jasmine.createSpy(),
                selectAll: jasmine.createSpy()
            } as unknown as ISynchronizationPanelApi;

            component.onGetApi(mockApi);
        });

        it('WHEN the page has unavailable dependencies THEN it hides Sync Items List', () => {
            const mockSyncStatus = {} as ISyncStatus;
            component.onSyncStatusReady({
                unavailableDependencies: [mockSyncStatus],
                lastSyncStatus: 1
            } as ISyncStatus);

            expect(mockApi.displayItemList).toHaveBeenCalledWith(false);
        });

        it('WHEN the page has no unavailable dependencies AND has no Sync Status THEN it selects all items and hides Sync Items List', () => {
            component.onSyncStatusReady({
                unavailableDependencies: [],
                lastSyncStatus: null
            } as ISyncStatus);

            expect(mockApi.selectAll).toHaveBeenCalledWith();
            expect(mockApi.displayItemList).toHaveBeenCalledWith(false);
        });

        it('WHEN the page has no unavailable dependencies AND has Sync Status THEN it shows Sync Items List', () => {
            component.onSyncStatusReady({
                unavailableDependencies: [],
                lastSyncStatus: 1
            } as ISyncStatus);

            expect(mockApi.displayItemList).toHaveBeenCalledWith(true);
        });

        it('publishes SYNC STATUS READY event', () => {
            component.onSyncStatusReady({
                unavailableDependencies: [],
                lastSyncStatus: 1
            } as ISyncStatus);

            expect(crossFrameEventService.publish).toHaveBeenCalled();
        });
    });
});
