/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { Injectable } from '@angular/core';
import * as lodash from 'lodash';

/** @internal */
interface Store {
    urlRegex: RegExp;
    operationContext: string;
}

/**
 * @ngdoc service
 * @name @smartutils.services:OperationContextService
 * @description
 * This service provides the functionality to register a url with its associated operation contexts and also finds operation context given an url.
 */
@Injectable()
export class OperationContextService {
    private store: Store[];
    constructor() {
        this.store = [];
    }
    /**
     * @ngdoc method
     * @name @smartutils.services:OperationContextService#register
     * @methodOf @smartutils.services:OperationContextService
     *
     * @description
     * Register a new url with it's associated operationContext.
     *
     * @param {String} url The url that is associated to the operation context.
     * @param {String} operationContext The operation context name that is associated to the given url.
     *
     * @return {Object} operationContextService The operationContextService service
     */
    register(url: string, operationContext: string): this {
        if (typeof url !== 'string' || lodash.isEmpty(url)) {
            throw new Error('operationContextService.register error: url is invalid');
        }
        if (typeof operationContext !== 'string' || lodash.isEmpty(operationContext)) {
            throw new Error('operationContextService.register error: operationContext is invalid');
        }
        const regexIndex = this.store.findIndex(
            (store) =>
                store.urlRegex.test(url) === true && store.operationContext === operationContext
        );

        if (regexIndex !== -1) {
            return this;
        }
        const urlRegex = new RegExp(url.replace(/\/:[^\/]*/g, '/.*'));
        this.store.push({
            urlRegex,
            operationContext
        });
        return this;
    }
    /**
     * @ngdoc method
     * @name @smartutils.services:OperationContextService#findOperationContext
     * @methodOf @smartutils.services:OperationContextService
     *
     * @description
     * Find the first matching operation context for the given url.
     *
     * @param {String} url The request url.
     *
     * @return {String} operationContext
     */
    findOperationContext(url: string): string | null {
        const regexIndex = this.store.findIndex((store) => store.urlRegex.test(url) === true);
        return ~regexIndex ? this.store[regexIndex].operationContext : null;
    }
}
