/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ObjectStatusModule } from '@fundamental-ngx/core/object-status';
import { TranslateModule } from '@ngx-translate/core';
import { SettingsService } from 'smarteditcommons/services';
import { RichTextFieldComponent } from './RichTextFieldComponent';
import { GenericEditorSanitizationService } from './services/GenericEditorSanitizationService';
import { RichTextFieldLocalizationService } from './services/RichTextFieldLocalizationService';
import { RichTextLoaderService } from './services/RichTextLoaderService';
import { RESOLVED_LOCALE_TO_CKEDITOR_LOCALE_MAP, RICH_TEXT_CONFIGURATION } from './tokens';
@NgModule({
    imports: [CommonModule, FormsModule, TranslateModule.forChild(), ObjectStatusModule],
    providers: [
        GenericEditorSanitizationService,
        RichTextFieldLocalizationService,
        RichTextLoaderService,
        SettingsService,
        {
            provide: RESOLVED_LOCALE_TO_CKEDITOR_LOCALE_MAP,
            useValue: {
                in: 'id',
                es_CO: 'es'
            }
        },
        {
            provide: RICH_TEXT_CONFIGURATION,
            useValue: {
                toolbar: 'full',
                toolbar_full: [
                    {
                        name: 'basicstyles',
                        items: ['Bold', 'Italic', 'Strike', 'Underline']
                    },
                    {
                        name: 'paragraph',
                        items: ['BulletedList', 'NumberedList', 'Blockquote']
                    },
                    {
                        name: 'editing',
                        items: ['JustifyLeft', 'JustifyCenter', 'JustifyRight', 'JustifyBlock']
                    },
                    {
                        name: 'links',
                        items: ['Link', 'Unlink', 'Anchor']
                    },
                    {
                        name: 'tools',
                        items: ['SpellChecker', 'Maximize']
                    },
                    '/',
                    {
                        name: 'styles',
                        items: [
                            'Format',
                            'Font',
                            'FontSize',
                            'TextColor',
                            'PasteText',
                            'PasteFromWord',
                            'RemoveFormat'
                        ]
                    },
                    {
                        name: 'insert',
                        items: ['Image', 'Table', 'SpecialChar']
                    },
                    {
                        name: 'forms',
                        items: ['Outdent', 'Indent']
                    },
                    '/',
                    {
                        name: 'clipboard',
                        items: ['Undo', 'Redo']
                    },
                    {
                        name: 'document',
                        items: ['PageBreak', 'Source']
                    }
                ],
                disableNativeSpellChecker: false,
                height: '100px',
                width: '100%',
                autoParagraph: false,
                basicEntities: false,
                fillEmptyBlocks: false,
                extraAllowedContent: 'div(*)[role]',
                extraPlugins: 'colorbutton, colordialog, font',
                font_names:
                    'Arial/Arial, Helvetica, sans-serif;Times New Roman/Times New Roman, Times, serif;Verdana',
                fontSize_sizes:
                    '10/10px;12/12px;14/14px;16/16px;18/18px;20/20px;22/22px;24/24px;36/36px;48/48px;',
                protectedSource: [/\r|\n|\r\n|\t/g]
            }
        }
    ],
    declarations: [RichTextFieldComponent],
    entryComponents: [RichTextFieldComponent],
    exports: [RichTextFieldComponent]
})
export class RichTextFieldModule {}
