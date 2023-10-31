export interface InternalFeature {
    /**
     * The feature (could be IDecorator | IToolbarItem | IContextualMenuButton) key defined in the API
     */
    key: string;
    /**
     * The i18n key that stores the feature name to be translated.
     */
    nameI18nKey?: string;
    /**
     * The i18n key that stores the feature description to be translated. The description is used as a tooltip in the web application. This is an optional parameter.
     */
    descriptionI18nKey?: string;
    /**
     * The list of permissions required to enable the feature.
     */
    permissions?: string[];
}
/**
 * Describes feature configuration DTO in FeatureService
 */
export interface IFeature extends InternalFeature {
    /**
     * The callback to be called when feature is enabled.
     */
    enablingCallback?: () => void;
    /**
     * The callback to be called when feature is disbled.
     */
    disablingCallback?: () => void;
}
