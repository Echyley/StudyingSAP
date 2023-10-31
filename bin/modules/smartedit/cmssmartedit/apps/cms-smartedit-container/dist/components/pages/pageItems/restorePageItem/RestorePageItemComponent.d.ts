import { OnInit } from '@angular/core';
import { ManagePageService } from 'cmssmarteditcontainer/services/pages/ManagePageService';
import { IDropdownMenuItemData, MultiNamePermissionContext, ICMSPage, IUserTrackingService } from 'smarteditcommons';
export declare class RestorePageItemComponent implements OnInit {
    private managePageService;
    private userTrackingService;
    private dropdownMenuData;
    pageInfo: ICMSPage;
    restorePagePermission: MultiNamePermissionContext[];
    constructor(managePageService: ManagePageService, userTrackingService: IUserTrackingService, dropdownMenuData: IDropdownMenuItemData);
    ngOnInit(): void;
    restorePage(): void;
}
