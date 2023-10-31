import { IPerspective } from '../perspectives/IPerspective';
export declare abstract class IPerspectiveService {
    /**
     * This method registers a perspective.
     * When an end user selects a perspective in the SmartEdit web application,
     * all features bound to the perspective will be enabled when their respective enablingCallback functions are invoked
     * and all features not bound to the perspective will be disabled when their respective disablingCallback functions are invoked.
     */
    register(configuration: IPerspective): Promise<void>;
    /**
     * This method activates a perspective identified by its key and deactivates the currently active perspective.
     * Activating a perspective consists in activating any feature that is bound to the perspective
     * or any feature that is bound to the perspective's referenced perspectives and deactivating any features
     * that are not bound to the perspective or to its referenced perspectives.
     * After the perspective is changed, the `seConstantsModule.EVENT_PERSPECTIVE_CHANGED`
     * event is published on the {@link CrossFrameEventService}, with no data.
     *
     * @param key The key that uniquely identifies the perspective to be activated. This is the same key as the key used in the [register]{@link IPerspectiveService#register} method.
     */
    switchTo(key: string): Promise<void>;
    /**
     * This method returns true if a perspective is selected.
     */
    hasActivePerspective(): Promise<boolean>;
    /**
     * This method switches the currently-selected perspective to the default perspective.
     * It will also disable all features for the default perspective before enabling them all back.
     * If no value has been stored in the smartedit-perspectives cookie, the value of the default perspective is se.none.
     * If a value is stored in the cookie, that value is used as the default perspective.
     */
    selectDefault(): Promise<void>;
    /**
     * This method returns true if the current active perspective is the Preview mode (No active overlay).
     *
     * @returns A promise with the boolean flag that indicates if the current perspective is the Preview mode.
     */
    isEmptyPerspectiveActive(): Promise<boolean>;
    /**
     * This method is used to refresh the prespective.
     * If there is an exising perspective set then it is refreshed by replaying all the features associated to the current perspective.
     * If there is no perspective set or if the perspective is not permitted then we set the default perspective.
     */
    refreshPerspective(): Promise<void>;
    /**
     * This method returns the key of the perspective that is currently loaded.
     *
     * @returns A promise that resolves to the key of the current perspective loaded in the storefront, null otherwise.
     */
    getActivePerspectiveKey(): Promise<string>;
    /**
     * This method returns true if the active perspective has the hotkey enabled
     */
    isHotkeyEnabledForActivePerspective(): Promise<boolean>;
    getActivePerspective(): IPerspective;
    getPerspectives(): Promise<IPerspective[]>;
}
