import { GatewayProxy, IPermissionService, IToolbarService, LogService, ToolbarItemInternal } from 'smarteditcommons';
export declare class ToolbarService extends IToolbarService {
    gatewayId: string;
    private onAliasesChange;
    constructor(gatewayId: string, gatewayProxy: GatewayProxy, logService: LogService, permissionService: IPermissionService);
    addAliases(aliases: ToolbarItemInternal[]): void;
    /**
     * This method removes the action and the aliases of the toolbar item identified by
     * the provided key.
     *
     * @param itemKey - Identifier of the toolbar item to remove.
     */
    removeItemByKey(itemKey: string): void;
    removeAliasByKey(itemKey: string): void;
    setOnAliasesChange(onAliasesChange: (aliases: ToolbarItemInternal[]) => void): void;
    triggerAction(action: ToolbarItemInternal): void;
    private get;
    private sortAliases;
}
