import { IRestServiceFactory, TreeNodeWithLevel, NavigationNodeItem, IUriContext, NavigationNode, CmsitemsRestService } from 'smarteditcommons';
import { NavigationNodeCMSItem } from '../types';
import { NodeAncestryService } from './NodeAncestryService';
export declare class NavigationEditorNodeService {
    private readonly cmsitemsRestService;
    private readonly nodeAncestryService;
    private readonly navigationNodeRestService;
    constructor(cmsitemsRestService: CmsitemsRestService, nodeAncestryService: NodeAncestryService, restServiceFactory: IRestServiceFactory);
    getNavigationNode(nodeUid: string, uriParams: IUriContext): Promise<NavigationNode>;
    /**
     * Updates the position of the navigation node within the children collection of its parent.
     * Fetches the parent node, and reorders the children, then updates the parent with the new child order.
     *
     * @param node The navigation node to be updated.
     *
     * E.g. Navigation Management Page -> Edit Node
     */
    updateNavigationNodePosition(node: NavigationNodeItem): Promise<NavigationNodeCMSItem>;
    /**
     * Returns the list of nodes belonging to the ancestry of the node identified by its uid. This list includes the queried node as well.
     */
    getNavigationNodeAncestry(nodeUid: string, uriParams: IUriContext): Promise<TreeNodeWithLevel[]>;
}
