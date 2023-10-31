/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/* eslint-disable @typescript-eslint/member-ordering */
/**
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import 'jasmine';
import { annotationService } from './annotation.service';
import { LogService } from './log.service';

/*
 * see test functions and classed at the end of the test suite
 */

const methodAnnotation1 = annotationService.getMethodAnnotationFactory('methodAnnotation1');
const methodAnnotation2 = annotationService.getMethodAnnotationFactory('methodAnnotation2');

const classAnnotation1 = annotationService.getClassAnnotationFactory('classAnnotation1');
const classAnnotation2 = annotationService.getClassAnnotationFactory('classAnnotation2');
const classAnnotation3 = annotationService.getClassAnnotationFactory('classAnnotation3');

describe('test annotationService', function () {
    //////////////////////////////////////////////
    //////////////// ACTUAL TESTS ////////////////
    //////////////////////////////////////////////

    let $log: jasmine.SpyObj<LogService>;

    let service: ServiceToBeAnnotated;
    let otherService: OtherService;
    let serviceWithMultipleClassAnnotations: ServiceWithMultipleClassAnnotations;
    beforeEach(() => {
        spyOn((annotationService as any).functionsUtils, 'isUnitTestMode').and.returnValue(false);

        $log = jasmine.createSpyObj<LogService>('$log', ['info']);

        classAnnotation1Factory($log);
        classAnnotation2Factory($log);
        classAnnotation3Factory($log);
    });

    function initializeBothMethodAnnotations() {
        // invocations normally performed when the pertaining angular factories are called
        methodAnnotation1Factory($log);
        methodAnnotation2Factory($log);
    }

    function onlyInitializemethodAnnotation1() {
        // invocations normally performed when the pertaining angular factories are called
        methodAnnotation1Factory($log);
        annotationService.setMethodAnnotationFactory('methodAnnotation2', null);
    }

    describe('class annotations', () => {
        it('GIVEN one necessary annotation is not initialized WHEN ServiceToBeAnnotated is instantiated THEN exception is thrown', () => {
            annotationService.setClassAnnotationFactory('classAnnotation1', null);

            expect(() => new ServiceToBeAnnotated($log, 'somestring', 5)).toThrow(
                new Error(
                    "annotation 'classAnnotation1' is used on 'ServiceToBeAnnotated' but its ClassAnnotationFactory may not have been added to the dependency injection"
                )
            );
        });

        it('GIVEN a constructor is annotated, it will be modified with data from factory arguments and a code executed at the end', () => {
            service = new ServiceToBeAnnotated($log, 'somestring', 5);

            expect(service.someMethod0()).toEqual('somestring5');
            expect((service as any).addedProperty).toEqual('classArg1');

            expect($log.info.calls.count()).toBe(2);
            expect($log.info.calls.argsFor(0)[0]).toEqual('original constructor invoked');
            expect($log.info.calls.argsFor(1)[0]).toContain(
                'ServiceToBeAnnotated is mutated into a proxied service with the arguments (classArg1)'
            );
        });

        it("GIVEN a constructor is annotated, its constructor will be replaced by another class's constructor", () => {
            otherService = new OtherService();

            expect((otherService as any).addedProperty).toEqual('classArg1');
            expect(otherService.constructor.name).toBe('ServiceToBeAnnotated');
        });

        it('GIVEN a constructor is annotated with two annotations, both annotations will have access to the same class instance', () => {
            serviceWithMultipleClassAnnotations = new ServiceWithMultipleClassAnnotations();

            expect((serviceWithMultipleClassAnnotations as any).addedProperty).toEqual('classArg1');
            expect((serviceWithMultipleClassAnnotations as any).anotherProperty).toEqual(
                'classArg3'
            );
        });

        it('GIVEN a constructor is annotated with two annotations, both annotations are retrieved through the same root original constructor', () => {
            const decoratorObj1 = annotationService.getClassAnnotation(
                ServiceWithMultipleClassAnnotations,
                classAnnotation1
            );
            expect(decoratorObj1).toEqual(['classArg1']);
            const decoratorObj3 = annotationService.getClassAnnotation(
                ServiceWithMultipleClassAnnotations,
                classAnnotation3
            );
            expect(decoratorObj3).toEqual(['classArg3']);
        });
    });

    describe('method annotations', () => {
        beforeEach(() => {
            $log = jasmine.createSpyObj<LogService>('$log', ['info']);
            service = new ServiceToBeAnnotated($log, 'somestring', 5);
            $log.info.calls.reset();
        });

        xit('GIVEN a same annotation is registered twice THEN an exception is thrown', () => {
            annotationService.getMethodAnnotationFactory('someannotation');

            expect(function () {
                annotationService.getMethodAnnotationFactory('someannotation');
            }).toThrow(new Error("annotation 'cache' has already been registered"));
        });

        it('GIVEN one necessary annotation is not initialized WHEN method1 is called THEN exception is thrown', () => {
            onlyInitializemethodAnnotation1();

            expect(function () {
                service.someMethod1();
            }).toThrow(
                new Error(
                    "annotation 'methodAnnotation2' is used but its MethodAnnotationFactory may not have been added to the dependency injection"
                )
            );
        });

        it('WHEN method1 is called THEN the method1 is proxied twice: first cache then transactional', () => {
            initializeBothMethodAnnotations();

            expect(service.someMethod1()).toBe(
                'wrappedInmethodAnnotation1_wrappedInmethodAnnotation2_rawMethod1Output'
            );
        });

        it('WHEN method2 is called THEN the method2 is proxied twice: first transactional then cache', () => {
            initializeBothMethodAnnotations();

            expect(service.someMethod2()).toBe(
                'wrappedInmethodAnnotation2_wrappedInmethodAnnotation1_rawMethod2Output'
            );
        });

        it('WHEN method1 is called THEN $log.info is called twice', () => {
            initializeBothMethodAnnotations();

            service.someMethod1();

            expect($log.info.calls.count()).toBe(2);
            expect($log.info).toHaveBeenCalledWith('will wrap in methodAnnotation1');
            expect($log.info).toHaveBeenCalledWith(
                'will wrap in methodAnnotation2 with factory arguments Frequent and 3'
            );
        });
    });
});

