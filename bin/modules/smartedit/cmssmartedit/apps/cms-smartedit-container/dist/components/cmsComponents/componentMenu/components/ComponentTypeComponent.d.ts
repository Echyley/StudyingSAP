export interface CMSComponentTypeInfo {
    code: string;
    i18nKey: string;
    name: string;
    technicalUniqueId?: string;
}
export declare class ComponentTypeComponent {
    typeInfo: CMSComponentTypeInfo;
}
