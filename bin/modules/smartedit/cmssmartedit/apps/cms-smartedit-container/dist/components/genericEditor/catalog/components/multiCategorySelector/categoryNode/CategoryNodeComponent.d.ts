import { IDropdownMenuItem, ITreeNodeItem, NavigationNode, NavigationNodeItem, NavigationNodeItemDTO, TreeComponent } from 'smarteditcommons';
export declare class CategoryNodeComponent {
    node: ITreeNodeItem<NavigationNode>;
    private parent;
    dropdownItems: IDropdownMenuItem[];
    constructor(node: ITreeNodeItem<NavigationNode>, parent: TreeComponent<NavigationNodeItem, NavigationNodeItemDTO>);
}
