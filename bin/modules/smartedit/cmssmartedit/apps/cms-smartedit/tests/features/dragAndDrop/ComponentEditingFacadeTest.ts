/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { TranslateService } from '@ngx-translate/core';
import {
    ComponentService,
    ICMSComponent,
    IComponentVisibilityAlertService,
    ISlotVisibilityService
} from 'cmscommons';
import { ComponentEditingFacade } from 'cmssmartedit/services';
import {
    CrossFrameEventService,
    IAlertService,
    IPageInfoService,
    IRenderService,
    IRestService,
    IRestServiceFactory,
    ISharedDataService,
    LogService,
    SystemEventService,
    IEditorModalService
} from 'smarteditcommons';

describe('ComponentEditingFacade', () => {
    const COMPONENT_UPDATED_EVENT = 'COMPONENT_UPDATED_EVENT';
    const EVENT_SMARTEDIT_COMPONENT_UPDATED = 'EVENT_SMARTEDIT_COMPONENT_UPDATED';
    const COMPONENT_CREATED_EVENT = 'COMPONENT_CREATED_EVENT';
    const MOCK_COMPONENT_TYPE = 'MOCK_COMPONENT_TYPE';
    const MOCK_TARGET_SLOT_UID = 'MOCK_TARGET_SLOT_UID';
    const MOCK_TARGET_SLOT_UUID = 'MOCK_TARGET_SLOT_UUID';
    const MOCK_EXISTING_COMPONENT_UID = 'MOCK_EXISTING_COMPONENT_UID';
    const MOCK_EXISTING_COMPONENT_UUID = 'MOCK_EXISTING_COMPONENT_UUID';
    const MOCK_CATALOG_VERSION_UUID = 'MOCK_CATALOG_VERSION_UUID';
    const MOCK_PAGE_UID = 'SomePageUID';
    const MOCK_ERROR = {
        error: {
            errors: [
                {
                    message: 'Some detailed error message'
                }
            ]
        }
    };
    const MOCK_COMPONENT = {
        uid: MOCK_EXISTING_COMPONENT_UID,
        uuid: MOCK_EXISTING_COMPONENT_UUID,
        typeCode: MOCK_COMPONENT_TYPE
    };

    const createModalItem = (): ICMSComponent =>
        (({
            uuid: 'MOCK_ITEM_UUID',
            itemtype: 'MOCK_ITEM_TYPE',
            catalogVersion: 'MOCK_CATALOG_VERSION',
            slotId: 'MOCK_SLOT_ID'
        } as unknown) as ICMSComponent);

    const mockModalItem = (scenario: string): ICMSComponent => {
        const MOCK_PAYLOAD = createModalItem();
        MOCK_PAYLOAD.visible = scenario.indexOf('VISIBLE') !== -1;
        MOCK_PAYLOAD.restricted = scenario.indexOf('WITH_RESTRICTIONS') !== -1;
        return MOCK_PAYLOAD;
    };

    let alertService: jasmine.SpyObj<IAlertService>;
    let componentService: jasmine.SpyObj<ComponentService>;
    let componentVisibilityAlertService: jasmine.SpyObj<IComponentVisibilityAlertService>;
    let crossFrameEventService: jasmine.SpyObj<CrossFrameEventService>;
    let editorModalService: jasmine.SpyObj<IEditorModalService>;
    let logService: jasmine.SpyObj<LogService>;
    let pageInfoService: jasmine.SpyObj<IPageInfoService>;
    let renderService: jasmine.SpyObj<IRenderService>;
    let restServiceFactory: jasmine.SpyObj<IRestServiceFactory>;
    let slotVisibilityService: jasmine.SpyObj<ISlotVisibilityService>;
    let sharedDataService: jasmine.SpyObj<ISharedDataService>;
    let systemEventService: jasmine.SpyObj<SystemEventService>;
    let translateService: jasmine.SpyObj<TranslateService>;
    let contentSlotComponentsRestService: jasmine.SpyObj<IRestService<void>>;

    let componentEditingFacade: ComponentEditingFacade;
    beforeEach(() => {
        alertService = jasmine.createSpyObj<IAlertService>('alertService', [
            'showSuccess',
            'showDanger',
            'showInfo'
        ]);

        componentService = jasmine.createSpyObj<ComponentService>('componentService', [
            'loadComponentItem',
            'addExistingComponent'
        ]);

        componentVisibilityAlertService = jasmine.createSpyObj<IComponentVisibilityAlertService>(
            'componentVisibilityAlertService',
            ['checkAndAlertOnComponentVisibility']
        );

        crossFrameEventService = jasmine.createSpyObj<CrossFrameEventService>(
            'crossFrameEventService',
            ['publish']
        );

        editorModalService = jasmine.createSpyObj<IEditorModalService>('editorModalService', [
            'open'
        ]);

        logService = jasmine.createSpyObj<LogService>('logService', ['error']);

        pageInfoService = jasmine.createSpyObj<IPageInfoService>('pageInfoService', ['getPageUID']);

        renderService = jasmine.createSpyObj<IRenderService>('renderService', ['renderSlots']);

        restServiceFactory = jasmine.createSpyObj<IRestServiceFactory>('restServiceFactory', [
            'get'
        ]);

        slotVisibilityService = jasmine.createSpyObj<ISlotVisibilityService>(
            'slotVisibilityService',
            ['reloadSlotsInfo']
        );

        sharedDataService = jasmine.createSpyObj<ISharedDataService>('sharedDataService', ['get']);

        systemEventService = jasmine.createSpyObj<SystemEventService>('systemEventService', [
            'publish'
        ]);

        translateService = jasmine.createSpyObj<TranslateService>('translateService', ['instant']);

        componentEditingFacade = new ComponentEditingFacade(
            alertService,
            componentService,
            componentVisibilityAlertService,
            crossFrameEventService,
            editorModalService,
            logService,
            pageInfoService,
            renderService,
            restServiceFactory,
            slotVisibilityService,
            sharedDataService,
            systemEventService,
            translateService
        );
    });

    beforeEach(() => {
        renderService.renderSlots.and.returnValue(Promise.resolve());

        slotVisibilityService.reloadSlotsInfo.and.returnValue(Promise.resolve() as any);

        translateService.instant.and.callFake((key: string) => key);

        contentSlotComponentsRestService = jasmine.createSpyObj<IRestService<void>>(
            'contentSlotComponentsRestService',
            ['update']
        );
        restServiceFactory.get.and.returnValue(contentSlotComponentsRestService);
    });

    describe('addNewComponentToSlot', () => {
        let componentCreated;
        const slotInfo = {
            targetSlotId: MOCK_TARGET_SLOT_UID,
            targetSlotUUId: MOCK_TARGET_SLOT_UUID
        };

        beforeEach(() => {
            componentCreated = {
                uid: 'someuid'
            };

            editorModalService.open.and.returnValue(Promise.resolve(componentCreated));
        });

        it('should open the component editor in a modal', async () => {
            // GIVEN
            renderService.renderSlots.and.returnValue(Promise.resolve());
            slotVisibilityService.reloadSlotsInfo.and.returnValue(Promise.resolve() as any);

            pageInfoService.getPageUID.and.returnValue(Promise.resolve(MOCK_PAGE_UID));

            const expectedData = {
                smarteditComponentType: MOCK_COMPONENT_TYPE,
                catalogVersionUuid: MOCK_CATALOG_VERSION_UUID
            };

            // WHEN
            await componentEditingFacade.addNewComponentToSlot(
                slotInfo,
                MOCK_CATALOG_VERSION_UUID,
                MOCK_COMPONENT_TYPE,
                1
            );

            // THEN
            expect(editorModalService.open).toHaveBeenCalledWith(
                expectedData,
                MOCK_TARGET_SLOT_UUID,
                1
            );
        });

        it('should publish COMPONENT_CREATED_EVENT after component is created', async () => {
            // WHEN
            await componentEditingFacade.addNewComponentToSlot(
                slotInfo,
                MOCK_CATALOG_VERSION_UUID,
                MOCK_COMPONENT_TYPE,
                1
            );

            // THEN
            expect(crossFrameEventService.publish).toHaveBeenCalledWith(
                COMPONENT_CREATED_EVENT,
                componentCreated
            );
        });

        it('should show an info message if the component is hidden and/or restricted after the component is created', async () => {
            // GIVEN
            componentCreated = {
                uuid: 'MOCK_EXISTING_COMPONENT_UUID',
                itemtype: 'MOCK_COMPONENT_TYPE',
                catalogVersion: 'MOCK_CATALOG_VERSION',
                restricted: true,
                slotId: 'MOCK_TARGET_SLOT_UID',
                visible: false
            };
            editorModalService.open.and.returnValue(Promise.resolve(componentCreated));

            // WHEN
            await componentEditingFacade.addNewComponentToSlot(
                slotInfo,
                MOCK_CATALOG_VERSION_UUID,
                MOCK_COMPONENT_TYPE,
                1
            );

            // THEN
            expect(
                componentVisibilityAlertService.checkAndAlertOnComponentVisibility
            ).toHaveBeenCalledWith({
                itemId: 'MOCK_EXISTING_COMPONENT_UUID',
                itemType: 'MOCK_COMPONENT_TYPE',
                catalogVersion: 'MOCK_CATALOG_VERSION',
                restricted: true,
                slotId: 'MOCK_TARGET_SLOT_UID',
                visible: false
            });
        });
    });

    describe('addExistingComponentToSlot', () => {
        const dragInfo = {
            componentId: MOCK_EXISTING_COMPONENT_UID,
            componentType: MOCK_COMPONENT_TYPE,
            componentUuid: MOCK_EXISTING_COMPONENT_UUID
        };

        beforeEach(() => {
            pageInfoService.getPageUID.and.returnValue(Promise.resolve(MOCK_PAGE_UID));
            componentService.addExistingComponent.and.returnValue(Promise.resolve());
            renderService.renderSlots.and.returnValue(Promise.resolve());
            slotVisibilityService.reloadSlotsInfo.and.returnValue(Promise.resolve() as any);
        });

        it('should get the current page UID by delegating to the pageInfoService', async () => {
            // GIVEN
            componentService.loadComponentItem.and.returnValue(
                Promise.resolve(mockModalItem('VISIBLE'))
            );

            // WHEN
            await componentEditingFacade.addExistingComponentToSlot(
                MOCK_TARGET_SLOT_UID,
                dragInfo,
                1
            );

            // THEN
            expect(pageInfoService.getPageUID).toHaveBeenCalled();
        });

        it('should delegate to componentService to add the existing component to the slot and show a success message if the component is visible', async () => {
            // GIVEN
            componentService.loadComponentItem.and.returnValue(
                Promise.resolve(mockModalItem('MOCK_ITEM_VISIBLE_NO_RESTRICTIONS'))
            );

            // WHEN
            await componentEditingFacade.addExistingComponentToSlot(
                MOCK_TARGET_SLOT_UID,
                dragInfo,
                1
            );

            // THEN
            expect(componentService.addExistingComponent).toHaveBeenCalledWith(
                MOCK_PAGE_UID,
                MOCK_EXISTING_COMPONENT_UID,
                MOCK_TARGET_SLOT_UID,
                1
            );
            expect(alertService.showSuccess).toHaveBeenCalledWith({
                message: 'se.cms.draganddrop.success',
                messagePlaceholders: {
                    sourceComponentId: 'MOCK_EXISTING_COMPONENT_UID',
                    targetSlotId: 'MOCK_TARGET_SLOT_UID'
                },
                minWidth: '',
                mousePersist: true,
                duration: 1000,
                dismissible: true,
                width: '300px'
            });
        });

        it('should delegate to componentService to add the existing component to the slot, show both a success and an info message if the component is hidden and restricted', async () => {
            // GIVEN
            componentService.loadComponentItem.and.returnValue(
                Promise.resolve(mockModalItem('MOCK_ITEM_HIDDEN_WITH_RESTRICTIONS'))
            );

            // WHEN
            await componentEditingFacade.addExistingComponentToSlot(
                MOCK_TARGET_SLOT_UID,
                dragInfo,
                1
            );

            // THEN
            expect(pageInfoService.getPageUID).toHaveBeenCalled();
            expect(componentService.addExistingComponent).toHaveBeenCalledWith(
                'SomePageUID',
                MOCK_EXISTING_COMPONENT_UID,
                MOCK_TARGET_SLOT_UID,
                1
            );
            expect(componentService.loadComponentItem).toHaveBeenCalledWith(
                'MOCK_EXISTING_COMPONENT_UUID'
            );
            expect(
                componentVisibilityAlertService.checkAndAlertOnComponentVisibility
            ).toHaveBeenCalledWith({
                itemId: 'MOCK_EXISTING_COMPONENT_UUID',
                itemType: 'MOCK_COMPONENT_TYPE',
                catalogVersion: 'MOCK_CATALOG_VERSION',
                restricted: true,
                slotId: 'MOCK_TARGET_SLOT_UID',
                visible: false
            });
            expect(alertService.showSuccess).toHaveBeenCalledWith({
                message: 'se.cms.draganddrop.success',
                messagePlaceholders: {
                    sourceComponentId: 'MOCK_EXISTING_COMPONENT_UID',
                    targetSlotId: 'MOCK_TARGET_SLOT_UID'
                },
                minWidth: '',
                mousePersist: true,
                duration: 1000,
                dismissible: true,
                width: '300px'
            });
            expect(renderService.renderSlots).toHaveBeenCalledWith('MOCK_TARGET_SLOT_UID');
            expect(slotVisibilityService.reloadSlotsInfo).toHaveBeenCalled();
        });

        it('should delegate to componentService to add the existing component to the slot, show both a success and an info message if the component is restricted', async () => {
            // GIVEN
            componentService.loadComponentItem.and.returnValue(
                Promise.resolve(mockModalItem('MOCK_ITEM_VISIBLE_WITH_RESTRICTIONS'))
            );

            // WHEN
            await componentEditingFacade.addExistingComponentToSlot(
                MOCK_TARGET_SLOT_UID,
                dragInfo,
                1
            );

            // THEN
            expect(pageInfoService.getPageUID).toHaveBeenCalled();
            expect(componentService.addExistingComponent).toHaveBeenCalledWith(
                'SomePageUID',
                MOCK_EXISTING_COMPONENT_UID,
                MOCK_TARGET_SLOT_UID,
                1
            );
            expect(componentService.loadComponentItem).toHaveBeenCalledWith(
                'MOCK_EXISTING_COMPONENT_UUID'
            );
            expect(
                componentVisibilityAlertService.checkAndAlertOnComponentVisibility
            ).toHaveBeenCalledWith({
                itemId: 'MOCK_EXISTING_COMPONENT_UUID',
                itemType: 'MOCK_COMPONENT_TYPE',
                catalogVersion: 'MOCK_CATALOG_VERSION',
                restricted: true,
                slotId: 'MOCK_TARGET_SLOT_UID',
                visible: true
            });
            expect(alertService.showSuccess).toHaveBeenCalledWith({
                message: 'se.cms.draganddrop.success',
                messagePlaceholders: {
                    sourceComponentId: 'MOCK_EXISTING_COMPONENT_UID',
                    targetSlotId: 'MOCK_TARGET_SLOT_UID'
                },
                minWidth: '',
                mousePersist: true,
                duration: 1000,
                dismissible: true,
                width: '300px'
            });
            expect(renderService.renderSlots).toHaveBeenCalledWith('MOCK_TARGET_SLOT_UID');
            expect(slotVisibilityService.reloadSlotsInfo).toHaveBeenCalled();
        });

        it('should delegate to componentService to add the existing component to the slot, show both a success and an info message if the component is hidden', async () => {
            // GIVEN
            componentService.loadComponentItem.and.returnValue(
                Promise.resolve(mockModalItem('MOCK_ITEM_HIDDEN_NO_RESTRICTIONS'))
            );

            // WHEN
            await componentEditingFacade.addExistingComponentToSlot(
                MOCK_TARGET_SLOT_UID,
                dragInfo,
                1
            );

            // THEN
            expect(pageInfoService.getPageUID).toHaveBeenCalled();
            expect(componentService.addExistingComponent).toHaveBeenCalledWith(
                'SomePageUID',
                MOCK_EXISTING_COMPONENT_UID,
                MOCK_TARGET_SLOT_UID,
                1
            );
            expect(componentService.loadComponentItem).toHaveBeenCalledWith(
                'MOCK_EXISTING_COMPONENT_UUID'
            );
            expect(
                componentVisibilityAlertService.checkAndAlertOnComponentVisibility
            ).toHaveBeenCalledWith({
                itemId: 'MOCK_EXISTING_COMPONENT_UUID',
                itemType: 'MOCK_COMPONENT_TYPE',
                catalogVersion: 'MOCK_CATALOG_VERSION',
                restricted: false,
                slotId: 'MOCK_TARGET_SLOT_UID',
                visible: false
            });
            expect(alertService.showSuccess).toHaveBeenCalledWith({
                message: 'se.cms.draganddrop.success',
                messagePlaceholders: {
                    sourceComponentId: 'MOCK_EXISTING_COMPONENT_UID',
                    targetSlotId: 'MOCK_TARGET_SLOT_UID'
                },
                minWidth: '',
                mousePersist: true,
                duration: 1000,
                dismissible: true,
                width: '300px'
            });
            expect(renderService.renderSlots).toHaveBeenCalledWith('MOCK_TARGET_SLOT_UID');
            expect(slotVisibilityService.reloadSlotsInfo).toHaveBeenCalled();
        });

        it('should delegate to the renderService to re-render the slot the componentService has successfully added the existing component to the slot', async () => {
            // GIVEN
            componentService.loadComponentItem.and.returnValue(
                Promise.resolve<any>('MOCK_ITEM_VISIBLE')
            );

            // WHEN
            await componentEditingFacade.addExistingComponentToSlot(
                MOCK_TARGET_SLOT_UID,
                dragInfo,
                1
            );

            // THEN
            expect(renderService.renderSlots).toHaveBeenCalledWith(MOCK_TARGET_SLOT_UID);
            expect(slotVisibilityService.reloadSlotsInfo).toHaveBeenCalled();
        });

        it('should push an alert if adding the existing component to the slot fails', async () => {
            // GIVEN
            pageInfoService.getPageUID.and.returnValue(Promise.resolve(MOCK_PAGE_UID));
            componentService.addExistingComponent.and.returnValue(Promise.reject(MOCK_ERROR));

            // WHEN
            await componentEditingFacade
                .addExistingComponentToSlot(MOCK_TARGET_SLOT_UID, dragInfo, 1)
                .catch(() => {
                    // ensure the promise has been rejected
                    expect(true).toBe(true);
                });

            // THEN
            expect(crossFrameEventService.publish).not.toHaveBeenCalled();
            expect(alertService.showDanger).toHaveBeenCalledWith({
                message: 'se.cms.draganddrop.error',
                messagePlaceholders: {
                    sourceComponentId: 'MOCK_EXISTING_COMPONENT_UID',
                    targetSlotId: 'MOCK_TARGET_SLOT_UID',
                    detailedError: 'Some detailed error message'
                },
                minWidth: '',
                mousePersist: true,
                duration: 1000,
                dismissible: true,
                width: '300px'
            });
        });

        it('should publish COMPONENT_UPDATED_EVENT AND EVENT_SMARTEDIT_COMPONENT_UPDATED after component is created', async () => {
            // GIVEN
            const component = {
                someProperty: 'some value'
            };

            const smarteditComponentUpdatedData = {
                componentId: MOCK_EXISTING_COMPONENT_UID,
                componentType: MOCK_COMPONENT_TYPE,
                componentUuid: MOCK_EXISTING_COMPONENT_UUID,
                requiresReplayingDecorators: true
            };

            componentService.loadComponentItem.and.returnValue(Promise.resolve<any>(component));

            // WHEN
            await componentEditingFacade.addExistingComponentToSlot(
                MOCK_TARGET_SLOT_UID,
                dragInfo,
                1
            );

            // THEN
            expect(systemEventService.publish).toHaveBeenCalledWith(
                COMPONENT_UPDATED_EVENT,
                component
            );
            expect(crossFrameEventService.publish).toHaveBeenCalledWith(
                EVENT_SMARTEDIT_COMPONENT_UPDATED,
                smarteditComponentUpdatedData
            );
        });
    });

    describe('cloneExistingComponentToSlot', () => {
        const dragInfo = {
            componentId: MOCK_EXISTING_COMPONENT_UID,
            componentType: MOCK_COMPONENT_TYPE,
            componentUuid: MOCK_EXISTING_COMPONENT_UUID
        };

        let componentProperties;
        let clonedComponent;

        beforeEach(() => {
            // GIVEN
            clonedComponent = {
                uuid: 'someuuid',
                uid: 'someuid',
                itemtype: 'someitemType',
                catalogVersion: 'somecatalogVersion',
                restricted: false,
                visible: false
            };
            componentService.loadComponentItem.and.returnValue(
                Promise.resolve<any>({
                    catalogVersion: 'someCatalogVersion'
                })
            );
            editorModalService.open.and.returnValue(Promise.resolve(clonedComponent));
            renderService.renderSlots.and.returnValue(Promise.resolve());
            slotVisibilityService.reloadSlotsInfo.and.returnValue(Promise.resolve() as any);
            sharedDataService.get.and.returnValue(
                Promise.resolve({
                    pageContext: {
                        catalogVersionUuid: 'someCatalogVersion'
                    }
                })
            );

            componentProperties = {
                targetSlotId: MOCK_TARGET_SLOT_UID,
                dragInfo,
                position: 1
            };
        });

        it('should load the source component and should open the component editor in a modal', async () => {
            // GIVEN
            componentService.loadComponentItem.and.returnValue(
                Promise.resolve<any>({
                    key1: 'value1',
                    key2: 'value2'
                })
            );
            editorModalService.open.and.returnValue(
                Promise.resolve({
                    uid: 'someuid'
                })
            );

            // WHEN
            await componentEditingFacade.cloneExistingComponentToSlot(componentProperties);

            // THEN
            expect(sharedDataService.get).toHaveBeenCalled();
            expect(componentService.loadComponentItem).toHaveBeenCalledWith(
                MOCK_EXISTING_COMPONENT_UUID
            );
            expect(editorModalService.open).toHaveBeenCalledWith(
                {
                    smarteditComponentType: MOCK_COMPONENT_TYPE,
                    catalogVersionUuid: 'someCatalogVersion',
                    content: {
                        key1: 'value1',
                        key2: 'value2',
                        cloneComponent: true,
                        catalogVersion: 'someCatalogVersion',
                        name: 'se.cms.component.name.clone.of.prefix undefined'
                    },
                    initialDirty: true
                },
                MOCK_TARGET_SLOT_UID,
                1
            );
        });

        it('should delegate to the renderService to re-render the slot the componentService has successfully added the cloned component to the slot', async () => {
            // GIVEN
            componentService.loadComponentItem.and.returnValue(
                Promise.resolve<any>({
                    catalogVersion: 'someCatalogVersion'
                })
            );
            editorModalService.open.and.returnValue(
                Promise.resolve({
                    uuid: 'someuuid',
                    uid: 'someuid',
                    itemtype: 'someitemType',
                    catalogVersion: 'somecatalogVersion',
                    restricted: false,
                    visible: false
                })
            );

            // WHEN
            await componentEditingFacade.cloneExistingComponentToSlot(componentProperties);

            // THEN
            expect(
                componentVisibilityAlertService.checkAndAlertOnComponentVisibility
            ).toHaveBeenCalledWith({
                itemId: 'someuuid',
                itemType: 'someitemType',
                catalogVersion: 'somecatalogVersion',
                restricted: false,
                slotId: MOCK_TARGET_SLOT_UID,
                visible: false
            });
            expect(alertService.showSuccess).toHaveBeenCalledWith({
                message: 'se.cms.draganddrop.success',
                messagePlaceholders: {
                    sourceComponentId: 'someuid',
                    targetSlotId: MOCK_TARGET_SLOT_UID
                },
                minWidth: '',
                mousePersist: true,
                duration: 1000,
                dismissible: true,
                width: '300px'
            });
            expect(renderService.renderSlots).toHaveBeenCalledWith('MOCK_TARGET_SLOT_UID');
            expect(slotVisibilityService.reloadSlotsInfo).toHaveBeenCalled();
        });

        it('should publish COMPONENT_CREATED_EVENT after component is cloned', async () => {
            // GIVEN
            componentService.loadComponentItem.and.returnValue(
                Promise.resolve<any>({
                    catalogVersion: 'someCatalogVersion'
                })
            );
            editorModalService.open.and.returnValue(
                Promise.resolve({
                    uuid: 'someuuid',
                    uid: 'someuid',
                    itemtype: 'someitemType',
                    catalogVersion: 'somecatalogVersion',
                    restricted: false,
                    visible: false
                })
            );

            // WHEN
            await componentEditingFacade.cloneExistingComponentToSlot(componentProperties);

            // THEN
            expect(crossFrameEventService.publish).toHaveBeenCalledWith(
                COMPONENT_CREATED_EVENT,
                clonedComponent
            );
        });
    });

    describe('moveComponent', () => {
        beforeEach(() => {
            pageInfoService.getPageUID.and.returnValue(Promise.resolve(MOCK_PAGE_UID));
            contentSlotComponentsRestService.update.and.returnValue(
                Promise.resolve<any>(MOCK_COMPONENT)
            );
            renderService.renderSlots.and.returnValue(Promise.resolve());
            slotVisibilityService.reloadSlotsInfo.and.returnValue(Promise.resolve() as any);
        });

        it('should delegate to the slot update REST service to update the slot', async () => {
            // WHEN
            await componentEditingFacade.moveComponent(
                'SomeSourceSlotUID',
                MOCK_TARGET_SLOT_UID,
                MOCK_EXISTING_COMPONENT_UID,
                1
            );

            // THEN
            expect(contentSlotComponentsRestService.update).toHaveBeenCalledWith({
                pageId: MOCK_PAGE_UID,
                currentSlotId: 'SomeSourceSlotUID',
                componentId: MOCK_EXISTING_COMPONENT_UID,
                slotId: MOCK_TARGET_SLOT_UID,
                position: 1
            });
        });

        it('should delegate to the renderService to re-render both changed slots', async () => {
            // WHEN
            await componentEditingFacade.moveComponent(
                'SomeSourceSlotUID',
                MOCK_TARGET_SLOT_UID,
                MOCK_EXISTING_COMPONENT_UID,
                1
            );

            // THEN
            expect(renderService.renderSlots).toHaveBeenCalledWith([
                'SomeSourceSlotUID',
                MOCK_TARGET_SLOT_UID
            ]);
            expect(slotVisibilityService.reloadSlotsInfo).toHaveBeenCalled();
        });

        it('should push an alert if updating the slot via the slot update REST service fails', async () => {
            // GIVEN
            pageInfoService.getPageUID.and.returnValue(Promise.resolve(MOCK_PAGE_UID));
            contentSlotComponentsRestService.update.and.returnValue(Promise.reject());

            // WHEN
            await componentEditingFacade
                .moveComponent(
                    'SomeSourceSlotUID',
                    MOCK_TARGET_SLOT_UID,
                    MOCK_EXISTING_COMPONENT_UID,
                    1
                )
                .catch(() => {
                    // ensure the promise has been rejected
                    expect(true).toBe(true);
                });

            // THEN
            expect(alertService.showDanger).toHaveBeenCalledWith({
                message: 'se.cms.draganddrop.move.failed',
                messagePlaceholders: {
                    slotID: 'MOCK_TARGET_SLOT_UID',
                    componentID: 'MOCK_EXISTING_COMPONENT_UID'
                },
                minWidth: '',
                mousePersist: true,
                duration: 1000,
                dismissible: true,
                width: '300px'
            });
        });
    });
});
