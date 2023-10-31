/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { NAVIGATION_NODE_ROOT_NODE_UID, NAVIGATION_NODE_TYPECODE } from 'cmscommons';
import { assign } from 'lodash';
import {
    ConfirmationModalConfig,
    IConfirmationModalService,
    IDropdownMenuItem,
    InvalidateCache,
    ITreeNodeItem,
    IUriContext,
    LogService,
    NavigationNode,
    NavigationNodeEntry,
    NavigationNodeItem,
    NavigationNodeItemDTO,
    TreeDragAndDropEvent,
    TreeService,
    CmsitemsRestService,
    cmsitemsEvictionTag,
    IUserTrackingService,
    USER_TRACKING_FUNCTIONALITY
} from 'smarteditcommons';
import { NavigationNodeEditorModalService } from '../navigationNodeEditor/NavigationNodeEditorModalService';
import { NavigationNodeCMSItem } from '../types';
import { NavigationEditorNodeService } from './NavigationEditorNodeService';
import { NavigationTreeActions } from './types';

// TODO: unit tests
@Injectable()
export class NavigationEditorTreeActions {
    private static readonly READY_ONLY_ERROR_I18N = 'navigation.in.readonly.mode';

    private readOnly: boolean;
    private rootNodeUid: string;
    private uriContext: IUriContext;

    constructor(
        private logService: LogService,
        private cmsitemsRestService: CmsitemsRestService,
        private confirmationModalService: IConfirmationModalService,
        private navigationEditorNodeService: NavigationEditorNodeService,
        private navigationNodeEditorModalService: NavigationNodeEditorModalService,
        private userTrackingService: IUserTrackingService
    ) {}

    /**
     * Sets fields required for this service to work properly
     */
    public setup(readOnly: boolean, rootNodeUid: string, uriContext: IUriContext): void {
        this.readOnly = readOnly;
        this.rootNodeUid = rootNodeUid;
        this.uriContext = uriContext;
    }

