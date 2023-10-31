import { GatewayProxy, IPermissionService, IToolbarService, LogService } from 'smarteditcommons';
export declare class ToolbarService extends IToolbarService {
    gatewayId: string;
    constructor(gatewayId: string, gatewayProxy: GatewayProxy, logService: LogService, permissionService: IPermissionService);
    _removeItemOnInner(itemKey: string): void;
    triggerActionOnInner(action: {
        key: string;
    }): void;
}
