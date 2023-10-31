/// <reference types="jquery" />
export declare class SliderPanelZIndexHelper {
    static BLACKLISTED_NODE_NAMES: Set<string>;
    /** Retrieve a highest value from array of zIndex integers. */
    getHighestZIndex(node: JQuery<HTMLElement>): number;
    private filterBlacklistedNodes;
    /** Retrieve zIndex integer value from node, fallback with 0 value in case of NaN. */
    private mapToZIndexIntegers;
    /** Return recurring flat array of node and it's children. */
    private getChildrenNodesFromTreeOrLeaf;
}
