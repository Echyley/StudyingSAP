/// <reference types="jquery" />
import { OnDestroy, OnInit } from '@angular/core';
import { BrowserService, IExperienceService, CrossFrameEventService, IStorageService, IPerspectiveService, SmarteditBootstrapGateway } from 'smarteditcommons';
import { IframeManagerService } from '../../../services/iframe/IframeManagerService';
export declare class StorefrontPageComponent implements OnInit, OnDestroy {
    private readonly browserService;
    private readonly iframeManagerService;
    private readonly experienceService;
    private readonly yjQuery;
    private readonly crossFrameEventService;
    private readonly storageService;
    private readonly perspectiveService;
    private readonly smarteditBootstrapGateway;
    width: string;
    private readonly PAGE_TREE_PANEL_OPEN_COOKIE_NAME;
    private isPageTreePanelOpen;
    private isReady;
    private unregisterPageTreePanelSwitch;
    private unregisterOpenInPageTree;
    private unregisterPerspectiveChanged;
    constructor(browserService: BrowserService, iframeManagerService: IframeManagerService, experienceService: IExperienceService, yjQuery: JQueryStatic, crossFrameEventService: CrossFrameEventService, storageService: IStorageService, perspectiveService: IPerspectiveService, smarteditBootstrapGateway: SmarteditBootstrapGateway);
    ngOnDestroy(): void;
    ngOnInit(): Promise<void>;
    showPageTreeList(): boolean;
    private updateStorage;
    private setPerspectiveToolbarVisible;
    private handlePageTreePanelSwitch;
    private handleOpenInPageTree;
    private handlePerspectiveChanged;
    private getPageTreePanelWidth;
    private getPageTreePanelStatus;
}
