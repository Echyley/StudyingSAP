/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {
    DEFAULT_SYNCHRONIZATION_POLLING,
    ISyncStatus,
    SynchronizationResourceService
} from 'cmscommons';
import { SyncPollingService } from 'cmssmarteditcontainer/services/SyncPollingServiceOuter';
import {
    CrossFrameEventService,
    EVENTS,
    ICatalogService,
    IExperienceService,
    IPageInfoService,
    IRestService,
    IUriContext,
    LogService,
    MessageGateway,
    SmarteditBootstrapGateway,
    SystemEventService,
    Timer,
    TimerService
} from 'smarteditcommons';

describe('Synchronization polling service with content catalog non active version - ', () => {
    let syncPollingService: SyncPollingService;
    let experienceService: jasmine.SpyObj<IExperienceService>;
    let crossFrameEventService: jasmine.SpyObj<CrossFrameEventService>;
    let systemEventService: jasmine.SpyObj<SystemEventService>;
    let pageInfoService: jasmine.SpyObj<IPageInfoService>;
    let catalogService: jasmine.SpyObj<ICatalogService>;
    let timerService: jasmine.SpyObj<TimerService>;
    let timer: jasmine.SpyObj<Timer>;
    let synchronizationResource: jasmine.SpyObj<SynchronizationResourceService>;
    let pageSynchronizationGetRestService: jasmine.SpyObj<IRestService<any>>;
    let pageSynchronizationPostRestService: jasmine.SpyObj<IRestService<any>>;
    let logService: jasmine.SpyObj<LogService>;
    let smarteditBootstrapGateway: jasmine.SpyObj<SmarteditBootstrapGateway>;
    let messageGateway: jasmine.SpyObj<MessageGateway>;

    const MOCK_PAGE_ID_1 = 'pageId1';

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
        sharedDependencies: [SLOT3_SYNC_STATUS],
        lastModifiedDate: null
    } as any as ISyncStatus;
    const pageId2SyncStatus = {
        itemId: 'pageId2',
        name: 'pageId2',
        itemType: 'page',
        status: 'SOME_STATUS',
        dependentItemTypesOutOfSync: ['someItem1', 'someItem2'],
        selectedDependencies: [SLOT1_SYNC_STATUS, SLOT2_SYNC_STATUS],
        sharedDependencies: [SLOT3_SYNC_STATUS],
        lastModifiedDate: null
    } as any as ISyncStatus;

    const SYNCHRONIZATION_SLOW_POLLING_TIME = DEFAULT_SYNCHRONIZATION_POLLING.SLOW_POLLING_TIME;
    const SYNCHRONIZATION_FAST_POLLING_TIME = DEFAULT_SYNCHRONIZATION_POLLING.FAST_POLLING_TIME;
    const SYNC_POLLING_SPEED_UP = 'syncPollingSpeedUp';
    const SYNC_POLLING_SLOW_DOWN = 'syncPollingSlowDown';

    beforeEach(() => {
        logService = jasmine.createSpyObj<LogService>('logService', ['info', 'error']);

        experienceService = jasmine.createSpyObj<IExperienceService>('experienceService', [
            'getCurrentExperience'
        ]);
        experienceService.getCurrentExperience.and.returnValue(
            Promise.resolve<any>({
                pageContext: {
                    active: false
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
        systemEventService.subscribe.calls.reset();

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
            'getPageSynchronizationGetRestService',
            'getPageSynchronizationPostRestService'
        ]);
        pageSynchronizationGetRestService = jasmine.createSpyObj<IRestService<any>>(
            'pageSynchronizationGetRestService',
            ['get']
        );
        pageSynchronizationPostRestService = jasmine.createSpyObj<IRestService<any>>(
            'pageSynchronizationPostRestService',
            ['save']
        );
        synchronizationResource.getPageSynchronizationGetRestService.and.returnValue(
            pageSynchronizationGetRestService
        );
        synchronizationResource.getPageSynchronizationPostRestService.and.returnValue(
            pageSynchronizationPostRestService
        );

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

    it('initSyncPolling will be called on service initialization and will set default values, register event handlers and start timer', () => {
        // GIVEN
        systemEventService.subscribe.and.returnValue({} as any);

        // THEN
        expect((syncPollingService as any).refreshInterval).toBe(SYNCHRONIZATION_SLOW_POLLING_TIME);
        expect(Array.from((syncPollingService as any).triggers)).toEqual([]);
        expect((syncPollingService as any).syncStatus).toEqual({});

        expect(systemEventService.subscribe.calls.count()).toEqual(2);
        expect(systemEventService.subscribe.calls.argsFor(0)[0]).toEqual(SYNC_POLLING_SPEED_UP);
        expect(systemEventService.subscribe.calls.argsFor(1)[0]).toEqual(SYNC_POLLING_SLOW_DOWN);

        expect(timer.restart).toHaveBeenCalledWith(SYNCHRONIZATION_SLOW_POLLING_TIME);
    });

    it('when syncStatus in the scope is empty then getSyncStatus will fetch the sync status by making a rest call and set it to the scope object ', async () => {
        // GIVEN
        pageInfoService.getPageUUID.and.returnValue(Promise.resolve(MOCK_PAGE_ID_1));
        pageSynchronizationGetRestService.get.and.returnValue(Promise.resolve(pageId1SyncStatus));

        // WHEN
        const result = await syncPollingService.getSyncStatus(MOCK_PAGE_ID_1);
        // THEN
        expect(result as any).toEqual(pageId1SyncStatus);
        expect((syncPollingService as any).syncStatus.pageId1).toEqual(pageId1SyncStatus);
    });

    it('when syncStatus object is not empty syncStatus but has an unmatched name, then getSyncStatus will fetch the sync status by making a rest call and reset the syncStatus scope object', async () => {
        // GIVEN
        pageInfoService.getPageUUID.and.returnValue(Promise.resolve(MOCK_PAGE_ID_1));
        pageSynchronizationGetRestService.get.and.returnValue(Promise.resolve(pageId1SyncStatus));
        (syncPollingService as any).syncStatus.pageId2 = pageId2SyncStatus;

        // WHEN
        const result = await syncPollingService.getSyncStatus(MOCK_PAGE_ID_1);
        // THEN
        expect(result as any).toEqual(pageId1SyncStatus);

        expect(pageSynchronizationGetRestService.get.calls.count()).toBe(1);
        expect(pageSynchronizationGetRestService.get).toHaveBeenCalledWith({
            target: 'Online',
            pageUid: MOCK_PAGE_ID_1
        });
        expect((syncPollingService as any).syncStatus.pageId1).toEqual(pageId1SyncStatus);
    });

    it('when syncStatus object is not empty syncStatus and matches the name then getSyncStatus with directly return the promise of the syncStatus object', async () => {
        // GIVEN
        pageInfoService.getPageUUID.and.returnValue(Promise.resolve(MOCK_PAGE_ID_1));
        pageSynchronizationGetRestService.get.and.returnValue(Promise.resolve(pageId1SyncStatus));
        (syncPollingService as any).syncStatus.pageId1 = pageId1SyncStatus;

        // WHEN
        const result = await syncPollingService.getSyncStatus(MOCK_PAGE_ID_1);
        // THEN
        expect(result as any).toEqual(pageId1SyncStatus);
        expect(pageSynchronizationGetRestService.get).not.toHaveBeenCalled();
    });

    it('when getSyncStatus is called multiple times, timer does not restart more than once', async () => {
        // GIVEN
        pageInfoService.getPageUUID.and.returnValue(Promise.resolve(MOCK_PAGE_ID_1));
        pageSynchronizationGetRestService.get.and.returnValue(Promise.resolve(pageId1SyncStatus));
        timer.restart.calls.reset();

        // WHEN
        await syncPollingService.getSyncStatus(MOCK_PAGE_ID_1);
        await syncPollingService.getSyncStatus(MOCK_PAGE_ID_1);
        await syncPollingService.getSyncStatus(MOCK_PAGE_ID_1);
        // THEN
        expect(timer.restart).toHaveBeenCalledWith(SYNCHRONIZATION_SLOW_POLLING_TIME);
        expect(timer.restart.calls.count()).toBe(1);
    });

    describe('fetchSyncStatus', () => {
        const PAGE_1_UUID = MOCK_PAGE_ID_1;

        beforeEach(() => {
            pageInfoService.getPageUUID.and.returnValue(Promise.resolve(PAGE_1_UUID));
            pageSynchronizationGetRestService.get.and.returnValue(
                Promise.resolve(pageId1SyncStatus)
            );
        });

        it('fetchSyncStatus will fetch the sync status by making a rest call and reset the syncStatus scope object', async () => {
            // WHEN
            const promise = await syncPollingService.fetchSyncStatus();

            // THEN
            expect(promise).toEqual(pageId1SyncStatus);
            expect(pageSynchronizationGetRestService.get).toHaveBeenCalled();
            expect((syncPollingService as any).syncStatus.pageId1).toEqual(pageId1SyncStatus);
        });

        it('GIVEN new sync status is the same as before WHEN fetchSyncStatus is called THEN event FAST_FETCH will NOT be published', async () => {
            // GIVEN
            (syncPollingService as any).syncStatus.pageId1 = pageId1SyncStatus;

            // WHEN
            await syncPollingService.fetchSyncStatus();

            // THEN
            expect(crossFrameEventService.publish).not.toHaveBeenCalledWith(
                DEFAULT_SYNCHRONIZATION_POLLING.FAST_FETCH,
                pageId1SyncStatus
            );
        });

        it('GIVEN new sync status WHEN fetchSyncStatus is called THEN it will publish a FAST_FETCH event', async () => {
            // GIVEN
            (syncPollingService as any).syncStatus.pageId1 = pageId2SyncStatus;

            // WHEN
            await syncPollingService.fetchSyncStatus();

            // THEN
            expect(crossFrameEventService.publish).toHaveBeenCalledWith(
                DEFAULT_SYNCHRONIZATION_POLLING.FAST_FETCH,
                pageId1SyncStatus
            );
        });

        it('GIVEN first poll WHEN fetchSyncStatus is called THEN it will publish a PAGE_UPDATED event', async () => {
            // WHEN

            await syncPollingService.fetchSyncStatus();

            // THEN
            expect(crossFrameEventService.publish).toHaveBeenCalledWith(
                DEFAULT_SYNCHRONIZATION_POLLING.FAST_FETCH,
                pageId1SyncStatus
            );
            expect(crossFrameEventService.publish).toHaveBeenCalledWith(EVENTS.PAGE_UPDATED, {
                uuid: MOCK_PAGE_ID_1
            });
        });

        it('GIVEN page has changed from last time it was synched WHEN fetchSyncStatus is called THEN it will publish a PAGE_UPDATED event', async () => {
            // GIVEN
            pageId1SyncStatus.lastModifiedDate = 123456789;
            pageId2SyncStatus.lastModifiedDate = 234567890;

            (syncPollingService as any).syncStatus.pageId1 = pageId2SyncStatus;

            // WHEN
            await syncPollingService.fetchSyncStatus();

            // THEN
            expect(crossFrameEventService.publish).toHaveBeenCalledWith(
                DEFAULT_SYNCHRONIZATION_POLLING.FAST_FETCH,
                pageId1SyncStatus
            );
            expect(crossFrameEventService.publish).toHaveBeenCalledWith(EVENTS.PAGE_UPDATED, {
                uuid: MOCK_PAGE_ID_1
            });
        });

        it('GIVEN page sync status has changed from last time it was synched WHEN fetchSyncStatus is called THEN it will publish a PAGE_UPDATED event', async () => {
            // GIVEN
            pageId1SyncStatus.status = 'some status';
            pageId2SyncStatus.status = 'some other status';

            (syncPollingService as any).syncStatus.pageId1 = pageId2SyncStatus;

            // WHEN
            await syncPollingService.fetchSyncStatus();

            // THEN
            expect(crossFrameEventService.publish).toHaveBeenCalledWith(
                DEFAULT_SYNCHRONIZATION_POLLING.FAST_FETCH,
                pageId1SyncStatus
            );
            expect(crossFrameEventService.publish).toHaveBeenCalledWith(EVENTS.PAGE_UPDATED, {
                uuid: MOCK_PAGE_ID_1
            });
        });

        it('GIVEN page information did not change from last time it was synched WHEN fetchSyncStatus is called THEN no page updated event is triggered', async () => {
            // GIVEN
            pageId1SyncStatus.lastModifiedDate = 123456789;
            pageId2SyncStatus.lastModifiedDate = 123456789;

            pageId1SyncStatus.status = 'some status';
            pageId2SyncStatus.status = 'some status';

            (syncPollingService as any).syncStatus.pageId1 = pageId2SyncStatus;

            // WHEN
            await syncPollingService.fetchSyncStatus();

            // THEN
            expect(crossFrameEventService.publish).toHaveBeenCalledWith(
                DEFAULT_SYNCHRONIZATION_POLLING.FAST_FETCH,
                pageId1SyncStatus
            );
            expect(crossFrameEventService.publish).not.toHaveBeenCalledWith(EVENTS.PAGE_UPDATED, {
                uuid: MOCK_PAGE_ID_1
            });
        });

        it('when no page id is available then fetchSyncStatus will return an empty object', async () => {
            // GIVEN
            pageInfoService.getPageUUID.and.returnValue(Promise.resolve(null));
            pageSynchronizationGetRestService.get.and.returnValue(
                Promise.resolve(pageId1SyncStatus)
            );

            // WHEN
            const promise = await syncPollingService.fetchSyncStatus();

            // THEN
            expect(promise).toEqual({} as ISyncStatus);
            expect(pageSynchronizationGetRestService.get).not.toHaveBeenCalled();
            expect((syncPollingService as any).syncStatus).toEqual({});
        });
    });

    it('when changePollingSpeed is called with syncPollingSpeedUp then the item is added to the triggers array and refreshInterval is set to speed up interval', () => {
        // GIVEN
        pageSynchronizationGetRestService.get.and.returnValue(Promise.resolve(pageId1SyncStatus));

        // WHEN
        syncPollingService.changePollingSpeed(SYNC_POLLING_SPEED_UP, 'slot1');

        // THEN
        expect(Array.from((syncPollingService as any).triggers)).toEqual(['slot1']);
        expect((syncPollingService as any).refreshInterval).toBe(SYNCHRONIZATION_FAST_POLLING_TIME);
        expect(timer.restart).toHaveBeenCalledWith(SYNCHRONIZATION_FAST_POLLING_TIME);

        // WHEN
        syncPollingService.changePollingSpeed(SYNC_POLLING_SPEED_UP, 'slot2');

        // THEN
        expect(Array.from((syncPollingService as any).triggers)).toEqual(['slot1', 'slot2']);
        expect((syncPollingService as any).refreshInterval).toBe(SYNCHRONIZATION_FAST_POLLING_TIME);
        expect(timer.restart).toHaveBeenCalledWith(SYNCHRONIZATION_FAST_POLLING_TIME);
    });

    it('when changePollingSpeed is called with syncPollingSlowDown then the item is removed from the triggers array and refreshInterval is set to slow down interval if the array is empty', () => {
        // GIVEN
        pageSynchronizationGetRestService.get.and.returnValue(Promise.resolve(pageId1SyncStatus));

        syncPollingService.changePollingSpeed(SYNC_POLLING_SPEED_UP, 'slot1');
        expect(Array.from((syncPollingService as any).triggers)).toEqual(['slot1']);

        // WHEN
        syncPollingService.changePollingSpeed(SYNC_POLLING_SLOW_DOWN, 'slot1');

        // THEN
        expect(Array.from((syncPollingService as any).triggers)).toEqual([]);
        expect((syncPollingService as any).refreshInterval).toBe(SYNCHRONIZATION_SLOW_POLLING_TIME);
        expect(timer.restart).toHaveBeenCalledWith(SYNCHRONIZATION_SLOW_POLLING_TIME);
    });

    it('when changePollingSpeed is called with syncPollingSlowDown then the item is removed from the triggers array and refreshInterval is unaltered if the array is not empty', () => {
        // GIVEN
        pageSynchronizationGetRestService.get.and.returnValue(Promise.resolve(pageId1SyncStatus));

        syncPollingService.changePollingSpeed(SYNC_POLLING_SPEED_UP, 'slot1');
        syncPollingService.changePollingSpeed(SYNC_POLLING_SPEED_UP, 'slot2');
        expect(Array.from((syncPollingService as any).triggers)).toEqual(['slot1', 'slot2']);

        // WHEN
        syncPollingService.changePollingSpeed(SYNC_POLLING_SLOW_DOWN, 'slot1');

        // THEN
        expect(Array.from((syncPollingService as any).triggers)).toEqual(['slot2']);
        expect((syncPollingService as any).refreshInterval).toBe(SYNCHRONIZATION_FAST_POLLING_TIME);
        expect(timer.restart).toHaveBeenCalledWith(SYNCHRONIZATION_FAST_POLLING_TIME);
    });

    it('will listen to OVERLAY_RERENDERED_EVENT events and proceed to one fetch', () => {
        const status = {
            a: 'b'
        };
        spyOn(syncPollingService, 'fetchSyncStatus').and.returnValue(Promise.resolve<any>(status));

        expect(crossFrameEventService.subscribe).toHaveBeenCalledWith(
            'overlayRerendered',
            jasmine.any(Function)
        );

        const callback = crossFrameEventService.subscribe.calls.argsFor(0)[1];

        callback(null);
        expect(syncPollingService.fetchSyncStatus).toHaveBeenCalled();
    });

    it('performSync will use activeVersion in REST call', async () => {
        // GIVEN
        const uriContext = {} as IUriContext;
        const array = [
            {
                a: 'b'
            }
        ];
        catalogService.getContentCatalogActiveVersion.and.returnValue(Promise.resolve('online'));

        // WHEN
        await syncPollingService.performSync(array, uriContext);

        // THEN
        expect(pageSynchronizationPostRestService.save).toHaveBeenCalledWith({
            target: 'online',
            items: array
        });
        expect(catalogService.getContentCatalogActiveVersion).toHaveBeenCalledWith(uriContext);
    });

    it('will listen to EVENTS.PAGE_CHANGE events and clear syncStatus', () => {
        // WHEN
        expect(crossFrameEventService.subscribe).toHaveBeenCalledWith(
            'PAGE_CHANGE',
            jasmine.any(Function)
        );

        const callback = crossFrameEventService.subscribe.calls.argsFor(2)[1];

        callback(null);

        // THEN
        expect((syncPollingService as any).syncStatus).toEqual({});
    });

    it('will listen to SYNCHRONIZATION_EVENT.CATALOG_SYNCHRONIZED events and clear the syncStatus cache.', () => {
        // GIVEN
        const callback = crossFrameEventService.subscribe.calls.argsFor(3)[1];
        spyOn(syncPollingService, 'fetchSyncStatus').and.rejectWith(null);

        // WHEN
        callback(null);

        // THEN
        expect((syncPollingService as any).syncStatus).toEqual({});
        expect(syncPollingService.fetchSyncStatus).toHaveBeenCalled();
    });
});
