/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
export enum PopoverTrigger {
    Hover = 'hover',
    Click = 'click'
}

export type PopupOverlayTrigger = PopoverTrigger | boolean | string;

// Copy from popper.js which is deprecated
export type Placement =
    | 'auto-start'
    | 'auto'
    | 'auto-end'
    | 'top-start'
    | 'top'
    | 'top-end'
    | 'right-start'
    | 'right'
    | 'right-end'
    | 'bottom-end'
    | 'bottom'
    | 'bottom-start'
    | 'left-end'
    | 'left'
    | 'left-start';
