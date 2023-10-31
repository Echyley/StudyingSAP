import { ModuleWithProviders } from '@angular/core';
import { Class } from '../../types';
import { IHttpErrorInterceptor } from './i-http-error.interceptor';
/**
 * @ngdoc overview
 * @name httpInterceptorModule
 *
 * @description
 * This module provides the functionality to add custom HTTP error interceptors.
 * Interceptors are used to execute code each time an HTTP request fails.
 *
 */
export declare class HttpInterceptorModule {
    static forRoot(...HttpErrorInterceptorClasses: Class<IHttpErrorInterceptor>[]): ModuleWithProviders<HttpInterceptorModule>;
}
