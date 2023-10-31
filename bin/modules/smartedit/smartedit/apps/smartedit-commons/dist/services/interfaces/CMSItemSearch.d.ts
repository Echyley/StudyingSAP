import { Pageable } from '@smart/utils';
export interface CMSItemSearch extends Pageable {
    typeCode?: string;
    itemSearchParams: string;
    catalogId?: string;
    catalogVersion?: string;
}
