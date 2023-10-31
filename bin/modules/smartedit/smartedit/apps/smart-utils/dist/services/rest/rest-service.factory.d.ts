/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { HttpClient } from '@angular/common/http';
import { IRestService } from './i-rest-service';
import { IRestServiceFactory } from './i-rest-service.factory';
/** @internal */
export declare class RestServiceFactory implements IRestServiceFactory {
    private httpClient;
    private static globalBasePath;
    private static logService;
    private map;
    private basePath;
    private DOMAIN;
    private IDENTIFIER;
    static setGlobalBasePath(globalBasePath: string): void;
    static getGlobalBasePath(): string;
    constructor(httpClient: HttpClient);
    setDomain(DOMAIN: string): void;
    setBasePath(basePath: string): void;
    get<T>(uri: string, identifier?: string): IRestService<T>;
    private shouldAppendDomain;
    private getNewBasePath;
}