    /**
     * Exposes methods of this service to a literal object.
     *
     * This literal object is used by `TreeComponent#setNodeActions` (smarteditcommons)
     * It sets new context and "inject" treeService param for all methods using `.bind`
     *
     * It is done this way, so TreeComponent can easily iterate over the methods, if we would passed instance of this class it wouldn't be easy to iterate over these methods
     */
    public getActions(): NavigationTreeActions {
        return {
            isReadOnly: (): boolean => this.isReadOnly(),
            hasChildren: (
                treeService: TreeService<NavigationNodeItem, NavigationNodeItemDTO>,
                nodeData: NavigationNodeItem
            ): boolean => this.hasChildren(treeService, nodeData),
            fetchData: (
                treeService: TreeService<NavigationNodeItem, NavigationNodeItemDTO>,
                nodeData: NavigationNodeItem
            ): Promise<ITreeNodeItem<NavigationNodeItem>[]> =>
                this.fetchData(treeService, nodeData),
            removeItem: (
                treeService: TreeService<NavigationNodeItem, NavigationNodeItemDTO>,
                nodeData: NavigationNodeItem
            ): Promise<void> => this.removeItem(treeService, nodeData),
            performMove: (
                treeService: TreeService<NavigationNodeItem, NavigationNodeItemDTO>,
                nodeData: NavigationNodeItem,
                refreshNodeItself?: boolean
            ): Promise<void> => this.performMove(treeService, nodeData, refreshNodeItself),
            dragAndDrop: (
                treeService: TreeService<NavigationNodeItem, NavigationNodeItemDTO>,
                event: TreeDragAndDropEvent<NavigationNodeItem>
            ): Promise<void> => this.dragAndDrop(treeService, event),
            moveUp: (
                treeService: TreeService<NavigationNodeItem, NavigationNodeItemDTO>,
                nodeData: NavigationNodeItem
            ): Promise<void> => this.moveUp(treeService, nodeData),
            moveDown: (
                treeService: TreeService<NavigationNodeItem, NavigationNodeItemDTO>,
                nodeData: NavigationNodeItem
            ): Promise<void> => this.moveDown(treeService, nodeData),
            isMoveUpAllowed: (
                treeService: TreeService<NavigationNodeItem, NavigationNodeItemDTO>,
                nodeData: NavigationNodeItem
            ): boolean => this.isMoveUpAllowed(treeService, nodeData),
            isMoveDownAllowed: (
                treeService: TreeService<NavigationNodeItem, NavigationNodeItemDTO>,
                nodeData: NavigationNodeItem
            ): boolean => this.isMoveDownAllowed(treeService, nodeData),
            refreshNode: (
                treeService: TreeService<NavigationNodeItem, NavigationNodeItemDTO>,
                nodeData: NavigationNodeItem
            ): Promise<ITreeNodeItem<NavigationNodeItem>[]> =>
                this.refreshNode(treeService, nodeData),
            refreshParentNode: (
                treeService: TreeService<NavigationNodeItem, NavigationNodeItemDTO>,
                nodeData: NavigationNodeItem
            ): Promise<ITreeNodeItem<NavigationNodeItem>[]> =>
                this.refreshParentNode(treeService, nodeData),
            editNavigationNode: (
                treeService: TreeService<NavigationNodeItem, NavigationNodeItemDTO>,
                nodeData: NavigationNodeItem
            ): Promise<ITreeNodeItem<NavigationNodeItem>[]> =>
                this.editNavigationNode(treeService, nodeData),
            addTopLevelNode: (
                treeService: TreeService<NavigationNodeItem, NavigationNodeItemDTO>,
                nodeData: NavigationNodeItem
            ): Promise<void> => this.addTopLevelNode(treeService, nodeData),
            getEntryString: (
                treeService: TreeService<NavigationNodeItem, NavigationNodeItemDTO>,
                nodeData: NavigationNodeItem
            ): string => this.getEntryString(treeService, nodeData),
            getEntryTooltipString: (
                treeService: TreeService<NavigationNodeItem, NavigationNodeItemDTO>,
                nodeData: NavigationNodeItem
            ): string => this.getEntryTooltipString(treeService, nodeData),
            addNewChild: (
                treeService: TreeService<NavigationNodeItem, NavigationNodeItemDTO>,
                nodeData: NavigationNodeItem
            ): Promise<ITreeNodeItem<NavigationNodeItem>[]> =>
                this.addNewChild(treeService, nodeData),
            addNewSibling: (
                treeService: TreeService<NavigationNodeItem, NavigationNodeItemDTO>,
                nodeData: NavigationNodeItem
            ): Promise<ITreeNodeItem<NavigationNodeItem>[]> =>
                this.addNewSibling(treeService, nodeData),
            getDropdownItems: (
                treeService: TreeService<NavigationNodeItem, NavigationNodeItemDTO>
            ): IDropdownMenuItem[] => this.getDropdownItems(treeService),
            _findNodeById: (
                treeService: TreeService<NavigationNodeItem, NavigationNodeItemDTO>,
                nodeUid: string
            ): NavigationNode => this._findNodeById(treeService, nodeUid),
            _expandIfNeeded: (
                treeService: TreeService<NavigationNodeItem, NavigationNodeItemDTO>,
                nodeData: NavigationNodeItem
            ): Promise<ITreeNodeItem<NavigationNodeItem>[] | void> =>
                this._expandIfNeeded(treeService, nodeData)
        };
    }

    @InvalidateCache(cmsitemsEvictionTag)
    private async getNavigationNodeCMSItemByUid(
        uid: string
    ): Promise<ITreeNodeItem<NavigationNodeItem>> {
        const result = await this.cmsitemsRestService.get<NavigationNodeCMSItem>({
            typeCode: NAVIGATION_NODE_TYPECODE,
            pageSize: 1,
            currentPage: 0,
            itemSearchParams: 'uid:' + uid
        });

        return result.response[0] as unknown as ITreeNodeItem<NavigationNodeItem>;
    }

    private hasNotMoved(
        source: NavigationNode,
        destinationPosition: number,
        destinationParent: NavigationNode
    ): boolean {
        return (
            source.position === destinationPosition && source.parentUid === destinationParent.uid
        );
    }

    private getEntriesCommaSeparated(entries: NavigationNodeEntry[]): string {
        return entries.map((entry) => `${entry.name} (${entry.itemType})`).join(', ');
    }

