import { ITreeNodeItem, NavigationNode, NavigationNodeItem, TreeNodeActions } from 'smarteditcommons';
export interface NavigationTreeActions extends TreeNodeActions<ITreeNodeItem<NavigationNodeItem>[]> {
    isReadOnly: () => boolean;
    hasChildren: (...args: any[]) => boolean;
    performMove: (...args: any[]) => Promise<void>;
    addTopLevelNode: (...args: any[]) => Promise<void>;
    dragAndDrop: (...args: any[]) => void;
    getEntryString: (...args: any[]) => string;
    getEntryTooltipString: (...args: any[]) => string;
    refreshNode: (...args: any[]) => Promise<ITreeNodeItem<NavigationNodeItem>[]>;
    refreshParentNode: (...args: any[]) => Promise<ITreeNodeItem<NavigationNodeItem>[]>;
    editNavigationNode: (...args: any[]) => Promise<ITreeNodeItem<NavigationNodeItem>[]>;
    addNewChild: (...args: any[]) => Promise<ITreeNodeItem<NavigationNodeItem>[]>;
    addNewSibling: (...args: any[]) => Promise<ITreeNodeItem<NavigationNodeItem>[]>;
    _findNodeById: (...args: any[]) => NavigationNode;
    _expandIfNeeded: (...args: any[]) => Promise<void | ITreeNodeItem<NavigationNodeItem>[]>;
}
