/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { HttpErrorResponse, HttpRequest } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IAlertService } from '../../../interfaces';
import { BackendError, IHttpErrorInterceptor } from '../i-http-error.interceptor';
/**
 * @ngdoc service
 * @name permissionErrorInterceptorModule.service:permissionErrorInterceptor
 * @description
 * Used for HTTP error code 403. Displays the alert message for permission error.
 */
@Injectable()
export class PermissionErrorInterceptor<T = any> implements IHttpErrorInterceptor<T> {
    constructor(private alertService: IAlertService) {}
    predicate(request: HttpRequest<T>, response: HttpErrorResponse): boolean {
        return response.status === 403;
    }
    responseError(
        request: HttpRequest<T>,
        response: HttpErrorResponse
    ): Promise<HttpErrorResponse> {
        if (response.error && response.error.errors) {
            response.error.errors
                .filter((error: BackendError) => error.type === 'TypePermissionError')
                .forEach((error: BackendError) => {
                    this.alertService.showDanger({
                        message: error.message,
                        duration: 10000,
                        minWidth: '',
                        mousePersist: true,
                        dismissible: true,
                        width: '300px'
                    });
                });
        }
        return Promise.reject(response);
    }
}
