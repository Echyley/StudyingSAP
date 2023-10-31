import { CMSItem } from 'smarteditcommons';
/**
 * @description
 * Interface for cms-component information
 */
export interface ICMSComponent extends CMSItem {
    uuid: string;
    visible: boolean;
    restricted?: boolean;
    cloneable: boolean;
    /** Contains the uuid of the slots where this component is used. */
    slots: string[];
    children?: any[];
}
