/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, OnInit, Inject, forwardRef } from '@angular/core';
import {
    TreeComponent,
    TREE_NODE,
    ITreeNodeItem,
    NavigationNode,
    NavigationNodeItem,
    NavigationNodeItemDTO
} from 'smarteditcommons';

@Component({
    selector: 'se-navigation-node-picker-render',
    templateUrl: './NavigationNodePickerRenderComponent.html'
})
export class NavigationNodePickerRenderComponent implements OnInit {
    public pick: (node: ITreeNodeItem<NavigationNode>) => Promise<void>;
    public isEditable: () => boolean;

    constructor(
        @Inject(forwardRef(() => TreeComponent))
        private parent: TreeComponent<NavigationNodeItem, NavigationNodeItemDTO>,
        @Inject(TREE_NODE)
        public node: ITreeNodeItem<NavigationNode>
    ) {}

    ngOnInit(): void {
        ({ pick: this.pick, isEditable: this.isEditable } = this.parent.nodeActions as any);
    }
}
