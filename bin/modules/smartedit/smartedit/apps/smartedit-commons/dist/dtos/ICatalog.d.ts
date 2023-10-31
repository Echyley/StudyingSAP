import { Payload, TypedMap } from '@smart/utils';
import { ISite } from 'smarteditcommons/services';
import { IHomepageVersions } from './IHomepage';
/** from Backend */
export interface IPageDisplayConditionsOption extends Payload {
    id: string;
    label: string;
}
export interface IPageDisplayConditions extends Payload {
    options: IPageDisplayConditionsOption[];
    typecode: string;
}
export interface IBaseCatalogVersion extends Payload {
    active: boolean;
    pageDisplayConditions: IPageDisplayConditions[];
    uuid: string;
    version: string;
    thumbnailUrl?: string;
}
export interface ICatalogVersion extends IBaseCatalogVersion {
    name?: {
        [index: string]: string;
    };
    catalogId?: string;
    siteId?: string;
    catalogName?: TypedMap<string>;
    siteDescriptor?: ISite;
    homepage?: IHomepageVersions;
}
export interface IParentCatalog {
    catalogId: string;
    catalogName: TypedMap<string>;
    sites: Pick<ISite, 'name' | 'uid'>[];
    versions: IBaseCatalogVersion[];
}
export interface IBaseCatalog {
    catalogId: string;
    versions: IBaseCatalogVersion[];
    name?: TypedMap<string>;
    parents?: IParentCatalog[];
}
export interface ICatalog {
    catalogId: string;
    versions: ICatalogVersion[];
    name?: TypedMap<string>;
    parents?: IParentCatalog[];
}
export interface IBaseCatalogs {
    catalogs: IBaseCatalog[];
}
export interface ICatalogs {
    catalogs: ICatalog[];
}
