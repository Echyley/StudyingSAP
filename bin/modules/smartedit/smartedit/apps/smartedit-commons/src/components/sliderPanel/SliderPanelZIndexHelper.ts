/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { flatten } from 'lodash';

export class SliderPanelZIndexHelper {
    static BLACKLISTED_NODE_NAMES: Set<string> = new Set(['SCRIPT', 'LINK', 'BASE']);

    /** Retrieve a highest value from array of zIndex integers. */
    public getHighestZIndex(node: JQuery<HTMLElement>): number {
        return Math.max(
            ...this.getChildrenNodesFromTreeOrLeaf(node[0])
                .filter((elem) => this.filterBlacklistedNodes(elem))
                .map((elem: HTMLElement) => this.mapToZIndexIntegers(elem))
        );
    }

    private filterBlacklistedNodes(elem: HTMLElement): boolean {
        return !SliderPanelZIndexHelper.BLACKLISTED_NODE_NAMES.has(elem.nodeName);
    }

    /** Retrieve zIndex integer value from node, fallback with 0 value in case of NaN. */
    private mapToZIndexIntegers(elem: HTMLElement): number {
        return (
            parseInt(window.getComputedStyle(elem).getPropertyValue('z-index'), 10) ||
            parseInt(elem.style.zIndex, 10) ||
            0
        );
    }

    /** Return recurring flat array of node and it's children. */
    private getChildrenNodesFromTreeOrLeaf(node: HTMLElement): HTMLElement[] {
        return [
            node,
            ...flatten(
                Array.from(node.children).map((child: HTMLElement) =>
                    this.getChildrenNodesFromTreeOrLeaf(child)
                )
            )
        ];
    }
}
