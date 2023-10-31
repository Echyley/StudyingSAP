import { IEditorEnablerService, IComponentMenuConditionAndCallbackService } from 'cmscommons';
import { IPermissionService } from 'smarteditcommons';
import { IPageTreeMenuBaseService } from './IPageTreeMenuBaseService';
export declare class PageTreeComponentMenuService extends IPageTreeMenuBaseService {
    private readonly editorEnablerService;
    private readonly componentMenuConditionAndCallbackService;
    constructor(editorEnablerService: IEditorEnablerService, componentMenuConditionAndCallbackService: IComponentMenuConditionAndCallbackService, permissionService: IPermissionService);
}
