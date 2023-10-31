/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/* forbiddenNameSpaces useValue:false */
/* forbiddenNameSpaces useFactory:false */
/* forbiddenNameSpaces useClass:false */
import { TypedMap } from '@smart/utils';

export interface SeDirectiveDefinition {
    /**
     * The CSS selector that triggers the instantiation of a directive.
     * selector may be declared as one of the following:
     *
     *      <ul>
     *          <li>element-name: select by element name.</li>
     *          <li>.class: select by class name.</li>
     *          <li>[attribute]: select by attribute name.</li>
     *      </ul>
     *
     * If no selector is set, will default to an element named as the lower camel case of the component class.
     */
    selector?: string;
    /**
     * The array of input data binding
     * The inputs property defines a set of directiveProperty to bindingProperty configuration:
     *
     *      <ul>
     *          <li>directiveProperty specifies the component property where the value is written.</li>
     *          <li>bindingProperty specifies the binding type and/or the DOM property where the value is read from.</li>
     *          binding type is legacy support for "@", "&" and "=" of Angular 1.x
     *      </ul>
     *
     * example: `inputs: ['bankName', 'id: account-id']`
     */
    inputs?: string[];
    // outputs?: string[]
    // host?: {...}
    /**
     * The list of
     * - [service classes]{@link SeClassProvider}
     * - [service factories]{@link SeFactoryProvider}
     * - [value]{@link SeValueProvider value}
     * or multi providers to be injected into the component.
     */
    providers?: SeProvider[];
    // exportAs?: string
    // queries?: {...},
    require?: string | string[] | TypedMap<string>;
    transclude?: boolean | TypedMap<string>;
    replace?: boolean;
    controllerAs?: string;
    template?: string;
    templateUrl?: string;
    scope?: boolean | { [boundProperty: string]: string };
}

export interface SeComponentDefinition extends SeDirectiveDefinition {
    /** for Angular components, if set to true, the component will be made available as a web component but with a selector to which  is appended. */
    custom?: boolean;
    // changeDetection?: ChangeDetectionStrategy
    // viewProviders?: Provider[]
    // moduleId?: string
    /** the HTML file location for this component */
    templateUrl?: string;
    /** the inline HTML template for this component */
    template?: string;
    // styleUrls?: string[]
    // styles?: string[]
    // animations?: any[]
    // encapsulation?: ViewEncapsulation
    // interpolation?: [string, string]
    /** the array of {@link SeComponent} that this new one requires. */
    entryComponents?: SeComponentConstructor[];
    // preserveWhitespaces?: boolean
    // inherited from core/Directive
}

export interface SeModuleDefinition {
    /**
     * The array of [\@SeDirective]{@link SeDirective} and [\@SeComponent]{@link SeComponent} on which this new [\@SeModule]{@link SeModule} depends.
     */
    declarations?: (SeDirectiveConstructor | SeComponentConstructor | SeFilterConstructor)[];
    entryComponents?: (SeDirectiveConstructor | SeComponentConstructor)[];

    /**
     * The array of modules on which this new module depends.
     * <br/> This is a mixed array of string (legacy approach) and [\@SeModule]{@link SeModule} annotated classes (recommended approach).
     */
    imports?: (string | SeModuleConstructor | SeModuleWithProviders)[];

    /**
     * The list of
     * [service classes]{@link SeClassProvider},
     * [service factories]{@link SeFactoryProvider},
     * [value]{@link SeValueProvider}
     * or multi providers to be injected into this new module.
     */
    providers?: SeProvider[];
    /**
     * The injectable callback to be executed at configuration time.
     */
    config?: (...args: any[]) => void;
    /**
     * The injectable callback to be executed at startup time.
     */
    initialize?: (...args: any[]) => void;
}

export type SeFactory = (...arg: any[]) => any;

export type SeConstructor<T = any> = new (...arg: any[]) => T;

export interface SeModuleConstructor extends SeConstructor {
    // used by Smartedit DI
    moduleName?: string;
}

export interface SeDirectiveConstructor extends SeConstructor {
    directiveName?: string;
    // definition?: angular.IDirective;
    // will be browsed by owning @SeModule
    providers?: SeProvider[];
}

export interface SeFilterConstructor extends SeConstructor {
    filterName?: string;
    transform: (...deps: any[]) => (...args: any[]) => any;
}

export interface SeComponentConstructor extends SeConstructor {
    componentName?: string;
    selector?: string;
    // definition?: angular.IComponentOptions;
    // will be browsed by owning @SeModule
    entryComponents?: SeComponentConstructor[];
    providers?: SeProvider[];
}

export interface SeBaseProvider {
    /**
     * The provider name.
     */
    provide: string;

    /**
     * If set to true, an array of instances will be provided for the same provider name. Useful for
     * configuring a module by many modules.
     */
    multi?: boolean;
}

/**
 * Configures an injectable value provider in a [module]{@link SeModule}, component or directive.
 */
export interface SeValueProvider extends SeBaseProvider {
    /**
     * An instance value of the provider.
     */
    useValue: any;
}

/**
 * Configures an injectable class provider in a [module]{@link SeModule}, component or directive.
 */
export interface SeClassProvider extends SeBaseProvider {
    /**
     * A class to invoke of the provider.
     */
    useClass: SeConstructor;
}

/**
 * Configures an injectable factory provider in a [module]{@link SeModule}, component or directive.
 */
export interface SeFactoryProvider extends SeBaseProvider {
    /**
     * A function to invoke the construction of the provider.
     */
    useFactory: SeFactory;

    /**
     * A list of strings or referenced dependencies to be injected into the factory. The 'ngInject;' hint may be used
     * in replacement of this property.
     */
    deps?: (SeConstructor | SeFactory | string)[];
}

/**
 * The returning type of a configurable module.
 */
export interface SeModuleWithProviders {
    seModule: SeModuleConstructor;
    providers: SeProvider[];
}

export type SeProvider =
    | SeValueProvider
    | SeClassProvider
    | SeFactoryProvider
    | SeConstructor
    | SeFactory;
