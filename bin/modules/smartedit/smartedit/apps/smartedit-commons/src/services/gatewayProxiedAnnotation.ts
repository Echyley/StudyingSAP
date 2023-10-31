/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { annotationService, ClassAnnotationFactory } from '@smart/utils';
import { diNameUtils } from 'smarteditcommons/di';
import { GatewayProxy } from './gateway';

const GatewayProxiedName = 'GatewayProxied';

export const GatewayProxied = annotationService.getClassAnnotationFactory(GatewayProxiedName) as (
    ...args: string[]
) => ClassDecorator;

export function GatewayProxiedAnnotationFactory(
    gatewayProxy: GatewayProxy
): ClassAnnotationFactory {
    'ngInject';
    return annotationService.setClassAnnotationFactory(
        GatewayProxiedName,
        function (factoryArguments?: string[]) {
            return function (
                instance: any,
                originalConstructor: (...x: any[]) => any,
                invocationArguments: any[]
            ): void {
                instance = new (originalConstructor.bind(instance))(...invocationArguments);

                instance.gatewayId = diNameUtils.buildServiceName(originalConstructor);

                gatewayProxy.initForService(
                    instance,
                    factoryArguments.length > 0 ? factoryArguments : null
                );

                return instance;
            };
        }
    );
}
