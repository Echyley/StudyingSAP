import { IToolbarService } from '../IToolbarService';
/**
 * The toolbar service factory generates instances of the {@link IToolbarService ToolbarService} based on
 * the gateway ID (toolbar-name) provided. Only one ToolbarService instance exists for each gateway ID, that is, the
 * instance is a singleton with respect to the gateway ID.
 */
export declare abstract class IToolbarServiceFactory<T extends IToolbarService = IToolbarService> {
    /**
     * Returns a single instance of the ToolbarService for the given gateway identifier. If one does not exist, an
     * instance is created and cached.
     *
     * @param gatewayId The toolbar name used for cross iframe communication (see {@link GatewayProxy}).
     * @returns Corresponding ToolbarService instance for given gateway ID.
     */
    getToolbarService(gatewayId: string): T;
}
