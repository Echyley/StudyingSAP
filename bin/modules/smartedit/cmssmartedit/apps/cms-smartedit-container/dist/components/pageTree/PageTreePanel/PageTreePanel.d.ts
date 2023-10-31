/// <reference types="jquery" />
import { ChangeDetectorRef, OnDestroy, OnInit } from '@angular/core';
import { CrossFrameEventService, LogService } from 'smarteditcommons';
import { SlotNode, NodeInfoService } from '../../../services/pageTree/NodeInfoService';
export declare class PageTreePanel implements OnInit, OnDestroy {
    private readonly crossFrameEventService;
    private readonly nodeInfoService;
    private readonly yjQuery;
    private readonly cdr;
    private readonly logService;
    private slotNodes;
    private scrollToNodeInterval;
    private expandComponentNodeInterval;
    private readonly unregisterOpenInPageTree;
    private readonly unregisterPartRefreshTreeNode;
    private readonly unregisterOverallRefreshTree;
    private readonly unregisterPageTreeSlotUpdate;
    private readonly unregisterScrollToModifiedSlot;
    constructor(crossFrameEventService: CrossFrameEventService, nodeInfoService: NodeInfoService, yjQuery: JQueryStatic, cdr: ChangeDetectorRef, logService: LogService);
    ngOnDestroy(): void;
    ngOnInit(): Promise<void>;
    onSlotExpanded(slot: SlotNode): void;
    handleEventOpenInPageTree(eventId: string, elementUuid: string): void;
    private updateSlotNode;
    private expandComponentNode;
    private scrollToNode;
    private scrollToNodeByInterval;
}
