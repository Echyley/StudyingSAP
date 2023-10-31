import { GatewayProxy, IPermissionService, IToolbarServiceFactory, LogService } from 'smarteditcommons';
import { ToolbarService } from './ToolbarService';
export declare class ToolbarServiceFactory implements IToolbarServiceFactory {
    private gatewayProxy;
    private logService;
    private permissionService;
    private toolbarServicesByGatewayId;
    constructor(gatewayProxy: GatewayProxy, logService: LogService, permissionService: IPermissionService);
    getToolbarService(gatewayId: string): ToolbarService;
}
