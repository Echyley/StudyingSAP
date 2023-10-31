/**
 * ValidatorParameters holds data to the synchronous and
 * asynchronous validators configuration for a FormField.
 */
export declare class ValidatorParameters {
    validators: {
        [index: string]: any;
    };
    asyncValidators: {
        [index: string]: any;
    };
    constructor(validators?: {
        [index: string]: any;
    }, asyncValidators?: {
        [index: string]: any;
    });
    /**
     * Determines if synchronous validator exists.
     *
     * @param name The name of the synchronous validator.
     * @returns A boolean if it has that parameter.
     */
    has(name: string): boolean;
    /**
     * Returns parameters of the synchronous validator.
     *
     * @param name The name of the synchronous validator.
     * @returns The param of the validator.
     */
    get(name: string): null | any;
    /**
     * Determines if asynchronous validator exists.
     *
     * @param name The name of the asynchronous validator.
     * @returns A boolean if it has that parameter.
     */
    hasAsync(name: string): boolean;
    /**
     * Returns parameters of the asynchronous validator.
     *
     * @param name The name of the asynchronous validator.
     * @returns The param of the validator.
     */
    getAsync(name: string): null | any;
    /**
     * @internal
     * Returns a object with all those keys that have
     * undefined values.
     */
    private _omitUndefinedValues;
}
