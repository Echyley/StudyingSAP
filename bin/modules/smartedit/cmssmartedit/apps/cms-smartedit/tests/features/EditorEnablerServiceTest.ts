/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ICMSComponent, IComponentVisibilityAlertService } from 'cmscommons';
import { EditorEnablerService } from 'cmssmartedit/services';
import {
    ComponentAttributes,
    IFeatureService,
    SystemEventService,
    ISlotRestrictionsService,
    IEditorModalService,
    IComponentHandlerService,
    CrossFrameEventService
} from 'smarteditcommons';

describe('EditorEnablerService', () => {
    let componentHandlerService: jasmine.SpyObj<IComponentHandlerService>;
    let componentVisibilityAlertService: jasmine.SpyObj<IComponentVisibilityAlertService>;
    let editorModalService: jasmine.SpyObj<IEditorModalService>;
    let featureService: jasmine.SpyObj<IFeatureService>;
    let slotRestrictionsService: jasmine.SpyObj<ISlotRestrictionsService>;
    let systemEventService: jasmine.SpyObj<SystemEventService>;
    let crossFrameEventService: jasmine.SpyObj<CrossFrameEventService>;

    const mockComponentAttributes = {} as ComponentAttributes;

    const createModalItem = (): ICMSComponent =>
        (({
            uuid: 'MOCK_ITEM_UUID',
            itemtype: 'MOCK_ITEM_TYPE',
            catalogVersion: 'MOCK_CATALOG_VERSION',
            slotId: 'MOCK_SLOT_ID'
        } as unknown) as ICMSComponent);

    const mockModalItem = (scenario: string): ICMSComponent => {
        const MOCK_PAYLOAD = createModalItem();
        MOCK_PAYLOAD.visible = scenario.includes('VISIBLE');
        MOCK_PAYLOAD.restricted = scenario.includes('WITH_RESTRICTIONS');
        return MOCK_PAYLOAD;
    };

    let editorEnablerService: EditorEnablerService;
    let editorEnablerServiceAny: any;
    beforeEach(() => {
        componentHandlerService = jasmine.createSpyObj<IComponentHandlerService>(
            'componentHandlerService',
            ['getOriginalComponent', 'getParentSlotForComponent', 'isExternalComponent']
        );

        componentVisibilityAlertService = jasmine.createSpyObj<IComponentVisibilityAlertService>(
            'componentVisibilityAlertService',
            ['checkAndAlertOnComponentVisibility']
        );

        editorModalService = jasmine.createSpyObj<IEditorModalService>('editorModalService', [
            'open'
        ]);

        featureService = jasmine.createSpyObj<IFeatureService>('featureService', [
            'addContextualMenuButton'
        ]);

        slotRestrictionsService = jasmine.createSpyObj<ISlotRestrictionsService>(
            'slotRestrictionsService',
            ['isSlotEditable']
        );

        systemEventService = jasmine.createSpyObj<SystemEventService>('systemEventService', [
            'publish'
        ]);

        editorEnablerService = new EditorEnablerService(
            componentHandlerService,
            componentVisibilityAlertService,
            editorModalService,
            featureService,
            slotRestrictionsService,
            crossFrameEventService,
            systemEventService
        );
        editorEnablerServiceAny = editorEnablerService as any;
    });

    it('enableForComponent will add the Edit Button to the contextual menu for specified component types', () => {
        const TYPE = 'SimpleResponsiveBannerComponent';

        // WHEN
        editorEnablerService.enableForComponents([TYPE]);

        // THEN
        expect(featureService.addContextualMenuButton).toHaveBeenCalledWith({
            key: 'se.cms.edit',
            nameI18nKey: 'se.cms.contextmenu.nameI18nKey.edit',
            descriptionI18nKey: 'se.cms.contextmenu.descriptionI18n.edit',
            priority: 400,
            regexpKeys: [TYPE],
            displayClass: 'editbutton',
            displayIconClass: 'sap-icon--edit',
            displaySmallIconClass: 'sap-icon--edit',
            i18nKey: 'se.cms.contextmenu.title.edit',
            permissions: ['se.context.menu.edit.component'],
            action: {
                callback: jasmine.any(Function) as any
            },
            condition: jasmine.any(Function) as any
        });
    });

    it('onClickEditButton delegates to the Editor Modal Service', async () => {
        // GIVEN
        const mockItem = mockModalItem('VISIBLE_NO_RESTRICTIONS');
        editorModalService.open.and.returnValue(Promise.resolve(mockItem));

        // WHEN
        // eslint-disable-next-line @typescript-eslint/no-unsafe-call
        await editorEnablerServiceAny.onClickEditButton({
            componentAttributes: mockComponentAttributes,
            slotId: 'MOCK_SLOT_ID'
        });

        // THEN
        expect(editorModalService.open).toHaveBeenCalledWith(
            mockComponentAttributes,
            null,
            null,
            null,
            null,
            null,
            { dialogPanelClass: 'modal-dialog' }
        );
        expect(
            componentVisibilityAlertService.checkAndAlertOnComponentVisibility
        ).not.toHaveBeenCalledWith(null);
        expect(systemEventService.publish).toHaveBeenCalledWith(
            'COMPONENT_UPDATED_EVENT',
            mockItem
        );
    });

    it("onClickEditButton delegates to the Editor Modal Service which triggers a 'hidden' CMS Item Alert", async () => {
        // GIVEN
        editorModalService.open.and.returnValue(
            Promise.resolve(mockModalItem('HIDDEN_NO_RESTRICTIONS'))
        );

        // WHEN
        // eslint-disable-next-line @typescript-eslint/no-unsafe-call
        await editorEnablerServiceAny.onClickEditButton({
            componentAttributes: mockComponentAttributes,
            slotId: 'MOCK_SLOT_ID'
        });

        // THEN
        expect(editorModalService.open).toHaveBeenCalledWith(
            mockComponentAttributes,
            null,
            null,
            null,
            null,
            null,
            { dialogPanelClass: 'modal-dialog' }
        );
        expect(
            componentVisibilityAlertService.checkAndAlertOnComponentVisibility
        ).toHaveBeenCalledWith({
            itemId: 'MOCK_ITEM_UUID',
            itemType: 'MOCK_ITEM_TYPE',
            catalogVersion: 'MOCK_CATALOG_VERSION',
            restricted: false,
            slotId: 'MOCK_SLOT_ID',
            visible: false
        });
    });

    it("onClickEditButton delegates to the Editor Modal Service which triggers a 'hidden' CMS Item Alert", async () => {
        // GIVEN
        editorModalService.open.and.returnValue(
            Promise.resolve(mockModalItem('HIDDEN_WITH_RESTRICTIONS'))
        );

        // WHEN
        // eslint-disable-next-line @typescript-eslint/no-unsafe-call
        await editorEnablerServiceAny.onClickEditButton({
            componentAttributes: mockComponentAttributes,
            slotId: 'MOCK_SLOT_ID'
        });

        // THEN
        expect(editorModalService.open).toHaveBeenCalledWith(
            mockComponentAttributes,
            null,
            null,
            null,
            null,
            null,
            { dialogPanelClass: 'modal-dialog' }
        );
        expect(
            componentVisibilityAlertService.checkAndAlertOnComponentVisibility
        ).toHaveBeenCalledWith({
            itemId: 'MOCK_ITEM_UUID',
            itemType: 'MOCK_ITEM_TYPE',
            catalogVersion: 'MOCK_CATALOG_VERSION',
            restricted: true,
            slotId: 'MOCK_SLOT_ID',
            visible: false
        });
    });

    it("onClickEditButton delegates to the Editor Modal Service which triggers an 'restricted' CMS Item Alert", async () => {
        // GIVEN
        editorModalService.open.and.returnValue(
            Promise.resolve(mockModalItem('VISIBLE_WITH_RESTRICTIONS'))
        );

        // WHEN
        // eslint-disable-next-line @typescript-eslint/no-unsafe-call
        await editorEnablerServiceAny.onClickEditButton({
            componentAttributes: mockComponentAttributes,
            slotId: 'MOCK_SLOT_ID'
        });

        // THEN
        expect(editorModalService.open).toHaveBeenCalledWith(
            mockComponentAttributes,
            null,
            null,
            null,
            null,
            null,
            { dialogPanelClass: 'modal-dialog' }
        );
        expect(
            componentVisibilityAlertService.checkAndAlertOnComponentVisibility
        ).toHaveBeenCalledWith({
            itemId: 'MOCK_ITEM_UUID',
            itemType: 'MOCK_ITEM_TYPE',
            catalogVersion: 'MOCK_CATALOG_VERSION',
            restricted: true,
            slotId: 'MOCK_SLOT_ID',
            visible: true
        });
    });

    it('isSlotEditableForNonExternalComponent delegates to the ComponentHandlerService and SlotRestrictionsService', async () => {
        // GIVEN
        const mockConfigElement = jasmine.createSpy('mockConfigElement');

        const contextualMenuConfig = {
            componentId: 'someId',
            componentType: 'someType',
            element: mockConfigElement
        };

        const mockSlotId = 'dummySlotId';
        componentHandlerService.getParentSlotForComponent.and.returnValue(mockSlotId);
        slotRestrictionsService.isSlotEditable.and.returnValue(Promise.resolve(false));

        // WHEN
        // eslint-disable-next-line @typescript-eslint/no-unsafe-call
        await editorEnablerServiceAny.isSlotEditableForNonExternalComponent(contextualMenuConfig);

        // THEN
        expect(componentHandlerService.getParentSlotForComponent).toHaveBeenCalledWith(
            mockConfigElement as any
        );
        expect(slotRestrictionsService.isSlotEditable).toHaveBeenCalledWith(mockSlotId);
    });
});
