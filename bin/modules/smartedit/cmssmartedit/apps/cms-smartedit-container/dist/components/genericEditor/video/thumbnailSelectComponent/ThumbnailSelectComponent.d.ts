import { OnDestroy } from '@angular/core';
import { GenericEditorField, GenericEditorWidgetData, SystemEventService } from 'smarteditcommons';
export declare enum ThumbnailSelectOption {
    uploadThumbnail = "UPLOAD_THUMBNAIL",
    noThumbnail = "NO_THUMBNAIL"
}
export declare class ThumbnailSelectComponent implements OnDestroy {
    private systemEventService;
    widget: GenericEditorWidgetData<string>;
    id: string;
    field: GenericEditorField;
    model: string;
    qualifier: string;
    private readonly unRegSelectValueChanged;
    private readonly unRegDependsOnValueChanged;
    private readonly optionEventId;
    constructor(systemEventService: SystemEventService, widget: GenericEditorWidgetData<string>);
    ngOnDestroy(): void;
    private onThumbnailSelectValueChanged;
    private onDependsOnValueChanged;
}
