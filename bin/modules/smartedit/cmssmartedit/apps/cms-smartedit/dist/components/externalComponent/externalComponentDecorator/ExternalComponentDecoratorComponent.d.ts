import { ElementRef, OnInit } from '@angular/core';
import { ICatalogService, L10nPipe, LogService, CMSModesService, IComponentHandlerService } from 'smarteditcommons';
export declare class ExternalComponentDecoratorComponent implements OnInit {
    private catalogService;
    private cMSModesService;
    private componentHandlerService;
    private l10nPipe;
    private logService;
    private element;
    smarteditCatalogVersionUuid: string;
    set active(val: string);
    catalogVersionText: string;
    isActive: boolean;
    isExternalSlot: boolean;
    isReady: boolean;
    isVersioningPerspective: boolean;
    constructor(catalogService: ICatalogService, cMSModesService: CMSModesService, componentHandlerService: IComponentHandlerService, l10nPipe: L10nPipe, logService: LogService, element: ElementRef);
    ngOnInit(): Promise<void>;
    private getCatalogVersionText;
}
