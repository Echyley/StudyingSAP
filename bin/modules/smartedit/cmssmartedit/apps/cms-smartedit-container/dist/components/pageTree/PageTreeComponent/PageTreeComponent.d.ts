import { EventEmitter, OnInit } from '@angular/core';
import { IComponentMenuConditionAndCallbackService } from 'cmscommons';
import { IPageTreeNodeService, LogService, IContextualMenuConfiguration, IPermissionService } from 'smarteditcommons';
import { NodeInfoService, ComponentNode } from '../../../services/pageTree/NodeInfoService';
export declare class PageTreeComponent implements OnInit {
    private readonly pageTreeNodeService;
    private readonly nodeInfoService;
    private readonly logService;
    private readonly componentMenuConditionAndCallbackService;
    private readonly permissionService;
    component: ComponentNode;
    slotId: string;
    slotUuid: string;
    slotElementUuid: string;
    slotCatalogVersionUuid: string;
    onComponentExpanded: EventEmitter<ComponentNode>;
    draggable: boolean;
    menuConfiguration: IContextualMenuConfiguration;
    private publishComponentInterval;
    constructor(pageTreeNodeService: IPageTreeNodeService, nodeInfoService: NodeInfoService, logService: LogService, componentMenuConditionAndCallbackService: IComponentMenuConditionAndCallbackService, permissionService: IPermissionService);
    ngOnInit(): Promise<void>;
    onClickComponentNode($event: Event): Promise<void>;
    getComponentNavigationIconTitle(): string;
    private checkComponentAndPublishSelected;
}
