/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { DEFAULT_SYNCHRONIZATION_POLLING, SynchronizationResourceService } from 'cmscommons';
import { SyncPollingService } from 'cmssmarteditcontainer/services/SyncPollingServiceOuter';
import {
    CrossFrameEventService,
    ICatalogService,
    IExperienceService,
    IPageInfoService,
    IRestService,
    LogService,
    SystemEventService,
    Timer,
    TimerService,
    functionsUtils,
    SmarteditBootstrapGateway,
    MessageGateway
} from 'smarteditcommons';

describe('Synchronization polling service with content catalog active version - ', () => {
    let syncPollingService: SyncPollingService;
    let logService: jasmine.SpyObj<LogService>;
    let pageInfoService: jasmine.SpyObj<IPageInfoService>;
    let experienceService: jasmine.SpyObj<IExperienceService>;
    let catalogService: jasmine.SpyObj<ICatalogService>;
    let synchronizationResource: jasmine.SpyObj<SynchronizationResourceService>;
    let crossFrameEventService: jasmine.SpyObj<CrossFrameEventService>;
    let systemEventService: jasmine.SpyObj<SystemEventService>;
    let timerService: jasmine.SpyObj<TimerService>;
    let timer: jasmine.SpyObj<Timer>;
    let restService: jasmine.SpyObj<IRestService<any>>;
    let smarteditBootstrapGateway: jasmine.SpyObj<SmarteditBootstrapGateway>;
    let messageGateway: jasmine.SpyObj<MessageGateway>;

    const SLOT1_SYNC_STATUS = {
        itemId: 'slot1',
        name: 'slot1',
        itemType: 'slot1',
        status: 'SOME_STATUS',
        dependentItemTypesOutOfSync: ['someItem3']
    };
    const SLOT2_SYNC_STATUS = {
        itemId: 'slot2',
        name: 'slot2',
        itemType: 'slot2',
        status: 'SOME_STATUS',
        dependentItemTypesOutOfSync: ['someItem3', 'someItem4']
    };
    const SLOT3_SYNC_STATUS = {
        itemId: 'slot3',
        name: 'slot3',
        itemType: 'slot3',
        status: 'SOME_STATUS'
    };
    const pageId1SyncStatus = {
        itemId: 'pageId1',
        name: 'pageId1',
        itemType: 'page',
        status: 'SOME_STATUS',
        dependentItemTypesOutOfSync: ['someItem1', 'someItem2'],
        selectedDependencies: [SLOT1_SYNC_STATUS, SLOT2_SYNC_STATUS],
        sharedDependencies: [SLOT3_SYNC_STATUS]
    };
    const pageId2SyncStatus = {
        itemId: 'pageId2',
        name: 'pageId2',
        itemType: 'page',
        status: 'SOME_STATUS',
        dependentItemTypesOutOfSync: ['someItem1', 'someItem2'],
        selectedDependencies: [SLOT1_SYNC_STATUS, SLOT2_SYNC_STATUS],
        sharedDependencies: [SLOT3_SYNC_STATUS]
    };

    beforeEach(() => {
        logService = jasmine.createSpyObj<LogService>('logService', ['info', 'error', 'debug']);

        experienceService = jasmine.createSpyObj<IExperienceService>('experienceService', [
            'getCurrentExperience'
        ]);
        experienceService.getCurrentExperience.and.returnValue(
            Promise.resolve<any>({
                pageContext: {
                    active: true
                }
            })
        );
        crossFrameEventService = jasmine.createSpyObj<CrossFrameEventService>(
            'crossFrameEventService',
            ['publish', 'subscribe']
        );
        systemEventService = jasmine.createSpyObj<SystemEventService>('systemEventService', [
            'publish',
            'subscribe'
        ]);

        pageInfoService = jasmine.createSpyObj<IPageInfoService>('pageInfoService', [
            'getPageUUID'
        ]);

        catalogService = jasmine.createSpyObj<ICatalogService>('catalogService', [
            'getContentCatalogActiveVersion'
        ]);
        catalogService.getContentCatalogActiveVersion.and.returnValue(Promise.resolve('Online'));

        timerService = jasmine.createSpyObj('timerService', ['createTimer']);
        timer = jasmine.createSpyObj('Timer', ['start', 'restart', 'stop', 'isActive']);

        timerService.createTimer.and.returnValue(timer);

        synchronizationResource = jasmine.createSpyObj('synchronizationResource', [
            'getPageSynchronizationGetRestService'
        ]);
        restService = jasmine.createSpyObj<IRestService<any>>('restService', ['get']);
        synchronizationResource.getPageSynchronizationGetRestService.and.returnValue(restService);

        messageGateway = jasmine.createSpyObj<MessageGateway>('messageGateway', ['subscribe']);
        messageGateway.subscribe.and.callFake((_eventId: any, callback: any) => {
            callback();
            return () => {};
        });

        smarteditBootstrapGateway = jasmine.createSpyObj<SmarteditBootstrapGateway>(
            'smarteditBootstrapGateway',
            ['getInstance']
        );
        smarteditBootstrapGateway.getInstance.and.returnValue(messageGateway);

        syncPollingService = new SyncPollingService(
            logService,
            pageInfoService,
            experienceService,
            catalogService,
            synchronizationResource,
            crossFrameEventService,
            systemEventService,
            timerService,
            smarteditBootstrapGateway
        );
    });

    it('getSyncStatus will reject, not proceed to rest call and leave the syncStatus unchanged', async () => {
        // GIVEN
        (syncPollingService as any).syncStatus = pageId2SyncStatus;
        pageInfoService.getPageUUID.and.returnValue(Promise.resolve('pageId1'));
        restService.get.and.returnValue(Promise.resolve(pageId1SyncStatus));

        try {
            // WHEN
            await syncPollingService.getSyncStatus('pageId1');

            functionsUtils.assertFail();
        } catch {
            // THEN
            expect(restService.get).not.toHaveBeenCalled();
            expect((syncPollingService as any).syncStatus).toBe(pageId2SyncStatus);
        }
    });

    it('fetchSyncStatus will reject, not proceed to rest call and leave the syncStatus unchanged', async () => {
        // GIVEN
        (syncPollingService as any).syncStatus = pageId2SyncStatus;
        pageInfoService.getPageUUID.and.returnValue(Promise.resolve('pageId1'));
        restService.get.and.returnValue(Promise.resolve(pageId1SyncStatus));

        try {
            // WHEN
            await syncPollingService.fetchSyncStatus();

            functionsUtils.assertFail();
        } catch {
            // THEN
            expect(restService.get).not.toHaveBeenCalled();
            expect((syncPollingService as any).syncStatus).toEqual(pageId2SyncStatus);
        }
    });

    it('startSync call without pollingType should restart the timer with SLOW_POLLING_TIME by default', () => {
        timer.isActive.and.returnValue(false);

        (syncPollingService as any).startSync();

        expect(timer.restart).toHaveBeenCalledWith(
            DEFAULT_SYNCHRONIZATION_POLLING.SLOW_POLLING_TIME
        );
    });

    it('startSync call with DEFAULT_SYNCHRONIZATION_POLLING.FAST_POLLING_TIME should restart the timer with FAST_POLLING_TIME', () => {
        timer.isActive.and.returnValue(false);

        (syncPollingService as any).startSync(DEFAULT_SYNCHRONIZATION_POLLING.SPEED_UP);

        expect(timer.restart).toHaveBeenCalledWith(
            DEFAULT_SYNCHRONIZATION_POLLING.FAST_POLLING_TIME
        );
    });

    it("startSync call should not restart the timer if it's active", () => {
        timer.isActive.and.returnValue(true);
        timer.restart.calls.reset();

        (syncPollingService as any).startSync();

        expect(timer.restart).not.toHaveBeenCalled();
    });

    it("stopSync should stop the timer when it's active", () => {
        timer.isActive.and.returnValue(true);

        (syncPollingService as any).stopSync();

        expect(timer.stop).toHaveBeenCalled();
    });

    it("stopSync should not stop the timer when it's not active", () => {
        timer.isActive.and.returnValue(false);

        (syncPollingService as any).stopSync();

        expect(timer.stop).not.toHaveBeenCalled();
    });
});
