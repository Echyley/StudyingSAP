/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { Injector } from '@angular/core';
import { TypedMap } from '../../../../dtos';
import { annotationService, ClassAnnotationFactory } from '../../../annotation.service';
import { OperationContextService } from './operation-context.service';

const operationContextName = 'OperationContextRegistered';

/**
 * @ngdoc object
 * @name @smartutils.object:@OperationContextRegistered
 * @description
 * Class level typescript {@link http://www.typescriptlang.org/docs/handbook/decorators.html decorator factory} is delegated to
 * {@link @smartutils.services:OperationContextService OperationContextService.register} and it provides the functionality
 * to register an url with  operation context(s).
 *
 * For example:
 * 1. @OperationContextRegistered('apiUrl', ['CMS', 'INTERACTIVE'])
 * 2. @OperationContextRegistered('apiUrl', 'TOOLING')
 *
 * @param {string} url
 * @param {string | string[]} operationContext
 */
export const OperationContextRegistered = annotationService.getClassAnnotationFactory(
    operationContextName
) as (url: string, operationContext: string | string[]) => ClassDecorator;

export function OperationContextAnnotationFactory(
    injector: Injector,
    operationContextService: OperationContextService,
    OPERATION_CONTEXT: TypedMap<string>
): ClassAnnotationFactory | null {
    'ngInject';
    return annotationService.setClassAnnotationFactory(
        operationContextName,
        function (factoryArguments: any[]) {
            return function (
                instance: any,
                originalConstructor: (...x: any[]) => any,
                invocationArguments: any[]
            ): void {
                instance = new (originalConstructor.bind(instance))(...invocationArguments);

                const url: string = injector.get<string>(factoryArguments[0], factoryArguments[0]);

                if (typeof factoryArguments[1] === 'string') {
                    const operationContext: string = (OPERATION_CONTEXT as any)[
                        factoryArguments[1]
                    ];
                    operationContextService.register(url, operationContext);
                } else if (Array.isArray(factoryArguments[1]) && factoryArguments[1].length > 0) {
                    factoryArguments[1].forEach((element: string) => {
                        operationContextService.register(url, (OPERATION_CONTEXT as any)[element]);
                    });
                }

                return instance;
            };
        }
    );
}
