import { GenericEditorWidgetData } from '../../../genericEditor/types';
export declare const DEFAULT_GENERIC_EDITOR_FLOAT_PRECISION = "0.01";
export declare class FloatComponent {
    widget: GenericEditorWidgetData<any>;
    precision: string;
    constructor(widget: GenericEditorWidgetData<any>);
}
