/// <reference types="jquery" />
import { ChangeDetectorRef, OnDestroy, OnInit, EventEmitter } from '@angular/core';
import { IPermissionService, PopupOverlayConfig, SmarteditRoutingService, SystemEventService, Workflow } from 'smarteditcommons';
import { WorkflowFacade } from '../../services/WorkflowFacade';
interface ButtonConfig {
    i18nKey: string;
    permissions?: string[];
}
export interface WorkflowItemMenuOpenedEventData {
    uid?: string;
    code?: string;
}
export declare class WorkflowItemMenuComponent implements OnInit, OnDestroy {
    private systemEventService;
    private workflowFacade;
    private routingService;
    private permissionService;
    private readonly cdr;
    private readonly yjQuery;
    workflowInfo: Workflow;
    isMenuOpenChange: EventEmitter<boolean>;
    isMenuOpen: boolean;
    popupConfig: PopupOverlayConfig;
    menuItems: ButtonConfig[];
    private unRegWorkflowMenuOpenedEvent;
    constructor(systemEventService: SystemEventService, workflowFacade: WorkflowFacade, routingService: SmarteditRoutingService, permissionService: IPermissionService, cdr: ChangeDetectorRef, yjQuery: JQueryStatic);
    ngOnInit(): Promise<void>;
    ngOnDestroy(): void;
    toggleMenu(): void;
    hideMenu(): void;
    editDescription(): Promise<void>;
    cancelWorkflow(): Promise<void>;
    private closeParentPopover;
    private onWorkflowItemMenuOpen;
    private getPermittedButtons;
}
export {};
