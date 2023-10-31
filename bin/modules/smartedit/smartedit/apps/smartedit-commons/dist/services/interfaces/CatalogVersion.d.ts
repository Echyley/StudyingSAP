import { TypedMap } from '@smart/utils';
export interface CatalogVersion {
    isCurrentCatalog: boolean;
    catalogName: TypedMap<string>;
    catalogId: string;
    catalogVersionId: string;
    id: string;
}
