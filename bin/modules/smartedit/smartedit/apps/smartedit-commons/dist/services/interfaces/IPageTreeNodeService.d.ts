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
export declare abstract class IPageTreeNodeService {
    protected slotNodes: PageTreeNode[];
    constructor();
    buildSlotNodes(): void;
    updateSlotNodes(data: AggregatedNode[]): void;
    getSlotNodes(): Promise<PageTreeNode[]>;
    scrollToElement(elementUuid: string): Promise<void>;
    existedSmartEditElement(elementUuid: string): Promise<boolean>;
    handleBodyWidthChange(): void;
    protected _buildSlotNode(node: HTMLElement): PageTreeNode;
    protected _isValidElement(ele: HTMLElement): boolean;
}
