/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ChangeDetectorRef } from '@angular/core';
import { CmsComponentDropdownComponent, CMSNavigationEntry } from 'cmssmarteditcontainer';
import {
    NestedComponentManagementService,
    SelectComponentTypeModalService
} from 'cmssmarteditcontainer/components/cmsComponents/cmsItemDropdown/services';
import { NavigationNodeCMSItem } from 'cmssmarteditcontainer/components/navigation/types';
import {
    GenericEditorWidgetData,
    SystemEventService,
    GenericEditorStackService,
    LogService,
    CMSItem
} from 'smarteditcommons';

describe('CmsComponentDropdownComponent', () => {
    let component: CmsComponentDropdownComponent;
    let componentAny: any;
    let injectedData: GenericEditorWidgetData<NavigationNodeCMSItem>;
    let systemEventService: jasmine.SpyObj<SystemEventService>;
    let genericEditorStackService: jasmine.SpyObj<GenericEditorStackService>;
    let nestedComponentManagementService: jasmine.SpyObj<NestedComponentManagementService>;
    let selectComponentTypeModalService: jasmine.SpyObj<SelectComponentTypeModalService>;
    let logService: jasmine.SpyObj<LogService>;
    let cdr: jasmine.SpyObj<ChangeDetectorRef>;

    let createUnsub: jasmine.Spy;
    let editUnsub: jasmine.Spy;

    beforeEach(() => {
        injectedData = {
            editor: {
                editorStackId: 'editorStackId'
            },
            id: 'editorId',
            field: {
                params: {},
                subTypes: {
                    type1: 'type1',
                    type2: 'type2'
                }
            },
            qualifier: 'entries', // or 'item'
            model: {
                entries: [],
                item: '',
                catalogVersion: 'CatalogVersion'
            }
        } as unknown as GenericEditorWidgetData<NavigationNodeCMSItem>;

        createUnsub = jasmine.createSpy();
        editUnsub = jasmine.createSpy();
        systemEventService = jasmine.createSpyObj<SystemEventService>('systemEventService', [
            'subscribe'
        ]);
        systemEventService.subscribe.and.returnValues(createUnsub, editUnsub);

        genericEditorStackService = jasmine.createSpyObj<GenericEditorStackService>(
            'genericEditorStackService',
            ['isTopEditorInStack']
        );
        nestedComponentManagementService = jasmine.createSpyObj<NestedComponentManagementService>(
            'nestedComponentManagementService',
            ['openNestedComponentEditor']
        );
        selectComponentTypeModalService = jasmine.createSpyObj<SelectComponentTypeModalService>(
            'selectComponentTypeModalService',
            ['open']
        );
        logService = jasmine.createSpyObj<LogService>('logService', ['warn']);
        cdr = jasmine.createSpyObj<ChangeDetectorRef>('cdr', ['detectChanges']);

        component = new CmsComponentDropdownComponent(
            injectedData,
            systemEventService,
            genericEditorStackService,
            nestedComponentManagementService,
            selectComponentTypeModalService,
            logService,
            cdr
        );
        componentAny = component;
    });

    describe('initialize', () => {
        it('WHEN initialized THEN it should set field params, event id and subscribe to events', () => {
            component.ngOnInit();

            expect(component.field.editorStackId).toEqual('editorStackId');
            expect(component.field.params).toEqual({
                catalogId: 'CURRENT_CONTEXT_CATALOG',
                catalogVersion: 'CURRENT_CONTEXT_CATALOG_VERSION'
            });
            expect(component.componentButtonPressedEventId).toEqual(
                'CREATE_NESTED_COMPONENT_BUTTON_PRESSED_EVENT_entries'
            );

            expect(systemEventService.subscribe).toHaveBeenCalledWith(
                'CREATE_NESTED_COMPONENT_BUTTON_PRESSED_EVENT_entries',
                jasmine.any(Function)
            );
            expect(systemEventService.subscribe).toHaveBeenCalledWith(
                'ON_EDIT_NESTED_COMPONENT',
                jasmine.any(Function)
            );
        });
    });

    describe('onDestroy', () => {
        it('GIVEN component is initialized WHEN it gets destroyed THEN it should unsubscribe from system events', () => {
            component.ngOnInit();

            component.ngOnDestroy();

            expect(createUnsub).toHaveBeenCalled();
            expect(editUnsub).toHaveBeenCalled();
        });
    });

    describe('onCreateComponentButtonPressed', () => {
        beforeEach(() => {
            spyOn(componentAny, 'createNestedComponent');
        });

        it('WHEN editor is not stack THEN it should do nothing', async () => {
            genericEditorStackService.isTopEditorInStack.and.returnValue(false);
            component.ngOnInit();

            await componentAny.onCreateComponentButtonPressed('asd');

            expect(componentAny.searchQuery).toEqual('asd');
            expect(genericEditorStackService.isTopEditorInStack).toHaveBeenCalledWith(
                'editorStackId',
                'editorId'
            );

            expect(selectComponentTypeModalService.open).not.toHaveBeenCalled();
            expect(componentAny.createNestedComponent).not.toHaveBeenCalled();
        });

        it('WHEN editor is in stack AND there are no subtypes in field THEN it should do nothing', async () => {
            genericEditorStackService.isTopEditorInStack.and.returnValue(true);
            component.field.subTypes = null;
            component.ngOnInit();

            await componentAny.onCreateComponentButtonPressed('asd');

            expect(componentAny.searchQuery).toEqual('asd');
            expect(genericEditorStackService.isTopEditorInStack).toHaveBeenCalledWith(
                'editorStackId',
                'editorId'
            );

            expect(selectComponentTypeModalService.open).not.toHaveBeenCalled();
            expect(componentAny.createNestedComponent).not.toHaveBeenCalled();
        });

        it('GIVEN editor is in stack and has more than one subType WHEN another subtype is created after opening selectComponentTypeModalService THEN should call createNestedComponent method with that subtype', async () => {
            genericEditorStackService.isTopEditorInStack.and.returnValue(true);
            selectComponentTypeModalService.open.and.returnValue(Promise.resolve('type3'));
            component.field.subTypes = { type1: 'type1', type2: 'type2' };

            await componentAny.onCreateComponentButtonPressed('text');

            expect(selectComponentTypeModalService.open).toHaveBeenCalledWith({
                type1: 'type1',
                type2: 'type2'
            });
            expect(componentAny.createNestedComponent).toHaveBeenCalledWith('type3');
        });

        it('GIVEN editor is in stack and has one subType THEN should call createNestedComponent method with that subtype', async () => {
            genericEditorStackService.isTopEditorInStack.and.returnValue(true);
            component.field.subTypes = { type1: 'type1' };

            await componentAny.onCreateComponentButtonPressed('text');

            expect(selectComponentTypeModalService.open).not.toHaveBeenCalled();
            expect(componentAny.createNestedComponent).toHaveBeenCalledWith('type1');
        });
    });

    describe('onEditComponentClicked', () => {
        beforeEach(() => {
            spyOn(componentAny, 'editComponent');
        });

        it('WHEN editor is in stack AND qualifier is equal to payload qualifier THEN it should edit component', () => {
            genericEditorStackService.isTopEditorInStack.and.returnValue(true);

            componentAny.onEditComponentClicked({ item: { uid: 'itemUid' }, qualifier: 'entries' });

            expect(componentAny.editComponent).toHaveBeenCalled();
        });
    });

    describe('createNestedComponent', () => {
        beforeEach(() => {
            spyOn(componentAny, 'nestedComponentEditorHandler');
        });

        it('should create component info and call handler', async () => {
            componentAny.searchQuery = 'text';

            await componentAny.createNestedComponent('someComponentType');

            expect(componentAny.nestedComponentEditorHandler).toHaveBeenCalledWith({
                componentId: null,
                componentUuid: null,
                componentType: 'someComponentType',
                content: {
                    name: 'text',
                    catalogVersion: 'CatalogVersion'
                }
            });
        });
    });

    describe('editComponent', () => {
        beforeEach(() => {
            spyOn(componentAny, 'nestedComponentEditorHandler');
        });

        it('should create component info and call handler', async () => {
            componentAny.searchQuery = 'text';
            const itemToEdit = {
                uid: 'uid',
                uuid: 'uuid',
                itemtype: 'itemtype'
            } as CMSNavigationEntry;

            await componentAny.editComponent(itemToEdit);

            expect(componentAny.nestedComponentEditorHandler).toHaveBeenCalledWith({
                componentId: 'uid',
                componentUuid: 'uuid',
                componentType: 'itemtype',
                content: {
                    uid: 'uid',
                    uuid: 'uuid',
                    itemtype: 'itemtype'
                }
            });
        });
    });

    describe('nestedComponentEditorHandler', () => {
        const componentInfo: any = {
            componentId: 'uid',
            componentUuid: 'uuid',
            componentType: 'itemtype',
            content: {
                uid: 'uid',
                uuid: 'uuid',
                itemtype: 'itemtype'
            }
        };

        const mockItem = {
            uuid: 'itemUUID'
        } as CMSItem;

        it('WHEN openNestedComponentEditor throws with no error THEN error should be handled without any log', async () => {
            nestedComponentManagementService.openNestedComponentEditor.and.returnValue(
                Promise.reject()
            );

            await componentAny.nestedComponentEditorHandler(componentInfo);

            expect(logService.warn).not.toHaveBeenCalled();
        });

        it('WHEN openNestedComponentEditor throws with error THEN error should be handled log warning provided', async () => {
            nestedComponentManagementService.openNestedComponentEditor.and.returnValue(
                Promise.reject('error')
            );

            await componentAny.nestedComponentEditorHandler(componentInfo);

            expect(logService.warn).toHaveBeenCalledWith(
                'Something went wrong when creating or editing Nested component (itemtype)',
                'error'
            );
        });

        it('GIVEN field collection is true WHEN value is not initialized THEN it should initialize and add it to collection', async () => {
            nestedComponentManagementService.openNestedComponentEditor.and.returnValue(
                Promise.resolve(mockItem)
            );
            component.ngOnInit();
            component.field.collection = true;
            component.model.entries = null;

            await componentAny.nestedComponentEditorHandler(componentInfo);

            expect(nestedComponentManagementService.openNestedComponentEditor).toHaveBeenCalledWith(
                componentInfo,
                'editorStackId'
            );
            expect(component.model.entries).toEqual([mockItem.uuid]);
        });

        it('GIVEN field collection is true WHEN there are already items THEN it should make sure there are no duplicates and add it to collection', async () => {
            nestedComponentManagementService.openNestedComponentEditor.and.returnValue(
                Promise.resolve(mockItem)
            );
            component.ngOnInit();
            component.field.collection = true;
            component.model.entries = [mockItem.uuid];

            await componentAny.nestedComponentEditorHandler(componentInfo, 'methodName');

            expect(nestedComponentManagementService.openNestedComponentEditor).toHaveBeenCalledWith(
                componentInfo,
                'editorStackId'
            );
            expect(component.model.entries).toEqual([mockItem.uuid]);
        });

        it('GIVEN field collection value is false AND flag to force detect changes is true THEN it should assign model value and force change detection', async () => {
            jasmine.clock().install();
            nestedComponentManagementService.openNestedComponentEditor.and.returnValue(
                Promise.resolve(mockItem)
            );
            component.ngOnInit();
            component.field.collection = false;
            component.qualifier = 'item';

            await componentAny.nestedComponentEditorHandler(componentInfo, 'methodName');

            expect(component.forceRecompile).toEqual(false);
            jasmine.clock().tick(1);

            expect(component.forceRecompile).toEqual(true);
            expect(nestedComponentManagementService.openNestedComponentEditor).toHaveBeenCalledWith(
                componentInfo,
                'editorStackId'
            );
            expect(component.model.item).toEqual(mockItem.uuid);
            jasmine.clock().uninstall();
        });
    });
});
