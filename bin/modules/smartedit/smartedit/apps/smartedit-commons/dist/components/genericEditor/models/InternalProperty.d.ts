/**
 * @internal
 */
export declare const INTERNAL_PROP_NAME = "$$internal";
/**
 * @internal
 * Adds an internal property on the component model for
 * watching property changes on an object.
 */
export declare class InternalProperty {
    private _map;
    /**
     * Watch property changes.
     */
    watch(property: string, fn: (value: any) => void): () => void;
    /**
     * Trigger prop change.
     *
     * @param property
     * @param value New value
     */
    trigger(property: string, value: any): void;
}
/**
 * @internal
 * Creates a proxied object to listen on property changes
 * for backwards compatibility with object mutations made by
 * widgets. This is used to proxy the model data called component in the
 * generic editor. The component data is the model that is
 * used for submitting to the backend. Old widgets mutate the
 * properties of component object, thus there is not way to
 * listen on properties changes except for the use of the ES6 Proxy
 * API. Some properties that are watched inside of the GenericEditorField
 * update the value of the AbstractForm of Angular used for validation.
 *
 * NOTE:
 * This function uses Proxy which is not supported in IE.
 */
export declare const proxifyDataObject: (obj: any) => any;
