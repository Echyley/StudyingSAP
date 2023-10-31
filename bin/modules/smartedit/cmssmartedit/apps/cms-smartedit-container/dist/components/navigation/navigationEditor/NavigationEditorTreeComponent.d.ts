import { OnInit } from '@angular/core';
import { IUriContext, NavigationNodeItem, TreeDragAndDropOptions } from 'smarteditcommons';
import { NavigationEditorTreeActions } from './NavigationEditorTreeActionsService';
import { NavigationEditorTreeDragOptions } from './NavigationEditorTreeDragOptionsService';
import { NavigationNodeComponent } from './NavigationNodeComponent';
import { NavigationTreeActions } from './types';
export declare class NavigationEditorTreeComponent implements OnInit {
    private dragOptionsService;
    private actionsService;
    uriContext: IUriContext;
    readOnly: boolean;
    rootNodeUid: string;
    nodeComponent: typeof NavigationNodeComponent;
    dragOptions: TreeDragAndDropOptions<NavigationNodeItem>;
    actions: NavigationTreeActions;
    nodeURI: string;
    constructor(dragOptionsService: NavigationEditorTreeDragOptions, actionsService: NavigationEditorTreeActions);
    ngOnInit(): void;
}
