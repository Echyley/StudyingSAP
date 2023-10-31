/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { APP_BOOTSTRAP_LISTENER, ComponentRef, ModuleWithProviders, NgModule } from '@angular/core';
import { Class } from '../../types';
import { BackendInterceptor, HttpBackendService } from './backend';
import {
    defaultRetryStrategyFactory,
    exponentialRetryStrategyFactory,
    linearRetryStrategyFactory,
    DefaultRetryStrategy,
    ExponentialRetry,
    ExponentialRetryStrategy,
    LinearRetry,
    LinearRetryStrategy,
    SimpleRetry
} from './errors';
import { FlawInjectionInterceptorModule } from './flaws';
import { HttpAuthInterceptor } from './http-auth.interceptor';
import { HttpErrorInterceptorService } from './http-error-interceptor.service';
import { HttpErrorInterceptor } from './http-error.interceptor';
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
@NgModule({
    imports: [FlawInjectionInterceptorModule],
    providers: [
        SimpleRetry,
        LinearRetry,
        ExponentialRetry,
        {
            provide: DefaultRetryStrategy,
            useFactory: defaultRetryStrategyFactory,
            deps: [SimpleRetry]
        },
        {
            provide: ExponentialRetryStrategy,
            useFactory: exponentialRetryStrategyFactory,
            deps: [ExponentialRetry]
        },
        {
            provide: LinearRetryStrategy,
            useFactory: linearRetryStrategyFactory,
            deps: [SimpleRetry]
        },
        HttpErrorInterceptorService,
        HttpBackendService
    ]
})
export class HttpInterceptorModule {
    static forRoot(
        ...HttpErrorInterceptorClasses: Class<IHttpErrorInterceptor>[]
    ): ModuleWithProviders<HttpInterceptorModule> {
        return {
            ngModule: HttpInterceptorModule,
            providers: [
                ...HttpErrorInterceptorClasses,
                {
                    provide: HTTP_INTERCEPTORS,
                    useClass: HttpAuthInterceptor,
                    multi: true
                },
                {
                    provide: HTTP_INTERCEPTORS,
                    useClass: HttpErrorInterceptor,
                    multi: true
                },
                {
                    provide: HTTP_INTERCEPTORS,
                    useClass: BackendInterceptor,
                    multi: true
                },
                {
                    provide: APP_BOOTSTRAP_LISTENER,
                    // eslint-disable-next-line @typescript-eslint/explicit-function-return-type
                    useFactory(httpErrorInterceptorService: HttpErrorInterceptorService) {
                        httpErrorInterceptorService.addInterceptors(HttpErrorInterceptorClasses);

                        // eslint-disable-next-line @typescript-eslint/explicit-function-return-type
                        return (component: ComponentRef<any>) => {
                            // an initializer useFactory must return a function
                        };
                    },
                    deps: [HttpErrorInterceptorService],
                    multi: true
                }
            ]
        };
    }
}
