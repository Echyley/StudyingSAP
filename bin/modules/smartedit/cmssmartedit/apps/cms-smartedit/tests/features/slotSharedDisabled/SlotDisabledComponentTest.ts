/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ChangeDetectorRef, SimpleChange } from '@angular/core';
import { SlotDisabledComponent } from 'cmssmartedit/components/slotSharedDisabled';
import { of } from 'rxjs';
import { ICatalogService, L10nPipe, CMSModesService, SlotSharedService } from 'smarteditcommons';
import { createSimulateNgOnChanges } from 'testhelpers';

describe('SlotSharedDisabledComponent', () => {
    let catalogService: jasmine.SpyObj<ICatalogService>;
    let cMSModesService: jasmine.SpyObj<CMSModesService>;
    let l10nPipe: jasmine.SpyObj<L10nPipe>;
    let slotSharedService: jasmine.SpyObj<SlotSharedService>;
    const cdr = jasmine.createSpyObj<ChangeDetectorRef>('changeDetectorRef', ['detectChanges']);

    const slotId = 'BottomHeaderSlot';
    const catalogName = 'Electronics Content Catalog';

    let component: SlotDisabledComponent;

    type Input = Partial<Pick<typeof component, 'active'>>;
    let simulateNgOnChanges: ReturnType<typeof createSimulateNgOnChanges>;
    beforeEach(() => {
        catalogService = jasmine.createSpyObj<ICatalogService>('catalogService', [
            'getCatalogVersionByUuid'
        ]);

        cMSModesService = jasmine.createSpyObj<CMSModesService>('cMSModesService', [
            'isVersioningPerspectiveActive'
        ]);

        l10nPipe = jasmine.createSpyObj<L10nPipe>('l10nPipe', ['transform']);

        slotSharedService = jasmine.createSpyObj<SlotSharedService>('slotSharedService', [
            'isGlobalSlot'
        ]);

        component = new SlotDisabledComponent(
            catalogService,
            cMSModesService,
            l10nPipe,
            slotSharedService,
            cdr
        );

        simulateNgOnChanges = createSimulateNgOnChanges<Input>(component);
    });

    beforeEach(() => {
        l10nPipe.transform.and.callFake((_catalogName: any) => of(_catalogName.en));
    });

    describe('initialization', () => {
        beforeEach(() => {
            component.componentAttributes = {
                smarteditCatalogVersionUuid: undefined,
                smarteditComponentId: 'BottomHeaderSlot',
                smarteditComponentType: undefined,
                smarteditComponentUuid: undefined,
                smarteditElementUuid: undefined
            };

            catalogService.getCatalogVersionByUuid.and.returnValue(
                Promise.resolve({
                    catalogName: {
                        en: 'Electronics Content Catalog'
                    }
                }) as any
            );
        });

        it('GIVEN versioning perspective AND global slot THEN it sets popover message properly', async () => {
            cMSModesService.isVersioningPerspectiveActive.and.returnValue(Promise.resolve(true));

            slotSharedService.isGlobalSlot.and.returnValue(Promise.resolve(true));

            await component.ngOnInit();

            expect(component.popoverMessage).toEqual({
                translate: 'se.cms.versioning.parent.shared.slot.from.label',
                translateParams: {
                    slotId: 'BottomHeaderSlot',
                    catalogName: 'Electronics Content Catalog'
                }
            });
        });

        it('GIVEN versioning perspective AND non global slot THEN it sets popover message properly', async () => {
            cMSModesService.isVersioningPerspectiveActive.and.returnValue(Promise.resolve(true));

            slotSharedService.isGlobalSlot.and.returnValue(Promise.resolve(false));

            await component.ngOnInit();

            expect(component.popoverMessage).toEqual({
                translate: 'se.cms.versioning.shared.slot.from.label',
                translateParams: {
                    slotId,
                    catalogName
                }
            });
        });

        it('GIVEN non versioning perspective AND global slot THEN it sets popover message properly', async () => {
            cMSModesService.isVersioningPerspectiveActive.and.returnValue(Promise.resolve(false));

            slotSharedService.isGlobalSlot.and.returnValue(Promise.resolve(true));

            await component.ngOnInit();

            expect(component.popoverMessage).toEqual({
                translate: 'se.cms.parentsharedslot.decorator.label',
                translateParams: {
                    slotId,
                    catalogName
                }
            });
        });

        it('GIVEN non versioning perspective AND non global slot THEN it sets popover message properly', async () => {
            cMSModesService.isVersioningPerspectiveActive.and.returnValue(Promise.resolve(false));

            slotSharedService.isGlobalSlot.and.returnValue(Promise.resolve(false));

            await component.ngOnInit();

            expect(component.popoverMessage).toEqual({
                translate: 'se.cms.sharedslot.decorator.label',
                translateParams: {
                    slotId,
                    catalogName
                }
            });
        });
    });

    describe('active changes', () => {
        describe('Slot Icon class', () => {
            it('GIVEN global slot THEN it sets Icon class properly', () => {
                component.isGlobalSlot = true;
                simulateNgOnChanges({
                    active: new SimpleChange(undefined, true, false)
                });

                expect(component.iconClass).toBe('sap-icon--globe');
            });

            it('GIVEN non global slot THEN it sets Icon class properly', () => {
                component.isGlobalSlot = false;
                simulateNgOnChanges({
                    active: new SimpleChange(undefined, true, false)
                });

                expect(component.iconClass).toBe('sap-icon--chain-link');
            });
        });

        describe('Outer Slot class', () => {
            it('GIVEN active is false THEN it sets Outer Slot class properly', () => {
                simulateNgOnChanges({
                    active: new SimpleChange(undefined, false, false)
                });

                expect(component.outerSlotClass).toEqual({
                    'disabled-shared-slot__icon--outer-hovered': false,
                    'disabled-shared-slot__icon--outer-globe': false
                });
            });

            it('GIVEN active is true AND non versioning perspective THEN it sets Outer Slot class properly', () => {
                component.isVersioningPerspective = false;
                simulateNgOnChanges({
                    active: new SimpleChange(undefined, true, false)
                });

                expect(component.outerSlotClass).toEqual({
                    'disabled-shared-slot__icon--outer-hovered': true,
                    'disabled-shared-slot__icon--outer-globe': false
                });
            });

            it('GIVEN active is true AND global slot THEN it sets Outer Slot class properly', () => {
                component.isGlobalSlot = true;
                simulateNgOnChanges({
                    active: new SimpleChange(undefined, true, false)
                });

                expect(component.outerSlotClass).toEqual({
                    'disabled-shared-slot__icon--outer-hovered': true,
                    'disabled-shared-slot__icon--outer-globe': true
                });
            });
        });
    });
});
