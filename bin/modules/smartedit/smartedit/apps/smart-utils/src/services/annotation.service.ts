/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import * as lodash from 'lodash';
import { TypedMap } from '../dtos';
import { Class } from '../types';
import { FunctionsUtils } from '../utils';
import {} from 'reflect-metadata';

/**
 * @ngdoc object
 * @name @smartutils.object:MethodAnnotation
 *
 * @description
 * Shorthand signature of a {@link https://www.typescriptlang.org/docs/handbook/decorators.html Typescript Decorator} function for methods
 * used by {@link @smartutils.services:AnnotationService AnnotationService}.
 * @param {any} target the instance the method of which is annotated
 * @param {any} propertyName the name of the method that is annotated
 * @param {any} originalMethod the original method being annotated, it is prebound to the instance
 * @returns {any} the final return value of the proxied method.
 * It is left to implementers to discard, modify, reuse the original method.
 */
export type MethodAnnotation = (
    target: any,
    propertyName: string,
    originalMethod: (...x: any[]) => any,
    ...invocationArguments: any[]
) => any;
/**
 * @ngdoc object
 * @name @smartutils.object:MethodAnnotationFactory
 *
 * @description
 * A {@link @smartutils.object:MethodAnnotation MethodAnnotation} factory
 * used by {@link @smartutils.services:AnnotationService AnnotationService}.
 * @param {...any[]} factoryArguments the factory arguments
 * @returns {MethodAnnotation} {@link @smartutils.object:MethodAnnotation MethodAnnotation}
 */
export type MethodAnnotationFactory = (...factoryArguments: any[]) => MethodAnnotation;
/**
 * @ngdoc object
 * @name @smartutils.object:ClassAnnotation
 *
 * @description
 * Shorthand signature of a {@link https://www.typescriptlang.org/docs/handbook/decorators.html Typescript Decorator} function for classes
 * used by {@link @smartutils.services:AnnotationService AnnotationService}.
 * @param {any} instance an instance of the class which is annotated
 * @param {(...x: any[]) => any} originalConstructor the prebound original constructor of the instance
 * @param {...any[]} invocationArguments the arguments with which the constructor is invoked
 * @returns {any} void or a new instance.
 * It is left to implementers to discard, modify, or reuse the original constructor then not to return or return a new instance.
 */
export type ClassAnnotation = (
    instance: any,
    originalConstructor: (...x: any[]) => any,
    ...invocationArguments: any[]
) => any;
/**
 * @ngdoc object
 * @name @smartutils.object:ClassAnnotationFactory
 *
 * @description
 * A {@link @smartutils.object:ClassAnnotation ClassAnnotation} factory
 * used by {@link @smartutils.services:AnnotationService AnnotationService}.
 * @param {...any[]} factoryArguments the factory arguments
 * @returns {ClassAnnotation} {@link @smartutils.object:ClassAnnotation ClassAnnotation}
 */
export type ClassAnnotationFactory = (...x: any[]) => ClassAnnotation;

/** @internal */
enum annotationType {
    ClassAnnotation = 'ClassAnnotation',
    MethodAnnotation = 'MethodAnnotation'
}

/**
 * @ngdoc service
 * @name @smartutils.services:AnnotationService
 *
 * @description
 * Utility service to declare and consume method level and class level {@link https://www.typescriptlang.org/docs/handbook/decorators.html Typescript decorator factories}.
 * <br/>Since Decorator is a reserved word in Smartedit, Typescript Decorators are called as Annotations.
 */
export class AnnotationService {
    public readonly INJECTABLE_NAME_KEY = 'getInjectableName';
    public readonly ORIGINAL_CONSTRUCTOR_KEY = 'originalConstructor';

    /**
     * @ngdoc method
     * @name @smartutils.services:AnnotationService#getClassAnnotations
     * @methodOf @smartutils.services:AnnotationService
     *
     * @description
     * Retrieves an object with all the string-indexed annotations defined on the given class target
     * @param {any} target The typescript class on which class annotations are defined
     * @returns {[index: string]: any} an object contains string-indexed annotation name and payload
     */
    getClassAnnotations = lodash.memoize(this.getClassAnnotationsLogic);

