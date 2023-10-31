import { Observable } from 'rxjs';
/**
 * Event payload when a property changes.
 */
export declare class InputPropertyChange {
    key: string;
    value: any;
    constructor(key: string, value: any);
}
/**
 * Used for storing component input values for the dynamic component. The values
 * are set onto the dynamic component's properties that are decorated by the @DynamicInput()
 * decorator. Values can be retrieved or set programmatically by the form element's 'input'
 * property.
 */
export declare class InputProperties {
    readonly changes: Observable<InputPropertyChange>;
    /**
     * @internal
     */
    private readonly _map;
    constructor(object?: {
        [key: string]: any;
    });
    /**
     * Get a property.
     *
     * @param {keyof T} key
     * @returns {T[keyof T] | undefined} value
     */
    get<T>(key: keyof T): T[keyof T] | undefined;
    /**
     * Setting a property.
     *
     * @param {keyof T} key
     * @param {T[keyof T]} value
     * @param {boolean} emit If emit is set to false. It will not emit changes to the
     * the component for those observing for property changes.
     */
    set<T>(key: keyof T, value: T[keyof T], emit?: boolean): void;
}
