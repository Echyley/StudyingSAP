/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { TranslateService } from '@ngx-translate/core';
import {
    ISyncStatus,
    SlotStatus,
    ISynchronizationPanelApi,
    SlotSynchronizationPanelWrapperComponent,
    SlotSynchronizationService
} from 'cmscommons';
import {
    IPageInfoService,
    functionsUtils,
    IAlertServiceType,
    IPageService,
    PageContentSlotsService
} from 'smarteditcommons';

describe('SlotSynchronizationPanelWrapperComponent', () => {
    let component: SlotSynchronizationPanelWrapperComponent;
    let pageService: jasmine.SpyObj<IPageService>;
    let pageInfoService: jasmine.SpyObj<IPageInfoService>;
    let slotSynchronizationService: jasmine.SpyObj<SlotSynchronizationService>;
    let pageContentSlotsService: jasmine.SpyObj<PageContentSlotsService>;
    let translateService: jasmine.SpyObj<TranslateService>;

    const mockPageUid = 'pageUid';
    const mockPageUuid = 'pageUuid';
    const mockSlotId = 'slotId';
    const mockSyncStatus = {
        catalogVersionUuid: 'CVUUID',
        itemId: 'itemId',
        name: 'name'
    } as ISyncStatus;

    let mockSyncApi: ISynchronizationPanelApi;
    let syncPanelApiSetMessage: jasmine.Spy;
    let syncPanelApiSetDisableItemList: jasmine.Spy;

    beforeEach(() => {
        syncPanelApiSetMessage = jasmine.createSpy();
        syncPanelApiSetDisableItemList = jasmine.createSpy();
        mockSyncApi = ({
            setMessage: syncPanelApiSetMessage,
            disableItemList: syncPanelApiSetDisableItemList
        } as unknown) as ISynchronizationPanelApi;

        pageService = jasmine.createSpyObj<IPageService>('pageService', ['isPageApproved']);
        pageInfoService = jasmine.createSpyObj<IPageInfoService>('pageInfoService', [
            'getPageUID',
            'getPageUUID'
        ]);
        pageInfoService.getPageUID.and.returnValue(Promise.resolve(mockPageUid));
        pageInfoService.getPageUUID.and.returnValue(Promise.resolve(mockPageUuid));

        slotSynchronizationService = jasmine.createSpyObj<SlotSynchronizationService>(
            'slotSynchronizationService',
            ['getSyncStatus', 'syncStatusExists', 'performSync']
        );
        slotSynchronizationService.getSyncStatus.and.returnValue(Promise.resolve(mockSyncStatus));

        pageContentSlotsService = jasmine.createSpyObj<PageContentSlotsService>(
            'pageContentSlotsService',
            ['getSlotStatus']
        );

        translateService = jasmine.createSpyObj<TranslateService>('translateService', ['instant']);
        translateService.instant.and.callFake((param: string) => param);

        component = new SlotSynchronizationPanelWrapperComponent(
            pageService,
            pageInfoService,
            slotSynchronizationService,
            pageContentSlotsService,
            translateService
        );
        component.slotId = mockSlotId;

        component.getApi(mockSyncApi);
    });

    describe('getSyncStatus', () => {
        it('WHEN sync status does not exist THEN it should throw an error', async () => {
            slotSynchronizationService.getSyncStatus.and.returnValue(Promise.resolve(null));
            slotSynchronizationService.syncStatusExists.and.returnValue(false);

            try {
                await component.getSyncStatus();
                functionsUtils.assertFail();
            } catch (e) {
                expect(e.message).toEqual(
                    'The SlotSynchronizationPanel must only be called for the slot whose sync status is available.'
                );
            }
        });

        it('GIVEN sync status exists WHEN syncing is disallowed THEN it should disable sync and return sync status', async () => {
            slotSynchronizationService.syncStatusExists.and.returnValue(true);
            pageContentSlotsService.getSlotStatus.and.returnValue(Promise.resolve(SlotStatus.PAGE));
            pageService.isPageApproved.and.returnValue(Promise.resolve(false));

            const actual = await component.getSyncStatus();

            expect(actual).toEqual(mockSyncStatus);

            expect(pageInfoService.getPageUID).toHaveBeenCalled();
            expect(slotSynchronizationService.getSyncStatus).toHaveBeenCalledWith(
                mockPageUid,
                mockSlotId
            );
            expect(pageContentSlotsService.getSlotStatus).toHaveBeenCalledWith(mockSlotId);
            expect(pageInfoService.getPageUUID).toHaveBeenCalled();

            expect(mockSyncApi.setMessage).toHaveBeenCalledWith({
                type: IAlertServiceType.WARNING,
                description: 'se.cms.synchronization.slot.disabled.msg'
            });
            expect(mockSyncApi.disableItemList).toHaveBeenCalledWith(true);
        });

        it('GIVEN sync status exists WHEN syncing is allowed THEN it should return sync status', async () => {
            slotSynchronizationService.syncStatusExists.and.returnValue(true);
            pageContentSlotsService.getSlotStatus.and.returnValue(Promise.resolve(SlotStatus.PAGE));
            pageService.isPageApproved.and.returnValue(Promise.resolve(true));

            const actual = await component.getSyncStatus();

            expect(actual).toEqual(mockSyncStatus);

            expect(pageInfoService.getPageUID).toHaveBeenCalled();
            expect(slotSynchronizationService.getSyncStatus).toHaveBeenCalledWith(
                mockPageUid,
                mockSlotId
            );
            expect(pageContentSlotsService.getSlotStatus).toHaveBeenCalledWith(mockSlotId);
            expect(pageInfoService.getPageUUID).toHaveBeenCalled();

            expect(mockSyncApi.setMessage).not.toHaveBeenCalled();
            expect(mockSyncApi.disableItemList).not.toHaveBeenCalled();
        });
    });
});
