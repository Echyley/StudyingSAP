import { Cloneable, TypedMap } from 'smarteditcommons';
/** @internal */
export declare class ConfigurationItem {
    key: string;
    value: string;
    isNew?: boolean;
    toDelete?: boolean;
    secured?: boolean;
    requiresUserCheck?: boolean;
    isCheckedByUser?: boolean;
    hasErrors?: boolean;
    uuid?: string;
    errors?: TypedMap<{
        message: string;
    }[]>;
}
/** @internal */
export declare type Configuration = ConfigurationItem[];
/** @internal */
export declare type ConfigurationObject = TypedMap<Cloneable>;
