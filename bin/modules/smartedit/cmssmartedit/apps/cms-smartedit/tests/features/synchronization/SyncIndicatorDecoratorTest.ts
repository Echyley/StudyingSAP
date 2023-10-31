/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ChangeDetectorRef } from '@angular/core';
import {
    DEFAULT_SYNCHRONIZATION_STATUSES,
    ISyncStatus,
    SlotSynchronizationService
} from 'cmscommons';
import { SyncIndicatorDecorator } from 'cmssmartedit/components/synchronize';
import { ICatalogService, CrossFrameEventService, IPageInfoService } from 'smarteditcommons';

describe('SyncIndicatorDecorator', () => {
    let decorator: SyncIndicatorDecorator;
    let catalogService: jasmine.SpyObj<ICatalogService>;
    let slotSynchronizationService: jasmine.SpyObj<SlotSynchronizationService>;
    let crossFrameEventService: jasmine.SpyObj<CrossFrameEventService>;
    let pageInfoService: jasmine.SpyObj<IPageInfoService>;
    let cdr: jasmine.SpyObj<ChangeDetectorRef>;

    let unregister: jasmine.Spy;

    const mockSyncStatus = ({
        status: DEFAULT_SYNCHRONIZATION_STATUSES.IN_SYNC,
        catalogVersionName: 'catalogName',
        itemId: 'itemId'
    } as unknown) as ISyncStatus;
    const mockPageUuid = 'pageUUID';
    const mockSmarteditId = 'smarteditId';

    beforeEach(() => {
        catalogService = jasmine.createSpyObj<ICatalogService>('catalogService', [
            'isContentCatalogVersionNonActive'
        ]);
        slotSynchronizationService = jasmine.createSpyObj<SlotSynchronizationService>(
            'slotSynchronizationService',
            ['getSyncStatus', 'syncStatusExists']
        );
        crossFrameEventService = jasmine.createSpyObj<CrossFrameEventService>(
            'crossFrameEventService',
            ['subscribe']
        );
        pageInfoService = jasmine.createSpyObj<IPageInfoService>('pageInfoService', [
            'getPageUUID'
        ]);
        cdr = jasmine.createSpyObj<ChangeDetectorRef>('cdr', ['detectChanges']);

        decorator = new SyncIndicatorDecorator(
            catalogService,
            slotSynchronizationService,
            crossFrameEventService,
            pageInfoService,
            cdr
        );
    });

    beforeEach(() => {
        unregister = jasmine.createSpy();

        catalogService.isContentCatalogVersionNonActive.and.returnValue(Promise.resolve(true));
        pageInfoService.getPageUUID.and.returnValue(Promise.resolve(mockPageUuid));
        slotSynchronizationService.getSyncStatus.and.returnValue(Promise.resolve(mockSyncStatus));
        slotSynchronizationService.syncStatusExists.and.returnValue(true);
        crossFrameEventService.subscribe.and.returnValue(unregister);

        spyOnProperty(decorator, 'componentAttributes', 'get').and.returnValue({
            smarteditComponentId: mockSmarteditId,
            smarteditCatalogVersionUuid: '',
            smarteditComponentType: '',
            smarteditComponentUuid: '',
            smarteditElementUuid: ''
        });
    });

    it('GIVEN catalog version is active WHEN decorator is initialized THEN it should assign non version value, page uuid and subscribe to event', async () => {
        catalogService.isContentCatalogVersionNonActive.and.returnValue(Promise.resolve(false));

        await decorator.ngOnInit();

        expect(decorator.pageUUID).toEqual(mockPageUuid);
        expect(decorator.isVersionNonActive).toEqual(false);
        expect(crossFrameEventService.subscribe).toHaveBeenCalledWith(
            'syncFastFetch',
            jasmine.any(Function)
        );

        expect(slotSynchronizationService.getSyncStatus).not.toHaveBeenCalled();
    });

    it('GIVEN catalog version is not active WHEN decorator is initialized THEN it should assign non version value, page uuid, sync status and subscribe to event', async () => {
        await decorator.ngOnInit();

        expect(decorator.pageUUID).toEqual(mockPageUuid);
        expect(decorator.isVersionNonActive).toEqual(true);
        expect(decorator.syncStatus).toEqual(mockSyncStatus);
        expect(crossFrameEventService.subscribe).toHaveBeenCalledWith(
            'syncFastFetch',
            jasmine.any(Function)
        );

        expect(slotSynchronizationService.getSyncStatus).toHaveBeenCalledWith(
            mockPageUuid,
            mockSmarteditId
        );
    });

    it('GIVEN catalog version is not active AND getting sync status throws an error WHEN decorator is initialized THEN it should assign non version value, page uuid, sync status to unavailable and subscribe to event', async () => {
        slotSynchronizationService.getSyncStatus.and.returnValue(Promise.reject());

        await decorator.ngOnInit();

        expect(decorator.pageUUID).toEqual(mockPageUuid);
        expect(decorator.isVersionNonActive).toEqual(true);
        expect(decorator.syncStatus).toEqual({
            status: DEFAULT_SYNCHRONIZATION_STATUSES.UNAVAILABLE
        } as ISyncStatus);
        expect(crossFrameEventService.subscribe).toHaveBeenCalledWith(
            'syncFastFetch',
            jasmine.any(Function)
        );

        expect(slotSynchronizationService.getSyncStatus).toHaveBeenCalledWith(
            mockPageUuid,
            mockSmarteditId
        );
    });
});
