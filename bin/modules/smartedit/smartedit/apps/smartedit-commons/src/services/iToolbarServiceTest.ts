/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component } from '@angular/core';
import { LogService } from '@smart/utils';
import { IPermissionService, ToolbarSection } from 'smarteditcommons';
import { IToolbarService, ToolbarItemType } from 'smarteditcommons/services/IToolbarService';

describe('outer toolbarInterfaceModule', () => {
    let logService: LogService;
    let iToolbarService: IToolbarService;
    let permissionService: jasmine.SpyObj<IPermissionService>;

    class ToolbarService extends IToolbarService {}

    @Component({
        selector: 'toolbar-comp1',
        template: `<p>ToolbarComp1</p>`
    })
    class ToolbarComp1 {}
    @Component({
        selector: 'toolbar-comp1',
        template: `<p>ToolbarComp2</p>`
    })
    class ToolbarComp2 {}

    beforeEach(() => {
        logService = jasmine.createSpyObj<LogService>('logService', ['error']);
        permissionService = jasmine.createSpyObj<IPermissionService>('permissionService', [
            'isPermitted'
        ]);
        permissionService.isPermitted.and.returnValue(Promise.resolve(true));

        iToolbarService = new ToolbarService(logService, permissionService);
    });

    it('ToolbarServiceInterface declares the expected set of empty functions', () => {
        expect(IToolbarService.prototype.addAliases).toBeEmptyFunction();
        expect(IToolbarService.prototype.removeItemByKey).toBeEmptyFunction();
        expect(IToolbarService.prototype.removeAliasByKey).toBeEmptyFunction();
        expect(IToolbarService.prototype.triggerActionOnInner).toBeEmptyFunction();
    });

    it('ToolbarServiceInterface.addItems converts actions into aliases (key-callback mapping of actions) before appending them by means of addAliases', async () => {
        const addAliasesSpy = spyOn(iToolbarService, 'addAliases').and.callThrough();
        spyOn(iToolbarService, 'getAliases').and.callThrough();

        permissionService.isPermitted.and.returnValue(Promise.resolve(false));

        const callback1 = () => {
            //
        };
        const callback2 = () => {
            //
        };

        expect(iToolbarService.getAliases()).toEqual([]);

        // Execution
        await iToolbarService.addItems([
            {
                key: 'key1',
                nameI18nKey: 'somenameI18nKey1',
                descriptionI18nKey: 'somedescriptionI18nKey1',
                callback: callback1,
                icons: ['icons1'],
                type: ToolbarItemType.ACTION,
                component: ToolbarComp1
            }
        ]);

        await iToolbarService.addItems([
            {
                key: 'key2',
                nameI18nKey: 'somenameI18nKey2',
                descriptionI18nKey: 'somedescriptionI18nKey2',
                callback: callback2,
                icons: ['icons2'],
                type: ToolbarItemType.TEMPLATE,
                component: ToolbarComp2
            }
        ]);

        // Tests
        expect(addAliasesSpy.calls.argsFor(0)[0]).toEqual([
            {
                key: 'key1',
                name: 'somenameI18nKey1',
                description: 'somedescriptionI18nKey1',
                icons: ['icons1'],
                type: ToolbarItemType.ACTION,
                component: ToolbarComp1,
                priority: 500,
                section: ToolbarSection.left,
                isOpen: false,
                keepAliveOnClose: false,
                isPermissionGranted: true,
                iconClassName: undefined,
                actionButtonFormat: undefined,
                contextComponent: undefined,
                dropdownPosition: undefined,
                permissions: undefined
            }
        ]);

        expect(addAliasesSpy.calls.argsFor(1)[0]).toEqual([
            {
                key: 'key2',
                name: 'somenameI18nKey2',
                description: 'somedescriptionI18nKey2',
                icons: ['icons2'],
                type: ToolbarItemType.TEMPLATE,
                component: ToolbarComp2,
                priority: 500,
                section: ToolbarSection.left,
                isOpen: false,
                keepAliveOnClose: false,
                isPermissionGranted: true,
                iconClassName: undefined,
                actionButtonFormat: undefined,
                contextComponent: undefined,
                dropdownPosition: undefined,
                permissions: undefined
            }
        ]);

        expect(iToolbarService.getActions()).toEqual({
            key1: callback1,
            key2: callback2
        });
    });

    it('ToolbarServiceInterface.addItems with permissions property', async () => {
        const addAliasesSpy = spyOn(iToolbarService, 'addAliases').and.callThrough();
        spyOn(iToolbarService, 'getAliases').and.callThrough();

        permissionService.isPermitted.and.returnValue(Promise.resolve(false));

        const callback1 = () => {
            //
        };

        expect(iToolbarService.getAliases()).toEqual([]);

        await iToolbarService.addItems([
            {
                key: 'key1',
                nameI18nKey: 'somenameI18nKey1',
                descriptionI18nKey: 'somedescriptionI18nKey1',
                callback: callback1,
                icons: ['icons1'],
                type: ToolbarItemType.ACTION,
                component: ToolbarComp1,
                permissions: ['xyz.permissions']
            }
        ]);

        // Tests
        expect(addAliasesSpy.calls.argsFor(0)[0]).toEqual([
            {
                key: 'key1',
                name: 'somenameI18nKey1',
                description: 'somedescriptionI18nKey1',
                icons: ['icons1'],
                type: ToolbarItemType.ACTION,
                component: ToolbarComp1,
                priority: 500,
                section: ToolbarSection.left,
                isOpen: false,
                keepAliveOnClose: false,
                isPermissionGranted: false,
                permissions: ['xyz.permissions'],
                iconClassName: undefined,
                actionButtonFormat: undefined,
                contextComponent: undefined,
                dropdownPosition: undefined
            }
        ]);
    });

    it('addItems logs an error when key is not provided in the configuration', async () => {
        const addAliasesSpy = spyOn(iToolbarService, 'addAliases');

        const callbacks = {
            callback1() {
                //
            }
        };

        // Act
        await iToolbarService.addItems([
            {
                key: null,
                type: ToolbarItemType.ACTION,
                callback: callbacks.callback1
            }
        ]);

        // Assert
        expect(addAliasesSpy).not.toHaveBeenCalled();
        expect(logService.error).toHaveBeenCalledWith('addItems() - Cannot add item without key.');
    });
});
