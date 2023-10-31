import { OnInit } from '@angular/core';
import { TreeComponent, ITreeNodeItem, NavigationNode, NavigationNodeItem, NavigationNodeItemDTO } from 'smarteditcommons';
export declare class NavigationNodePickerRenderComponent implements OnInit {
    private parent;
    node: ITreeNodeItem<NavigationNode>;
    pick: (node: ITreeNodeItem<NavigationNode>) => Promise<void>;
    isEditable: () => boolean;
    constructor(parent: TreeComponent<NavigationNodeItem, NavigationNodeItemDTO>, node: ITreeNodeItem<NavigationNode>);
    ngOnInit(): void;
}
