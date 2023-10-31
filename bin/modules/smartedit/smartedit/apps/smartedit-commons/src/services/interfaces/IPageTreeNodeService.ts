/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Payload } from '@smart/utils';
import { AggregatedNode } from './ISmartEditContractChangeListener';

export interface PageTreeNode extends Payload {
    componentId: string;
    componentUuid: string;
    componentTypeFromPage: string;
    containerId: string;
    containerType: string;
    elementUuid: string;
    catalogVersionUuid: string;
    isExpanded: boolean;
    childrenNode: PageTreeNode[];
}

/**
 * Provides an abstract extensible pageTreeNode service. Used to build slotNodes in SmartEdit
 * application and get slotNodes in the SmartEdit container.
 * When SmartEdit bootstrap, PageTreeNodeServiceInner builds the slotNodes array from the page.
 * PageTreeNodeServiceOuter get the slotNodes when needs.
 *
 * This class serves as an interface and should be extended, not instantiated.
 */
export abstract class IPageTreeNodeService {
    protected slotNodes: PageTreeNode[];

    constructor() {
        this.slotNodes = [];
    }

    /// //////////////////////////////////
    // Proxied Functions : these functions will be proxied if left unimplemented
    /// //////////////////////////////////
    buildSlotNodes(): void {
        'proxyFunction';
    }

    updateSlotNodes(data: AggregatedNode[]): void {
        'proxyFunction';
    }

    getSlotNodes(): Promise<PageTreeNode[]> {
        'proxyFunction';
        return null;
    }

    scrollToElement(elementUuid: string): Promise<void> {
        'proxyFunction';
        return null;
    }

    existedSmartEditElement(elementUuid: string): Promise<boolean> {
        'proxyFunction';
        return null;
    }

    handleBodyWidthChange(): void {
        'proxyFunction';
        return null;
    }

    protected _buildSlotNode(node: HTMLElement): PageTreeNode {
        'proxyFunction';
        return null;
    }

    protected _isValidElement(ele: HTMLElement): boolean {
        'proxyFunction';
        return true;
    }
}
