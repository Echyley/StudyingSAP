import { TranslateService } from '@ngx-translate/core';
import { IComponentMenuConditionAndCallbackService, IComponentSharedService, IComponentVisibilityAlertService, IRemoveComponentService, ISlotVisibilityService, IComponentEditingFacade, TypePermissionsRestService } from 'cmscommons';
import { CmsitemsRestService, IComponentHandlerService, CrossFrameEventService, IAlertService, ICatalogService, ICatalogVersionPermissionService, IConfirmationModalService, IContextualMenuConfiguration, IEditorModalService, IFeatureService, IPageInfoService, ISlotRestrictionsService, LogService } from 'smarteditcommons';
import { ComponentInfoService } from './ComponentInfoService';
export declare class ComponentMenuConditionAndCallbackService extends IComponentMenuConditionAndCallbackService {
    private readonly componentHandlerService;
    private readonly typePermissionsRestService;
    private readonly componentVisibilityAlertService;
    private readonly editorModalService;
    private readonly featureService;
    private readonly slotRestrictionsService;
    private readonly confirmationModalService;
    private readonly logService;
    private readonly alertService;
    private readonly removeComponentService;
    private readonly slotVisibilityService;
    private readonly crossFrameEventService;
    private readonly translateService;
    private readonly componentInfoService;
    private readonly cmsitemsRestService;
    private readonly componentEditingFacade;
    private readonly componentSharedService;
    private readonly pageInfoService;
    private readonly catalogService;
    private readonly catalogVersionPermissionService;
    constructor(componentHandlerService: IComponentHandlerService, typePermissionsRestService: TypePermissionsRestService, componentVisibilityAlertService: IComponentVisibilityAlertService, editorModalService: IEditorModalService, featureService: IFeatureService, slotRestrictionsService: ISlotRestrictionsService, confirmationModalService: IConfirmationModalService, logService: LogService, alertService: IAlertService, removeComponentService: IRemoveComponentService, slotVisibilityService: ISlotVisibilityService, crossFrameEventService: CrossFrameEventService, translateService: TranslateService, componentInfoService: ComponentInfoService, cmsitemsRestService: CmsitemsRestService, componentEditingFacade: IComponentEditingFacade, componentSharedService: IComponentSharedService, pageInfoService: IPageInfoService, catalogService: ICatalogService, catalogVersionPermissionService: ICatalogVersionPermissionService);
    editConditionForHiddenComponent(configuration: IContextualMenuConfiguration): Promise<boolean>;
    sharedCondition(configuration: IContextualMenuConfiguration): Promise<boolean>;
    externalCondition(configuration: IContextualMenuConfiguration): Promise<boolean>;
    removeCondition(configuration: IContextualMenuConfiguration): Promise<boolean>;
    removeCallback(configuration: IContextualMenuConfiguration, $event: Event): Promise<void>;
    cloneCondition(configuration: IContextualMenuConfiguration): Promise<boolean>;
    cloneCallback(configuration: IContextualMenuConfiguration): Promise<void>;
    dragCondition(configuration: IContextualMenuConfiguration): Promise<boolean>;
}