import { Payload } from '@smart/utils';
export interface IDragAndDropEvents {
    TRACK_MOUSE_POSITION: string;
    DROP_ELEMENT: string;
    DRAG_DROP_START: string;
    DRAG_DROP_END: string;
    DRAG_DROP_CROSS_ORIGIN_START: string;
}
/**
 * Pointer coordinates.
 */
export interface IMousePosition extends Payload {
    /**
     * Abscissa of the pointer position.
     */
    x: number;
    /**
     * Ordinate of the pointer position
     */
    y: number;
}
export declare type IDragEventType = 'drop' | 'dragenter' | 'dragover' | 'dragleave';
export declare const IDragEventType: {
    DROP: IDragEventType;
    DRAG_ENTER: IDragEventType;
    DRAG_OVER: IDragEventType;
    DRAG_LEAVE: IDragEventType;
};
