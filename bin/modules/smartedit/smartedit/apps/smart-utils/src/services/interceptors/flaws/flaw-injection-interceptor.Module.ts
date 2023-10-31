/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
/* forbiddenNameSpaces useClass:false */
import { HttpRequest, HTTP_INTERCEPTORS } from '@angular/common/http';
import { APP_INITIALIZER, ComponentRef, NgModule } from '@angular/core';
import { FlawInjectionInterceptor } from './flaw-injection.interceptor';

/** @internal */
@NgModule({
    imports: [],
    providers: [
        {
            provide: HTTP_INTERCEPTORS,
            useClass: FlawInjectionInterceptor,
            multi: true
        },
        {
            provide: APP_INITIALIZER,
            // eslint-disable-next-line @typescript-eslint/explicit-function-return-type
            useFactory() {
                FlawInjectionInterceptor.registerRequestFlaw({
                    test: (request: HttpRequest<any>) => /sites\/[\w-]+\//.test(request.url),
                    mutate: (request: HttpRequest<any>) =>
                        request.clone({
                            url: request.url.replace(
                                /sites\/([\w-]+)\//,
                                'sites/' + Math.random() + '/'
                            )
                        })
                });
                // eslint-disable-next-line @typescript-eslint/explicit-function-return-type
                return (component: ComponentRef<any>) => {
                    // an initializer useFactory must return a function
                };
            },
            multi: true
        }
    ]
})
export class FlawInjectionInterceptorModule {}
