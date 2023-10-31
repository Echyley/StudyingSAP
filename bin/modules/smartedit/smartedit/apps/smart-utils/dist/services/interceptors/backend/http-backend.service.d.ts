/**
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { HttpRequest } from '@angular/common/http';
import { Cloneable, Payload, TypedMap } from '../../../dtos';
export declare type RESTMETHOD = 'GET' | 'POST' | 'PUT' | 'PATCH' | 'DELETE';
/**
 * @ngdoc object
 * @name @smartutils.object:BackendRespond
 * @description
 * Mocked response of a {@link @smartutils.services:HttpBackendService HttpBackendService} invocation
 * It is either a Cloneable payload (returned automatically with 200 status code)
 * or a function returning an array of 2 arguments: the status code and the payload
 * if a function, it is invoked with:
 * @param {string=} method GET, POST, PUT or DELETE
 * @param {string=} url the full url with query string
 * @param {any=} data the payload or the request, or the POST querystring
 * @param {TypedMap<string>=} headers the outpbound request headers map
 */
export declare type BackendRespond = Payload | Payload[] | ((method?: string, url?: string, data?: any, headers?: TypedMap<string>) => [number, Payload | Payload[]] | PromiseLike<[number, Payload | Payload[]]>);
/**
 * @ngdoc service
 * @name @smartutils.services:BackendEntry
 * @description
 * Invocations of {@link @smartutils.services:HttpBackendService} when, whenGET, whenPOST, whenPUT, whenDELETE
 * all return an instance of {@link @smartutils.services:BackendEntry BackendEntry}
 * It is used to specify the mocked response for the given conditions.
 */
export declare class BackendEntry {
    pattern: string | RegExp;
    matchingPayload?: Cloneable;
    mock?: BackendRespond;
    constructor(pattern: string | RegExp, matchingPayload?: Cloneable);
    /**
     * @ngdoc method
     * @name @smartutils.services:BackendEntry#respond
     * @methodOf @smartutils.services:BackendEntry
     * @description
     * @param {BackendRespond} mock the {@link @smartutils.object:BackendRespond} to return for the given conditions
     */
    respond(mock: BackendRespond): BackendEntry;
    passThrough(): void;
}
export interface BackendEntryMap {
    [index: string]: BackendEntry[];
}
/**
 * @ngdoc service
 * @name @smartutils.services:HttpBackendService
 * @description
 * Service aimed to provide mocked backend responses for given http request patterns.
 */
