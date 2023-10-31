/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Inject } from '@angular/core';
import { YJQUERY_TOKEN, IComponentHandlerService } from 'smarteditcommons';

/**
 * Internal service
 *
 * Service that resizes slots and components in the Inner Frame when the overlay is enabled or disabled.
 */
export class ResizeComponentService {
    constructor(
        private readonly componentHandlerService: IComponentHandlerService,
        @Inject(YJQUERY_TOKEN) private readonly yjQuery: JQueryStatic
    ) {}

    /**
     * This methods appends CSS classes to inner frame slots and components. Passing a boolean true to showResizing
     * enables the resizing, and false vice versa.
     */
    public resizeComponents(showResizing: boolean): void {
        const slots = this.yjQuery(this.componentHandlerService.getAllSlotsSelector());
        const components = this.yjQuery(this.componentHandlerService.getAllComponentsSelector());

        if (showResizing) {
            slots.addClass('ySEEmptySlot');
            components.addClass('se-storefront-component');
        } else {
            slots.removeClass('ySEEmptySlot');
            components.removeClass('se-storefront-component');
        }
    }
}
