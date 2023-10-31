/**
 * @description
 * Interface for cms-restriction information
 */
export interface CMSRestriction {
    name: string;
    type: {
        [language: string]: string;
    };
    typeCode: string;
    description: string;
}
