/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
// File named form-builder.module for now cuz of build blocking me to use Module suffix.

import { CommonModule } from '@angular/common';
import { ANALYZE_FOR_ENTRY_COMPONENTS, ModuleWithProviders, NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';

// Components
import { FormListerComponent } from './components/form-lister/form-lister.component';
import { FormRendererDirective } from './directives/form-renderer/form-renderer.directive';
import { FormBuilderConfig } from './models';

// Services
import { ASYNC_VALIDATOR_MAP, COMPONENT_MAP, VALIDATOR_MAP } from './services/injection-tokens';

@NgModule({
    imports: [CommonModule, ReactiveFormsModule],
    declarations: [FormRendererDirective, FormListerComponent],
    entryComponents: [FormListerComponent],
    exports: [FormRendererDirective]
})
export class FormBuilderModule {
    static forRoot(option: FormBuilderConfig): ModuleWithProviders<FormBuilderModule> {
        return {
            ngModule: FormBuilderModule,
            providers: [
                {
                    provide: ANALYZE_FOR_ENTRY_COMPONENTS,
                    useValue: [option.types],
                    multi: true
                },
                {
                    provide: COMPONENT_MAP,
                    useValue: option.types
                },
                {
                    provide: VALIDATOR_MAP,
                    useValue: option.validators
                },
                {
                    provide: ASYNC_VALIDATOR_MAP,
                    useValue: option.asyncValidators
                }
            ]
        };
    }
}
