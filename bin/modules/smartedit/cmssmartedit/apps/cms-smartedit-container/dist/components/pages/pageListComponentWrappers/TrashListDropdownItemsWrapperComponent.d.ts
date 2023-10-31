import { ChangeDetectorRef, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { DataTableComponentData, ICatalogVersionPermissionService, IDropdownMenuItem, ICMSPage } from 'smarteditcommons';
export declare class TrashListDropdownItemsWrapperComponent implements OnInit {
    private route;
    data: DataTableComponentData;
    private catalogVersionPermissionService;
    private cdr;
    dropdownItems: IDropdownMenuItem[];
    item: ICMSPage;
    loaded: boolean;
    constructor(route: ActivatedRoute, data: DataTableComponentData, catalogVersionPermissionService: ICatalogVersionPermissionService, cdr: ChangeDetectorRef);
    ngOnInit(): Promise<void>;
    private setDropdownItems;
    private addDropdownItem;
}
