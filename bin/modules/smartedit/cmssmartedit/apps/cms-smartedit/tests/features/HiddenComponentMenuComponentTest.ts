/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ChangeDetectorRef } from '@angular/core';
import { HiddenComponentMenuComponent } from 'cmssmartedit/components/slotVisibility';
import { HiddenComponentMenuService } from 'cmssmartedit/services';
import { IComponentHandlerService } from 'smarteditcommons';

describe('HiddenComponentMenuComponent', () => {
    let componentHandlerService: jasmine.SpyObj<IComponentHandlerService>;
    let hiddenComponentMenuService: jasmine.SpyObj<HiddenComponentMenuService>;
    const cdr = jasmine.createSpyObj<ChangeDetectorRef>('changeDetectorRef', ['detectChanges']);
    const slotUuid = 'eyJpdGVtSWQiOiJjbXNpdGVtXz';

    let component: HiddenComponentMenuComponent;
    beforeEach(() => {
        componentHandlerService = jasmine.createSpyObj<IComponentHandlerService>(
            'componentHandlerService',
            ['getOriginalComponent', 'getUuid']
        );

        hiddenComponentMenuService = jasmine.createSpyObj<HiddenComponentMenuService>(
            'hiddenComponentMenuService',
            ['getItemsForHiddenComponent']
        );

        component = new HiddenComponentMenuComponent(
            componentHandlerService,
            hiddenComponentMenuService,
            cdr
        );
    });

    describe('initialize', () => {
        beforeEach(async () => {
            componentHandlerService.getOriginalComponent.and.returnValue({} as any);

            componentHandlerService.getUuid.and.returnValue(slotUuid);

            hiddenComponentMenuService.getItemsForHiddenComponent.and.returnValue(
                Promise.resolve<any>({
                    configuration: {},
                    buttons: [
                        {
                            key: 'cancel'
                        },
                        {
                            key: 'approve'
                        }
                    ]
                })
            );

            await component.ngOnInit();
        });
        it('gets slotUuid', () => {
            expect(componentHandlerService.getUuid).toHaveBeenCalled();
        });

        it('gets configuration properly', () => {
            expect(hiddenComponentMenuService.getItemsForHiddenComponent).toHaveBeenCalled();
        });

        it('sets menuItems properly', () => {
            expect(component.menuItems.length).toBe(2);
        });
    });

    describe('toggle', () => {
        it('GIVEN menu is closed WHEN toggle button is clicked THEN it will open the menu', () => {
            component.isMenuOpen = false;

            component.toggle(new MouseEvent('click'));

            expect(component.isMenuOpen).toBe(true);
        });

        it('GIVEN menu is opened WHEN toggle button is clicked THEN it will close the menu', () => {
            component.isMenuOpen = true;

            component.toggle(new MouseEvent('click'));

            expect(component.isMenuOpen).toBe(false);
        });
    });

    it('WHEN menu is opened THEN it will hide menu', () => {
        component.isMenuOpen = true;

        component.hideMenu();

        expect(component.isMenuOpen).toBe(false);
    });

    it('WHEN menu item has been clicked THEN it executes a callback action AND closes the menu', () => {
        const item = {
            key: null,
            action: {
                callback: jasmine.createSpy()
            }
        };
        const event = new MouseEvent('click');
        component.executeMenuItemCallback(item, event);

        expect(item.action.callback).toHaveBeenCalledWith(undefined, event);
    });
});
