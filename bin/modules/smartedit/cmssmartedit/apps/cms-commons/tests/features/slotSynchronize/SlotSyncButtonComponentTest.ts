/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ChangeDetectorRef } from '@angular/core';
import { SlotSyncButtonComponent, SlotSynchronizationService } from 'cmscommons';
import {
    IPageInfoService,
    CrossFrameEventService,
    ContextualMenuItemData,
    EVENT_OUTER_FRAME_CLICKED
} from 'smarteditcommons';

describe('SlotSyncButtonComponent', () => {
    let component: SlotSyncButtonComponent;
    let componentAny: any;
    let injectedData: ContextualMenuItemData;
    let slotSynchronizationService: jasmine.SpyObj<SlotSynchronizationService>;
    let pageInfoService: jasmine.SpyObj<IPageInfoService>;
    let crossFrameEventService: jasmine.SpyObj<CrossFrameEventService>;
    const cdr = jasmine.createSpyObj<ChangeDetectorRef>('changeDetectorRef', ['detectChanges']);

    let unregisterOuterFrame: jasmine.Spy;
    let unregisterSyncPolling: jasmine.Spy;
    let unregisterInnerFrameClicked: jasmine.Spy;

    const mockPageUuid = 'page_uuid';
    const mockSyncStatus = {
        fromSharedDependency: false,
        status: 'IN_SYNC',
        lastSyncStatus: 0
    };

    beforeEach(() => {
        unregisterOuterFrame = jasmine.createSpy();
        unregisterSyncPolling = jasmine.createSpy();
        unregisterInnerFrameClicked = jasmine.createSpy();
        injectedData = {
            componentAttributes: {
                smarteditComponentId: 'ApparelUKHomepageFreeDelBannerComponent',
                smarteditCatalogVersionUuid: undefined,
                smarteditComponentType: undefined,
                smarteditComponentUuid: undefined,
                smarteditElementUuid: undefined
            },
            setRemainOpen: jasmine.createSpy('setRemainOpen')
        };
        slotSynchronizationService = jasmine.createSpyObj<SlotSynchronizationService>(
            'slotSynchronizationService',
            ['getSyncStatus', 'syncStatusExists']
        );
        pageInfoService = jasmine.createSpyObj<IPageInfoService>('pageInfoService', [
            'getPageUUID'
        ]);
        crossFrameEventService = jasmine.createSpyObj<CrossFrameEventService>(
            'crossFrameEventService',
            ['subscribe']
        );
        component = new SlotSyncButtonComponent(
            injectedData,
            slotSynchronizationService,
            pageInfoService,
            crossFrameEventService,
            cdr
        );
        componentAny = component;
    });

    beforeEach(() => {
        pageInfoService.getPageUUID.and.returnValue(Promise.resolve(mockPageUuid));

        slotSynchronizationService.getSyncStatus.and.returnValue(
            Promise.resolve<any>(mockSyncStatus)
        );
        slotSynchronizationService.syncStatusExists.and.returnValue(true);

        crossFrameEventService.subscribe.and.callFake((eventName) => {
            if (eventName === 'syncFastFetch') {
                return unregisterSyncPolling;
            } else if (eventName === EVENT_OUTER_FRAME_CLICKED) {
                return unregisterOuterFrame;
            }
            return unregisterInnerFrameClicked;
        });
    });

    it('should have initial values set', () => {
        expect(componentAny.buttonName).toEqual('slotSyncButton');
        expect(component.isPopupOpened).toEqual(false);
        expect(component.isReady).toEqual(false);
        expect(component.isSlotInSync).toEqual(true);
        expect(component.newSlotIsNotSynchronized).toEqual(false);
        expect(component.slotIsShared).toEqual(false);

        expect(component.popupConfig).toEqual({
            halign: 'left',
            valign: 'bottom',
            additionalClasses: [
                'se-slot-ctx-menu__divider',
                'se-slot-ctx-menu__dropdown-toggle-wrapper'
            ]
        });
    });

    it('WHEN initialized THEN it should get sync status and subscribe to events', async () => {
        spyOn(componentAny, 'getSyncStatus');
        await component.ngOnInit();

        expect(componentAny.getSyncStatus).toHaveBeenCalled();

        expect(crossFrameEventService.subscribe).toHaveBeenCalledTimes(3);
    });

    it('GIVEN component is initialized WHEN it gets destroyed THEN it should unsubscribe from listening to events', async () => {
        await component.ngOnInit();

        component.ngOnDestroy();

        expect(unregisterOuterFrame).toHaveBeenCalled();
        expect(unregisterSyncPolling).toHaveBeenCalled();
        expect(unregisterInnerFrameClicked).toHaveBeenCalled();
    });

    describe('ngDoCheck', () => {
        it('GIVEN isPopupOpened is set to false WHEN ngDoCheck lifecycle call THEN it sets isPopupOpenedPreviousValue to false', () => {
            // GIVEN
            componentAny.isPopupOpenedPreviousValue = true;
            component.isPopupOpened = false;

            // WHEN
            component.ngDoCheck();

            // THEN
            expect(componentAny.isPopupOpenedPreviousValue).toEqual(false);
        });

        it('GIVEN isPopupOpened is set to false WHEN ngDoCheck lifecycle call THEN it sets isPopupOpenedPreviousValue to false', () => {
            // GIVEN
            componentAny.isPopupOpenedPreviousValue = true;
            component.isPopupOpened = false;

            // WHEN
            component.ngDoCheck();

            // THEN
            expect(componentAny.isPopupOpenedPreviousValue).toEqual(false);
        });

        it('GIVEN isPopupOpenedPreviousValue and isPopupOpened are both true WHEN ngDoCheck is called THEN isPopupOpenedPreviousValue will not change', () => {
            // GIVEN
            componentAny.isPopupOpenedPreviousValue = true;
            component.isPopupOpened = true;

            // WHEN
            component.ngDoCheck();

            // THEN
            expect(componentAny.isPopupOpenedPreviousValue).toEqual(true);
        });

        it('GIVEN isPopupOpenedPreviousValue and isPopupOpened are both false WHEN ngDoCheck is called THEN isPopupOpenedPreviousValue will not change', () => {
            // GIVEN
            componentAny.isPopupOpenedPreviousValue = false;
            component.isPopupOpened = false;

            // WHEN
            component.ngDoCheck();

            // THEN
            expect(componentAny.isPopupOpenedPreviousValue).toEqual(false);
        });
    });

    describe('getSyncStatus', () => {
        it('GIVEN sync status exists THEN it should set proper flags', async () => {
            await componentAny.getSyncStatus();

            expect(component.isReady).toEqual(true);

            // status IN_SYNC
            expect(component.isSlotInSync).toEqual(true);
            // lastSyncStatus is equal 0;
            expect(component.newSlotIsNotSynchronized).toEqual(true);
            // fromSharedDependency
            expect(component.slotIsShared).toEqual(false);
        });

        it('GIVEN sync status does not exists THEN it should set readiness to false', async () => {
            slotSynchronizationService.syncStatusExists.and.returnValue(false);

            await componentAny.getSyncStatus();

            expect(component.isReady).toEqual(false);

            // status IN_SYNC
            expect(component.isSlotInSync).toEqual(true);
            // lastSyncStatus is equal 0;
            expect(component.newSlotIsNotSynchronized).toEqual(false);
            // fromSharedDependency
            expect(component.slotIsShared).toEqual(false);
        });
    });
});
