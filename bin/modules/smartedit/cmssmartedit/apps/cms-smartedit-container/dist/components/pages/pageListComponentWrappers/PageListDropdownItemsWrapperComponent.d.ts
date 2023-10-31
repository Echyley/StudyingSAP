import { ChangeDetectorRef, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ManagePageService } from 'cmssmarteditcontainer/services/pages/ManagePageService';
import { DataTableComponentData, ICatalogVersionPermissionService, IDropdownMenuItem, ICMSPage } from 'smarteditcommons';
export declare class PageListDropdownItemsWrapperComponent implements OnInit {
    private route;
    data: DataTableComponentData<ICMSPage>;
    private catalogVersionPermissionService;
    private managePageService;
    private cdr;
    dropdownItems: IDropdownMenuItem[];
    item: ICMSPage;
    constructor(route: ActivatedRoute, data: DataTableComponentData<ICMSPage>, catalogVersionPermissionService: ICatalogVersionPermissionService, managePageService: ManagePageService, cdr: ChangeDetectorRef);
    ngOnInit(): Promise<void>;
    private setDropdownItems;
    private addDropdownItem;
}