    /** Actions */
    private isReadOnly(): boolean {
        return this.readOnly;
    }

    private hasChildren(
        _treeService: TreeService<NavigationNodeItem, NavigationNodeItemDTO>,
        nodeData: NavigationNodeItem
    ): boolean {
        return nodeData.hasChildren;
    }

    private async fetchData(
        treeService: TreeService<NavigationNodeItem, NavigationNodeItemDTO>,
        nodeData: ITreeNodeItem<NavigationNodeItem>
    ): Promise<ITreeNodeItem<NavigationNodeItem>[]> {
        if (nodeData.initiated) {
            return nodeData.nodes;
        }

        if (nodeData.uid === NAVIGATION_NODE_ROOT_NODE_UID) {
            nodeData.initiated = false;
            const node = await this.getNavigationNodeCMSItemByUid(this.rootNodeUid);
            assign(nodeData, node);
        }

        nodeData.removeAllNodes();
        return treeService.fetchChildren(nodeData);
    }

    private async removeItem(
        treeService: TreeService<NavigationNodeItem, NavigationNodeItemDTO>,
        node: NavigationNodeItem
    ): Promise<void> {
        if (this.readOnly) {
            throw NavigationEditorTreeActions.READY_ONLY_ERROR_I18N;
        }

        this.userTrackingService.trackingUserAction(
            USER_TRACKING_FUNCTIONALITY.NAVIGATION_MANAGEMENT,
            'Delete'
        );

        let isConfirmed = true;
        try {
            // IMPORTANT NOTE
            // `confirmationModalService.confirm` resolves to null
            // that's why `isConfirmed` is not set here
            await this.confirmationModalService.confirm({
                description: 'se.cms.navigationmanagement.navnode.removenode.confirmation.message',
                title: 'se.cms.navigationmanagement.navnode.removenode.confirmation.title'
            } as ConfirmationModalConfig);
        } catch {
            isConfirmed = false;
        }

        if (isConfirmed) {
            await this.cmsitemsRestService.delete(node.uuid);
            this.refreshParentNode(treeService, node);
        }
    }

    private async performMove(
        treeService: TreeService<NavigationNodeItem, NavigationNodeItemDTO>,
        node: NavigationNodeItem,
        refreshNodeItself?: boolean
    ): Promise<void> {
        if (this.readOnly) {
            throw NavigationEditorTreeActions.READY_ONLY_ERROR_I18N;
        }
        try {
            await this.navigationEditorNodeService.updateNavigationNodePosition(node);

            if (!node) {
                await this.fetchData(treeService, treeService.root);
            } else if (refreshNodeItself) {
                await this.refreshNode(treeService, node);
            } else {
                await this.refreshParentNode(treeService, node);
            }
        } catch (err) {
            this.logService.error(`Error updating node position:\n${err}`);
        }
    }

    private async dragAndDrop(
        treeService: TreeService<NavigationNodeItem, NavigationNodeItemDTO>,
        event: TreeDragAndDropEvent<NavigationNodeItem>
    ): Promise<void> {
        const nodeData = event.sourceNode;
        const destinationNodes = event.destinationNodes;

        const destination = destinationNodes.find((node) => node.uid !== nodeData.uid);

        // this method is still triggered on drop, even if drop is not allowed
        // so its possible that destination does not exist, in which case we return silently
        if (!destination) {
            return;
        }
        const destinationParent = destination.parent;

        if (this.hasNotMoved(nodeData, event.position, destinationParent)) {
            return;
        }

        nodeData.position = event.position;

        nodeData.setParent(destinationParent);

        await this.performMove(treeService, nodeData, true);

        if (event.sourceParentNode.uid !== event.destinationParentNode.uid) {
            this.refreshNode(treeService, event.sourceParentNode);
        }
    }

    private moveUp(
        treeService: TreeService<NavigationNodeItem, NavigationNodeItemDTO>,
        nodeData: NavigationNodeItem
    ): Promise<void> {
        if (this.readOnly) {
            throw NavigationEditorTreeActions.READY_ONLY_ERROR_I18N;
        }

        this.userTrackingService.trackingUserAction(
            USER_TRACKING_FUNCTIONALITY.NAVIGATION_MANAGEMENT,
            'Move Up'
        );

        nodeData.position = nodeData.position - 1;
        return this.performMove(treeService, nodeData);
    }

