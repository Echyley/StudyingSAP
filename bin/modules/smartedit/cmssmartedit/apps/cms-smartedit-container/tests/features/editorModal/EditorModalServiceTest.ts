/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { DialogConfig } from '@fundamental-ngx/core';
import { TranslateService } from '@ngx-translate/core';
import { EditorModalService, GenericEditorModalService } from 'cmssmarteditcontainer/services';
import { ContextAwareEditableItemService } from 'cmssmarteditcontainer/services/contextAwareEditableItem/ContextAwareEditableItemServiceOuter';
import {
    GenericEditorStackService,
    IRenderService,
    CmsitemsRestService,
    IGenericEditorModalServiceComponent,
    IComponent
} from 'smarteditcommons';

describe('editorModalService', () => {
    let editorModalService: EditorModalService;
    let genericEditorModalServiceMock: jasmine.SpyObj<GenericEditorModalService>;
    let renderServiceMock: jasmine.SpyObj<IRenderService>;
    let contextAwareEditableItemService: jasmine.SpyObj<ContextAwareEditableItemService>;
    let cmsitemsRestService: jasmine.SpyObj<CmsitemsRestService>;
    let translateService: jasmine.SpyObj<TranslateService>;
    let genericEditorStackService: jasmine.SpyObj<GenericEditorStackService>;
    const componentService = jasmine.createSpyObj('ComponentService', ['getSlotsForComponent']);

    let COMPONENT_SERVICE_SLOTS_FOR_COMPONENT: string[];
    const COMPONENT_UID = 'componentUid';
    const COMPONENT_SHARED_IN_PAGE_CONTEXT_MESSAGE = 'se.cms.component.shared.component';
    const COMPONENT_SHARED_IN_WORKFLOW_CONTEXT_MESSAGE =
        'se.cms.component.workflow.shared.component';
    const COMPONENT_IN_WORKFLOW_CONTEXT_MESSAGE = 'se.cms.component.workflow.component';
    beforeEach(() => {
        COMPONENT_SERVICE_SLOTS_FOR_COMPONENT = ['some-slot-id'];
        renderServiceMock = jasmine.createSpyObj('renderService', ['renderSlots']);
        genericEditorModalServiceMock = jasmine.createSpyObj('genericEditorModalServiceMock', [
            'open'
        ]);
        contextAwareEditableItemService = jasmine.createSpyObj('contextAwareEditableItemService', [
            'isItemEditable'
        ]);
        cmsitemsRestService = jasmine.createSpyObj('cmsitemsRestService', ['getById']);
        translateService = jasmine.createSpyObj<TranslateService>('translateService', ['instant']);
        componentService.getSlotsForComponent.and.returnValue(
            Promise.resolve<string[]>(COMPONENT_SERVICE_SLOTS_FOR_COMPONENT)
        );

        cmsitemsRestService.getById.and.returnValue(
            Promise.resolve<any>({
                uid: COMPONENT_UID
            })
        );

        genericEditorStackService = jasmine.createSpyObj('genericEditorStackService', [
            'getEditorsStack'
        ]);

        genericEditorStackService.getEditorsStack.and.returnValue([
            { component: { uuid: 'uuid ' } }
        ] as any);
        contextAwareEditableItemService.isItemEditable.and.returnValue(Promise.resolve(true));

        genericEditorModalServiceMock.open.and.callFake(
            (componentData: IGenericEditorModalServiceComponent): any => {
                const resolvedValue = (
                    componentData && componentData.content
                        ? componentData.content.slotId
                        : undefined
                ) as string;
                return Promise.resolve<string>(resolvedValue);
            }
        );
        editorModalService = new EditorModalService(
            genericEditorModalServiceMock,
            componentService,
            renderServiceMock,
            contextAwareEditableItemService,
            cmsitemsRestService,
            translateService,
            genericEditorStackService
        );
    });

    it('open will delegate to genericEditorModalService.open and invoke a rerendering upon closing', async () => {
        // GIVEN
        const editorStackId = 'editorStackId';
        const editorModalServiceEditComponentMock: IComponent = {
            smarteditComponentUuid: 'smarteditComponentUuid',
            smarteditComponentType: 'smarteditComponentType',
            catalogVersionUuid: 'catalogVersionUuid',
            initialDirty: false
        };

        const genericEditorModalServiceEditComponentMock: IGenericEditorModalServiceComponent = {
            componentUuid: editorModalServiceEditComponentMock.smarteditComponentUuid,
            componentType: editorModalServiceEditComponentMock.smarteditComponentType,
            title:
                'type.' +
                editorModalServiceEditComponentMock.smarteditComponentType.toLowerCase() +
                '.name',
            targetedQualifier: undefined,
            content: {
                uid: COMPONENT_UID
            },
            initialDirty: false,
            readOnlyMode: false,
            editorStackId
        };

        const modalConfigMock: DialogConfig = {
            dialogPanelClass: 'modal-stretched'
        };

        // WHEN
        await editorModalService.open(
            editorModalServiceEditComponentMock,
            undefined,
            undefined,
            undefined,
            undefined,
            editorStackId,
            modalConfigMock
        );

        // THEN
        expect(genericEditorModalServiceMock.open.calls.count()).toBe(1);
        expect(genericEditorModalServiceMock.open.calls.argsFor(0).length).toBe(4);

        expect(genericEditorModalServiceMock.open.calls.argsFor(0)[0]).toEqual(
            genericEditorModalServiceEditComponentMock
        );

        expect(genericEditorModalServiceMock.open.calls.argsFor(0)[3]).toEqual(modalConfigMock);

        expect(renderServiceMock.renderSlots).not.toHaveBeenCalled();
        const callback = genericEditorModalServiceMock.open.calls.argsFor(0)[1];
        await callback(null);
        expect(genericEditorStackService.getEditorsStack).toHaveBeenCalledWith(editorStackId);
        expect(renderServiceMock.renderSlots).toHaveBeenCalledWith(
            COMPONENT_SERVICE_SLOTS_FOR_COMPONENT
        );
    });

    it('GIVEN creating a new component THEN renderService.renderComponent is not called', async () => {
        // GIVEN
        const editorStackId = 'editorStackId';
        const editorModalServiceCreateComponentMock: IComponent = {
            smarteditComponentType: 'smarteditComponentType',
            catalogVersionUuid: 'catalogVersionUuid',
            initialDirty: false
        };

        const genericEditorModalServiceCreateComponentMock: IGenericEditorModalServiceComponent = {
            componentUuid: undefined,
            componentType: editorModalServiceCreateComponentMock.smarteditComponentType,
            title:
                'type.' +
                editorModalServiceCreateComponentMock.smarteditComponentType.toLowerCase() +
                '.name',
            targetedQualifier: undefined,
            content: {
                slotId: undefined,
                typeCode: editorModalServiceCreateComponentMock.smarteditComponentType,
                itemtype: editorModalServiceCreateComponentMock.smarteditComponentType,
                catalogVersion: editorModalServiceCreateComponentMock.catalogVersionUuid,
                visible: true,
                position: undefined
            },
            initialDirty: false,
            editorStackId
        };

        componentService.getSlotsForComponent.and.returnValue(Promise.resolve([]));

        // WHEN
        await editorModalService.open(
            editorModalServiceCreateComponentMock,
            undefined,
            undefined,
            undefined,
            undefined,
            editorStackId
        );

        // THEN
        expect(renderServiceMock.renderSlots).not.toHaveBeenCalled();
        expect(genericEditorModalServiceMock.open.calls.argsFor(0)[0]).toEqual(
            genericEditorModalServiceCreateComponentMock
        );
        const callback = genericEditorModalServiceMock.open.calls.argsFor(0)[1];
        callback(null);
        expect(renderServiceMock.renderSlots).not.toHaveBeenCalled();
    });

    it('should mark component read only if it is not editable in current context', async () => {
        // GIVEM
        const editorModalServiceEditComponentMock: IComponent = {
            smarteditComponentUuid: 'smarteditComponentUuid',
            smarteditComponentType: 'smarteditComponentType',
            catalogVersionUuid: 'catalogVersionUuid',
            initialDirty: false
        };
        contextAwareEditableItemService.isItemEditable.and.returnValue(Promise.resolve(false));

        // WHEN
        await editorModalService.open(editorModalServiceEditComponentMock);

        // THEN
        const genericEditorParams = genericEditorModalServiceMock.open.calls.argsFor(0)[0];
        expect(genericEditorParams.readOnlyMode).toEqual(true);
    });

    it('should display message if component is shared and is editable in current context', async () => {
        // GIVEM
        const editorModalServiceEditComponentMock: IComponent = {
            smarteditComponentUuid: 'smarteditComponentUuid',
            smarteditComponentType: 'smarteditComponentType',
            catalogVersionUuid: 'catalogVersionUuid',
            initialDirty: false
        };
        cmsitemsRestService.getById.and.returnValue(
            Promise.resolve<any>({
                uid: COMPONENT_UID,
                slots: ['a', 'b']
            })
        );
        contextAwareEditableItemService.isItemEditable.and.returnValue(Promise.resolve(true));
        translateService.instant.and.returnValue(COMPONENT_SHARED_IN_PAGE_CONTEXT_MESSAGE);

        // WHEN
        await editorModalService.open(editorModalServiceEditComponentMock);

        // THEN
        expect(translateService.instant).toHaveBeenCalledWith(
            COMPONENT_SHARED_IN_PAGE_CONTEXT_MESSAGE
        );
        const genericEditorParams = genericEditorModalServiceMock.open.calls.argsFor(0)[0];
        expect(genericEditorParams.messages).toContain({
            type: 'info',
            message: COMPONENT_SHARED_IN_PAGE_CONTEXT_MESSAGE
        });
    });

    it('should display message if component is shared and is not editable in current context', async () => {
        // GIVEM
        const editorModalServiceEditComponentMock: IComponent = {
            smarteditComponentUuid: 'smarteditComponentUuid',
            smarteditComponentType: 'smarteditComponentType',
            catalogVersionUuid: 'catalogVersionUuid',
            initialDirty: false
        };

        cmsitemsRestService.getById.and.returnValue(
            Promise.resolve<any>({
                uid: COMPONENT_UID,
                slots: ['a', 'b']
            })
        );

        contextAwareEditableItemService.isItemEditable.and.returnValue(Promise.resolve(false));
        translateService.instant.and.returnValue(COMPONENT_SHARED_IN_WORKFLOW_CONTEXT_MESSAGE);

        // WHEN
        await editorModalService.open(editorModalServiceEditComponentMock);

        // THEN
        expect(translateService.instant).toHaveBeenCalledWith(
            COMPONENT_SHARED_IN_WORKFLOW_CONTEXT_MESSAGE
        );
        const genericEditorParams = genericEditorModalServiceMock.open.calls.argsFor(0)[0];
        expect(genericEditorParams.messages).toContain({
            type: 'info',
            message: COMPONENT_SHARED_IN_WORKFLOW_CONTEXT_MESSAGE
        });
    });

    it('should display message if component is not shared in a page in workflow', async () => {
        // GIVEM
        const editorModalServiceEditComponentMock: IComponent = {
            smarteditComponentUuid: 'smarteditComponentUuid',
            smarteditComponentType: 'smarteditComponentType',
            catalogVersionUuid: 'catalogVersionUuid',
            initialDirty: false
        };
        cmsitemsRestService.getById.and.returnValue(
            Promise.resolve<any>({
                uid: COMPONENT_UID
            })
        );
        contextAwareEditableItemService.isItemEditable.and.returnValue(Promise.resolve(false));
        translateService.instant.and.returnValue(COMPONENT_IN_WORKFLOW_CONTEXT_MESSAGE);

        // WHEN
        await editorModalService.open(editorModalServiceEditComponentMock);

        // THEN
        expect(translateService.instant).toHaveBeenCalledWith(
            COMPONENT_IN_WORKFLOW_CONTEXT_MESSAGE
        );
        const genericEditorParams = genericEditorModalServiceMock.open.calls.argsFor(0)[0];
        expect(genericEditorParams.messages).toContain({
            type: 'info',
            message: COMPONENT_IN_WORKFLOW_CONTEXT_MESSAGE
        });
    });

    it('should display only one message if component is editable and is shared in current context at the same time', async () => {
        // GIVEN
        const editorModalServiceEditComponentMock: IComponent = {
            smarteditComponentUuid: 'smarteditComponentUuid',
            smarteditComponentType: 'smarteditComponentType',
            catalogVersionUuid: 'catalogVersionUuid',
            initialDirty: false
        };
        contextAwareEditableItemService.isItemEditable.and.returnValue(Promise.resolve(false));
        cmsitemsRestService.getById.and.returnValue(
            Promise.resolve<any>({
                uid: COMPONENT_UID,
                slots: ['a', 'b']
            })
        );
        translateService.instant.and.callFake((key) => key);

        // WHEN
        await editorModalService.open(editorModalServiceEditComponentMock);

        // THEN
        const genericEditorParams = genericEditorModalServiceMock.open.calls.argsFor(0)[0];
        expect(genericEditorParams.messages).toContain({
            type: 'info',
            message: COMPONENT_SHARED_IN_WORKFLOW_CONTEXT_MESSAGE
        });
        expect(genericEditorParams.messages).not.toContain({
            type: 'info',
            message: COMPONENT_SHARED_IN_PAGE_CONTEXT_MESSAGE
        });
        expect(genericEditorParams.messages).not.toContain({
            type: 'info',
            message: COMPONENT_IN_WORKFLOW_CONTEXT_MESSAGE
        });
    });

    it('should display save button when the editor is opened', async () => {
        // GIVEN
        const saveCallback = function saveCallback1(item: any) {
            return Promise.resolve(true);
        };
        const editorModalServiceEditComponentMock: IComponent = {
            smarteditComponentUuid: 'smarteditComponentUuid',
            smarteditComponentType: 'smarteditComponentType',
            catalogVersionUuid: 'catalogVersionUuid',
            initialDirty: false
        };

        const genericEditorModalServiceEditComponentMock: IGenericEditorModalServiceComponent = {
            componentUuid: editorModalServiceEditComponentMock.smarteditComponentUuid,
            componentType: editorModalServiceEditComponentMock.smarteditComponentType,
            title:
                'type.' +
                editorModalServiceEditComponentMock.smarteditComponentType.toLowerCase() +
                '.name',
            targetedQualifier: undefined,
            content: {
                uid: COMPONENT_UID
            },
            initialDirty: false,
            readOnlyMode: false,
            saveCallback
        };
        // WHEN
        await editorModalService.open(
            editorModalServiceEditComponentMock,
            undefined,
            undefined,
            undefined,
            saveCallback
        );

        // THEN
        expect(genericEditorModalServiceEditComponentMock.saveCallback).toBeDefined();
    });
});
