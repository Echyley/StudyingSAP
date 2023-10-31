/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/* eslint-disable max-classes-per-file */
/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, EventEmitter, Inject, Input, Output, Type } from '@angular/core';
import { isEqual } from 'lodash';
import { IDropdownMenuItem } from '../dropdown/dropdownMenu';
import {
    ITreeNodeItem,
    TreeDragAndDropEvent,
    TreeDragAndDropOptions,
    TreeNodeActions,
    TreeService,
    TREE_NODE
} from '../treeModule';
import { EditableListNodeItem, EditableListNodeItemDTO } from './EditableListNodeItem';

/**
 * The EditableListComponent component allows displaying a list of items. The list can be managed dynamically, by
 * adding, removing, and re-ordering it.
 *
 * ### Example
 *
 *      <se-editable-list
 *          [itemComponent]="myComponent"
 *          [(items)]="items"
 *          [onChange]="onChange"
 *          [editable]="editable"
 *          [(refresh)]="refresh"
 *      ></se-editable-list>
 */
@Component({
    selector: 'se-editable-list',
    templateUrl: './EditableListComponent.html'
})
export class EditableListComponent {
    @Input() public refresh: () => void;
    @Input() public items: EditableListNodeItemDTO[];
    @Input() public onChange: () => void;
    @Input() public itemTemplateUrl: string;
    @Input() public itemComponent: Type<any>;
    @Input() public editable: boolean;
    @Input() public id: string;

    @Output() refreshChange: EventEmitter<() => void> = new EventEmitter();
    @Output() itemsChange: EventEmitter<ITreeNodeItem<EditableListNodeItem>[]> = new EventEmitter();

    public dragOptions: TreeDragAndDropOptions<EditableListNodeItem> =
        {} as TreeDragAndDropOptions<EditableListNodeItem>;
    public actions: TreeNodeActions<EditableListNodeItem>;
    public rootId: string;

    private itemsOld: ITreeNodeItem<EditableListNodeItem>[] = [];
    private _enableDragAndDrop: () => void;

    ngOnInit(): void {
        this._enableDragAndDrop = (): void => {
            this.dragOptions.allowDropCallback = (
                event: TreeDragAndDropEvent<EditableListNodeItem>
            ): boolean =>
                // Just allow dropping elements of the same list.
                event.sourceNode.parentUid === this.rootId;
        };

        this.actions = this.getTreeActions();
        this.refreshChange.emit(() => this.actions.refreshList());

        if (!this.itemTemplateUrl && !this.itemComponent) {
            this.itemComponent = EditableListDefaultItem;
        }

        this.rootId = 'root' + this.id;

        if (this.editable === undefined) {
            this.editable = true;
        }

        if (this.editable === true) {
            this._enableDragAndDrop();
        }
    }

    public handleTreeUpdated(items: ITreeNodeItem<EditableListNodeItem>[]): void {
        // Perform an update only when the items has changed.
        if (this.hasItemsChanged(this.itemsOld, items)) {
            this.itemsOld = items;
            this.itemsChange.emit(items);
            this.actions.performUpdate();
        }
    }

    public getDropdownItems(): IDropdownMenuItem[] {
        return [
            {
                key: 'se.ydropdownmenu.remove',
                callback: (handle: EditableListNodeItem): void => {
                    this.actions.removeItem(handle);
                }
            },
            {
                key: 'se.ydropdownmenu.move.up',
                condition: (handle: EditableListNodeItem): boolean =>
                    this.actions.isMoveUpAllowed(handle),
                callback: (handle: EditableListNodeItem): void => {
                    this.actions.moveUp(handle);
                }
            },
            {
                key: 'se.ydropdownmenu.move.down',
                condition: (handle: EditableListNodeItem): boolean =>
                    this.actions.isMoveDownAllowed(handle),
                callback: (handle: EditableListNodeItem): void => {
                    this.actions.moveDown(handle);
                }
            }
        ];
    }

    /**
     * Since items to compare are objects, instead of deep checking we compare by uid to improve performance.
     */
    private hasItemsChanged(
        oldItems: ITreeNodeItem<EditableListNodeItem>[],
        newItems: ITreeNodeItem<EditableListNodeItem>[]
    ): boolean {
        const oldUids = oldItems.map(({ uid }) => uid);
        const newUids = newItems.map(({ uid }) => uid);
        return !isEqual(oldUids, newUids);
    }

    private getTreeActions(): TreeNodeActions<EditableListNodeItem> {
        const items = this.getDropdownItems();

        return {
            fetchData: (
                treeService: TreeService<EditableListNodeItem, EditableListNodeItemDTO>,
                nodeData: EditableListNodeItem
            ): Promise<EditableListNodeItem> => {
                const nodeItems = this.items.map(
                    (dto: EditableListNodeItemDTO) => new EditableListNodeItem(dto)
                );

                nodeItems.forEach((item: EditableListNodeItem) => {
                    if (item.id && !item.uid) {
                        item.uid = item.id;
                    }

                    item.setParent(nodeData);
                });

                nodeData.removeAllNodes().addNodes(nodeItems);

                treeService.update();

                return Promise.resolve(nodeData);
            },
            getDropdownItems: (): IDropdownMenuItem[] => items,
            removeItem(
                treeService: TreeService<EditableListNodeItem, EditableListNodeItemDTO>,
                nodeData: EditableListNodeItem
            ): void {
                nodeData.parent.removeNode(nodeData);
                treeService.update();
            },
            moveUp(
                treeService: TreeService<EditableListNodeItem, EditableListNodeItemDTO>,
                nodeData: EditableListNodeItem
            ): void {
                treeService.rearrange(
                    nodeData as ITreeNodeItem<EditableListNodeItem>,
                    treeService.root,
                    nodeData.position - 1
                );
            },
            moveDown(
                treeService: TreeService<EditableListNodeItem, EditableListNodeItemDTO>,
                nodeData: EditableListNodeItem
            ): void {
                treeService.rearrange(
                    nodeData as ITreeNodeItem<EditableListNodeItem>,
                    treeService.root,
                    nodeData.position + 1
                );
            },

            isMoveUpAllowed(
                treeService: TreeService<EditableListNodeItem, EditableListNodeItemDTO>,
                nodeData: EditableListNodeItem
            ): boolean {
                return nodeData.position > 0;
            },

            isMoveDownAllowed(
                treeService: TreeService<EditableListNodeItem, EditableListNodeItemDTO>,
                nodeData: EditableListNodeItem
            ): boolean {
                return treeService.root.nodes.length !== nodeData.position + 1;
            },

            performUpdate: (
                treeService: TreeService<EditableListNodeItem, EditableListNodeItemDTO>
            ): void => {
                if (this.onChange) {
                    this.onChange();
                }
            },

            refreshList(
                treeService: TreeService<EditableListNodeItem, EditableListNodeItemDTO>
            ): void {
                this.fetchData(treeService.root);
            }
        };
    }
}

@Component({
    selector: 'se-editable-list-default-item',
    template: `
        <div>
            <span>{{ node.uid }}</span>
            <se-dropdown-menu
                *ngIf="parent.editable"
                [dropdownItems]="parent.getDropdownItems()"
                [selectedItem]="node"
                class="pull-right se-tree-node__actions--more-menu"
                title="{{ 'se.contextmenu.title.more' | translate }}"></se-dropdown-menu>
        </div>
    `
})
export class EditableListDefaultItem {
    constructor(
        public parent: EditableListComponent,
        @Inject(TREE_NODE) public node: ITreeNodeItem<EditableListNodeItem>
    ) {}
}
