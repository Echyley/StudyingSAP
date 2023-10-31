import { DialogConfig } from '@fundamental-ngx/core';
import { TranslateService } from '@ngx-translate/core';
import { ComponentService, IContextAwareEditableItemService } from 'cmscommons';
import { GenericEditorStackService, IRenderService, CmsitemsRestService, IGenericEditorModalServiceComponent, IEditorModalService, IComponent } from 'smarteditcommons';
import { GenericEditorModalService } from './GenericEditorModalService';
export declare class EditorModalService extends IEditorModalService {
    private genericEditorModalService;
    private componentService;
    private renderService;
    private contextAwareEditableItemService;
    private cmsitemsRestService;
    private translateService;
    private genericEditorStackService;
    constructor(genericEditorModalService: GenericEditorModalService, componentService: ComponentService, renderService: IRenderService, contextAwareEditableItemService: IContextAwareEditableItemService, cmsitemsRestService: CmsitemsRestService, translateService: TranslateService, genericEditorStackService: GenericEditorStackService);
    openAndRerenderSlot(componentType: string, componentUuid: string, targetedQualifier: string, config?: DialogConfig, saveCallback?: (item: any) => void, editorStackId?: string): Promise<any>;
    open(componentAttributes: IComponent, targetSlotId?: string, position?: number, targetedQualifier?: string, saveCallback?: (item: any) => void, editorStackId?: string, config?: DialogConfig): Promise<any>;
    openGenericEditor(data: IGenericEditorModalServiceComponent, saveCallback?: () => void, errorCallback?: () => void, config?: DialogConfig): Promise<any>;
    /**
     * Loads content of the item by uuid and populates the content attribute of componentAttributes object only if it's not already provided.
     */
    private preloadContent;
    /**
     * Opens generic editor.
     *
     * @param componentData Object that contains all parameters for generic editor.
     * Note: if the componentUuid is not provided the generic editor will be opened for creation.
     *
     * @param saveCallback the save callback that is triggered after submit.
     */
    private markAndOpenGenericEditor;
    /**
     * Retrieves a content object by its uuid. If the uuid is undefined, null is returned.
     */
    private getContentByUuid;
    /**
     * Verifies whether the component is shared in workflow context. If yes, then makes the component readonly and adds a message that the component
     * is used in different workflow. If no, checks whether the component is shared in page context and adds a message about it.
     */
    private markComponentAsShared;
    /**
     * Verifies whether the component is shared or not by checking the slots attribute of the component payload.
     */
    private componentIsShared;
    /**
     * Adds a message to a messages attribute of IGenericEditorModalServiceComponent. If the attribute is undefined then the new one is created.
     */
    private addComponentInfoMessage;
    private prepareContentForCreate;
    private createComponentData;
}
