/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ChangeDetectorRef } from '@angular/core';
import { ICMSComponent, ISlotVisibilityService } from 'cmscommons';
import { SlotVisibilityButtonComponent } from 'cmssmartedit/components';
import {
    CrossFrameEventService,
    ISharedDataService,
    ContextualMenuItemData
} from 'smarteditcommons';

describe('SlotVisibilityButtonComponent', () => {
    let contextualMenuItem: ContextualMenuItemData;
    let slotVisibilityService: jasmine.SpyObj<ISlotVisibilityService>;
    let sharedDataService: jasmine.SpyObj<ISharedDataService>;
    let crossFrameEventService: jasmine.SpyObj<CrossFrameEventService>;
    const cdr = jasmine.createSpyObj<ChangeDetectorRef>('changeDetectorRef', ['detectChanges']);

    let component: SlotVisibilityButtonComponent;
    let componentAny: any;
    beforeEach(() => {
        contextualMenuItem = {
            componentAttributes: {
                smarteditComponentId: 'ApparelUKHomepageFreeDelBannerComponent',
                smarteditCatalogVersionUuid: null,
                smarteditComponentType: null,
                smarteditComponentUuid: null,
                smarteditElementUuid: null
            },
            setRemainOpen: jasmine.createSpy('setRemainOpen')
        };

        slotVisibilityService = jasmine.createSpyObj<ISlotVisibilityService>(
            'slotVisibilityService',
            ['getHiddenComponents']
        );

        sharedDataService = jasmine.createSpyObj<ISharedDataService>('sharedDataService', ['get']);

        crossFrameEventService = jasmine.createSpyObj<CrossFrameEventService>(
            'crossFrameEventService',
            ['subscribe']
        );

        component = new SlotVisibilityButtonComponent(
            contextualMenuItem,
            slotVisibilityService,
            sharedDataService,
            crossFrameEventService,
            cdr
        );
        componentAny = component;
    });

    describe('initialize', () => {
        beforeEach(() => {
            sharedDataService.get.and.returnValue(
                Promise.resolve({
                    pageContext: {
                        catalogVersionUuid: 'apparel-ukContentCatalog/Online'
                    }
                })
            );
        });

        it('GIVEN some external component THEN it sets the isExternal flag properly', async () => {
            const hiddenComponents: any = [
                {
                    catalogVersion: 'apparel-ukContentCatalog/Staged'
                },
                {
                    catalogVersion: 'apparel-ukContentCatalog/Online'
                }
            ];
            slotVisibilityService.getHiddenComponents.and.returnValue(
                Promise.resolve(hiddenComponents)
            );

            await component.ngOnInit();

            expect(component.hiddenComponents).toEqual(([
                {
                    catalogVersion: 'apparel-ukContentCatalog/Staged',
                    isExternal: true
                },
                {
                    catalogVersion: 'apparel-ukContentCatalog/Online',
                    isExternal: false
                }
            ] as unknown) as ICMSComponent[]);
        });

        it('GIVEN hidden components THEN it sets the count AND displays menu button', async () => {
            const hiddenComponents: any = [
                {
                    catalogVersion: 'apparel-ukContentCatalog/Staged'
                },
                {
                    catalogVersion: 'apparel-ukContentCatalog/Online'
                }
            ];
            slotVisibilityService.getHiddenComponents.and.returnValue(
                Promise.resolve(hiddenComponents)
            );

            await component.ngOnInit();

            expect(component.hiddenComponentCount).toEqual(2);
            expect(component.buttonVisible).toBe(true);
        });

        it('GIVEN no hiddenComponents THEN it sets the count to 0 AND does not display menu button', async () => {
            slotVisibilityService.getHiddenComponents.and.returnValue(Promise.resolve([]));

            await component.ngOnInit();

            expect(component.hiddenComponentCount).toEqual(0);
            expect(component.buttonVisible).toBe(false);
        });
    });

    describe('isPopupOpened', () => {
        it('GIVEN isPopupOpened is set to false WHEN ngDoCheck lifecycle call THEN it sets isPopupOpenedPreviousValue to false', () => {
            // GIVEN
            componentAny.isPopupOpenedPreviousValue = true;
            component.isPopupOpened = false;

            // WHEN
            component.ngDoCheck();

            // THEN
            expect(componentAny.isPopupOpenedPreviousValue).toEqual(false);
        });

        it('GIVEN isPopupOpened is set to false AND isPopupOpenedPreviousValue is set to true WHEN ngDoCheck lifecycle call THEN it sets isPopupOpenedPreviousValue to false', () => {
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
});
