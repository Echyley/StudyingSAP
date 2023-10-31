import { OnInit, ChangeDetectorRef } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ICatalogService, IPermissionService, IUriContext, IUrlService, TypedMap } from 'smarteditcommons';
export declare class NavigationManagementPageComponent implements OnInit {
    private activatedRoute;
    private urlService;
    private permissionService;
    private catalogService;
    private cdr;
    catalogName: TypedMap<string>;
    catalogVersion: string;
    uriContext: IUriContext;
    readOnly: boolean;
    constructor(activatedRoute: ActivatedRoute, urlService: IUrlService, permissionService: IPermissionService, catalogService: ICatalogService, cdr: ChangeDetectorRef);
    ngOnInit(): Promise<void>;
    private setCatalogName;
    private setReadOnly;
}
