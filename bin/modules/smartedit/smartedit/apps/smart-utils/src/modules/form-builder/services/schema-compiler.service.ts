/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { Injectable } from '@angular/core';
import { AsyncValidatorFn, ValidatorFn } from '@angular/forms';

import { FormListerComponent } from '../components/form-lister/form-lister.component';
import { FormBuilderModule } from '../form-builder.module';
import {
    AbstractForm,
    AbstractForms,
    AbstractFormSchema,
    ComponentType,
    FormField,
    FormFieldSchema,
    FormGrouping,
    FormGroupSchema,
    FormList,
    FormListSchema,
    InputProperties,
    ValidatorParameters
} from '../models';

import {
    AsyncValidatorMap,
    AsyncValidatorRegistryService
} from './async-validator-registry.service';
import { ComponentRegistryService, ComponentTypeMap } from './component-registry.service';
import { Registry } from './registry';
import { ValidatorMap, ValidatorRegistryService } from './validator-registry.service';

/**
 * Options to be passed inline into the compiler.
 */
export interface SchemaCompilerOptions {
    /**
     * A map of Angular components to be passed inline. Component found
     * in this map will take precedence of those from the registry.
     */
    components?: ComponentTypeMap;
    /**
     * A map of validator functions to be passed inline.
     * Validators found in this map will take precedence of those
     * taken from the registry.
     */
    validators?: ValidatorMap;
    /**
     * A map of async validators to be passed inline.
     * Validators found in this map will take precedence of those
     * taken from the registry.
     */
    asyncValidators?: AsyncValidatorMap;
}

/**
 * Schema compilers service is used for compiling a schema to concrete classes for use
 * by the FormRendererDirective.
 */
@Injectable({
    providedIn: FormBuilderModule
})
export class SchemaCompilerService {
    constructor(
        private types: ComponentRegistryService,
        private validators: ValidatorRegistryService,
        private asyncValidators: AsyncValidatorRegistryService
    ) {}

    /**
     * Compile a schema group.
     *
     * @param value
     * @param schema
     * @returns
     */
    compileGroup(
        value: any | null,
        schema: FormGroupSchema,
        options: SchemaCompilerOptions = {}
    ): FormGrouping {
        const abstractForms = Object.keys(schema.schemas).reduce((acc, key: string) => {
            acc[key] = this._toAbstractForm(
                value ? value[key] : null,
                schema.schemas[key],
                options
            );
            return acc;
        }, {} as AbstractForms);

        return new FormGrouping(abstractForms, this._getValidators(schema, options), {
            component: schema.component
                ? this._getComponent(schema.component, options.components)
                : FormListerComponent,
            inputs: new InputProperties(schema.inputs),
            validatorParams: new ValidatorParameters(schema.validators, schema.asyncValidators),
            persist: this._toPersist(schema.persist)
        });
    }

    /**
     * Compiles a list of values with a schema.
     *
     * @param values An array of values.
     * @param listSchema
     */
    compileList(
        values: any[],
        schema: FormListSchema,
        options: SchemaCompilerOptions = {}
    ): FormList {
        /**
         * The schema list for each value since each value can have different
         * schemas. Or they can have the same schema for all values in the list.
         */
        const schemaList = Array.isArray(schema.schema) ? schema.schema : [schema.schema];

        if (!schemaList.length) {
            throw Error(
                'SchemaCompilerService - One or more schemas must be provided to compile a form list.'
            );
        }

        const list = (Array.isArray(values) ? values : []).map((value: any, index: number) => {
            const childSchema = schemaList[index]
                ? /**
                   * Get the schema one to one for the value, or get the last schema
                   * which may be repeated for all values.
                   */
                  schemaList[index]
                : schemaList[schemaList.length - 1];

            return this._toAbstractForm(value, childSchema, options);
        });

        return new FormList(list, this._getValidators(schema, options), {
            component: this._getComponent(schema.component, options.components),
            inputs: new InputProperties(schema.inputs),
            validatorParams: new ValidatorParameters(schema.validators, schema.asyncValidators),
            persist: this._toPersist(schema.persist)
        });
    }

