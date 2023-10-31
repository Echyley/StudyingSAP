import { TypedMap, CMSItem } from 'smarteditcommons';
export interface CMSLinkItem extends CMSItem {
    currentSelectedOptionValue: string;
    external: boolean;
    linkName: TypedMap<string>;
    linkTo: string;
    position: number;
    product?: string;
    productCatalog: string;
    restrictions: string[];
    slotId: string;
    styleClasses?: string;
    target: boolean;
    visible: boolean;
}
