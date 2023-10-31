import { ICatalogService, IUriContext, Payload } from 'smarteditcommons';
import { GenericEditorModalService } from '../../../services/GenericEditorModalService';
import { NavigationNode } from '../types';
/**
 * Convenience service to open an editor modal window for a given navigation node's data.
 */
export declare class NavigationNodeEditorModalService {
    private genericEditorModalService;
    private catalogService;
    constructor(genericEditorModalService: GenericEditorModalService, catalogService: ICatalogService);
    /**
     *
     * Opens a modal for creating and editing a navigation node with the CSM items API. Leave the current parameter to trigger
     * a creation operation.
     *
     * @param uriContext The uri context of the navigational node.
     * @param parent The parent navigational node.
     * @param current The current navigational node. If the current node is left empty, the modal
     * will process a creation operation.
     */
    open(uriContext: IUriContext, parent: NavigationNode, current?: NavigationNode): Promise<Payload>;
}
