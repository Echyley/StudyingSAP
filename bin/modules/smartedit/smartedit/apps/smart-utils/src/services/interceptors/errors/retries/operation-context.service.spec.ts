/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { OperationContextService } from './operation-context.service';

describe('operation context service', function () {
    let operationContextService: OperationContextService;

    beforeEach(() => {
        operationContextService = new OperationContextService();
    });

    it('should be able to find an operation context for a given url', function () {
        operationContextService.register(
            '/cmswebservices/v1/sites/:siteUID/catalogversiondetails',
            'ANY_OPERATION_CONTEXT'
        );
        const oc = operationContextService.findOperationContext(
            '/cmswebservices/v1/sites/123/catalogversiondetails'
        );
        expect(oc).toBe('ANY_OPERATION_CONTEXT');
    });

    it('should return null if there is no operation context registered', function () {
        const oc = operationContextService.findOperationContext('/any_url');
        expect(oc).toBeNull();
    });

    it('should be able to chain the register function', function () {
        expect(
            operationContextService
                .register('/any_url', 'ANY_OPERATION_CONTEXT')
                .register('/another_url', 'ANOTHER_CONTEXT')
        ).toEqual(operationContextService);
    });

    it('should throw an error if trying to register with an invalid url', function () {
        const expectedErrorFunction = function () {
            operationContextService.register('', 'oc');
        };
        expect(expectedErrorFunction).toThrowError(
            'operationContextService.register error: url is invalid'
        );
    });

    it('should throw an error if trying to register with an invalid url', function () {
        const expectedErrorFunction = function () {
            operationContextService.register(123 as any, 'oc');
        };
        expect(expectedErrorFunction).toThrowError(
            'operationContextService.register error: url is invalid'
        );
    });

    it('should throw an error if trying to register with an invalid operationContext', function () {
        const expectedErrorFunction = function () {
            operationContextService.register('test', '');
        };
        expect(expectedErrorFunction).toThrowError(
            'operationContextService.register error: operationContext is invalid'
        );
    });

    it('should throw an error if trying to register with an invalid operationContext', function () {
        const expectedErrorFunction = function () {
            operationContextService.register('test', {} as any);
        };
        expect(expectedErrorFunction).toThrowError(
            'operationContextService.register error: operationContext is invalid'
        );
    });
});
