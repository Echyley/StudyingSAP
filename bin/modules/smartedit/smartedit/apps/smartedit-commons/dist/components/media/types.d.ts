import { EventEmitter, InjectionToken, Type } from '@angular/core';
import { TypedMap } from '@smart/utils';
import { Media } from 'smarteditcommons/index';
export interface MediaFileSelectorCustom {
    component: Type<any>;
}
export interface MediaFileSelectorCustomInjector {
    onSelect: (file: FileList | string) => void;
}
export declare const MEDIA_FILE_SELECTOR_CUSTOM_TOKEN: InjectionToken<MediaFileSelectorCustom>;
export declare const MEDIA_FILE_SELECTOR_CUSTOM_INJECTOR_TOKEN: InjectionToken<MediaFileSelectorCustomInjector>;
export declare const MEDIA_SELECTOR_I18N_KEY: {
    UPLOAD: string;
    REPLACE: string;
    UNDER_EDIT: string;
    REMOVE: string;
};
export declare type MediaSelectorI18nKey = typeof MEDIA_SELECTOR_I18N_KEY;
export declare const MEDIA_SELECTOR_I18N_KEY_TOKEN: InjectionToken<{
    UPLOAD: string;
    REPLACE: string;
    UNDER_EDIT: string;
    REMOVE: string;
}>;
export interface IMediaUploadFieldsCustomComponent {
    /** @input The reference of parent component's image */
    image: any;
    /** @output emit the changed custom fields, e.g. { purpose: "some purpose" } */
    customFieldsChange: EventEmitter<TypedMap<string>>;
}
export interface MediaUploadFieldsCustom {
    component: Type<IMediaUploadFieldsCustomComponent>;
}
export declare const MEDIA_UPLOAD_FIELDS_CUSTOM_TOKEN: InjectionToken<MediaUploadFieldsCustom>;
export interface IMediaAdvancedPropertiesCustomComponent {
    media: Media;
}
export interface MediaAdvancedPropertiesCustom {
    component: Type<IMediaAdvancedPropertiesCustomComponent>;
}
export declare const MEDIA_ADVANCED_PROPERTIES_CUSTOM_TOKEN: InjectionToken<MediaAdvancedPropertiesCustom>;
