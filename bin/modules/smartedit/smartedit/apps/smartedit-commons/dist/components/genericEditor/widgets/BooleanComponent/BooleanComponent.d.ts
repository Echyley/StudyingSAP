import { OnInit } from '@angular/core';
import { SystemEventService } from 'smarteditcommons/services';
import { GenericEditorWidgetData } from '../../../genericEditor/types';
export declare class BooleanComponent implements OnInit {
    private readonly systemEventService;
    widget: GenericEditorWidgetData<any>;
    qualifierId: string;
    private eventId;
    constructor(systemEventService: SystemEventService, widget: GenericEditorWidgetData<any>);
    ngOnInit(): void;
    checkboxOnClick(event: HTMLInputElement): void;
}
