import { OnInit, ChangeDetectorRef } from '@angular/core';
import { ManagePageService } from 'cmssmarteditcontainer/services/pages/ManagePageService';
import { ICatalogService, MultiNamePermissionContext, SystemEventService, IDropdownMenuItemData, ICMSPage, IUserTrackingService } from 'smarteditcommons';
export declare class DeletePageItemComponent implements OnInit {
    private dropdownMenuData;
    private managePageService;
    private systemEventService;
    private catalogService;
    private ref;
    private userTrackingService;
    pageInfo: ICMSPage;
    isDeletePageEnabled: boolean;
    tooltipMessage: string;
    deletePagePermission: MultiNamePermissionContext[];
    constructor(dropdownMenuData: IDropdownMenuItemData, managePageService: ManagePageService, systemEventService: SystemEventService, catalogService: ICatalogService, ref: ChangeDetectorRef, userTrackingService: IUserTrackingService);
    ngOnInit(): Promise<void>;
    onClickOnDeletePage(): Promise<void>;
    private setDeletePermissions;
    private getDisableDeleteTooltipMessage;
}
