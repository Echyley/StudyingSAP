export interface SearchProfileActionContext {
    customizationCode: string;
    variationCode: string;
    initialOrder: any[];
    searchProfilesOrder: any[];
}
export interface SearchProfileAction {
    baseIndex?: number;
    catalog?: string;
    catalogVersion?: string;
    code?: string;
    rank?: string;
    type: string;
    searchProfileCode: string;
    searchProfileCatalog: string;
}
export interface SearchProfileFilter {
    indexTypes?: string;
    code: string;
    pageSize: number;
    currentPage: number;
}
export interface SearchProfileItem {
    id?: string;
    label?: string;
    indexType: string;
    code: string;
    name: string;
    catalogVersion: string;
}
