import { OnInit } from '@angular/core';
import { GenericEditorWidgetData, ICMSPage } from 'smarteditcommons';
export declare class DuplicatePrimaryNonContentPageComponent implements OnInit {
    data: GenericEditorWidgetData<ICMSPage>;
    label: string;
    private page;
    private readonly PRODUCT_PAGE;
    private readonly labelI18nKeys;
    constructor(data: GenericEditorWidgetData<ICMSPage>);
    ngOnInit(): void;
}
