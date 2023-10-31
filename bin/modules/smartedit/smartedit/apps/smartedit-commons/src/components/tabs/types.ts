/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Type } from '@angular/core';

export interface Tab {
    /** Used to track the tab within the tabs. */
    id: string;
    hasErrors: boolean;
    active?: boolean;
    message?: string;
    /** The tab header. */
    title: string;
    /** Angular component to be dynamically injected. */
    component: Type<any>;
    /** Whether the tab is disabled. Makes the tab non clickable and applies appropriate styles. */
    disabled?: boolean;
}