export declare class HttpBackendService {
    private matchLatestDefinition;
    private backends;
    /**
     * @ngdoc method
     * @name @smartutils.services:HttpBackendService#whenGET
     * @methodOf @smartutils.services:HttpBackendService
     * @description
     * method similar to {@link https://docs.angularjs.org/api/ngMockE2E/service/$httpBackend#whenGET $httpBackend#whenGET}
     * but with only the url pattern as parameter
     * @param {string | RegExp} pattern url end of the url pattern to match
     * @returns {BackendEntry} the {@link @smartutils.services:BackendEntry backenEntry}
     */
    whenGET(pattern: string | RegExp): BackendEntry;
    /**
     * @ngdoc method
     * @name @smartutils.services:HttpBackendService#whenPOST
     * @methodOf @smartutils.services:HttpBackendService
     * @description
     * method similar to {@link https://docs.angularjs.org/api/ngMockE2E/service/$httpBackend#whenPOST $httpBackend#whenPOST}
     * but with only the first 2 arguments
     * @param {string | RegExp} pattern url end of the url pattern to match
     * @param {Cloenable=} matchingPayload HTTP request body to be matched
     * @returns {BackendEntry} the {@link @smartutils.services:BackendEntry backenEntry}
     */
    whenPOST(pattern: string | RegExp, matchingPayload?: Cloneable): BackendEntry;
    /**
     * @ngdoc method
     * @name @smartutils.services:HttpBackendService#whenPUT
     * @methodOf @smartutils.services:HttpBackendService
     * @description
     * method similar to {@link https://docs.angularjs.org/api/ngMockE2E/service/$httpBackend#whenPUT $httpBackend#whenPUT}
     * but with only the first 2 arguments
     * @param {string | RegExp} pattern url end of the url pattern to match
     * @param {Cloenable=} matchingPayload HTTP request body to be matched
     * @returns {BackendEntry} the {@link @smartutils.services:BackendEntry backenEntry}
     */
    whenPUT(pattern: string | RegExp, matchingPayload?: Cloneable): BackendEntry;
    /**
     * @ngdoc method
     * @name @smartutils.services:HttpBackendService#whenPATCH
     * @methodOf @smartutils.services:HttpBackendService
     * @description
     * method similar to {@link https://docs.angularjs.org/api/ngMockE2E/service/$httpBackend#whenPATCH $httpBackend#whenPATCH}
     * but with only the first 2 arguments
     * @param {string | RegExp} pattern url end of the url pattern to match
     * @param {Cloenable=} matchingPayload HTTP request body to be matched
     * @returns {BackendEntry} the {@link @smartutils.services:BackendEntry backenEntry}
     */
    whenPATCH(pattern: string | RegExp, matchingPayload?: Cloneable): BackendEntry;
    /**
    /**
     * @ngdoc method
     * @name @smartutils.services:HttpBackendService#whenPUT
     * @methodOf @smartutils.services:HttpBackendService
     * @description
     * method similar to {@link https://docs.angularjs.org/api/ngMockE2E/service/$httpBackend#whenPUT $httpBackend#whenPUT}
     * but with only the url pattern as parameter
     * @param {string | RegExp} pattern url end of the url pattern to match
     * @returns {BackendEntry} the {@link @smartutils.services:BackendEntry backenEntry}
     */
    whenDELETE(pattern: string | RegExp): BackendEntry;
    /**
     * @ngdoc method
     * @name @smartutils.services:HttpBackendService#when
     * @methodOf @smartutils.services:HttpBackendService
     * @description
     * method similar to {@link https://docs.angularjs.org/api/ngMockE2E/service/$httpBackend#when $httpBackend#when}
     * @param {string} method GET, POST, PUT, or DELETE
     * @param {string | RegExp} pattern url end of the url pattern to match
     * @param {Cloenable=} matchingPayload HTTP request body to be matched
     * @returns {BackendEntry} the {@link @smartutils.services:BackendEntry backenEntry}
     */
    when(method: RESTMETHOD, pattern: string | RegExp, matchingPayload?: Cloneable): BackendEntry;
    /**
     * @ngdoc method
     * @name @smartutils.services:HttpBackendService#whenAsync
     * @methodOf @smartutils.services:HttpBackendService
     * @description
     * method similar to legacy $httpBackend#whenAsync, use {@link @smartutils.services:HttpBackendService#when HttpBackendService#when} instead
     * @param {string} method GET, POST, PUT, or DELETE
     * @param {string | RegExp} pattern url end of the url pattern to match
     * @param {Cloenable=} matchingPayload HTTP request body to be matched
     * @returns {BackendEntry} the {@link @smartutils.services:BackendEntry backenEntry}
     */
    whenAsync(method: RESTMETHOD, pattern: string | RegExp, matchingPayload?: Cloneable): BackendEntry;
    /**
     * @ngdoc method
     * @name @smartutils.services:HttpBackendService#matchLatestDefinitionEnabled
     * @methodOf @smartutils.services:HttpBackendService
     * @description
     * method similar to {@link https://docs.angularjs.org/api/ngMockE2E/service/$httpBackend#matchLatestDefinitionEnabled $httpBackend#matchLatestDefinitionEnabled}
     * @param {boolean=false} matchLatestDefinitionEnabled if true, the last matching pattern will be picked. Otherwise the first is picked
     */
    matchLatestDefinitionEnabled(matchLatestDefinitionEnabled: boolean): void;
    findMatchingMock(request: HttpRequest<any>): BackendRespond | undefined;
    private _whenMethod;
    private matchingPayloadRestriction;
}
