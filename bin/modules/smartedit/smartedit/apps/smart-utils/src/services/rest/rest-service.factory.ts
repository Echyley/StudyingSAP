/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import * as lodash from 'lodash';
import { LogService } from '../log.service';
import { IRestService } from './i-rest-service';
import { IRestServiceFactory } from './i-rest-service.factory';
import { RestClient } from './rest-client';

/** @internal */
@Injectable()
export class RestServiceFactory implements IRestServiceFactory {
    private static globalBasePath: string | null = null;
    private static logService: LogService = new LogService();

    private map = new Map();
    private basePath: string | null = null;
    private DOMAIN: string | null = null;
    private IDENTIFIER = 'identifier';

    static setGlobalBasePath(globalBasePath: string): void {
        if (!RestServiceFactory.globalBasePath) {
            RestServiceFactory.globalBasePath = globalBasePath;
        } else {
            RestServiceFactory.logService.warn(
                'The value of a global base path was already set. ' +
                    'Update is not possible, the value remained unchanged!'
            );
        }
    }

    static getGlobalBasePath(): string {
        return RestServiceFactory.globalBasePath ? RestServiceFactory.globalBasePath : '';
    }

    constructor(private httpClient: HttpClient) {}

    setDomain(DOMAIN: string): void {
        this.DOMAIN = DOMAIN;
    }

    setBasePath(basePath: string): void {
        this.basePath = basePath;
    }

    get<T>(uri: string, identifier: string = this.IDENTIFIER): IRestService<T> {
        if (this.map.has(uri + identifier)) {
            return this.map.get(uri + identifier);
        }

        if (!/^https?\:\/\//.test(uri)) {
            const newBasePath: string | null = this.getNewBasePath();
            const basePathURI: string = lodash.isEmpty(newBasePath)
                ? uri
                : newBasePath + (/^\//.test(uri) ? uri : `/${uri}`);

            uri = this.shouldAppendDomain(uri) ? `${this.DOMAIN}/${uri}` : basePathURI;
        }

        const restClient: RestClient<T> = new RestClient<T>(this.httpClient, uri, identifier);
        this.map.set(uri + identifier, restClient);
        return restClient;
    }

    private shouldAppendDomain(uri: string): boolean {
        return !!this.DOMAIN && !/^\//.test(uri);
    }

    private getNewBasePath(): string | null {
        return this.basePath ? this.basePath : RestServiceFactory.globalBasePath;
    }
}
