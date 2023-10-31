/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { IRestService, RestServiceFactory } from '@smart/utils';
import { BehaviorSubject, Observable } from 'rxjs';
import { filter } from 'rxjs/operators';

import { apiUtils } from '../../utils';
import { TreeNestedDataSource } from './TreeNestedDataSource';
import { TreeNodeItemFactory } from './TreeNodeItemFactory';
import { ITreeNodeItem, TreeNodeItemDTO } from './types';

/**
 * A service to manage tree nodes through a REST API.
 */
@Injectable()
export class TreeService<T, D extends TreeNodeItemDTO> {
    public nodesRestService: IRestService<D | D[]>;
    public dataSource: TreeNestedDataSource<ITreeNodeItem<T>> = new TreeNestedDataSource();
    public root: ITreeNodeItem<T>;

    private $onTreeUpdated: BehaviorSubject<boolean> = new BehaviorSubject(false);

    constructor(
        private restServiceFactory: RestServiceFactory,
        private treeNodeItemFactory: TreeNodeItemFactory
    ) {}

    public onTreeUpdated(): Observable<boolean> {
        return this.$onTreeUpdated.pipe(filter((value) => !!value));
    }

    /**
     * Initializes the REST service and sets root node.
     *
     * @param nodeUri URI passed to {@link TreeComponent}.
     * @param rootNodeUid root uid passed to {@link TreeComponent}.
     */
    init(nodeUri: string, rootNodeUid: string): void {
        if (nodeUri) {
            this.nodesRestService = this.restServiceFactory.get(nodeUri);
        }

        this.setRoot(rootNodeUid);
    }

    /**
     * Updates the position of the node within the tree.
     *
     * @param node Node to be rearranged.
     * @param position New position of node.
     */
    public rearrange(node: ITreeNodeItem<T>, parent: ITreeNodeItem<T>, position: number): void {
        const siblings: ITreeNodeItem<T>[] = parent.nodes.filter(
            (_node: ITreeNodeItem<T>) => _node.uid !== node.uid
        );
        const rearranged: ITreeNodeItem<T>[] = [
            ...siblings.slice(0, position),
            node,
            ...siblings.slice(position, siblings.length)
        ];

        node.parent.removeNode(node);
        parent.removeAllNodes().addNodes(rearranged);
        this.update();
    }

    /**
     * Fetches the node children and updates the tree.
     */
    async fetchChildren(_parent?: ITreeNodeItem<T>): Promise<ITreeNodeItem<T>[]> {
        const parent = _parent || this.root;

        if (parent.initiated) {
            this.update();

            return Promise.resolve(parent.nodes);
        } else {
            const response = await this.nodesRestService.get({ parentUid: parent.uid });
            const children = (apiUtils.getDataFromResponse(response) || []).map((dto: D) =>
                this.treeNodeItemFactory.get<T, D>(dto)
            );

            parent.removeAllNodes().addNodes(children).setInitiated(true);

            this.update();

            return children;
        }
    }

    /**
     * Toggles the passed node and fetches children
     *
     * @param node Node to be rearranged.
     */
    async toggle(node: ITreeNodeItem<T>): Promise<ITreeNodeItem<T>[]> {
        node.toggle();

        return this.fetchChildren(node);
    }

    /**
     * Adds a new child to passed node.
     *
     * @param node Node to be rearranged.
     */
    async newChild(node?: ITreeNodeItem<T>): Promise<void> {
        const nodeData = node || this.root;
        const response: D = await this.saveNode(nodeData);

        if (!nodeData.isExpanded) {
            this.toggle(nodeData);
        } else {
            const elm = nodeData.nodes.find(
                (_node: ITreeNodeItem<T>) => _node.uid === response.uid
            );

            if (!elm) {
                nodeData.addNode(this.treeNodeItemFactory.get<T, D>(response));
                this.update();
            }
        }
    }

    /**
     * Adds new sibling to passed node.
     *
     * @param node Node to be rearranged.
     */
    async newSibling(node: ITreeNodeItem<T>): Promise<void> {
        const response: D = await this.saveNode(node.parent);

        node.parent.addNode(this.treeNodeItemFactory.get<T, D>(response));

        this.update();
    }

    /**
     * Removes passed node.
     *
     * @param node Node to be rearranged.
     */
    async removeNode(node: ITreeNodeItem<T>): Promise<void> {
        await this.nodesRestService.remove({ identifier: node.uid });

        node.parent.removeNode(node);

        this.update();
    }

    /**
     * Updates the data source from where the nodes are retrieved
     */
    public update(): void {
        this.dataSource.set(null);
        this.dataSource.set(this.root.nodes);
        this.$onTreeUpdated.next(true);
    }

    /**
     * Expands all nodes from the root node
     */
    public expandAll(): void {
        this.root.expandAll();
        this.update();
    }

    /**
     * Collapses all nodes from the root node
     */
    public collapseAll(): void {
        this.root.collapseAll();
        this.update();
    }

    public getNodePositionById(nodeUid: string): number {
        return this.getNodeById(nodeUid).position;
    }

    public getNodeById(nodeUid: string, nodeArray?: ITreeNodeItem<T>[]): ITreeNodeItem<T> {
        if (nodeUid === this.root.uid) {
            return this.root;
        }

        const nodes = nodeArray || this.root.nodes;

        // eslint-disable-next-line @typescript-eslint/no-for-in-array
        for (const node of nodes) {
            if (node.uid === nodeUid) {
                return node;
            }

            if (node.hasChildren) {
                node.nodes = node.nodes || [];
                return this.getNodeById(nodeUid, node.nodes);
            }
        }

        return null;
    }
    private async saveNode(_parent: ITreeNodeItem<T>): Promise<D> {
        const response: D = await this.nodesRestService.save({
            parentUid: _parent.uid,
            name: (_parent.name ? _parent.name : _parent.uid) + _parent.nodes.length
        });

        return response;
    }

    private setRoot(uid: string): void {
        this.root = this.treeNodeItemFactory.get({ uid, name: 'root', level: 0 });
    }
}
