import { FormField, FormFieldSchema, FormGrouping, FormGroupSchema, FormList, FormListSchema } from '../models';
import { AsyncValidatorMap, AsyncValidatorRegistryService } from './async-validator-registry.service';
import { ComponentRegistryService, ComponentTypeMap } from './component-registry.service';
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
export declare class SchemaCompilerService {
    private types;
    private validators;
    private asyncValidators;
    constructor(types: ComponentRegistryService, validators: ValidatorRegistryService, asyncValidators: AsyncValidatorRegistryService);
    /**
     * Compile a schema group.
     *
     * @param value
     * @param schema
     * @returns
     */
    compileGroup(value: any | null, schema: FormGroupSchema, options?: SchemaCompilerOptions): FormGrouping;
    /**
     * Compiles a list of values with a schema.
     *
     * @param values An array of values.
     * @param listSchema
     */
    compileList(values: any[], schema: FormListSchema, options?: SchemaCompilerOptions): FormList;
    /**
     * Compiles a schema field.
     *
     * @param value
     * @param {FormFieldSchema} schema
     * @returns {FormField}
     */
    compileField(value: any, schema: FormFieldSchema, options?: SchemaCompilerOptions): FormField;
    /**
     * @internal
     * Returns form validators and ayncValidators
     * @param schema
     * @param options
     */
    private _getValidators;
    /**
     * @internal
     * @param value
     * @param schema
     */
    private _toAbstractForm;
    /**
     * @internal
     *
     * Maps schema validators to actual validators in the registry and passes custom params to a validator.
     * If params are undefined then the validator isn't added to the array of validators. Validators
     * that are found the inline registry will take precedence of those in registries.
     */
    private _mapValidator;
    /**
     * @internal
     * Sets default to true if parameter persist is undefined.
     */
    private _toPersist;
    /**
     * @internal
     * Decides if should get the type from the inline map or registry.
     * If no component is found, it would throw an error.
     *
     * @param name The name of the component in the registry.
     * @param components An component type name, used for inline components.
     */
    private _getComponent;
}
