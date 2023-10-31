/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ButtonModule } from '@fundamental-ngx/core';
import { FormModule as FundamentalFormModule } from '@fundamental-ngx/core/form';
import {
    GenericEditorDropdownModule,
    SelectModule,
    SpinnerModule,
    TooltipModule,
    TranslationModule,
    MessageModule,
    MEDIA_SELECTOR_I18N_KEY_TOKEN,
    MEDIA_SELECTOR_I18N_KEY,
    IFileValidation,
    moduleUtils
} from 'smarteditcommons';
import {
    MediaPreviewComponent,
    MediaAdvancedPropertiesComponent,
    MediaErrorsComponent,
    MediaFileSelectorComponent,
    MediaSelectorComponent,
    MediaPrinterComponent,
    MediaActionLabelComponent,
    MediaRemoveButtonComponent,
    MediaFormatComponent,
    MediaFormatUploadedComponent,
    MediaUploadFormComponent,
    MediaUploadFieldComponent,
    MediaComponent,
    MediaContainerComponent,
    MediaContainerSelectorItemComponent,
    MediaContainerSelectorComponent
} from './components';

import { MediaAdvancedPropertiesCustomDirective } from './directives/MediaAdvancedPropertiesCustomDirective';
import { MediaUploadFieldsCustomDirective } from './directives/MediaUploadFieldsCustomDirective';
import { MediaBackendValidationHandler, MediaFolderService, MediaUtilService } from './services';
import './media.scss';

@NgModule({
    imports: [
        TranslationModule.forChild(),
        CommonModule,
        FormsModule,
        TooltipModule,
        SelectModule,
        SpinnerModule,
        GenericEditorDropdownModule,
        MessageModule,
        ButtonModule,
        FundamentalFormModule
    ],
    providers: [
        MediaBackendValidationHandler,
        MediaFolderService,
        MediaUtilService,
        {
            provide: MEDIA_SELECTOR_I18N_KEY_TOKEN,
            useValue: MEDIA_SELECTOR_I18N_KEY
        },
        // eslint-disable-next-line prettier/prettier
        moduleUtils.bootstrap((_fileValidation: IFileValidation) => void 0, [IFileValidation])
    ],
    declarations: [
        MediaErrorsComponent,
        MediaFileSelectorComponent,
        MediaPreviewComponent,
        MediaAdvancedPropertiesComponent,
        MediaSelectorComponent,
        MediaPrinterComponent,
        MediaActionLabelComponent,
        MediaRemoveButtonComponent,
        MediaFormatComponent,
        MediaFormatUploadedComponent,
        MediaUploadFormComponent,
        MediaUploadFieldComponent,
        MediaComponent,
        MediaContainerComponent,
        MediaContainerSelectorComponent,
        MediaContainerSelectorItemComponent,
        MediaUploadFieldsCustomDirective,
        MediaAdvancedPropertiesCustomDirective
    ],
    entryComponents: [
        MediaErrorsComponent,
        MediaFileSelectorComponent,
        MediaPreviewComponent,
        MediaAdvancedPropertiesComponent,
        MediaSelectorComponent,
        MediaPrinterComponent,
        MediaActionLabelComponent,
        MediaRemoveButtonComponent,
        MediaFormatComponent,
        MediaFormatUploadedComponent,
        MediaUploadFormComponent,
        MediaUploadFieldComponent,
        MediaComponent,
        MediaContainerComponent,
        MediaContainerSelectorItemComponent
    ]
})
export class MediaModule {}