    /**
     * @ngdoc method
     * @name @smartutils.services:AnnotationService#getMethodAnnotations
     * @methodOf @smartutils.services:AnnotationService
     *
     * @description
     * Retrieves an object with all the string indexed annotations defined on the given class method
     * @param {any} target The typescript class to the inspected
     * @param {string} propertyName The name of the method on which annotations are defined
     * @returns {[index: string]: any} an object contains string-indexed annotation name and payload
     */
    getMethodAnnotations = lodash.memoize(
        this.getMethodAnnotationsLogic,
        function (target: any, propertyName: string) {
            return JSON.stringify(target.prototype) + propertyName;
        }
    );

    private functionsUtils: FunctionsUtils = new FunctionsUtils();

    private annotationFactoryMap = {} as TypedMap<
        MethodAnnotationFactory | ClassAnnotationFactory | null
    >;

    /**
     * @ngdoc method
     * @name @smartutils.services:AnnotationService#getClassAnnotation
     * @methodOf @smartutils.services:AnnotationService
     *
     * @description
     * Retrieves arguments of class annotation under a given annotation name
     * @param {any} target The typescript class on which class annotation is defined
     * @param {(args?: any) => ClassDecorator} annotation The type of the class annotation
     * @returns {any} the payload passed to the annotation
     */
    getClassAnnotation(target: any, annotation: (args?: any) => ClassDecorator): any {
        const annotationMap: TypedMap<any> = this.getClassAnnotations(target);
        const annotationName: string = (annotation as any).annotationName;
        if (annotationMap && annotationName in annotationMap) {
            // eslint-disable-next-line @typescript-eslint/no-unsafe-return
            return annotationMap[annotationName];
        } else {
            return null;
        }
    }

    /**
     * @ngdoc method
     * @name @smartutils.services:AnnotationService#getMethodAnnotation
     * @methodOf @smartutils.services:AnnotationService
     *
     * @description
     * Retrieves arguments of method annotation for a given typescript class
     * @param {any} target The typescript class
     * @param {string} propertyName The name of the method on which annotation is defined
     * @param {(args?: any) => MethodDecorator)} annotation The type of the method annotation
     * @returns {any} the payload passed to the annotation
     */
    getMethodAnnotation(
        target: any,
        propertyName: string,
        annotation: (args?: any) => MethodDecorator
    ): any {
        const annotationMap: TypedMap<any> = this.getMethodAnnotations(target, propertyName);
        const annotationName: string = (annotation as any).annotationName;
        if (annotationMap && annotationName in annotationMap) {
            // eslint-disable-next-line @typescript-eslint/no-unsafe-return
            return annotationMap[annotationName];
        } else {
            return null;
        }
    }

    /**
     * @ngdoc method
     * @name @smartutils.services:AnnotationService#hasClassAnnotation
     * @methodOf @smartutils.services:AnnotationService
     *
     * @description
     * Determines whether a given class target has given annotation name defined or not
     * @param {any} target The typescript class on which class annotation is defined
     * @param {(args?: any) => ClassDecorator} annotation The type of the class annotation
     * @returns {boolean} true if a given target has given annotation name. Otherwise false.
     */
    hasClassAnnotation(target: any, annotation: (args?: any) => ClassDecorator): boolean {
        const annotationMap: TypedMap<any> = this.getClassAnnotations(target);
        return (annotation as any).annotationName in annotationMap ? true : false;
    }

    /**
     * @ngdoc method
     * @name @smartutils.services:AnnotationService#hasMethodAnnotation
     * @methodOf @smartutils.services:AnnotationService
     *
     * @description
     * Determines whether a given method name has given annotation name defined or not under a given typescript class
     * @param {any} target The typescript class object
     * @param {string} propertyName The name of the method on which annotation is defined
     * @param {(args?: any) => MethodDecorator} annotation The type of the method annotation
     * @returns {boolean} true if a given method name has given annotation name. Otherwise false.
     */
    hasMethodAnnotation(
        target: any,
        propertyName: string,
        annotation: (args?: any) => MethodDecorator
    ): boolean {
        const annotationMap: TypedMap<any> = this.getMethodAnnotations(target, propertyName);
        return (annotation as any).annotationName in annotationMap ? true : false;
    }

