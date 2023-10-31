/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Pageable } from '@smart/utils';
export interface CMSItemSearch extends Pageable {
    typeCode?: string;
    itemSearchParams: string;
    catalogId?: string;
    catalogVersion?: string;
}
