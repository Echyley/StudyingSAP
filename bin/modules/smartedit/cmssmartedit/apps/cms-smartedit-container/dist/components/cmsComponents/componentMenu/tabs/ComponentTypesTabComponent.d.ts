import { OnInit, OnDestroy } from '@angular/core';
import { ComponentService, CMSItemStructure } from 'cmscommons';
import { LogService, ICatalogService, TabData, CrossFrameEventService, LanguageService, IPageService, IPage } from 'smarteditcommons';
import { ComponentMenuTabModel } from '../types';
export declare class ComponentTypesTabComponent implements OnInit, OnDestroy {
    private tabData;
    private logService;
    private componentService;
    private pageService;
    private catalogService;
    private crossFrameEventService;
    private languageService;
    searchTerm: string;
    componentsContext: {
        items: CMSItemStructure[];
    };
    private pageInfo;
    private uriContext;
    private unRegisterSwitchLanguageEvent;
    constructor(tabData: TabData<ComponentMenuTabModel>, logService: LogService, componentService: ComponentService, pageService: IPageService, catalogService: ICatalogService, crossFrameEventService: CrossFrameEventService, languageService: LanguageService);
    ngOnInit(): void;
    ngOnDestroy(): void;
    onSearchTermChanged(newSearchTerm: string): void;
    isTabActive(): boolean;
    isMenuOpen(): boolean;
    loadComponentTypes: (mask: string, pageSize: number, currentPage: number) => Promise<IPage<CMSItemStructure>>;
    private loadPageContext;
}
