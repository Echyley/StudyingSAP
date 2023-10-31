import { OnInit } from '@angular/core';
import { IDropdownMenuItem, ITreeNodeItem, NavigationNode, NavigationNodeItem, NavigationNodeItemDTO, TreeComponent } from 'smarteditcommons';
export declare class NavigationNodeComponent implements OnInit {
    private parent;
    node: ITreeNodeItem<NavigationNode>;
    dropdownItems: IDropdownMenuItem[];
    isReadOnly: boolean;
    entryString: string;
    constructor(parent: TreeComponent<NavigationNodeItem, NavigationNodeItemDTO>, node: ITreeNodeItem<NavigationNode>);
    ngOnInit(): void;
}
