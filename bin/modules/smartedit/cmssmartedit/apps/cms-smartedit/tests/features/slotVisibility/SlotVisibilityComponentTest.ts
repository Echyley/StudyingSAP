/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ChangeDetectorRef } from '@angular/core';
import {
    ICMSComponent,
    IComponentSharedService,
    IComponentVisibilityAlertService
} from 'cmscommons';
import { SlotVisibilityComponent } from 'cmssmartedit/components/slotVisibility';
import {
    ICatalogService,
    ICatalogVersionPermissionService,
    LogService,
    IEditorModalService,
    IUserTrackingService
} from 'smarteditcommons';

describe('SlotVisibilityComponent', () => {
    let catalogService: jasmine.SpyObj<ICatalogService>;
    let componentSharedService: jasmine.SpyObj<IComponentSharedService>;
    let editorModalService: jasmine.SpyObj<IEditorModalService>;
    let componentVisibilityAlertService: jasmine.SpyObj<IComponentVisibilityAlertService>;
    let catalogVersionPermissionService: jasmine.SpyObj<ICatalogVersionPermissionService>;
    let logService: jasmine.SpyObj<LogService>;
    let userTrackingService: jasmine.SpyObj<IUserTrackingService>;
    const cdr = jasmine.createSpyObj<ChangeDetectorRef>('changeDetectorRef', ['detectChanges']);

    let component: SlotVisibilityComponent;
    beforeEach(() => {
        catalogService = jasmine.createSpyObj<ICatalogService>('catalogService', [
            'getCatalogVersionByUuid'
        ]);

        componentSharedService = jasmine.createSpyObj<IComponentSharedService>(
            'componentSharedService',
            ['isComponentShared']
        );

        editorModalService = jasmine.createSpyObj<IEditorModalService>('editorModalService', [
            'openAndRerenderSlot'
        ]);

        componentVisibilityAlertService = jasmine.createSpyObj<IComponentVisibilityAlertService>(
            'componentVisibilityAlertService',
            ['checkAndAlertOnComponentVisibility']
        );

        catalogVersionPermissionService = jasmine.createSpyObj<ICatalogVersionPermissionService>(
            'catalogVersionPermissionService',
            ['hasWritePermission']
        );

        logService = jasmine.createSpyObj<LogService>('logService', ['warn']);

        userTrackingService = jasmine.createSpyObj<IUserTrackingService>('userTrackingService', [
            'trackingUserAction'
        ]);

        component = new SlotVisibilityComponent(
            catalogService,
            componentSharedService,
            editorModalService,
            componentVisibilityAlertService,
            catalogVersionPermissionService,
            logService,
            cdr,
            userTrackingService
        );
    });

    describe('initialize', () => {
        beforeEach(() => {
            component.component = {
                catalogVersion: 'apparel-ukContentCatalog/Staged',
                isExternal: false,
                visible: true,
                restrictions: []
            } as unknown as ICMSComponent;

            componentSharedService.isComponentShared.and.returnValue(Promise.resolve(true));

            catalogService.getCatalogVersionByUuid.and.returnValue(
                Promise.resolve({
                    catalogId: 'apparel-ukContentCatalog',
                    version: 'Staged'
                }) as any
            );
        });

        it('sets isSharedComponent properly', async () => {
            await component.ngOnInit();

            expect(componentSharedService.isComponentShared).toHaveBeenCalled();

            expect(component.isSharedComponent).toBe(true);
        });

        it('GIVEN component is not writable THEN it is set as read only', async () => {
            catalogVersionPermissionService.hasWritePermission.and.returnValue(
                Promise.resolve(false)
            );

            await component.ngOnInit();

            expect(component.readOnly).toBe(true);
        });

        it('GIVEN component is writeable AND is external THEN it is set as read only', async () => {
            catalogVersionPermissionService.hasWritePermission.and.returnValue(
                Promise.resolve(true)
            );
            component.component.isExternal = true;

            await component.ngOnInit();

            expect(component.readOnly).toBe(true);
        });

        it('GIVEN component is visible THEN it sets text properly', async () => {
            await component.ngOnInit();

            expect(component.componentVisibilitySwitch).toEqual(
                'se.cms.component.visibility.status.on'
            );
        });

        it('GIVEN component is not visible THEN it sets text properly', async () => {
            component.component.visible = false;

            await component.ngOnInit();
        });

        it('GIVEN component has any restrictions applied THEN it sets text with the restrictions count properly', async () => {
            await component.ngOnInit();

            expect(component.componentRestrictionsCount).toBe('(0)');
        });

        it('GIVEN component has no restrictions applied THEN it sets text with the restrictions count set to 0', async () => {
            component.component.restrictions = ['uuid1', 'uuid2'];

            await component.ngOnInit();

            expect(component.componentRestrictionsCount).toBe('(2)');
        });
    });

    describe('openEditorModal', () => {
        it('WHEN called THEN it opens a modal with the given component uuid AND performs a check for component visibility AND track user action', async () => {
            const slotIdInput = 'slotId';
            const componentInput = {
                typeCode: 'CMSParagraphComponent',
                uuid: 'eyJpdGVtSWQiOiJjbXNpdG'
            } as unknown as ICMSComponent;
            component.slotId = slotIdInput;
            component.component = componentInput;

            editorModalService.openAndRerenderSlot.and.returnValue(Promise.resolve({}));

            await component.openEditorModal();

            expect(editorModalService.openAndRerenderSlot).toHaveBeenCalledWith(
                componentInput.typeCode,
                componentInput.uuid,
                'visible'
            );

            expect(
                componentVisibilityAlertService.checkAndAlertOnComponentVisibility
            ).toHaveBeenCalled();

            expect(userTrackingService.trackingUserAction).toHaveBeenCalled();
        });

        it('handles openAndRerenderSlot exception properly AND track user action', async () => {
            component.component = {
                typeCode: null,
                uuid: null
            } as unknown as ICMSComponent;

            editorModalService.openAndRerenderSlot.and.returnValue(Promise.reject());

            await component.openEditorModal();
            expect(logService.warn).not.toHaveBeenCalled();
            expect(userTrackingService.trackingUserAction).toHaveBeenCalled();
        });
    });
});
