import { Cloneable } from 'smarteditcommons';
export interface SmarteditBundleProperties {
    [index: string]: Cloneable;
    domain: string;
    smarteditroot: string;
    applications: string[];
}
export interface SmarteditBundleJsFile {
    src: string;
    namespaceToCheck?: string;
}
export interface SmarteditBundle {
    properties: SmarteditBundleProperties;
    js: SmarteditBundleJsFile[];
    css: string[];
}
