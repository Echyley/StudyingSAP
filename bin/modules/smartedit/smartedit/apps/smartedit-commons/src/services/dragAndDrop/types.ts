/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
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

export type IDragEventType = 'drop' | 'dragenter' | 'dragover' | 'dragleave';
// eslint-disable-next-line no-redeclare
export const IDragEventType = {
    DROP: 'drop' as IDragEventType,
    DRAG_ENTER: 'dragenter' as IDragEventType,
    DRAG_OVER: 'dragover' as IDragEventType,
    DRAG_LEAVE: 'dragleave' as IDragEventType
};