    /**
     * @ngdoc method
     * @name @smartutils.services:AnnotationService#setClassAnnotationFactory
     * @methodOf @smartutils.services:AnnotationService
     *
     * @description
     * Registers a {@link @smartutils.object:ClassAnnotationFactory ClassAnnotationFactory} under a given name.
     * <br/>Typically, in order for the ClassAnnotationFactory to benefit from Angular dependency injection, this method will be called within an Angular factory.
     * @param {string} name the name of the factory.
     * @returns {ClassAnnotationFactory} a {@link @smartutils.object:ClassAnnotationFactory ClassAnnotationFactory}
     */
    setClassAnnotationFactory(
        name: string,
        annotationFactory: ClassAnnotationFactory | null
    ): ClassAnnotationFactory | null {
        this.annotationFactoryMap[name] = annotationFactory;
        return annotationFactory;
    }

    /**
     * @ngdoc method
     * @name @smartutils.services:AnnotationService#getClassAnnotationFactory
     * @methodOf @smartutils.services:AnnotationService
     *
     * @description
     * Retrieves a {@link @smartutils.object:ClassAnnotationFactory ClassAnnotationFactory}
     * previously registered under the given name:
     *
     * <pre>
     *   export const GatewayProxied = annotationService.getClassAnnotationFactory('GatewayProxied');
     * </pre>
     *
     * @param {string} name The name of the factory
     * @returns {ClassAnnotationFactory} a {@link @smartutils.object:ClassAnnotationFactory ClassAnnotationFactory}
     */

    getClassAnnotationFactory(name: string): (...args: any[]) => ClassDecorator {
        const instance = this;

        const classAnnotationFactory = function (...factoryArgument: any[]) {
            return function (originalConstructor: any): any {
                const newConstructor = instance.functionsUtils.extendsConstructor(
                    originalConstructor,
                    function (...args: any[]) {
                        const annotationFactory = instance.annotationFactoryMap[
                            name
                        ] as ClassAnnotationFactory;
                        if (annotationFactory) {
                            // Note: Before we used to bind originalConstructor.bind(this). However, it had to be left up to the caller
                            // since that causes problems in IE; when a function is bound in IE, the browser wraps it in a function with
                            // native code, making it impossible to retrieve its name.
                            const result = annotationFactory(factoryArgument)(
                                this,
                                originalConstructor,
                                args
                            );
                            if (result) {
                                // eslint-disable-next-line @typescript-eslint/no-unsafe-return
                                return result;
                            } else {
                                return null;
                            }
                        } else {
                            throw new Error(
                                `annotation '${name}' is used on '${originalConstructor.name}' but its ClassAnnotationFactory may not have been added to the dependency injection`
                            );
                        }
                    }
                );

                /*
                 * enable Angular to inject this new constructor even though it has an empty signature
                 * by copying $inject property and DI related Angular metadata
                 * For idempotent purposes we copy all properties anyways
                 */
                lodash.merge(newConstructor, originalConstructor);
                /*
                 * some properties set by Angular are not enumerable and yet contain
                 * such information as @Inject "metadata" necessary for DI
                 */
                newConstructor.__annotations__ = originalConstructor.__annotations__;
                newConstructor.__parameters__ = originalConstructor.__parameters__;
                newConstructor.__prop__metadata__ = originalConstructor.__prop__metadata__;

                /*
                 * copying such metadata as design:paramtypes necessary for DI
                 */
                Reflect.getMetadataKeys(originalConstructor).forEach((key) => {
                    Reflect.defineMetadata(
                        key,
                        Reflect.getMetadata(key, originalConstructor),
                        newConstructor
                    );
                });

                const rootOriginalConstructor =
                    instance.getOriginalConstructor(originalConstructor);

                Reflect.defineMetadata(
                    instance.ORIGINAL_CONSTRUCTOR_KEY,
                    rootOriginalConstructor,
                    newConstructor
                );

                Reflect.defineMetadata(
                    `${annotationType.ClassAnnotation}:${name}`,
                    factoryArgument,
                    rootOriginalConstructor
                );

                // override original constructor
                // eslint-disable-next-line @typescript-eslint/no-unsafe-return
                return newConstructor;
            };
        };
        (classAnnotationFactory as any).annotationName = name;
        return classAnnotationFactory;
    }

