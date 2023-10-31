import { OnInit } from '@angular/core';
import { FetchStrategy, GenericEditorWidgetData, LogService, SelectItem, IPageService, ICMSPage } from 'smarteditcommons';
export declare class MissingPrimaryContentPageComponent implements OnInit {
    data: GenericEditorWidgetData<ICMSPage>;
    private pageService;
    private logService;
    cmsPage: ICMSPage;
    fetchStrategy: FetchStrategy<SelectItem>;
    private readonly CONTENT_PAGE_TYPE_CODE;
    private readonly ERROR_MSG;
    constructor(data: GenericEditorWidgetData<ICMSPage>, pageService: IPageService, logService: LogService);
    ngOnInit(): void;
    private fetchEntity;
    private fetchPage;
    private getSelectItemFromPrimaryPage;
}