    private moveDown(
        treeService: TreeService<NavigationNodeItem, NavigationNodeItemDTO>,
        nodeData: NavigationNodeItem
    ): Promise<void> {
        if (this.readOnly) {
            throw NavigationEditorTreeActions.READY_ONLY_ERROR_I18N;
        }

        this.userTrackingService.trackingUserAction(
            USER_TRACKING_FUNCTIONALITY.NAVIGATION_MANAGEMENT,
            'Move Down'
        );
        nodeData.position = nodeData.position + 1;
        return this.performMove(treeService, nodeData);
    }

    private isMoveUpAllowed(
        _treeService: TreeService<NavigationNodeItem, NavigationNodeItemDTO>,
        nodeData: NavigationNodeItem
    ): boolean {
        return nodeData.position !== 0;
    }

    private isMoveDownAllowed(
        _treeService: TreeService<NavigationNodeItem, NavigationNodeItemDTO>,
        nodeData: NavigationNodeItem
    ): boolean {
        nodeData.parent.nodes = nodeData.parent.nodes || [];
        return nodeData.position < nodeData.parent.nodes.length - 1;
    }

    private refreshNode(
        treeService: TreeService<NavigationNodeItem, NavigationNodeItemDTO>,
        nodeData: NavigationNodeItem
    ): Promise<ITreeNodeItem<NavigationNodeItem>[]> {
        nodeData.setInitiated(false);

        return treeService.fetchChildren(nodeData);
    }

    private refreshParentNode(
        treeService: TreeService<NavigationNodeItem, NavigationNodeItemDTO>,
        nodeData: NavigationNodeItem
    ): Promise<ITreeNodeItem<NavigationNodeItem>[]> {
        return this.refreshNode(treeService, nodeData.parent);
    }

    private async editNavigationNode(
        treeService: TreeService<NavigationNodeItem, NavigationNodeItemDTO>,
        nodeData: NavigationNodeItem
    ): Promise<ITreeNodeItem<NavigationNodeItem>[]> {
        this.userTrackingService.trackingUserAction(
            USER_TRACKING_FUNCTIONALITY.NAVIGATION_MANAGEMENT,
            'Edit'
        );

        try {
            await this.navigationNodeEditorModalService.open(
                this.uriContext,
                nodeData.parent,
                nodeData
            );
            await this.refreshNode(treeService, nodeData.parent);

            let targetNode: NavigationNodeItem;

            if (nodeData.parent.uid === NAVIGATION_NODE_ROOT_NODE_UID) {
                targetNode = nodeData;
            } else {
                targetNode = nodeData.parent;
            }

            const refreshedNode = await this.navigationEditorNodeService.getNavigationNode(
                targetNode.uid,
                this.uriContext
            );
            assign(targetNode, refreshedNode);

            if (nodeData.parent.uid === NAVIGATION_NODE_ROOT_NODE_UID) {
                return this.refreshNode(treeService, nodeData);
            }

            return this.refreshParentNode(treeService, nodeData);
        } catch {
            this.logService.warn(
                'navigationNodeEditorModalService closed when editing navigation node'
            );
            return null;
        }
    }

    private async addTopLevelNode(
        treeService: TreeService<NavigationNodeItem, NavigationNodeItemDTO>,
        nodeData: NavigationNodeItem
    ): Promise<void> {
        this.userTrackingService.trackingUserAction(
            USER_TRACKING_FUNCTIONALITY.NAVIGATION_MANAGEMENT,
            'Create Top Level'
        );
        const created = !!(await this.addNewChild(treeService, nodeData));
        if (created) {
            const node = await this.getNavigationNodeCMSItemByUid(this.rootNodeUid);
            await this.fetchData(treeService, node);
        }
    }

    private getEntryString(
        _treeService: TreeService<NavigationNodeItem, NavigationNodeItemDTO>,
        node: NavigationNodeItem
    ): string {
        return this.getEntriesCommaSeparated(node.entries || []);
    }

