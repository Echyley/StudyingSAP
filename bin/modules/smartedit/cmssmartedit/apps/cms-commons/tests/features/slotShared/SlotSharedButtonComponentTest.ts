/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ChangeDetectorRef } from '@angular/core';
import { SlotSharedButtonComponent } from 'cmscommons';
import {
    CrossFrameEventService,
    ICatalogService,
    ContextualMenuItemData,
    IComponentHandlerService,
    SlotSharedService,
    IPageInfoService
} from 'smarteditcommons';

describe('SlotSharedButtonComponent', () => {
    let contextualMenuItem: ContextualMenuItemData;
    let catalogService: jasmine.SpyObj<ICatalogService>;
    let componentHandlerService: jasmine.SpyObj<IComponentHandlerService>;
    let crossFrameEventService: jasmine.SpyObj<CrossFrameEventService>;
    let slotSharedService: jasmine.SpyObj<SlotSharedService>;
    let pageInfoService: jasmine.SpyObj<IPageInfoService>;
    const cdr = jasmine.createSpyObj<ChangeDetectorRef>('changeDetectorRef', ['detectChanges']);
    let component: SlotSharedButtonComponent;
    let componentAny: any;
    beforeEach(() => {
        contextualMenuItem = {
            componentAttributes: {
                smarteditComponentId: 'ApparelUKHomepageFreeDelBannerComponent',
                smarteditCatalogVersionUuid: undefined,
                smarteditComponentType: undefined,
                smarteditComponentUuid: undefined,
                smarteditElementUuid: undefined
            },
            setRemainOpen: jasmine.createSpy('setRemainOpen')
        };

        catalogService = jasmine.createSpyObj<ICatalogService>('catalogService', [
            'isCurrentCatalogMultiCountry'
        ]);

        componentHandlerService = jasmine.createSpyObj<IComponentHandlerService>(
            'componentHandlerService',
            ['isExternalComponent']
        );

        crossFrameEventService = jasmine.createSpyObj<CrossFrameEventService>(
            'crossFrameEventService',
            ['subscribe']
        );

        slotSharedService = jasmine.createSpyObj<SlotSharedService>('slotSharedService', [
            'isSlotShared',
            'isGlobalSlot',
            'replaceGlobalSlot',
            'replaceSharedSlot'
        ]);

        pageInfoService = jasmine.createSpyObj<IPageInfoService>('pageInfoService', [
            'isSameCatalogVersionOfPageAndPageTemplate'
        ]);

        component = new SlotSharedButtonComponent(
            contextualMenuItem,
            catalogService,
            componentHandlerService,
            crossFrameEventService,
            pageInfoService,
            slotSharedService,
            cdr
        );
        componentAny = component;
    });

    beforeEach(() => {
        componentHandlerService.isExternalComponent.and.returnValue(Promise.resolve(true));
        catalogService.isCurrentCatalogMultiCountry.and.returnValue(Promise.resolve(true));

        slotSharedService.isSlotShared.and.returnValue(Promise.resolve(true));
        slotSharedService.isGlobalSlot.and.returnValue(Promise.resolve(true));
        slotSharedService.replaceGlobalSlot.and.returnValue(Promise.resolve());
        slotSharedService.replaceSharedSlot.and.returnValue(Promise.resolve());

        componentAny.reload = jasmine.createSpy('reload');
    });

    it('isPopupOpened is initialized to false', () => {
        expect(component.isPopupOpened).toEqual(false);
    });

    it('GIVEN isPopupOpened is set to true WHEN ngDoCheck lifecycle call THEN it sets isPopupOpenedPreviousValue to true', () => {
        // GIVEN
        componentAny.isPopupOpenedPreviousValue = false;
        component.isPopupOpened = true;

        // WHEN
        component.ngDoCheck();

        // THEN
        expect(componentAny.isPopupOpenedPreviousValue).toEqual(true);
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

    it('GIVEN global slot AND popup is opened WHEN replaceSlot is called THEN popup is closed', async () => {
        // GIVEN
        component.isPopupOpened = true;
        component.isGlobalSlot = true;

        // WHEN
        await component.replaceSlot(new MouseEvent('click'));

        // THEN
        expect(slotSharedService.replaceGlobalSlot).toHaveBeenCalledWith(
            contextualMenuItem.componentAttributes
        );
        expect(component.isPopupOpened).toEqual(false);
        expect(componentAny.reload).toHaveBeenCalled();
    });

    it('GIVEN shared slot and popup is opened WHEN replaceSlot is called THEN popup is closed', async () => {
        // GIVEN
        component.isPopupOpened = true;
        component.isExternalSlot = false;
        componentAny.isMultiCountry = false;
        componentAny.isSlotShared = true;

        // WHEN
        await component.replaceSlot(new MouseEvent('click'));

        // THEN
        expect(slotSharedService.replaceSharedSlot).toHaveBeenCalledWith(
            contextualMenuItem.componentAttributes
        );
        expect(component.isPopupOpened).toEqual(false);
        expect(componentAny.reload).toHaveBeenCalled();
    });

    it('GIVEN non multi country catalog AND non external slot AND slot is shared THEN slot can be shared', async () => {
        componentHandlerService.isExternalComponent.and.returnValue(Promise.resolve(false));
        catalogService.isCurrentCatalogMultiCountry.and.returnValue(Promise.resolve(false));

        await component.ngOnInit();

        expect(component.isSlotShared).toEqual(true);
    });

    it('GIVEN multi country catalog AND same catalogVersion of page and pageTemplate non external slot AND slot is shared THEN slot can be shared', async () => {
        componentHandlerService.isExternalComponent.and.returnValue(Promise.resolve(false));
        pageInfoService.isSameCatalogVersionOfPageAndPageTemplate.and.returnValue(
            Promise.resolve(true)
        );
        catalogService.isCurrentCatalogMultiCountry.and.returnValue(Promise.resolve(true));

        await component.ngOnInit();

        expect(component.isSlotShared).toEqual(true);
    });
});
