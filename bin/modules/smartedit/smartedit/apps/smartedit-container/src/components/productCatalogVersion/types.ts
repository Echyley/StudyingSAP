/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { FetchStrategy, IBaseCatalog, IBaseCatalogVersion } from 'smarteditcommons';

export const MULTI_PRODUCT_CATALOGS_UPDATED = 'MULTI_PRODUCT_CATALOGS_UPDATED';

export interface SelectAdaptedCatalogVersion extends IBaseCatalogVersion {
    id: string;
    label: string;
}

export interface SelectAdaptedCatalog extends IBaseCatalog {
    fetchStrategy: FetchStrategy<IBaseCatalogVersion>;
    versions: SelectAdaptedCatalogVersion[];
    selectedItem: string;
}
export interface MultiProductCatalogVersionSelectorData {
    productCatalogs: IBaseCatalog[];
    selectedCatalogVersions: string[];
}
