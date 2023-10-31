/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { HttpErrorResponse, HttpRequest } from '@angular/common/http';
import * as lodash from 'lodash';
export const HTTP_METHODS_UPDATE = ['PUT', 'POST', 'DELETE', 'PATCH'];
export const HTTP_METHODS_READ = ['GET', 'OPTIONS', 'HEAD'];

export function updatePredicate(request: HttpRequest<any>, response: HttpErrorResponse): boolean {
    return lodash.includes(HTTP_METHODS_UPDATE, request.method);
}
export function readPredicate(request: HttpRequest<any>, response: HttpErrorResponse): boolean {
    return lodash.includes(HTTP_METHODS_READ, request.method);
}
