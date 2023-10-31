/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { HttpErrorResponse } from '@angular/common/http';
import { BooleanUtils } from './boolean-utils';

describe('BooleanUtils test', () => {
    let booleanUtils: BooleanUtils;
    const TEST_URL = 'http://any_url';

    beforeEach(() => {
        booleanUtils = new BooleanUtils();
    });

    it('areAllTruthy should return true if each given function returns true', () => {
        const mockData = {
            url: TEST_URL,
            status: 500
        };

        const fn1 = function (data: HttpErrorResponse) {
            return data.url === TEST_URL;
        };
        const fn2 = function (data: HttpErrorResponse, anyParameter: string) {
            return data.status === 500 && anyParameter === 'test';
        };

        expect(booleanUtils.areAllTruthy(fn1, fn2)(mockData, 'test')).toBeTruthy();
    });

    it('areAllTruthy should return false if not all given function return true', () => {
        const mockData = {
            url: TEST_URL,
            status: 500
        };

        const fn1 = function (data: HttpErrorResponse) {
            return data.url === TEST_URL;
        };
        const fn2 = function (data: HttpErrorResponse, anyParameter: string) {
            return data.status === 404 && anyParameter === 'test';
        };

        expect(booleanUtils.areAllTruthy(fn1, fn2)(mockData, 'test')).toBeFalsy();
    });

    it('isAnyTruthy should return true if one of the given function returns true', () => {
        const mockData = {
            url: TEST_URL,
            status: 500
        };

        const fn1 = function (data: HttpErrorResponse) {
            return data.url !== TEST_URL;
        };
        const fn2 = function (data: HttpErrorResponse, anyParameter: string) {
            return data.status === 500 && anyParameter === 'test';
        };

        expect(booleanUtils.isAnyTruthy(fn1, fn2)(mockData, 'test')).toBeTruthy();
    });
});
