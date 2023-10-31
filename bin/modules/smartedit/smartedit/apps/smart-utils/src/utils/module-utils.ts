/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import {
    APP_BOOTSTRAP_LISTENER,
    APP_INITIALIZER,
    ComponentRef,
    Injectable,
    Provider,
    ValueProvider
} from '@angular/core';
import { Cloneable, TypedMap } from '../dtos';

/*
 * internal utility service to handle ES6 modules
 */
/* forbiddenNameSpaces angular.module:false */
/** @internal */
@Injectable()
export class ModuleUtils {
    public initialize(useFactory: (...args: any[]) => void, deps: any[] = []): Provider {
        return {
            provide: APP_INITIALIZER,
            // eslint-disable-next-line @typescript-eslint/explicit-function-return-type
            useFactory(...args: any[]) {
                useFactory.apply(undefined, args);
                // eslint-disable-next-line @typescript-eslint/explicit-function-return-type
                return (component: ComponentRef<any>) => {
                    // an initializer useFactory must return a function
                };
            },
            deps,
            multi: true
        };
    }

    public bootstrap(useFactory: (...args: any[]) => void, deps: any[] = []): Provider {
        return {
            provide: APP_BOOTSTRAP_LISTENER,
            // eslint-disable-next-line @typescript-eslint/explicit-function-return-type
            useFactory(...args: any[]) {
                useFactory.apply(undefined, args);
                // eslint-disable-next-line @typescript-eslint/explicit-function-return-type
                return (component: ComponentRef<any>) => {
                    // an initializer useFactory must return a function
                };
            },
            deps,
            multi: true
        };
    }

    provideValues(_constants?: TypedMap<Cloneable | RegExp>): ValueProvider[] {
        const constants = _constants || {};
        return Object.keys(constants).map((key) => ({
            provide: key,
            useValue: constants[key]
        }));
    }
}

export const moduleUtils = new ModuleUtils();