    private getEntryTooltipString(
        _treeService: TreeService<NavigationNodeItem, NavigationNodeItemDTO>,
        node: NavigationNodeItem
    ): string {
        return [
            '<div>',
            ...(node.entries || []).map(
                (entry: NavigationNodeEntry) => `<div>${entry.name} (${entry.itemType})</div>`
            ),
            '</div>'
        ].join('');
    }

    private async addNewChild(
        treeService: TreeService<NavigationNodeItem, NavigationNodeItemDTO>,
        nodeData: NavigationNodeItem
    ): Promise<ITreeNodeItem<NavigationNodeItem>[]> {
        this.userTrackingService.trackingUserAction(
            USER_TRACKING_FUNCTIONALITY.NAVIGATION_MANAGEMENT,
            'Add a Child'
        );
        const parent = nodeData ? nodeData : this._findNodeById(treeService, this.rootNodeUid);
        await this._expandIfNeeded(treeService, nodeData);
        try {
            await this.navigationNodeEditorModalService.open(this.uriContext, parent);

            return this.refreshNode(treeService, parent as NavigationNodeItem);
        } catch {
            this.logService.warn('navigationNodeEditorModalService closed when adding new child');
            return null;
        }
    }

    private async addNewSibling(
        treeService: TreeService<NavigationNodeItem, NavigationNodeItemDTO>,
        nodeData: NavigationNodeItem
    ): Promise<ITreeNodeItem<NavigationNodeItem>[]> {
        this.userTrackingService.trackingUserAction(
            USER_TRACKING_FUNCTIONALITY.NAVIGATION_MANAGEMENT,
            'Add a Sibling'
        );
        const parent = nodeData.parent;
        try {
            await this.navigationNodeEditorModalService.open(this.uriContext, parent);

            return this.refreshNode(treeService, parent);
        } catch {
            this.logService.warn('navigationNodeEditorModalService closed when adding new sibling');
            return null;
        }
    }

    private getDropdownItems(
        treeService: TreeService<NavigationNodeItem, NavigationNodeItemDTO>
    ): IDropdownMenuItem[] {
        return [
            {
                key: 'se.cms.navigationmanagement.navnode.edit',
                callback: (node: NavigationNodeItem): void => {
                    this.editNavigationNode(treeService, node);
                }
            },
            {
                key: 'se.cms.navigationmanagement.navnode.removenode',
                customCss: 'se-dropdown-item__delete',
                callback: (node: NavigationNodeItem): void => {
                    this.removeItem(treeService, node);
                }
            },
            {
                key: 'se.cms.navigationmanagement.navnode.move.up',
                condition: (node: NavigationNodeItem): boolean =>
                    this.isMoveUpAllowed(treeService, node),
                callback: (node: NavigationNodeItem): void => {
                    this.moveUp(treeService, node);
                }
            },
            {
                key: 'se.cms.navigationmanagement.navnode.move.down',
                condition: (node: NavigationNodeItem): boolean =>
                    this.isMoveDownAllowed(treeService, node),
                callback: (node: NavigationNodeItem): void => {
                    this.moveDown(treeService, node);
                }
            },
            {
                key: 'se.cms.navigationmanagement.navnode.addchild',
                callback: (node: NavigationNodeItem): void => {
                    this.addNewChild(treeService, node);
                }
            },
            {
                key: 'se.cms.navigationmanagement.navnode.addsibling',
                callback: (node: NavigationNodeItem): void => {
                    this.addNewSibling(treeService, node);
                }
            }
        ];
    }

    private _findNodeById(
        treeService: TreeService<NavigationNodeItem, NavigationNodeItemDTO>,
        nodeUid: string
    ): NavigationNode {
        return treeService.getNodeById(nodeUid) as unknown as NavigationNode;
    }

    private _expandIfNeeded(
        treeService: TreeService<NavigationNodeItem, NavigationNodeItemDTO>,
        nodeData: NavigationNodeItem
    ): Promise<ITreeNodeItem<NavigationNodeItem>[] | void> {
        return nodeData && !nodeData.isExpanded ? treeService.toggle(nodeData) : Promise.resolve();
    }
}
