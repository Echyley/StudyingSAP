import { OnInit } from '@angular/core';
import { MultiNamePermissionContext, IDropdownMenuItemData, ICMSPage, IUserTrackingService } from 'smarteditcommons';
import { PageInfoMenuService } from '../../services';
export declare class EditPageItemComponent implements OnInit {
    private dropdownMenuData;
    private pageInfoMenuService;
    private userTrackingService;
    pageInfo: ICMSPage;
    editPagePermission: MultiNamePermissionContext[];
    constructor(dropdownMenuData: IDropdownMenuItemData, pageInfoMenuService: PageInfoMenuService, userTrackingService: IUserTrackingService);
    ngOnInit(): void;
    onClickOnEdit(): void;
}
