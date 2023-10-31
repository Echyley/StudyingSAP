/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { Injector } from '@angular/core';
import { TypedMap } from '../../../../dtos';
import { ClassAnnotationFactory } from '../../../annotation.service';
import { OperationContextService } from './operation-context.service';
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
export declare const OperationContextRegistered: (url: string, operationContext: string | string[]) => ClassDecorator;
export declare function OperationContextAnnotationFactory(injector: Injector, operationContextService: OperationContextService, OPERATION_CONTEXT: TypedMap<string>): ClassAnnotationFactory | null;
