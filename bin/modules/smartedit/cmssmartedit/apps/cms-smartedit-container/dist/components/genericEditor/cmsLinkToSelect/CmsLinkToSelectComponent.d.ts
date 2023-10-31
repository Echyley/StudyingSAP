import { ChangeDetectorRef, OnDestroy, OnInit } from '@angular/core';
import { GenericEditorField, GenericEditorWidgetData, SystemEventService } from 'smarteditcommons';
import { CmsLinkToSelectOption } from './types';
export declare class CmsLinkToSelectComponent implements OnInit, OnDestroy {
    private cdr;
    private systemEventService;
    id: string;
    field: GenericEditorField;
    optionModel: CmsLinkToSelectOption;
    qualifier: string;
    private unRegSelectValueChanged;
    constructor(cdr: ChangeDetectorRef, systemEventService: SystemEventService, data: GenericEditorWidgetData<CmsLinkToSelectOption>);
    ngOnInit(): void;
    ngOnDestroy(): void;
    private onLinkToSelectValueChanged;
    private getLinkToValue;
    private clearModel;
}
