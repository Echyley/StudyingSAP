import { IEditorEnablerService, IComponentMenuConditionAndCallbackService } from 'cmscommons';
import { IContextualMenuConfiguration, IPermissionService, IContextualMenuButton } from 'smarteditcommons';
import { IPageTreeMenuBaseService } from './IPageTreeMenuBaseService';
export declare class PageTreeChildComponentMenuService extends IPageTreeMenuBaseService {
    private readonly editorEnablerService;
    private readonly componentMenuConditionAndCallbackService;
    constructor(editorEnablerService: IEditorEnablerService, componentMenuConditionAndCallbackService: IComponentMenuConditionAndCallbackService, permissionService: IPermissionService);
    getPageTreeChildComponentMenus(parentConfiguration: IContextualMenuConfiguration, configuration: IContextualMenuConfiguration): Promise<IContextualMenuButton[]>;
}
