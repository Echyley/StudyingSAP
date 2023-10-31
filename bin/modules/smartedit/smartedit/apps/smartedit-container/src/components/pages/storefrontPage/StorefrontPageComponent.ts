/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import {
    BrowserService,
    IExperienceService,
    YJQUERY_TOKEN,
    CrossFrameEventService,
    EVENT_PERSPECTIVE_CHANGED,
    EVENT_PAGE_TREE_PANEL_SWITCH,
    EVENT_OPEN_IN_PAGE_TREE,
    IStorageService,
    PAGE_TREE_PANEL_WIDTH_COOKIE_NAME,
    IPerspectiveService,
    CMSModesService,
    SmarteditBootstrapGateway
} from 'smarteditcommons';
import { IframeManagerService } from '../../../services/iframe/IframeManagerService';

/**
 * Component responsible of displaying the SmartEdit storefront page.
 *
 * @internal
 * @ignore
 */
@Component({
    selector: 'se-storefront-page',
    templateUrl: './StorefrontPageComponent.html',
    styleUrls: ['./StorefrontPageComponent.scss']
})
export class StorefrontPageComponent implements OnInit, OnDestroy {
    public width: string;
    private readonly PAGE_TREE_PANEL_OPEN_COOKIE_NAME: string = 'smartedit-page-tree-panel-open';
    private isPageTreePanelOpen = false;
    private isReady = false;
    private unregisterPageTreePanelSwitch: () => void;
    private unregisterOpenInPageTree: () => void;
    private unregisterPerspectiveChanged: () => void;

    constructor(
        private readonly browserService: BrowserService,
        private readonly iframeManagerService: IframeManagerService,
        private readonly experienceService: IExperienceService,
        @Inject(YJQUERY_TOKEN) private readonly yjQuery: JQueryStatic,
        private readonly crossFrameEventService: CrossFrameEventService,
        private readonly storageService: IStorageService,
        private readonly perspectiveService: IPerspectiveService,
        private readonly smarteditBootstrapGateway: SmarteditBootstrapGateway
    ) {
        this.width = '20%';
    }

    ngOnDestroy(): void {
        if (this.unregisterPageTreePanelSwitch) {
            this.unregisterPageTreePanelSwitch();
        }

        if (this.unregisterOpenInPageTree) {
            this.unregisterOpenInPageTree();
        }

        if (this.unregisterPerspectiveChanged) {
            this.unregisterPerspectiveChanged();
        }
    }

    async ngOnInit(): Promise<void> {
        this.iframeManagerService.applyDefault();
        await this.experienceService.initializeExperience();
        this.yjQuery(document.body).addClass('is-storefront');

        if (this.browserService.isSafari()) {
            this.yjQuery(document.body).addClass('is-safari');
        }

        const activePerspective = this.perspectiveService.getActivePerspective();
        if (
            activePerspective &&
            activePerspective.key === CMSModesService.ADVANCED_PERSPECTIVE_KEY
        ) {
            await this.getPageTreePanelStatus();
            await this.getPageTreePanelWidth();
        }
        this.unregisterPageTreePanelSwitch = this.crossFrameEventService.subscribe(
            EVENT_PAGE_TREE_PANEL_SWITCH,
            this.handlePageTreePanelSwitch.bind(this)
        );
        this.unregisterOpenInPageTree = this.crossFrameEventService.subscribe(
            EVENT_OPEN_IN_PAGE_TREE,
            this.handleOpenInPageTree.bind(this)
        );
        this.unregisterPerspectiveChanged = this.crossFrameEventService.subscribe(
            EVENT_PERSPECTIVE_CHANGED,
            this.handlePerspectiveChanged.bind(this)
        );
        this.smarteditBootstrapGateway
            .getInstance()
            .subscribe('smartEditReady', this.setPerspectiveToolbarVisible.bind(this));
    }

    showPageTreeList(): boolean {
        return this.isPageTreePanelOpen;
    }

    private updateStorage(): void {
        this.storageService.setValueInLocalStorage(
            this.PAGE_TREE_PANEL_OPEN_COOKIE_NAME,
            this.isPageTreePanelOpen,
            true
        );
    }

    private setPerspectiveToolbarVisible(): void {
        this.isReady = true;
    }

    private async handlePageTreePanelSwitch(): Promise<void> {
        this.isPageTreePanelOpen = !this.isPageTreePanelOpen;

        if (this.isPageTreePanelOpen) {
            await this.getPageTreePanelWidth();
        }

        this.updateStorage();
    }

    private async handleOpenInPageTree(): Promise<void> {
        if (!this.isPageTreePanelOpen) {
            this.isPageTreePanelOpen = true;
            await this.getPageTreePanelWidth();
            this.updateStorage();
        }
    }

    private async handlePerspectiveChanged(): Promise<void> {
        const activePerspective = this.perspectiveService.getActivePerspective();
        if (
            activePerspective &&
            activePerspective.key === CMSModesService.ADVANCED_PERSPECTIVE_KEY
        ) {
            await this.getPageTreePanelStatus();
            await this.getPageTreePanelWidth();
        } else {
            this.isPageTreePanelOpen = false;
        }
    }

    private async getPageTreePanelWidth(): Promise<void> {
        const width = await this.storageService.getValueFromLocalStorage(
            PAGE_TREE_PANEL_WIDTH_COOKIE_NAME,
            true
        );
        if (!!width) {
            this.width = width + 'px';
        }
    }

    private async getPageTreePanelStatus(): Promise<void> {
        const pageTreePanelStatus = await this.storageService.getValueFromLocalStorage(
            this.PAGE_TREE_PANEL_OPEN_COOKIE_NAME,
            true
        );

        this.isPageTreePanelOpen = pageTreePanelStatus as boolean;
    }
}