    /**
     * Compiles a schema field.
     *
     * @param value
     * @param {FormFieldSchema} schema
     * @returns {FormField}
     */
    compileField(
        value: any,
        schema: FormFieldSchema,
        options: SchemaCompilerOptions = {}
    ): FormField {
        return new FormField(
            { value, disabled: schema.disabled },
            this._getValidators(schema, options),
            {
                component: this._getComponent(schema.component, options.components),
                inputs: new InputProperties(schema.inputs),
                validatorParams: new ValidatorParameters(schema.validators, schema.asyncValidators),
                persist: this._toPersist(schema.persist)
            }
        );
    }

    /**
     * @internal
     * Returns form validators and ayncValidators
     * @param schema
     * @param options
     */
    private _getValidators(
        schema: {
            validators?: {
                [index: string]: any;
            };
            asyncValidators?: {
                [index: string]: any;
            };
        },
        options: SchemaCompilerOptions = {}
    ): { validators: ValidatorFn[]; asyncValidators: AsyncValidatorFn[] } {
        let validators: ValidatorFn[] = [];
        let asyncValidators: AsyncValidatorFn[] = [];

        if (schema.validators) {
            validators = this._mapValidator<ValidatorRegistryService, ValidatorFn>(
                schema.validators,
                this.validators,
                options.validators
            );
        }

        if (schema.asyncValidators) {
            asyncValidators = this._mapValidator<AsyncValidatorRegistryService, AsyncValidatorFn>(
                schema.asyncValidators,
                this.asyncValidators,
                options.asyncValidators
            );
        }

        return {
            validators,
            asyncValidators
        };
    }

    /**
     * @internal
     * @param value
     * @param schema
     */
    private _toAbstractForm(
        value: any | null,
        schema: AbstractFormSchema,
        options: SchemaCompilerOptions
    ): AbstractForm {
        if (schema.type === 'field') {
            return this.compileField(value, schema as FormFieldSchema, options);
        }
        if (schema.type === 'group') {
            return this.compileGroup(value, schema as FormGroupSchema, options);
        }
        return this.compileList(value, schema as FormListSchema, options);
    }

    /**
     * @internal
     *
     * Maps schema validators to actual validators in the registry and passes custom params to a validator.
     * If params are undefined then the validator isn't added to the array of validators. Validators
     * that are found the inline registry will take precedence of those in registries.
     */
    private _mapValidator<T extends Registry<any>, M>(
        validators: { [index: string]: any },
        registry: T,
        inline: { [key: string]: (...args: any[]) => M } = {}
    ): any[] {
        return Object.keys(validators).reduce((acc, name) => {
            const params = validators[name];
            if (params !== undefined) {
                const fn = inline[name] ? inline[name] : registry.get(name);

                if (!fn) {
                    throw new Error(
                        `SchemaCompilerService - Validator not found in ${this.validators.constructor.name} for: ${name}.`
                    );
                }

                acc.push(fn(params));
            }
            return acc;
        }, [] as any[]);
    }

    /**
     * @internal
     * Sets default to true if parameter persist is undefined.
     */
    private _toPersist(persist: boolean | undefined = true): boolean {
        return persist;
    }

    /**
     * @internal
     * Decides if should get the type from the inline map or registry.
     * If no component is found, it would throw an error.
     *
     * @param name The name of the component in the registry.
     * @param components An component type name, used for inline components.
     */
    private _getComponent(name: string, components: ComponentTypeMap = {}): ComponentType {
        const comp = components[name] ? components[name] : this.types.get(name);

        if (!comp) {
            throw new Error(`SchemaCompilerService - Did not find component for: ${name}.`);
        }

        return comp;
    }
}
