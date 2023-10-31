/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

import { ComponentAttributes } from 'smarteditcommons';

export interface ContextMenuDto extends ComponentAttributes {
    [index: string]: any;
    selectedCustomizationCode?: string;
    selectedVariationCode?: string;
    containerSourceId?: string;
    slotsToRefresh?: string[];
    editEnabled?: boolean;
    componentAttributes?: any;
    slotId?: string;
    containerId?: string;
    componentType?: string;
    componentId?: string;
    element?: JQuery<HTMLElement>;
    actionId?: string;
    componentCatalog?: string;
    componentCatalogVersion?: string;
    catalog?: string;
    catalogVersion?: string;
    slotCatalog?: string;
    componentUuid?: string;
}
