/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ChangeDetectorRef } from '@angular/core';
import { RestrictionManagementSelectComponent } from 'cmssmarteditcontainer/components/restrictions/components';
import {
    RestrictionManagementSelectModel,
    RestrictionManagementSelectModelFactory
} from 'cmssmarteditcontainer/components/restrictions/services';

import { RestrictionsService } from 'cmssmarteditcontainer/services/RestrictionsService';
import { SystemEventService } from 'smarteditcommons';

describe('RestrictionManagementSelectComponent', () => {
    let component: RestrictionManagementSelectComponent;
    let restrictionManagementSelectModelFactory: jasmine.SpyObj<RestrictionManagementSelectModelFactory>;
    let restrictionsService: jasmine.SpyObj<RestrictionsService>;
    let systemEventService: jasmine.SpyObj<SystemEventService>;
    let cdr: jasmine.SpyObj<ChangeDetectorRef>;

    const factoryMock: any = {
        getRestrictionsPaged: jasmine.createSpy(),
        getRestrictionFromBackend: jasmine.createSpy(),
        getRestrictionTypes: jasmine.createSpy(),
        restrictionTypeSelected: jasmine.createSpy(),
        restrictionSelected: jasmine.createSpy(),
        createRestrictionSelected: jasmine.createSpy(),
        getRestriction: jasmine.createSpy(),
        isTypeSupported: jasmine.createSpy()
    };

    beforeEach(() => {
        restrictionManagementSelectModelFactory =
            jasmine.createSpyObj<RestrictionManagementSelectModelFactory>(
                'restrictionManagementSelectModelFactory',
                ['createRestrictionManagementSelectModel']
            );
        restrictionsService = jasmine.createSpyObj<RestrictionsService>('restrictionsService', [
            'getStructureApiUri'
        ]);
        systemEventService = jasmine.createSpyObj<SystemEventService>('systemEventService', [
            'subscribe'
        ]);
        cdr = jasmine.createSpyObj<ChangeDetectorRef>('cdr', ['detectChanges']);

        restrictionManagementSelectModelFactory.createRestrictionManagementSelectModel.and.returnValue(
            factoryMock
        );
        restrictionsService.getStructureApiUri.and.returnValue('structureApi');

        component = new RestrictionManagementSelectComponent(
            restrictionManagementSelectModelFactory,
            restrictionsService,
            systemEventService,
            cdr
        );
    });

    describe('initialize', () => {
        it('should create factory instance', () => {
            component.ngOnInit();

            expect(component.selectModel).toEqual(
                factoryMock as unknown as RestrictionManagementSelectModel
            );
        });

        it('should set controlling model values', () => {
            component.ngOnInit();

            expect(component.viewConfig).toEqual({
                showRestrictionSelector: false,
                showRestrictionEditor: false,
                mode: 'add',
                contentApi: '/cmswebservices/v1/sites/CURRENT_CONTEXT_SITE_ID/cmsitems/:identifier'
            });
        });

        it('should set fetch options and strategy', () => {
            component.ngOnInit();

            expect(component.fetchOptions).toEqual({
                fetchPage: jasmine.any(Function),
                fetchEntity: jasmine.any(Function)
            });

            expect(component.fetchStrategy).toEqual({
                fetchAll: jasmine.any(Function)
            });
        });

        it('should set function for disabling restriction choice', () => {
            expect(component.disableRestrictionChoice).not.toBeDefined();
            component.ngOnInit();

            expect(typeof component.disableRestrictionChoice).toEqual('function');
        });

        it('should emit submit and isDirty functions', () => {
            jasmine.clock().install();
            spyOn(component.submitFnChange, 'emit');
            spyOn(component.isDirtyFnChange, 'emit');

            component.ngOnInit();
            // simulating setTimeout
            jasmine.clock().tick(1);

            expect(component.submitFnChange.emit).toHaveBeenCalledWith(jasmine.any(Function));
            expect(component.isDirtyFnChange.emit).toHaveBeenCalledWith(jasmine.any(Function));
            jasmine.clock().uninstall();
        });
    });

    describe('destroy', () => {
        it('WHEN method is set THEN it should be called', () => {
            const unsubscribe = jasmine.createSpy();
            systemEventService.subscribe.and.returnValue(unsubscribe);

            component.ngOnInit();

            component.ngOnDestroy();

            expect(unsubscribe).toHaveBeenCalled();
        });
    });

    describe('selectRestrictionType', () => {
        it('WHEN restriction type is selected AND restriction selector is visible THEN it should reset restriction selector AND hide restriction editor', () => {
            factoryMock.restrictionTypeSelected.and.returnValue(true);
            component.reset = jasmine.createSpy();

            component.ngOnInit();
            component.viewConfig.showRestrictionSelector = true;

            component.selectRestrictionType();

            expect(component.reset).toHaveBeenCalled();
            expect(component.viewConfig.showRestrictionEditor).toEqual(false);
        });

        it('WHEN restriction type is selected AND restriction selector is NOT visible THEN it should show restriction selector AND hide restriction editor', () => {
            factoryMock.restrictionTypeSelected.and.returnValue(true);
            component.reset = jasmine.createSpy();

            component.ngOnInit();
            component.viewConfig.showRestrictionSelector = false;

            component.selectRestrictionType();

            expect(component.viewConfig.showRestrictionSelector).toEqual(true);
            expect(component.reset).not.toHaveBeenCalled();
            expect(component.viewConfig.showRestrictionEditor).toEqual(false);
        });

        it('WHEN restriction selector type support THEN it should have actionableSearchItem', () => {
            factoryMock.isTypeSupported.and.returnValue(true);
            component.ngOnInit();
            component.selectRestrictionType();

            expect(component.actionableSearchItem.eventId).toEqual(
                component.RESTRICTION_CREATE_BUTTON_PRESSED_EVENT_ID
            );
        });

        it('WHEN restriction selector type not support THEN it should not have actionableSearchItem', () => {
            factoryMock.isTypeSupported.and.returnValue(false);
            component.ngOnInit();
            component.selectRestrictionType();

            expect(component.actionableSearchItem).toEqual(undefined);
        });
    });

    describe('selectRestriction', () => {
        it('WHEN restriction is selected THEN it should set proper values', () => {
            factoryMock.restrictionSelected.and.returnValue(true);

            component.ngOnInit();
            component.selectRestriction();

            expect(component.editorHeader).toEqual(
                'se.cms.restriction.management.select.editor.header.add'
            );
            expect(component.viewConfig.mode).toEqual('add');
            expect(component.viewConfig.structureApi).toEqual('structureApi');
            expect(restrictionsService.getStructureApiUri).toHaveBeenCalledWith('add');
        });

        it('WHEN restriction is selected AND restriction editor is not visible THEN it should show it', () => {
            factoryMock.restrictionSelected.and.returnValue(true);

            component.ngOnInit();
            component.viewConfig.showRestrictionEditor = false;
            component.selectRestriction();

            expect(component.viewConfig.showRestrictionEditor).toEqual(true);
        });
    });

    describe('createRestriction', () => {
        it('should call factory in order to create restriction, and set proper values', async () => {
            component.uriContext = { context: 'context' };
            component.ngOnInit();

            await component.createRestriction('name');

            expect(component.editorHeader).toEqual(
                'se.cms.restriction.management.select.editor.header.create'
            );
            expect(component.viewConfig.mode).toEqual('create');
            expect(component.viewConfig.structureApi).toEqual('structureApi');
            expect(restrictionsService.getStructureApiUri).toHaveBeenCalledWith('create');
            expect(factoryMock.createRestrictionSelected).toHaveBeenCalledWith('name', {
                context: 'context'
            });
        });

        it('WHEN restriction selector is visible THEN it should call reset on this selector', async () => {
            component.reset = jasmine.createSpy();
            component.ngOnInit();
            component.viewConfig.showRestrictionEditor = true;

            await component.createRestriction('name');

            expect(component.reset).toHaveBeenCalled();
        });

        it('WHEN restriction selector is not visible THEN it should this selector visible', async () => {
            component.reset = jasmine.createSpy();
            component.ngOnInit();

            await component.createRestriction('name');

            expect(component.reset).not.toHaveBeenCalled();
            expect(component.viewConfig.showRestrictionEditor).toEqual(true);
        });
    });

    describe('showWarningMessage', () => {
        it('should return true when restriction is set and restriction type is not supported', () => {
            factoryMock.getRestriction.and.returnValue({});
            factoryMock.isTypeSupported.and.returnValue(false);

            component.ngOnInit();

            expect(component.showWarningMessage()).toEqual(true);
        });
    });
});
