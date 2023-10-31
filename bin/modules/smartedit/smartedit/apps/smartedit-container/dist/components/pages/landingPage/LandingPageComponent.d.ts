/// <reference types="jquery" />
import './LandingPageComponent.scss';
import { Location } from '@angular/common';
import { OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { GenericEditorField, IAlertService, IBaseCatalog, ICatalogService, IStorageService, SystemEventService } from 'smarteditcommons';
import { SiteService } from '../../../services';
export declare class LandingPageComponent implements OnInit, OnDestroy {
    private siteService;
    private catalogService;
    private systemEventService;
    private storageService;
    private alertService;
    private route;
    private location;
    private yjQuery;
    readonly sitesId = "sites-id";
    readonly qualifier = "site";
    catalogs: IBaseCatalog[];
    field: Partial<GenericEditorField>;
    model: {
        site: string;
    };
    private paramMapSubscription;
    private unregisterSitesDropdownEventHandler;
    private unregisterUserChangesEventHandler;
    private readonly SELECTED_SITE_COOKIE_NAME;
    constructor(siteService: SiteService, catalogService: ICatalogService, systemEventService: SystemEventService, storageService: IStorageService, alertService: IAlertService, route: ActivatedRoute, location: Location, yjQuery: JQueryStatic);
    ngOnInit(): void;
    ngOnDestroy(): void;
    private init;
    private getCurrentSiteId;
    private updateRouteToRemoveSite;
    private removeStorefrontCssClass;
    private selectedSiteDropdownEventHandler;
    private setSelectedSite;
    private loadCatalogsBySite;
    private setModel;
}
