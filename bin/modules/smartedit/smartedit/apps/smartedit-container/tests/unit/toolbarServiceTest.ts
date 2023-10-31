/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {
    GatewayProxy,
    IPermissionService,
    LogService,
    ToolbarItemInternal,
    ToolbarItemType,
    ToolbarSection
} from 'smarteditcommons';
import { ToolbarService, ToolbarServiceFactory } from 'smarteditcontainer/services';

let logService: jasmine.SpyObj<LogService>;
let toolbarService: ToolbarService;
let permissionService: jasmine.SpyObj<IPermissionService>;
let gatewayProxy: GatewayProxy;
let toolbarServiceFactory: ToolbarServiceFactory;

function initTest() {
    logService = jasmine.createSpyObj<LogService>('logService', ['warn']);
    gatewayProxy = jasmine.createSpyObj<GatewayProxy>('gatewayProxy', ['initForService']);

    permissionService = jasmine.createSpyObj<IPermissionService>('permissionService', [
        'isPermitted'
    ]);
    permissionService.isPermitted.and.returnValue(Promise.resolve(true));

    toolbarServiceFactory = new ToolbarServiceFactory(gatewayProxy, logService, permissionService);
}

// The specs is too long(over 200 lines), so split it to tow part.
describe('test outer toolbarService Module', () => {
    beforeEach(() => {
        initTest();
    });

    it('factory called twice on the same toolbar name returns the same instance', () => {
        expect(toolbarServiceFactory.getToolbarService('toolBar1')).toBe(
            toolbarServiceFactory.getToolbarService('toolBar1')
        );
    });

    it('factory called twice on different toolbar names returns different instances', () => {
        expect(toolbarServiceFactory.getToolbarService('toolBar1')).not.toBe(
            toolbarServiceFactory.getToolbarService('toolBar2')
        );
    });

    it('on first acquisition of a new ToolbarServiceInstance, it is registered with the gateway proxy', () => {
        toolbarService = toolbarServiceFactory.getToolbarService('toolBar1');
        expect(gatewayProxy.initForService).toHaveBeenCalledWith(toolbarService, [
            'addAliases',
            'removeItemByKey',
            'removeAliasByKey',
            '_removeItemOnInner',
            'triggerActionOnInner'
        ]);
    });

    it('on change of aliases in setAliases, the onAliasChange callback is triggered', async () => {
        toolbarService = toolbarServiceFactory.getToolbarService('toolBar1');
        const onAliasesChange = jasmine.createSpy('onAliasesChange');

        toolbarService.setOnAliasesChange(onAliasesChange);
        await toolbarService.addItems([
            {
                key: 'somekey',
                type: ToolbarItemType.ACTION,
                callback: () => {
                    //
                }
            }
        ]);

        expect(onAliasesChange).toHaveBeenCalled();
    });

    it('triggerAction triggers the associated action for the given key if it exists on the outer toolbar', async () => {
        toolbarService = toolbarServiceFactory.getToolbarService('toolBar1');
        const someAction = jasmine.createSpy('someAction');
        spyOn(toolbarService, 'triggerActionOnInner');

        await toolbarService.addItems([
            {
                key: 'key1',
                type: ToolbarItemType.ACTION,
                callback: someAction
            }
        ]);

        toolbarService.triggerAction({
            key: 'key1'
        } as ToolbarItemInternal);

        expect(someAction).toHaveBeenCalled();
        expect(toolbarService.triggerActionOnInner).not.toHaveBeenCalled();
    });

    it('triggerAction dispatches the associated action for the given key on the inner toolbar if it does not exist on the outer toolbar', async () => {
        toolbarService = toolbarServiceFactory.getToolbarService('toolBar1');
        const someAction = jasmine.createSpy('someAction');
        spyOn(toolbarService, 'triggerActionOnInner');

        await toolbarService.addItems([
            {
                key: 'key2',
                type: ToolbarItemType.ACTION,
                callback: someAction
            }
        ]);

        toolbarService.triggerAction({
            key: 'key1'
        } as ToolbarItemInternal);

        expect(someAction).not.toHaveBeenCalled();
        expect(toolbarService.triggerActionOnInner).toHaveBeenCalledWith({
            key: 'key1'
        });
    });

    it('triggerActionOnInner is an empty function', () => {
        toolbarService = toolbarServiceFactory.getToolbarService('toolBar1');
        expect(toolbarService.triggerActionOnInner).toBeEmptyFunction();
    });

    it('adding 2 actions with the no priority gives them the same default priority and triggers a warning', async () => {
        toolbarService = toolbarServiceFactory.getToolbarService('toolBar1');

        await toolbarService.addItems([
            {
                key: 'key1',
                type: ToolbarItemType.ACTION,
                callback: () => {
                    //
                }
            }
        ]);

        await toolbarService.addItems([
            {
                key: 'key2',
                type: ToolbarItemType.ACTION,
                callback: () => {
                    //
                }
            }
        ]);

        expect(logService.warn).toHaveBeenCalled();
        expect(logService.warn.calls.argsFor(0)[0]).toContain('WARNING: In toolBar1 the items ');
        expect(logService.warn.calls.argsFor(0)[0]).toContain('key2');
        expect(logService.warn.calls.argsFor(0)[0]).toContain('key1');
        expect(logService.warn.calls.argsFor(0)[0]).toContain('have the same priority');
        expect(toolbarService.getAliases()[0].priority).toBe(500);
        expect(toolbarService.getAliases()[1].priority).toBe(500);
    });
});

