import { OnInit } from '@angular/core';
import { GenericEditorField, ICatalogService, IUriContext, GenericEditorWidgetData, IPageService, ICMSPage } from 'smarteditcommons';
export declare class InfoPageNameComponent implements OnInit {
    private catalogService;
    private pageService;
    field: GenericEditorField;
    qualifier: string;
    model: ICMSPage;
    uriContext: IUriContext;
    cmsPage: ICMSPage;
    constructor(catalogService: ICatalogService, pageService: IPageService, data: GenericEditorWidgetData<ICMSPage>);
    ngOnInit(): Promise<void>;
}
