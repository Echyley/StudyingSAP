import { EventEmitter, Type } from '@angular/core';
import { IDropdownMenuItem } from '../dropdown/dropdownMenu';
import { ITreeNodeItem, TreeDragAndDropOptions, TreeNodeActions } from '../treeModule';
import { EditableListNodeItem, EditableListNodeItemDTO } from './EditableListNodeItem';
export declare class EditableListComponent {
    refresh: () => void;
    items: EditableListNodeItemDTO[];
    onChange: () => void;
    itemTemplateUrl: string;
    itemComponent: Type<any>;
    editable: boolean;
    id: string;
    refreshChange: EventEmitter<() => void>;
    itemsChange: EventEmitter<ITreeNodeItem<EditableListNodeItem>[]>;
    dragOptions: TreeDragAndDropOptions<EditableListNodeItem>;
    actions: TreeNodeActions<EditableListNodeItem>;
    rootId: string;
    private itemsOld;
    private _enableDragAndDrop;
    ngOnInit(): void;
    handleTreeUpdated(items: ITreeNodeItem<EditableListNodeItem>[]): void;
    getDropdownItems(): IDropdownMenuItem[];
    private hasItemsChanged;
    private getTreeActions;
}
export declare class EditableListDefaultItem {
    parent: EditableListComponent;
    node: ITreeNodeItem<EditableListNodeItem>;
    constructor(parent: EditableListComponent, node: ITreeNodeItem<EditableListNodeItem>);
}