    /**
     * @ngdoc method
     * @name @smartutils.services:AnnotationService#setMethodAnnotationFactory
     * @methodOf @smartutils.services:AnnotationService
     *
     * @description
     * Registers a {@link @smartutils.object:MethodAnnotationFactory MethodAnnotationFactory} under a given name.
     * <br/>Typically, in order for the MethodAnnotationFactory to benefit from Angular dependency injection, this method will be called within an Angular factory.
     * @param {string} name The name of the factory.
     * @returns {MethodAnnotationFactory} a {@link @smartutils.object:MethodAnnotationFactory MethodAnnotationFactory}
     */
    setMethodAnnotationFactory(
        name: string,
        annotationFactory: MethodAnnotationFactory | null
    ): MethodAnnotationFactory | null {
        this.annotationFactoryMap[name] = annotationFactory;
        return annotationFactory;
    }

    /**
     * @ngdoc method
     * @name @smartutils.services:AnnotationService#getMethodAnnotationFactory
     * @methodOf @smartutils.services:AnnotationService
     *
     * @description
     * Retrieves a method level {@link @smartutils.object:MethodAnnotationFactory MethodAnnotationFactory}
     * previously registered under the given name:
     *
     * <pre>
     *   export const Cached = annotationService.getMethodAnnotationFactory('Cached');
     * </pre>
     *
     * @param {string} name the name of the factory.
     * @returns {MethodAnnotationFactory} a {@link @smartutils.object:MethodAnnotationFactory MethodAnnotationFactory}.
     */
    getMethodAnnotationFactory(name: string): any {
        const instance = this;

        const methodAnnotationFactory = function (...factoryArgument: any[]) {
            /*
             * when decorating an abstract class, strangely enough target is an instance of the abstract class
             * we need pass "this" instead to the annotationFactory invocation
             */
            return (
                target: any,
                propertyName: string,
                descriptor: TypedPropertyDescriptor<(...x: any[]) => any>
            ): void => {
                const originalMethod = descriptor.value;

                descriptor.value = function (...args: any[]): any {
                    const annotationFactory: MethodAnnotationFactory = instance
                        .annotationFactoryMap[name] as MethodAnnotationFactory;

                    if (annotationFactory) {
                        // eslint-disable-next-line @typescript-eslint/no-unsafe-return
                        return originalMethod
                            ? annotationFactory(factoryArgument)(
                                  this,
                                  propertyName,
                                  originalMethod.bind(this),
                                  args
                              )
                            : undefined;
                    } else {
                        throw new Error(
                            `annotation '${name}' is used but its MethodAnnotationFactory may not have been added to the dependency injection`
                        );
                    }
                };

                Reflect.defineMetadata(
                    `${annotationType.MethodAnnotation}:${name}`,
                    factoryArgument,
                    target,
                    propertyName
                );
            };
        };
        (methodAnnotationFactory as any).annotationName = name;
        return methodAnnotationFactory;
    }

    /**
     * @ngdoc method
     * @name @smartutils.services:AnnotationService#getOriginalConstructor
     * @methodOf @smartutils.services:AnnotationService
     *
     * @description
     * Given a class constructor, returns the original constructor of it prior to any class level
     * proxying by annotations declared through {@link @smartutils.services:AnnotationService AnnotationService}
     *
     * @param {Class} target the constructor
     */
    public getOriginalConstructor(target: any): Class {
        // eslint-disable-next-line @typescript-eslint/no-unsafe-return
        return Reflect.getMetadata(this.ORIGINAL_CONSTRUCTOR_KEY, target) || target;
    }

    private getClassAnnotationsLogic(target: any): TypedMap<any> {
        const originalConstructor = this.getOriginalConstructor(target);
        const annotationMap: TypedMap<any> = {};

        Reflect.getMetadataKeys(originalConstructor)
            .filter((key: string) => key.toString().startsWith(annotationType.ClassAnnotation))
            .forEach((key: string) => {
                annotationMap[key.split(':')[1]] = Reflect.getMetadata(key, originalConstructor);
            });
        return annotationMap;
    }

    private getMethodAnnotationsLogic(target: any, propertyName: string): TypedMap<any> {
        const annotationMap: TypedMap<any> = {};

        Reflect.getMetadataKeys(target.prototype, propertyName)
            .filter((key: string) => key.toString().startsWith(annotationType.MethodAnnotation))
            .forEach((key: string) => {
                annotationMap[key.split(':')[1]] = Reflect.getMetadata(
                    key,
                    target.prototype,
                    propertyName
                );
            });

        return annotationMap;
    }
}

export const annotationService: AnnotationService = new AnnotationService();
