import { ChangeDetectorRef, OnInit, SimpleChanges } from '@angular/core';
import { ICMSComponent, IComponentSharedService, IComponentVisibilityAlertService } from 'cmscommons';
import { ICatalogService, ICatalogVersionPermissionService, LogService, IEditorModalService, IUserTrackingService } from 'smarteditcommons';
export declare class SlotVisibilityComponent implements OnInit {
    private catalogService;
    private componentSharedService;
    private editorModalService;
    private componentVisibilityAlertService;
    private catalogVersionPermissionService;
    private logService;
    private cdr;
    private userTrackingService;
    component: ICMSComponent;
    componentId: string;
    slotId: string;
    isReady: boolean;
    isSharedComponent: boolean;
    readOnly: boolean;
    componentVisibilitySwitch: string;
    componentRestrictionsCount: string;
    constructor(catalogService: ICatalogService, componentSharedService: IComponentSharedService, editorModalService: IEditorModalService, componentVisibilityAlertService: IComponentVisibilityAlertService, catalogVersionPermissionService: ICatalogVersionPermissionService, logService: LogService, cdr: ChangeDetectorRef, userTrackingService: IUserTrackingService);
    ngOnInit(): Promise<void>;
    ngOnChanges(changes: SimpleChanges): void;
    openEditorModal(): Promise<void>;
}
