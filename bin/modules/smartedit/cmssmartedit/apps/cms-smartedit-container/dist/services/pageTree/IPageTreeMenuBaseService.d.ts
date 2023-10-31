import { IContextualMenuButton, IContextualMenuConfiguration, IPermissionService } from 'smarteditcommons';
export declare abstract class IPageTreeMenuBaseService {
    private readonly permissionService;
    protected originalMenus: IContextualMenuButton[];
    constructor(permissionService: IPermissionService);
    getPageTreeComponentMenus(configuration: IContextualMenuConfiguration): Promise<IContextualMenuButton[]>;
    protected buildMenusByPermission(): Promise<IContextualMenuButton[]>;
}
