/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ChangeDetectionStrategy, Component, forwardRef, Inject } from '@angular/core';
import {
    IDropdownMenuItem,
    ITreeNodeItem,
    NavigationNode,
    NavigationNodeItem,
    NavigationNodeItemDTO,
    TreeComponent,
    TREE_NODE
} from 'smarteditcommons';
import { NavigationTreeActions } from '../../../../../navigation/navigationEditor/types';

@Component({
    selector: 'se-category-node',
    templateUrl: './CategoryNodeComponent.html',
    styleUrls: ['./CategoryNodeComponent.scss', '../../ProductRow.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class CategoryNodeComponent {
    public dropdownItems: IDropdownMenuItem[];

    constructor(
        @Inject(TREE_NODE) public node: ITreeNodeItem<NavigationNode>,
        @Inject(forwardRef(() => TreeComponent))
        private parent: TreeComponent<NavigationNodeItem, NavigationNodeItemDTO>
    ) {
        const { getDropdownItems } = this.parent.nodeActions as unknown as NavigationTreeActions;

        this.dropdownItems = getDropdownItems();
    }
}
