/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { HttpErrorResponse, HttpRequest } from '@angular/common/http';
export declare const HTTP_METHODS_UPDATE: string[];
export declare const HTTP_METHODS_READ: string[];
export declare function updatePredicate(request: HttpRequest<any>, response: HttpErrorResponse): boolean;
export declare function readPredicate(request: HttpRequest<any>, response: HttpErrorResponse): boolean;
