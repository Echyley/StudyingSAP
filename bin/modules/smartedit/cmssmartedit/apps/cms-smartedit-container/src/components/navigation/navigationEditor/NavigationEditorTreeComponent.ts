/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {
    ChangeDetectionStrategy,
    Component,
    Input,
    OnInit,
    ViewEncapsulation
} from '@angular/core';
import { NAVIGATION_MANAGEMENT_RESOURCE_URI, NAVIGATION_NODE_ROOT_NODE_UID } from 'cmscommons';
import {
    IUriContext,
    NavigationNodeItem,
    TreeDragAndDropEvent,
    TreeDragAndDropOptions,
    URIBuilder
} from 'smarteditcommons';
import { NavigationEditorTreeActions } from './NavigationEditorTreeActionsService';
import { NavigationEditorTreeDragOptions } from './NavigationEditorTreeDragOptionsService';
import { NavigationNodeComponent } from './NavigationNodeComponent';
import { NavigationTreeActions } from './types';

/**
 * Navigation Editor directive used to display navigation editor tree
 * @param uriContext necessary to perform operations
 * @param readOnly when true, no CRUD facility shows on the editor. OPTIONAL, default false.
 * @param rootNodeUid the uid of the node to be taken as root, OPTIONAL, default "root"
 */
@Component({
    selector: 'se-navigation-editor-tree',
    templateUrl: './NavigationEditorTreeComponent.html',
    styleUrls: ['./NavigationEditorTreeComponent.scss'],
    providers: [NavigationEditorTreeDragOptions, NavigationEditorTreeActions],
    changeDetection: ChangeDetectionStrategy.OnPush,
    // This component is also used inside modal and it requires some extra styles while in modal
    encapsulation: ViewEncapsulation.None
})
export class NavigationEditorTreeComponent implements OnInit {
    @Input() uriContext: IUriContext;
    @Input() readOnly: boolean;
    @Input() rootNodeUid: string;

    public nodeComponent = NavigationNodeComponent;
    public dragOptions: TreeDragAndDropOptions<NavigationNodeItem>;
    public actions: NavigationTreeActions;
    public nodeURI: string;

    constructor(
        private dragOptionsService: NavigationEditorTreeDragOptions,
        private actionsService: NavigationEditorTreeActions
    ) {}

    ngOnInit(): void {
        this.nodeURI = new URIBuilder(NAVIGATION_MANAGEMENT_RESOURCE_URI)
            .replaceParams(this.uriContext)
            .build();
        this.rootNodeUid = this.rootNodeUid || NAVIGATION_NODE_ROOT_NODE_UID;

        this.actionsService.setup(this.readOnly, this.rootNodeUid, this.uriContext);
        this.actions = this.actionsService.getActions();

        if (!this.readOnly) {
            this.dragOptionsService.setup((event: TreeDragAndDropEvent<NavigationNodeItem>) => {
                this.actions.dragAndDrop(event);
            });
            this.dragOptions = this.dragOptionsService.getDragOptions();
        }
    }
}
