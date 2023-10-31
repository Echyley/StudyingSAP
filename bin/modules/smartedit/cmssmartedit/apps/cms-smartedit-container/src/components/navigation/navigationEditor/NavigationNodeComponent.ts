/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {
    ChangeDetectionStrategy,
    Component,
    forwardRef,
    Inject,
    OnInit,
    ViewEncapsulation
} from '@angular/core';
import {
    IDropdownMenuItem,
    ITreeNodeItem,
    NavigationNode,
    NavigationNodeItem,
    NavigationNodeItemDTO,
    TreeComponent,
    TREE_NODE
} from 'smarteditcommons';
import { NavigationTreeActions } from './types';

@Component({
    selector: 'se-navigation-node',
    templateUrl: './NavigationNodeComponent.html',
    styleUrls: ['./NavigationNodeComponent.scss'],
    encapsulation: ViewEncapsulation.None,
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class NavigationNodeComponent implements OnInit {
    dropdownItems: IDropdownMenuItem[] = [];
    isReadOnly: boolean;
    entryString: string;

    constructor(
        @Inject(forwardRef(() => TreeComponent))
        private parent: TreeComponent<NavigationNodeItem, NavigationNodeItemDTO>,
        @Inject(TREE_NODE) public node: ITreeNodeItem<NavigationNode>
    ) {}

    ngOnInit(): void {
        // For some reason, when accessing `parent` properties (e.g: {{ parent.getDropdownItems() }})
        // directly from template, it falls into some kind of infinite loop.
        // That's why it's implemented the way below
        const { getDropdownItems, isReadOnly, getEntryString } = this.parent
            .nodeActions as unknown as NavigationTreeActions;

        this.dropdownItems = getDropdownItems();
        this.isReadOnly = isReadOnly();
        this.entryString = getEntryString(this.node);
    }
}
