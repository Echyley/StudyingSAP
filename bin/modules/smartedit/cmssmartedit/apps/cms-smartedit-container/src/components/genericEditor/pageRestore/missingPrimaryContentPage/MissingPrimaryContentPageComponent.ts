/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, Inject, OnInit } from '@angular/core';
import {
    FetchStrategy,
    GENERIC_EDITOR_WIDGET_DATA,
    GenericEditorWidgetData,
    LogService,
    Page,
    SelectItem,
    IPageService,
    ICMSPage,
    CMSItem
} from 'smarteditcommons';

@Component({
    selector: 'se-component-missing-primary-content-page',
    templateUrl: './MissingPrimaryContentPageComponent.html',
    styleUrls: ['./MissingPrimaryContentPageComponent.scss']
})
export class MissingPrimaryContentPageComponent implements OnInit {
    public cmsPage: ICMSPage;
    public fetchStrategy: FetchStrategy<SelectItem>;
    private readonly CONTENT_PAGE_TYPE_CODE = 'ContentPage';
    private readonly ERROR_MSG =
        '[MissingPrimaryContentPageComponent] - Cannot retrieve list of alternative content primary pages.';

    constructor(
        @Inject(GENERIC_EDITOR_WIDGET_DATA)
        public data: GenericEditorWidgetData<ICMSPage>,
        private pageService: IPageService,
        private logService: LogService
    ) {
        ({ model: this.cmsPage } = data);
    }

    ngOnInit(): void {
        this.fetchStrategy = {
            fetchEntity: (): Promise<SelectItem> => this.fetchEntity(),
            fetchPage: (
                search: string,
                pageSize: number,
                currentPage: number
            ): Promise<Page<SelectItem>> => this.fetchPage(search, pageSize, currentPage)
        };
    }

    private fetchEntity(): Promise<SelectItem> {
        return Promise.resolve(this.getSelectItemFromPrimaryPage(this.cmsPage));
    }

    private async fetchPage(
        search: string,
        pageSize: number,
        currentPage: number
    ): Promise<Page<SelectItem>> {
        try {
            const page = await this.pageService.getPaginatedPrimaryPagesForPageType(
                this.CONTENT_PAGE_TYPE_CODE,
                null,
                {
                    search,
                    pageSize,
                    currentPage
                }
            );
            const targetPage: Page<SelectItem> = {
                pagination: page.pagination,
                results: null
            };
            targetPage.results = page.response.map((rawPage) =>
                this.getSelectItemFromPrimaryPage(rawPage)
            );
            return targetPage;
        } catch (error) {
            this.logService.warn(this.ERROR_MSG, error);
            return Promise.reject();
        }
    }

    private getSelectItemFromPrimaryPage(page: CMSItem): SelectItem {
        return {
            id: page.label as string,
            label: page.name
        };
    }
}
