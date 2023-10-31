import { IComponentVisibilityAlertService, IEditorEnablerService } from 'cmscommons';
import { IContextualMenuConfiguration, IFeatureService, SystemEventService, ISlotRestrictionsService, IEditorModalService, IComponentHandlerService, CrossFrameEventService } from 'smarteditcommons';
/**
 * Allows enabling the Edit Component contextual menu item,
 * providing a SmartEdit CMS admin the ability to edit various properties of the given component.
 *
 * Convenience service to attach the open Editor Modal action to the contextual menu of a given component type, or
 * given regex corresponding to a selection of component types.
 *
 * Example: The Edit button is added to the contextual menu of the CMSParagraphComponent, and all types postfixed
 * with BannerComponent.
 */
export declare class EditorEnablerService extends IEditorEnablerService {
    private readonly componentHandlerService;
    private readonly componentVisibilityAlertService;
    private readonly editorModalService;
    private readonly featureService;
    private readonly slotRestrictionsService;
    private readonly crossFrameEventService;
    private readonly systemEventService;
    private readonly contextualMenuButton;
    private isEditorModalOpen;
    constructor(componentHandlerService: IComponentHandlerService, componentVisibilityAlertService: IComponentVisibilityAlertService, editorModalService: IEditorModalService, featureService: IFeatureService, slotRestrictionsService: ISlotRestrictionsService, crossFrameEventService: CrossFrameEventService, systemEventService: SystemEventService);
    /**
     * Enables the Edit contextual menu item for the given component types.
     *
     * @param componentTypes The list of component types, as defined in the platform, for which to enable the Edit contextual menu.
     */
    enableForComponents(componentTypes: string[]): void;
    onClickEditButton({ slotId, componentAttributes, isComponentHidden, componentType, componentUuid }: IContextualMenuConfiguration): Promise<void>;
    isSlotEditableForNonExternalComponent(config: IContextualMenuConfiguration): Promise<boolean>;
}
