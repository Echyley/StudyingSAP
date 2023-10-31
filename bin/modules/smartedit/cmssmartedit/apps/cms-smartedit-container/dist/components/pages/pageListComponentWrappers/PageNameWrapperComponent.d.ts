import { OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { IExperienceService, DataTableComponentData, IUriContext, IUrlService, ICMSPage } from 'smarteditcommons';
export declare class PageNameWrapperComponent implements OnInit {
    data: DataTableComponentData;
    private route;
    private experienceService;
    private urlService;
    item: ICMSPage;
    uriContext: IUriContext;
    private siteUid;
    private catalogId;
    private catalogVersion;
    constructor(data: DataTableComponentData, route: ActivatedRoute, experienceService: IExperienceService, urlService: IUrlService);
    ngOnInit(): void;
    goToPage(event: Event): void;
}
