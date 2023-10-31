import { GenericEditorWidgetData, SystemEventService } from 'smarteditcommons';
export declare class MuteBooleanComponent {
    private systemEventService;
    widget: GenericEditorWidgetData<any>;
    private unRegClickValueChanged;
    constructor(systemEventService: SystemEventService, widget: GenericEditorWidgetData<any>);
    onDependencyValueChangedEvent(checked: string): void;
}