describe('test outer toolbarService Module', () => {
    beforeEach(() => {
        initTest();
    });

    it('toolbarService will properly sort actions based on their priority', async () => {
        toolbarService = toolbarServiceFactory.getToolbarService('toolBar1');

        await toolbarService.addItems([
            {
                key: 'key1',
                type: ToolbarItemType.ACTION,
                callback: () => {
                    //
                },
                priority: 99
            },
            {
                key: 'key2',
                type: ToolbarItemType.ACTION,
                callback: () => {
                    //
                },
                priority: 1
            },
            {
                key: 'key3',
                type: ToolbarItemType.ACTION,
                callback: () => {
                    //
                },
                priority: 75
            }
        ]);

        expect(toolbarService.getAliases()[0].priority).toBe(1);
        expect(toolbarService.getAliases()[1].priority).toBe(75);
        expect(toolbarService.getAliases()[2].priority).toBe(99);
    });

    it('removeItemByKey calls _removeItemOnInner when the key is not found in the outer frame', () => {
        // Arrange
        const invalidKey = 'some Invalid Key';
        toolbarService = toolbarServiceFactory.getToolbarService('toolBar1');

        spyOn(toolbarService, '_removeItemOnInner');

        // Act
        toolbarService.removeItemByKey(invalidKey);

        // Assert
        expect(toolbarService._removeItemOnInner).toHaveBeenCalledWith(invalidKey);
    });

    it('removeItemByKey removes the action and calls removeAliasByKey', async () => {
        // Arrange
        const key = 'someKey';
        toolbarService = toolbarServiceFactory.getToolbarService('toolBar1');
        spyOn(toolbarService, 'removeAliasByKey');

        await toolbarService.addItems([
            {
                key,
                type: ToolbarItemType.ACTION,
                callback: () => {
                    //
                }
            }
        ]);

        // Act
        toolbarService.removeItemByKey(key);

        // Assert
        expect(key in toolbarService.getActions()).toBe(false);
        expect(toolbarService.removeAliasByKey).toHaveBeenCalledWith(key);
    });

    it('toolbarService will add actions with icon image and icon font', async () => {
        const ICON_FILE_NAME = 'icon-file-name';
        toolbarService = toolbarServiceFactory.getToolbarService('toolBar1');

        await toolbarService.addItems([
            {
                key: 'key1',
                type: ToolbarItemType.ACTION,
                iconClassName: 'testIconClass',
                callback: () => {
                    //
                }
            },
            {
                key: 'key2',
                type: ToolbarItemType.ACTION,
                icons: [ICON_FILE_NAME],
                section: ToolbarSection.right,
                callback: () => {
                    //
                }
            },
            {
                key: 'key3',
                type: ToolbarItemType.ACTION,
                callback: () => {
                    //
                },
                section: ToolbarSection.left
            },
            {
                key: 'key4',
                type: ToolbarItemType.HYBRID_ACTION,
                callback: () => {
                    //
                },
                icons: [ICON_FILE_NAME],
                section: ToolbarSection.left
            }
        ]);

        expect(toolbarService.getAliases()[0].type).toBe('ACTION');
        expect(toolbarService.getAliases()[0].iconClassName).toBe('testIconClass');
        expect(toolbarService.getAliases()[0].section).toBe('left');
        expect(toolbarService.getAliases()[1].type).toBe('ACTION');
        expect(toolbarService.getAliases()[1].icons[0]).toBe(ICON_FILE_NAME);
        expect(toolbarService.getAliases()[1].section).toBe('right');
        expect(toolbarService.getAliases()[2].section).toBe('left');
        expect(toolbarService.getAliases()[3].type).toBe('HYBRID_ACTION');
        expect(toolbarService.getAliases()[3].icons[0]).toBe(ICON_FILE_NAME);
    });
});
