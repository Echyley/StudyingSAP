/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { CommonModule } from '@angular/common';
import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import {
    AbstractControl,
    FormsModule,
    ReactiveFormsModule,
    ValidationErrors,
    Validators
} from '@angular/forms';

import { FormBuilderModule, TranslationModule } from '@smart/utils';
import { FundamentalsModule } from '../../FundamentalsModule';
import { YjqueryModule } from '../../services/vendors/YjqueryModule';
import { SharedComponentsModule } from '../../SharedComponentsModule';
import { SelectModule } from '../select';
import { GenericEditorBreadcrumbComponent } from './components/breadcrumb';
import { ContentManager } from './components/ContentManager';
import { GenericEditorDropdownModule } from './components/dropdown';
import { GenericEditorDynamicFieldComponent } from './components/dynamicField/GenericEditorDynamicFieldComponent';
import { FormBuilderDirective } from './components/formBuilder/FormBuilderDirective';
import { GenericEditorFieldComponent } from './components/GenericEditorFieldComponent';
import { GenericEditorFieldMessagesComponent } from './components/GenericEditorFieldMessagesComponent';
import { GenericEditorFieldWrapperComponent } from './components/GenericEditorFieldWrapperComponent';
import { GenericEditorTabComponent } from './components/GenericEditorTabComponent';
import { LocalizedElementComponent } from './components/LocalizedElementComponent';
import { GenericEditorRootTabsComponent } from './components/rootTabs/GenericEditorRootTabsComponent';
import { SubmitBtnDirective } from './components/SubmitButtonDirective';
import { GenericEditorComponent } from './GenericEditorComponent';
import { GenericEditorFactoryService } from './GenericEditorFactoryService';
import { isBlankValidator } from './services/BlankValidator';
import { EditorFieldMappingService } from './services/EditorFieldMappingService';
import { FetchEnumDataHandler } from './services/FetchEnumDataHandler';
import { GenericEditorStackService } from './services/GenericEditorStackService';
import { GenericEditorStateBuilderService } from './services/GenericEditorStateBuilderService';
import { GenericEditorTabService } from './services/GenericEditorTabService';
import { SeValidationMessageParser } from './services/SeValidationMessageParser';
import { GenericEditorWidgetModule } from './widgets';

/**
 * Form Builder Setup
 */

@NgModule({
    schemas: [CUSTOM_ELEMENTS_SCHEMA],
    imports: [
        CommonModule,
        YjqueryModule,
        FormsModule,
        ReactiveFormsModule,
        FundamentalsModule,
        SharedComponentsModule,
        GenericEditorWidgetModule,
        SelectModule,
        GenericEditorDropdownModule,
        TranslationModule.forChild(),
        FormBuilderModule.forRoot({
            validators: {
                required: (): ((control: AbstractControl) => ValidationErrors) =>
                    Validators.required,
                email: (): ((control: AbstractControl) => ValidationErrors) => Validators.email,
                notBlank: (): ((control: AbstractControl) => ValidationErrors) => isBlankValidator()
            },
            types: {
                tabs: GenericEditorRootTabsComponent,
                localized: LocalizedElementComponent,
                field: GenericEditorDynamicFieldComponent
            }
        })
    ],
    providers: [
        GenericEditorFactoryService,
        EditorFieldMappingService,
        FetchEnumDataHandler,
        GenericEditorStackService,
        GenericEditorTabService,
        SeValidationMessageParser,
        GenericEditorStateBuilderService
    ],
    declarations: [
        GenericEditorDynamicFieldComponent,
        GenericEditorComponent,
        GenericEditorTabComponent,
        LocalizedElementComponent,
        GenericEditorFieldComponent,
        GenericEditorFieldMessagesComponent,
        GenericEditorFieldWrapperComponent,
        GenericEditorBreadcrumbComponent,
        FormBuilderDirective,
        ContentManager,
        GenericEditorRootTabsComponent,
        SubmitBtnDirective
    ],
    entryComponents: [
        GenericEditorComponent,
        GenericEditorTabComponent,
        LocalizedElementComponent,
        GenericEditorFieldComponent,
        GenericEditorFieldMessagesComponent,
        GenericEditorFieldWrapperComponent,
        GenericEditorBreadcrumbComponent
    ],
    exports: [GenericEditorComponent, GenericEditorWidgetModule, GenericEditorDropdownModule]
})
export class SeGenericEditorModule {}
