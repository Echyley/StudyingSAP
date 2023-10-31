/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { InjectionToken, Type } from '@angular/core';

/**
 * Configuration object passed by input binding to {@link PopupOverlayComponent}.
 */
export interface PopupOverlayConfig {
    /**
     * Vertical align modifier. Relative to the anchor (element).
     */
    valign?: 'bottom' | 'top';

    /**
     * Horizontal align modifier. Relative to the anchor (element).
     */
    halign?: 'right' | 'left';

    /** Classes applied on <fd-popover>. */
    additionalClasses?: string[];

    /**
     * An Angular component rendered within the overlay.
     */
    component?: Type<any>;
}

export const POPUP_OVERLAY_DATA = new InjectionToken('POPUP_OVERLAY_DATA');
