/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { HttpErrorResponse, HttpRequest } from '@angular/common/http';
import { readPredicate, updatePredicate } from './http-method.predicates';

const errorResponse = new HttpErrorResponse({ error: null });
describe('http method predicates', function () {
    const HTTP_METHODS_UPDATE = ['PUT', 'POST', 'DELETE', 'PATCH'];
    const HTTP_METHODS_READ = ['GET', 'OPTIONS', 'HEAD'];

    it('update predicate should match only with http methods update', function () {
        HTTP_METHODS_UPDATE.forEach(function (method) {
            expect(
                updatePredicate(new HttpRequest(method, 'someurl', {}), errorResponse)
            ).toBeTruthy();
        });
    });

    it('read predicate should match only with http methods read', function () {
        HTTP_METHODS_READ.forEach(function (method) {
            expect(
                readPredicate(new HttpRequest(method, 'someurl', {}), errorResponse)
            ).toBeTruthy();
        });
    });
});
