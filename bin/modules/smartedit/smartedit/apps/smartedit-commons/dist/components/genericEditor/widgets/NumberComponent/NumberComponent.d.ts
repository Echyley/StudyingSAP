import { SystemEventService } from '../../../../services';
import { GenericEditorWidgetData } from '../../../genericEditor/types';
export declare class NumberComponent {
    private readonly systemEventService;
    widget: GenericEditorWidgetData<any>;
    private readonly unRegClickValueChanged;
    constructor(systemEventService: SystemEventService, widget: GenericEditorWidgetData<any>);
    onClickEvent(data: string): void;
}