//////////////////////////////////////////////
///////// TEST FUNCTION AND CLASSES //////////
//////////////////////////////////////////////
const noMagicNumber5 = 5;
const noMagicNumber3 = 3;

function methodAnnotation1Factory($dep1: LogService) {
    return annotationService.setMethodAnnotationFactory(
        'methodAnnotation1',
        (factoryArgument: any[]) =>
            function (
                target: any,
                propertyName: string,
                originalMethod: (...x: any[]) => any,
                ...invocationArguments: any[]
            ) {
                $dep1.info(`will wrap in methodAnnotation1`);
                return (
                    'wrappedInmethodAnnotation1_' + originalMethod.apply(this, invocationArguments)
                );
            }
    );
}

function methodAnnotation2Factory($dep2: LogService) {
    return annotationService.setMethodAnnotationFactory(
        'methodAnnotation2',
        (factoryArgument: any[]) =>
            function (
                target: any,
                propertyName: string,
                originalMethod: (...x: any[]) => any,
                ...invocationArguments: any[]
            ) {
                $dep2.info(
                    `will wrap in methodAnnotation2 with factory arguments ${factoryArgument[0]} and ${factoryArgument[1]}`
                );
                return (
                    'wrappedInmethodAnnotation2_' + originalMethod.apply(this, invocationArguments)
                );
            }
    );
}

function classAnnotation1Factory($dep3: LogService) {
    return annotationService.setClassAnnotationFactory(
        'classAnnotation1',
        function (factoryArgument: any[]) {
            return function (
                instance: any,
                originalConstructor: (...x: any[]) => any,
                invocationArguments: any[]
            ) {
                instance = new (originalConstructor.bind(instance))(...invocationArguments);
                instance.addedProperty = factoryArgument[0];
                $dep3.info(
                    `${originalConstructor.name} is mutated into a proxied service with the arguments (${factoryArgument})`
                );
                return instance;
            };
        }
    );
}

function classAnnotation2Factory($dep3: LogService) {
    return annotationService.setClassAnnotationFactory(
        'classAnnotation2',
        function (factoryArgument: any[]) {
            return function (
                instance: any,
                originalConstructor: (...x: any[]) => any,
                invocationArguments: any[]
            ) {
                return new ServiceToBeAnnotated($dep3, 'somestring', noMagicNumber5);
            };
        }
    );
}

function classAnnotation3Factory($dep3: LogService) {
    return annotationService.setClassAnnotationFactory(
        'classAnnotation3',
        function (factoryArgument: any[]) {
            return function (
                instance: any,
                originalConstructor: (...x: any[]) => any,
                invocationArguments: any[]
            ) {
                instance = new (originalConstructor.bind(instance))(...invocationArguments);
                instance.anotherProperty = factoryArgument[0];
                return instance;
            };
        }
    );
}

@classAnnotation1('classArg1')
class ServiceToBeAnnotated {
    constructor(
        private readonly _$log: LogService,
        private readonly arg1: string,
        private readonly arg2: number
    ) {
        this._$log.info('original constructor invoked');
    }

    someMethod0(): string {
        return `${this.arg1}${this.arg2}`;
    }

    @methodAnnotation1()
    @methodAnnotation2('Frequent', noMagicNumber3)
    someMethod1(): string {
        return 'rawMethod1Output';
    }

    @methodAnnotation2('Never', noMagicNumber5)
    @methodAnnotation1()
    someMethod2(): string {
        return 'rawMethod2Output';
    }
}

@classAnnotation2('classArg1')
class OtherService {}

@classAnnotation1('classArg1')
@classAnnotation3('classArg3')
class ServiceWithMultipleClassAnnotations {}
