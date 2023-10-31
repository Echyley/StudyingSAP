import { TypedMap, CMSItem } from 'smarteditcommons';
export { NavigationNode, NavigationNodeEntry } from 'smarteditcommons';
export interface NavigationNodeCMSItem extends CMSItem {
    catalogVersion: string;
    visible: boolean;
    title: TypedMap<string>;
    uuid: string;
    uid: string;
    entries: string[];
    pages: number[];
    itemType: string;
    modifiedtime: Date;
    children: string[];
    name: string;
    links: string[];
    creationtime: Date;
}
