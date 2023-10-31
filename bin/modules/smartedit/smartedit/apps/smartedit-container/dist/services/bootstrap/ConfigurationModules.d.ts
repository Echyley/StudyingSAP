import { TypedMap } from 'smarteditcommons';
/** @internal */
export interface Module {
    location: string;
    name: string;
    extends?: string;
}
/** @internal */
export interface ConfigurationModules {
    authenticationMap: TypedMap<string>;
    applications: Module[];
}
