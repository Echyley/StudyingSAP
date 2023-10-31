import { DoCheck } from '@angular/core';
import { DynamicInputChange, FormGrouping, ISessionService, Payload } from '@smart/utils';
import { ILanguage } from '../../../services';
import { GenericEditorField, GenericEditorTab } from '../types';
export declare class LocalizedElementComponent implements DoCheck, DynamicInputChange {
    private sessionService;
    form: FormGrouping;
    field: GenericEditorField;
    component: Payload;
    languages: ILanguage[];
    tabs: GenericEditorTab[];
    private _previousMessages;
    constructor(sessionService: ISessionService);
    onDynamicInputChange(): void;
    ngDoCheck(): void;
    private _createLocalizedTabs;
}
