/**
 * Class level typescript {@link http://www.typescriptlang.org/docs/handbook/decorators.html decorator factory}
 * used to declare a Smartedit module as entry point.
 * @param id The module identifier used when loading it into Smartedit.
 */
export declare const SeEntryModule: (id: string) => (moduleConstructor: any) => any;
