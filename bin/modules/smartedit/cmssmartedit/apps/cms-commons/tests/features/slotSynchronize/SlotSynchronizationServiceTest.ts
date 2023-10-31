/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ISyncStatus } from 'cmscommons';
import { SlotSynchronizationService } from 'cmscommons/services/SlotSynchronizationService';
import { TypedMap, functionsUtils, ISyncPollingService } from 'smarteditcommons';

/* jshint unused:false, undef:false */
describe('slot synchronization service - ', () => {
    let slotSynchronizationService: SlotSynchronizationService;
    let syncPollingService: jasmine.SpyObj<ISyncPollingService>;
    const uriContext = { context: 'uriContext' };

    const slot1_syncStatus = ({
        itemId: 'slot1',
        name: 'slot1',
        itemType: 'slot1',
        status: 'SOME_STATUS',
        dependentItemTypesOutOfSync: ['someItem3']
    } as unknown) as ISyncStatus;
    const slot2_syncStatus = ({
        itemId: 'slot2',
        name: 'slot2',
        itemType: 'slot2',
        status: 'SOME_STATUS',
        dependentItemTypesOutOfSync: ['someItem3', 'someItem4']
    } as unknown) as ISyncStatus;
    const slot3_syncStatus = ({
        itemId: 'slot3',
        name: 'slot3',
        itemType: 'slot3',
        status: 'SOME_STATUS'
    } as unknown) as ISyncStatus;
    const pageSyncStatus = ({
        itemId: 'pageId1',
        name: 'pageId1',
        itemType: 'page',
        status: 'SOME_STATUS',
        dependentItemTypesOutOfSync: ['someItem1', 'someItem2'],
        selectedDependencies: [slot1_syncStatus, slot2_syncStatus],
        sharedDependencies: [slot3_syncStatus]
    } as unknown) as ISyncStatus;

    beforeEach(() => {
        syncPollingService = jasmine.createSpyObj<ISyncPollingService>('syncPollingService', [
            'getSyncStatus',
            'performSync'
        ]);

        slotSynchronizationService = new SlotSynchronizationService(syncPollingService);
    });

    it(
        'GIVEN sync polling service returns a successful promise ' +
            'WHEN getSyncStatus is called ' +
            'THEN will fetch the status for the page in which the slot is present and then will retrieve the status of the slot if present in selectedDependencies',
        async () => {
            // GIVEN
            syncPollingService.getSyncStatus.and.returnValue(Promise.resolve(pageSyncStatus));

            // WHEN
            const promise = await slotSynchronizationService.getSyncStatus('pageId1', 'slot1');

            // THEN
            expect(promise).toEqual({
                ...slot1_syncStatus,
                fromSharedDependency: false
            });
        }
    );

    it(
        'GIVEN sync polling service returns a successful promise ' +
            'WHEN getSyncStatus is called ' +
            'THEN will fetch the status for the page in which the slot is present and then will retrieve the status of the slot if present in sharedDependencies',
        async () => {
            // GIVEN
            syncPollingService.getSyncStatus.and.returnValue(Promise.resolve(pageSyncStatus));

            // WHEN
            const promise = await slotSynchronizationService.getSyncStatus('pageId1', 'slot3');

            // THEN
            expect(promise).toEqual({
                ...slot3_syncStatus,
                fromSharedDependency: true
            });
        }
    );

    it(
        'GIVEN sync polling service returns a rejected promise ' +
            'WHEN getSyncStatus is called ' +
            'THEN the result is rejected',
        async () => {
            // GIVEN
            syncPollingService.getSyncStatus.and.returnValue(Promise.reject(false));

            try {
                // WHEN
                await slotSynchronizationService.getSyncStatus('pageId1', 'slot3');

                functionsUtils.assertFail();
            } catch (e) {
                // THEN
                // Simple check to indicate that error has been thrown
                expect(e).toEqual(false);
            }
        }
    );

    it(
        'GIVEN getPageSynchronizationPostRestService returns a successful promise ' +
            'WHEN performSync is called ' +
            'THEN the result is resolved',
        async () => {
            // GIVEN
            syncPollingService.performSync.and.returnValue(Promise.resolve<any>({}));
            const toSync: TypedMap<string> = {
                itemId: pageSyncStatus.itemId,
                itemType: pageSyncStatus.itemType
            };

            // WHEN
            await slotSynchronizationService.performSync([toSync], uriContext);

            // THEN
            expect(syncPollingService.performSync).toHaveBeenCalledWith([toSync], uriContext);
        }
    );
});
