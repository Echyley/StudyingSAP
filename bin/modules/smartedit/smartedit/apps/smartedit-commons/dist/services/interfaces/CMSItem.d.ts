import { Payload } from '@smart/utils';
/**
 * @description
 * Interface for CMSItem information
 */
export interface CMSItem extends Payload {
    name: string;
    typeCode: string;
    itemtype?: string;
    uid: string;
    uuid: string;
    catalogVersion: string;
    componentType?: string;
    creationtime?: Date;
    modifiedtime?: Date;
    onlyOneRestrictionMustApply?: boolean;
}
