/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { InjectionToken } from '@angular/core';
import { TypedMap } from '@smart/utils';
import { IDropdownMenuItem } from '../../components';

export interface NavigationNodeEntry {
    id: string;
    name: string;
    itemType: string;
}

export interface TreeNode {
    /**
     * Boolean specifying whether the retrieved node has children. This is read only and ignored upon saving.
     */
    hasChildren: boolean;
    /**
     * The non localized node name. Required upon posting.
     */
    name: string;
    parent: TreeNode;
    /**
     * The unique identifier of the parent node for the given catalog. Required upon posting.
     */
    parentUid: string;
    position: number;
    itemType: string;
    /**
     * The unique identifier of a node for the given catalog. Optional upon posting.
     */
    uid: string;
    uuid: string;
    nodes?: TreeNode[];
    initiated?: boolean;
    mouseHovered?: boolean;
    isExpanded?: boolean;
}

export interface TreeNodeWithLevel extends TreeNode {
    level: number;
    formattedLevel: string;
}

export interface NavigationNode extends TreeNode {
    id: string;
    nodes: NavigationNode[];
    entries?: NavigationNodeEntry[];
    parent: NavigationNode;
}

export interface TreeNodeItemDTO {
    hasChildren?: boolean;
    name?: string;
    parentUid?: string;
    position?: number;
    itemType?: string;
    uid?: string;
    uuid?: string;
    title?: TypedMap<string>;
    level?: number;
}

export interface NavigationNodeItemDTO extends TreeNodeItemDTO {
    id: string;
    entries?: NavigationNodeEntry[];
}

/**
 * An object representing tree nodes consumed by the {@link TreeComponent}.
 */
export interface ITreeNodeItem<T> {
    hasChildren: boolean;
    name: string;

    parent: ITreeNodeItem<T>;

    /**
     * Carries information about parent string uid.
     */
    parentUid: string;

    /**
     * Holds number value of node position in the tree.
     */
    position: number;

    /**
     * Type of the node, based on that value different node class may be used from {@link TreeModule.service:TreeNodeItemFactory TreeNodeItemFactory}.
     * You can override the default factory to use your custom classes.
     */
    itemType: string;

    /**
     * Uid of the node.
     */
    uid: string;

    /**
     * Uuid of the nodes.
     */
    uuid: string;

    /**
     * Array of node descendants.
     */
    nodes?: ITreeNodeItem<T>[];

    initiated?: boolean;

    /**
     * Flags whether the UI instance of the node is being mouse hovered.
     */
    mouseHovered?: boolean;

    isExpanded?: boolean;

    /**
     * Map of localized titles.
     */
    title?: TypedMap<string>;

    /**
     * A nesting level indicator in which node resides.
     */
    level?: number;

    /**
     * Sets node initiated property.
     */
    setInitiated(isInitiated: boolean): ITreeNodeItem<T>;

    addNode(node: ITreeNodeItem<T>): ITreeNodeItem<T>;

    addNodes(nodes: ITreeNodeItem<T>[]): ITreeNodeItem<T>;

    removeAllNodes(): ITreeNodeItem<T>;

    removeNode(node: ITreeNodeItem<T>): ITreeNodeItem<T>;

    /**
     * Sets the immediate parent of the node element.
     */
    setParent(node: ITreeNodeItem<T>): ITreeNodeItem<T>;

    /**
     * Toggles the node to expand/collapse children.
     */
    toggle(): ITreeNodeItem<T>;

    /**
     * Collapses the tree hiding children.
     */
    collapse(): ITreeNodeItem<T>;

    /**
     * Expands the tree revealing all children.
     */
    expand(): ITreeNodeItem<T>;

    /**
     * Sets mouseHovered flag.
     */
    setMouseHovered(isHovered: boolean): ITreeNodeItem<T>;

    /**
     * Collapses all descendant children.
     */
    collapseAll(): void;

    /**
     * Expands all descendant children.
     */
    expandAll(): void;
}

/**
 * Represents an object holding callbacks related to nodes drag and drop functionality in the {@link TreeComponent}.
 * Each callback exposes the {@link TreeDragAndDropEvent}.
 */
export interface TreeDragAndDropOptions<T> {
    /**
     * Callback function executed after the node is dropped.
     */
    onDropCallback: (event: TreeDragAndDropEvent<T>) => void;

    /**
     * Callback function executed before drop. Return true allows drop, false rejects, and an object {confirmDropI18nKey: 'key'} opens a confirmation modal.
     */
    beforeDropCallback: (event: TreeDragAndDropEvent<T>) => Promise<any>;

    /**
     * Callback function executed when hovering over droppable slots, return true to allow, return false to block.
     */
    acceptDropCallback?: (event: TreeDragAndDropEvent<T>) => boolean;
    allowDropCallback: (event: TreeDragAndDropEvent<T>) => boolean;
}

/**
 * Represents the event triggered when dragging and dropping nodes in the {@link TreeComponent}.
 */
export class TreeDragAndDropEvent<T> {
    /**
     * @param sourceNode `ITreeNodeItem` that is being dragged.
     * @param destinationNodes The set of the destination's parent's children `ITreeNodeItem`.
     * @param sourceParentNode Parent `ITreeNodeItem` of the dragged node.
     * @param destinationParentNode Parent `ITreeNodeItem` of the destination node.
     * @param position Index at which the `ITreeNodeItem` was dropped.
     */
    constructor(
        public sourceNode: T,
        public destinationNodes: T[],
        public sourceParentNode: T,
        public destinationParentNode: T,
        public position: number
    ) {}
}

export const TREE_NODE = new InjectionToken('TREE_NODE');

export interface TreeNodeActions<T> {
    getDropdownItems?(...args: any[]): IDropdownMenuItem[];
    fetchData?(...args: any[]): Promise<T>;
    removeItem?(...args: any[]): void;
    moveUp?(...args: any[]): void;
    moveDown?(...args: any[]): void;
    isMoveUpAllowed?(...args: any[]): boolean;
    isMoveDownAllowed?(...args: any[]): boolean;
    performUpdate?(...args: any[]): void;
    refreshList?(...args: any[]): void;
}
