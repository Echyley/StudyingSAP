import { Injector } from '@angular/core';
import { SeComponentConstructor } from './types';
/**
 * Class level typescript {@link http://www.typescriptlang.org/docs/handbook/decorators.html decorator factory}
 * used to declare a Smartedit custom component from a dependency injection standpoint.
 */
export declare const SeCustomComponent: () => <T extends SeComponentConstructor>(componentConstructor: T) => T;
export declare const parseComponentSelector: (selector: string, seContructor: SeComponentConstructor) => string;
export declare const seCustomComponents: SeComponentConstructor[];
export declare function registerCustomComponents(injector: Injector): void;
