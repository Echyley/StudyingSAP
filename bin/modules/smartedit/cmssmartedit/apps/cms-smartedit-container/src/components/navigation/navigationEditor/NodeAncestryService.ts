/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { reverse, cloneDeep } from 'lodash';
import { TreeNode, TreeNodeWithLevel } from 'smarteditcommons';

/**
 * Used for converting Tree Nodes that are displayed in Breadcrumb.
 */
@Injectable()
export class NodeAncestryService {
    public buildOrderedListOfAncestors(nodes: TreeNode[], uid: string): TreeNodeWithLevel[] {
        const ancestry = reverse<TreeNode[]>(this.fetchAncestors(nodes, uid));
        let level = -1;
        return ancestry.map((node) => {
            const nextLevel = ++level;
            return {
                ...cloneDeep(node),
                level: nextLevel,
                formattedLevel:
                    nextLevel === 0
                        ? 'se.cms.navigationcomponent.management.node.level.root'
                        : 'se.cms.navigationcomponent.management.node.level.non.root'
            };
        });
    }

    private fetchAncestors(nodes: TreeNode[], uid: string): TreeNode[] {
        const parent = nodes.find((element) => element.uid === uid);
        if (!parent) {
            return [];
        }
        return [parent].concat(this.fetchAncestors(nodes, parent.parentUid));
    }
}
