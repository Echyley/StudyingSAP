import { ComponentType } from '../models';
import { Registry } from './registry';
export interface ComponentTypeMap {
    [name: string]: ComponentType;
}
/**
 * A registry for form components.
 */
export declare class ComponentRegistryService extends Registry<ComponentType> {
    constructor(types: ComponentTypeMap);
}
