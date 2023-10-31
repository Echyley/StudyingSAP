import { OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ManagePageService } from 'cmssmarteditcontainer/services/pages/ManagePageService';
import { ICatalogService, IUrlService, SystemEventService, SmarteditRoutingService, TypedMap } from 'smarteditcommons';
export declare class TrashLinkComponent implements OnInit, OnDestroy {
    private route;
    private routingsService;
    private managePageService;
    private urlService;
    private catalogService;
    private readonly systemEventService;
    private readonly cdr;
    trashedPagesTranslationData: TypedMap<number>;
    isNonActiveCatalog: boolean;
    private siteId;
    private catalogId;
    private catalogVersion;
    private uriContext;
    private unsubscribeContentCatalogUpdateEvent;
    constructor(route: ActivatedRoute, routingsService: SmarteditRoutingService, managePageService: ManagePageService, urlService: IUrlService, catalogService: ICatalogService, systemEventService: SystemEventService, cdr: ChangeDetectorRef);
    ngOnInit(): Promise<void>;
    ngOnDestroy(): void;
    updateTrashedPagesCount(): Promise<void>;
    goToTrash(): void;
}
