export interface IPerspective {
    /**
     * The key that uniquely identifies the perspective in the registry.
     */
    key: string;
    /**
     * The i18n key that stores the perspective name to be translated.
     */
    nameI18nKey: string;
    /**
     * The i18n key that stores the perspective description to be translated. The description is used as a tooltip in the web application. This is an optional parameter.
     */
    descriptionI18nKey?: string;
    /**
     * A list of feature keys to be bound to the perspective.
     */
    features: string[];
    /**
     * A list of referenced perspectives to be extended to this system perspective. This list is optional.
     */
    perspectives?: string[];
    /**
     * A list of permission names to be bound to the perspective to determine if the user is allowed to see it and use it. This list is optional.
     */
    permissions?: string[];
    /**
     * Used to prevent the default enablement of the 'hot key' mechanism. Default is false.
     */
    isHotkeyDisabled?: boolean;
}
