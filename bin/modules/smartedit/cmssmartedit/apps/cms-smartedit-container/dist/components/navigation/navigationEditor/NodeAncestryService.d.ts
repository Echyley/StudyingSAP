import { TreeNode, TreeNodeWithLevel } from 'smarteditcommons';
/**
 * Used for converting Tree Nodes that are displayed in Breadcrumb.
 */
export declare class NodeAncestryService {
    buildOrderedListOfAncestors(nodes: TreeNode[], uid: string): TreeNodeWithLevel[];
    private fetchAncestors;
}
