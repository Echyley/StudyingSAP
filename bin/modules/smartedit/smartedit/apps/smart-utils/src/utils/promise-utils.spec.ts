/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { PromiseUtils } from './promise-utils';

describe('PromiseUtilsTests', () => {
    const promiseUtils: PromiseUtils = new PromiseUtils();

    it('promise will reject when deferred.reject is called', (done) => {
        const rejectValue = 'rejectValue';

        const deferred = promiseUtils.defer();

        deferred.promise.then(
            (value: any) => fail('should have rejected'),
            (reason: any) => {
                expect(reason).toBe(rejectValue);
                done();
            }
        );

        deferred.reject(rejectValue);
    });

    it('promise will resolve when deferred.resolve is called', (done) => {
        const resolveValue = 'resolveValue';

        const deferred = promiseUtils.defer();

        deferred.promise.then(
            (value: any) => {
                expect(value).toBe(resolveValue);
                done();
            },
            (reason: any) => fail('should have resolved')
        );

        deferred.resolve(resolveValue);
    });
});
