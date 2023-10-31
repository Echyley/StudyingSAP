import { TypedMap } from '@smart/utils';
import { NavigationNodeEntry } from '../treeModule';
import { ITreeNodeItem, NavigationNodeItemDTO, TreeNodeItemDTO } from './types';
export declare class TreeNodeItem implements ITreeNodeItem<TreeNodeItem> {
    hasChildren: boolean;
    name: string;
    parent: TreeNodeItem;
    parentUid: string;
    position: number;
    itemType: string;
    uid: string;
    uuid: string;
    nodes?: TreeNodeItem[];
    initiated?: boolean;
    mouseHovered?: boolean;
    isExpanded?: boolean;
    title?: TypedMap<string>;
    level?: number;
    constructor(config: TreeNodeItemDTO);
    setPosition(position: number): TreeNodeItem;
    setMouseHovered(isHovered: boolean): TreeNodeItem;
    setInitiated(isInitiated: boolean): TreeNodeItem;
    setLevel(level: number): TreeNodeItem;
    addNode(node: TreeNodeItem): TreeNodeItem;
    addNodes(nodes: TreeNodeItem[]): TreeNodeItem;
    removeAllNodes(): TreeNodeItem;
    removeNode(node: TreeNodeItem): TreeNodeItem;
    setParent(node: TreeNodeItem): TreeNodeItem;
    toggle(): TreeNodeItem;
    collapse(): TreeNodeItem;
    expand(): TreeNodeItem;
    collapseAll(): void;
    expandAll(): void;
    private collapseRecursively;
    private expandRecursively;
    private updateHasChildren;
}
export declare class NavigationNodeItem extends TreeNodeItem implements ITreeNodeItem<NavigationNodeItem> {
    id: string;
    nodes: NavigationNodeItem[];
    entries?: NavigationNodeEntry[];
    parent: NavigationNodeItem;
    constructor(config: NavigationNodeItemDTO);
}
