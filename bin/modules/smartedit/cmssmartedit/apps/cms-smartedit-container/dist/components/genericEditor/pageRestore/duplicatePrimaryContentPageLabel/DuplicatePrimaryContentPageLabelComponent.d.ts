import { GenericEditorWidgetData, ICMSPage } from 'smarteditcommons';
export declare class DuplicatePrimaryContentPageLabelComponent {
    data: GenericEditorWidgetData<ICMSPage>;
    conflictResolution: number;
    private page;
    private readonly RESOLUTION_OPTIONS;
    constructor(data: GenericEditorWidgetData<ICMSPage>);
    selectResolution(resolutionSelected: number): void;
}
