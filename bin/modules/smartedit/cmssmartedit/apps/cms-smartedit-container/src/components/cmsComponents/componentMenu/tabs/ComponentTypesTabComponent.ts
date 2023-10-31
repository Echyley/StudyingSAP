/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, ViewEncapsulation, Inject, OnInit, OnDestroy } from '@angular/core';
import { ComponentService, CMSItemStructure } from 'cmscommons';
import {
    LogService,
    ICatalogService,
    IUriContext,
    TAB_DATA,
    TabData,
    CrossFrameEventService,
    SWITCH_LANGUAGE_EVENT,
    LanguageService,
    IPageService,
    IPage,
    ICMSPage
} from 'smarteditcommons';
import { ComponentMenuTabModel } from '../types';

@Component({
    selector: 'se-component-types-tab',
    templateUrl: './ComponentTypesTabComponent.html',
    styleUrls: ['./ComponentTypesTabComponent.scss'],
    encapsulation: ViewEncapsulation.None
})
export class ComponentTypesTabComponent implements OnInit, OnDestroy {
    public searchTerm: string;
    public componentsContext: { items: CMSItemStructure[] };

    private pageInfo: ICMSPage;
    private uriContext: IUriContext;
    private unRegisterSwitchLanguageEvent: () => void;

    constructor(
        @Inject(TAB_DATA) private tabData: TabData<ComponentMenuTabModel>,
        private logService: LogService,
        private componentService: ComponentService,
        private pageService: IPageService,
        private catalogService: ICatalogService,
        private crossFrameEventService: CrossFrameEventService,
        private languageService: LanguageService
    ) {
        this.componentsContext = { items: [] };
        this.pageInfo = null;
        this.searchTerm = '';
        this.uriContext = null;
    }

    ngOnInit(): void {
        this.unRegisterSwitchLanguageEvent = this.crossFrameEventService.subscribe(
            SWITCH_LANGUAGE_EVENT,
            () => {
                // for trigger search when language changed.
                console.log('switch language called before', this.searchTerm);
                this.searchTerm = this.searchTerm !== undefined ? undefined : '';
                console.log('switch language called after', this.searchTerm);
            }
        );
    }

    ngOnDestroy(): void {
        this.unRegisterSwitchLanguageEvent?.();
    }

    public onSearchTermChanged(newSearchTerm: string): void {
        this.searchTerm = newSearchTerm;
    }

    public isTabActive(): boolean {
        return this.tabData.tab.active;
    }

    public isMenuOpen(): boolean {
        return this.tabData.model.isOpen;
    }

    public loadComponentTypes = async (
        mask: string,
        pageSize: number,
        currentPage: number
    ): Promise<IPage<CMSItemStructure>> => {
        try {
            const [locale] = await Promise.all([
                this.languageService.getResolveLocale(),
                this.loadPageContext()
            ]);

            const payload = {
                pageId: this.pageInfo.uid,
                catalogId: this.uriContext.CURRENT_CONTEXT_CATALOG,
                catalogVersion: this.uriContext.CURRENT_CONTEXT_CATALOG_VERSION,
                langIsoCode: locale,
                mask,
                pageSize,
                currentPage
            };
            const components = await this.componentService.getSupportedComponentTypesForCurrentPage(
                payload
            );

            return components;
        } catch (error) {
            this.logService.error(
                'ComponentTypesTab - loadComponentTypes - error loading types.',
                error
            );
            return null;
        }
    };

    private async loadPageContext(): Promise<void> {
        if (this.pageInfo) {
            return;
        }

        const [pageInfo, uriContext] = await Promise.all([
            this.pageService.getCurrentPageInfo(),
            this.catalogService.retrieveUriContext()
        ]);
        this.pageInfo = pageInfo;
        this.uriContext = uriContext;
    }
}
