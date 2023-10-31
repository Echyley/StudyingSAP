/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, Inject, OnInit } from '@angular/core';
import {
    GenericEditorField,
    ICatalogService,
    IUriContext,
    GENERIC_EDITOR_WIDGET_DATA,
    GenericEditorWidgetData,
    IPageService,
    ICMSPage
} from 'smarteditcommons';

/**
 * Component responsible for rendering the page name inside the page info menu and a home icon beside the name
 * if the current page is a home page (current or old)
 */
@Component({
    selector: 'se-info-page-name',
    templateUrl: './InfoPageNameComponent.html',
    styleUrls: ['./InfoPageNameComponent.scss']
})
export class InfoPageNameComponent implements OnInit {
    public field: GenericEditorField;
    public qualifier: string;
    public model: ICMSPage;
    public uriContext: IUriContext;
    public cmsPage: ICMSPage;

    constructor(
        private catalogService: ICatalogService,
        private pageService: IPageService,
        @Inject(GENERIC_EDITOR_WIDGET_DATA)
        data: GenericEditorWidgetData<ICMSPage>
    ) {
        ({ model: this.model, field: this.field, qualifier: this.qualifier } = data);
    }

    async ngOnInit(): Promise<void> {
        const [uriContext, cmsPage] = await Promise.all([
            this.catalogService.retrieveUriContext(),
            this.pageService.getCurrentPageInfo()
        ]);
        this.uriContext = uriContext;
        this.cmsPage = cmsPage;
    }
}
