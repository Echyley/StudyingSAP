/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { EventEmitter, InjectionToken, Type } from '@angular/core';
import { TypedMap } from '@smart/utils';
import { Media } from 'smarteditcommons/index';

// media file selector customization.
export interface MediaFileSelectorCustom {
    component: Type<any>;
}

export interface MediaFileSelectorCustomInjector {
    onSelect: (file: FileList | string) => void;
}

export const MEDIA_FILE_SELECTOR_CUSTOM_TOKEN = new InjectionToken<MediaFileSelectorCustom>(
    'MEDIA_FILE_SELECTOR_CUSTOM_TOKEN'
);

export const MEDIA_FILE_SELECTOR_CUSTOM_INJECTOR_TOKEN =
    new InjectionToken<MediaFileSelectorCustomInjector>(
        'MEDIA_FILE_SELECTOR_CUSTOM_INJECTOR_TOKEN'
    );

export const MEDIA_SELECTOR_I18N_KEY = {
    UPLOAD: 'se.media.format.upload',
    REPLACE: 'se.media.format.replace',
    UNDER_EDIT: 'se.media.format.under.edit',
    REMOVE: 'se.media.format.remove'
};

export type MediaSelectorI18nKey = typeof MEDIA_SELECTOR_I18N_KEY;

export const MEDIA_SELECTOR_I18N_KEY_TOKEN = new InjectionToken<MediaSelectorI18nKey>(
    'MEDIA_SELECTOR_I18N_KEY_TOKEN'
);

// media upload form field customization.
export interface IMediaUploadFieldsCustomComponent {
    /** @input The reference of parent component's image */
    image: any;
    /** @output emit the changed custom fields, e.g. { purpose: "some purpose" } */
    customFieldsChange: EventEmitter<TypedMap<string>>;
}
export interface MediaUploadFieldsCustom {
    component: Type<IMediaUploadFieldsCustomComponent>;
}

export const MEDIA_UPLOAD_FIELDS_CUSTOM_TOKEN = new InjectionToken<MediaUploadFieldsCustom>(
    'MEDIA_UPLOAD_FIELDS_CUSTOM_TOKEN'
);

// media advanced properties customization
export interface IMediaAdvancedPropertiesCustomComponent {
    media: Media;
}

export interface MediaAdvancedPropertiesCustom {
    component: Type<IMediaAdvancedPropertiesCustomComponent>;
}

export const MEDIA_ADVANCED_PROPERTIES_CUSTOM_TOKEN =
    new InjectionToken<MediaAdvancedPropertiesCustom>('MEDIA_ADVANCED_PROPERTIES_CUSTOM_TOKEN');
