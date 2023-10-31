import { EventEmitter, OnDestroy } from '@angular/core';
import { IPageTreeNodeService, LogService, SystemEventService, IUserTrackingService } from 'smarteditcommons';
import { SlotNode, ComponentNode, NodeInfoService } from '../../../services/pageTree/NodeInfoService';
export declare class PageTreeSlot implements OnDestroy {
    private readonly pageTreeNodeService;
    private readonly nodeInfoService;
    private readonly systemEventService;
    private readonly logService;
    private readonly userTrackingService;
    node: SlotNode;
    onSlotExpanded: EventEmitter<SlotNode>;
    private publishSlotInterval;
    private readonly unregisterPageTreeSlotExpanded;
    constructor(pageTreeNodeService: IPageTreeNodeService, nodeInfoService: NodeInfoService, systemEventService: SystemEventService, logService: LogService, userTrackingService: IUserTrackingService);
    ngOnDestroy(): void;
    onClickSlotNode($event: Event): Promise<void>;
    onComponentExpanded(component: ComponentNode): void;
    getNodeNavigationIconTitle(): string;
    private checkSlotAndPublishSelected;
}
