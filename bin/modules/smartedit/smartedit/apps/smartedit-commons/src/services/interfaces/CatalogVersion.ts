/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { TypedMap } from '@smart/utils';
export interface CatalogVersion {
    isCurrentCatalog: boolean;
    catalogName: TypedMap<string>;
    catalogId: string;
    catalogVersionId: string;
    id: string;
}
