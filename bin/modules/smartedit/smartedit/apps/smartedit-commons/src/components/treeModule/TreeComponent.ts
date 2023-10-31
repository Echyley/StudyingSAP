/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, EventEmitter, Input, Output, Type } from '@angular/core';
import { TypedMap } from '@smart/utils';

import { TreeDragAndDropService } from './TreeDragAndDropService';
import { TreeNodeItemFactory } from './TreeNodeItemFactory';
import { TreeService } from './TreeService';
import { ITreeNodeItem, TreeDragAndDropOptions } from './types';

/**
 * This components renders a tree of nodes and manages CRUD operations around the nodes.
 *
 * It relies on {@link https://material.angular.io/cdk angular/cdk} third party library.
 */
@Component({
    selector: 'se-tree',
    providers: [TreeService, TreeDragAndDropService, TreeNodeItemFactory],
    host: {
        class: 'se-tree'
    },
    templateUrl: './TreeComponent.html'
})
export class TreeComponent<T, D> {
    /**
     * HTML node template to be included besides each node to enhance rendering and behaviour of the tree.
     * This template may use the `nodeActions`.
     */
    @Input() nodeTemplateUrl: string;

    /** Component to be rendered as tree node. */
    @Input() nodeComponent: Type<any>;

    /** the REST entry point to be used to manage the nodes (GET, POST, PUT and DELETE). */
    @Input() nodeUri: string;

    /**
     * A map of methods to be closure-bound to the instance of the component in order to manage the tree from the parent scope or from the optional node template.
     *
     * All nodeActions methods must take `TreeService` instance as first parameter.
     *
     * `TreeService` instance will then pre-bound in the closure made available in the node template or in the parent scope.
     *
     * ### Example in a parent controller:
     *
     *      this.actions = {
     * 		    myMethod(treeService, arg1, arg2) {
     * 			    //some action expecting 'this'
     * 			    //to be the YTreeController
     * 			    this.newChild(treeService.root.nodes[0]);
     * 		    }
     * 	    };
     *
     * ### Passed to the component through:
     *
     *
     * Angular:
     *
     *      <se-tree [nodeUri]='nodeURI' [nodeComponent]='nodeComponent' [nodeActions]='actions'/>
     *
     *
     * In Angular component inject the parent TreeComponent reference into your node component constructor to access actions and invoke it like this:
     *
     *      <button (click)="treeComponentRef.myMethod('arg1', 'arg2')">my action</button>
     */
    @Input() nodeActions: TypedMap<(...args: any[]) => void>;
    @Input() rootNodeUid: string;
    @Input() dragOptions: TreeDragAndDropOptions<T>;
    @Input() removeDefaultTemplate: string;
    @Input() showAsList: boolean;

    @Output() onTreeUpdated: EventEmitter<ITreeNodeItem<T>[]> = new EventEmitter<
        ITreeNodeItem<T>[]
    >();

    public isDropDisabled: boolean;

    constructor(
        private treeService: TreeService<T, D>,
        private treeDragAndDropService: TreeDragAndDropService<T, D>
    ) {}

    ngOnInit(): void {
        this.setNodeActions();

        this.treeService.init(this.nodeUri, this.rootNodeUid);
        this.treeDragAndDropService.init(this.dragOptions);

        this.fetchData(this.treeService.root);

        this.treeService
            .onTreeUpdated()
            .subscribe(() => this.onTreeUpdated.emit(this.treeService.root.nodes));
    }

    public fetchData(nodeData: ITreeNodeItem<T>): Promise<ITreeNodeItem<T>[]> {
        return this.treeService.fetchChildren(nodeData);
    }

    /**
     * Return a boolean to determine if the node is expandable or not by checking if a given node has children.
     */
    public hasChildren(node: ITreeNodeItem<T>): boolean {
        return node.hasChildren;
    }

    /**
     * Causes all the nodes of the tree to collapse.
     *
     * It does not affect their "initiated" status though.
     */
    public collapseAll(): void {
        this.treeService.collapseAll();
    }

    /**
     * Causes all the nodes of the tree to expand.
     * It does not affect their "initiated" status though.
     */
    public expandAll(): void {
        this.treeService.expandAll();
    }

    /**
     * Will remove node referenced by the parameter.
     *
     * The child is added only if [removeNode]{@link TreeService#removeNode} is successful.
     */
    public remove(node: ITreeNodeItem<T>): void {
        this.treeService.removeNode(node);
    }

    /**
     * Will add a new sibling to the node referenced by the parameter.
     *
     * The child is added only if [saveNode]{@link TreeService#saveNode} is successful.
     */
    public newSibling(node: ITreeNodeItem<T>): void {
        this.treeService.newSibling(node);
    }

    /**
     * Will refresh a node, causing it to expand after fetch if it was expanded before.
     */
    public refresh(node: ITreeNodeItem<T>): Promise<ITreeNodeItem<T>[]> {
        node.setInitiated(false);

        return this.treeService.fetchChildren(node);
    }
    /**
     * Will refresh a node, causing it to expand after fetch if it was expanded before.
     */
    public refreshParent(node: ITreeNodeItem<T>): void {
        this.refresh(node.parent);
    }

    /**
     * Will add a new child to the node referenced by the parameter.
     *
     * The child is added only if [saveNode]{@link TreeService#saveNode} is successful.
     */
    // eslint-disable-next-line @typescript-eslint/require-await
    async newChild(node?: ITreeNodeItem<T>): Promise<void> {
        this.treeService.newChild(node);
    }

    /**
     * Will fetch from the existing tree the node whose identifier is the given nodeUid
     */
    getNodeById(nodeUid: string, nodeArray?: ITreeNodeItem<T>[]): ITreeNodeItem<T> {
        return this.treeService.getNodeById(nodeUid, nodeArray);
    }

    public get dragEnabled(): boolean {
        return this.treeDragAndDropService.isDragEnabled;
    }

    private setNodeActions(): void {
        Object.keys(this.nodeActions).forEach((functionName: Extract<keyof this, string>) => {
            this[functionName] = this.nodeActions[functionName].bind(this, this.treeService);
            this.nodeActions[functionName] = this.nodeActions[functionName].bind(
                this,
                this.treeService
            );
        });
    }
}
