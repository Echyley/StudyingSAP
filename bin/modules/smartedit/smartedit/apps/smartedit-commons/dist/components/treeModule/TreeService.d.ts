import { IRestService, RestServiceFactory } from '@smart/utils';
import { Observable } from 'rxjs';
import { TreeNestedDataSource } from './TreeNestedDataSource';
import { TreeNodeItemFactory } from './TreeNodeItemFactory';
import { ITreeNodeItem, TreeNodeItemDTO } from './types';
/**
 * A service to manage tree nodes through a REST API.
 */
export declare class TreeService<T, D extends TreeNodeItemDTO> {
    private restServiceFactory;
    private treeNodeItemFactory;
    nodesRestService: IRestService<D | D[]>;
    dataSource: TreeNestedDataSource<ITreeNodeItem<T>>;
    root: ITreeNodeItem<T>;
    private $onTreeUpdated;
    constructor(restServiceFactory: RestServiceFactory, treeNodeItemFactory: TreeNodeItemFactory);
    onTreeUpdated(): Observable<boolean>;
    /**
     * Initializes the REST service and sets root node.
     *
     * @param nodeUri URI passed to {@link TreeComponent}.
     * @param rootNodeUid root uid passed to {@link TreeComponent}.
     */
    init(nodeUri: string, rootNodeUid: string): void;
    /**
     * Updates the position of the node within the tree.
     *
     * @param node Node to be rearranged.
     * @param position New position of node.
     */
    rearrange(node: ITreeNodeItem<T>, parent: ITreeNodeItem<T>, position: number): void;
    /**
     * Fetches the node children and updates the tree.
     */
    fetchChildren(_parent?: ITreeNodeItem<T>): Promise<ITreeNodeItem<T>[]>;
    /**
     * Toggles the passed node and fetches children
     *
     * @param node Node to be rearranged.
     */
    toggle(node: ITreeNodeItem<T>): Promise<ITreeNodeItem<T>[]>;
    /**
     * Adds a new child to passed node.
     *
     * @param node Node to be rearranged.
     */
    newChild(node?: ITreeNodeItem<T>): Promise<void>;
    /**
     * Adds new sibling to passed node.
     *
     * @param node Node to be rearranged.
     */
    newSibling(node: ITreeNodeItem<T>): Promise<void>;
    /**
     * Removes passed node.
     *
     * @param node Node to be rearranged.
     */
    removeNode(node: ITreeNodeItem<T>): Promise<void>;
    /**
     * Updates the data source from where the nodes are retrieved
     */
    update(): void;
    /**
     * Expands all nodes from the root node
     */
    expandAll(): void;
    /**
     * Collapses all nodes from the root node
     */
    collapseAll(): void;
    getNodePositionById(nodeUid: string): number;
    getNodeById(nodeUid: string, nodeArray?: ITreeNodeItem<T>[]): ITreeNodeItem<T>;
    private saveNode;
    private setRoot;
}
