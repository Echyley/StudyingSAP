/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { NavigationNodeItem, TreeNodeItem } from './TreeNodeItem';
import { ITreeNodeItem, NavigationNodeItemDTO, TreeNodeItemDTO } from './types';

enum TreeNodeItemType {
    CMSNavigationNode = 'CMSNavigationNode'
}

/**
 * A service used to generate instance of node item to be consumed by {@link TreeComponent}.
 */
export class TreeNodeItemFactory {
    /**
     * @param dto DTO based on which the class is returned.
     *
     * Returns a class depending on itemtype.
     */
    get<T, D extends TreeNodeItemDTO>(dto: D): ITreeNodeItem<T> {
        switch (dto.itemType) {
            case TreeNodeItemType.CMSNavigationNode:
                return new NavigationNodeItem(dto as unknown as NavigationNodeItemDTO);
            default:
                return new TreeNodeItem(dto);
        }
    }
}
