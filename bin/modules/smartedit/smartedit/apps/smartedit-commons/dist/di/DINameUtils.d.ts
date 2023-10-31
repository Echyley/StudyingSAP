import { SeConstructor, SeFactory } from './types';
/** @internal */
export declare class DINameUtils {
    buildComponentName(componentConstructor: SeConstructor): string;
    buildServiceName(serviceConstructor: SeConstructor | SeFactory): string;
    buildModuleName(moduleConstructor: SeConstructor | SeFactory): string;
    buildName(myConstructor: SeConstructor | SeFactory): string;
    convertNameCasing(originalName: string): string;
}
export declare const diNameUtils: DINameUtils;
