/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import * as lodash from 'lodash';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Page, Pageable, Payload, TypedMap } from '../../dtos';
import { URIBuilder } from '../../utils';
import { IRestOptions, IRestService, SearchParams } from './i-rest-service';

export type ObjectWithHeaders<T> = T & { headers?: HttpHeaders };

/** @internal */
export class RestClient<T> implements IRestService<T> {
    private readonly DEFAULT_HEADERS: TypedMap<string> = { 'x-requested-with': 'Angular' };
    private readonly DEFAULT_OPTIONS: IRestOptions = { headers: {} };
    // will activate response headers appending
    private metadataActivated = false;

    constructor(
        private httpClient: HttpClient,
        public readonly url: string,
        private identifierName: string
    ) {}

    getById<S extends T = T>(
        identifier: string,
        options: IRestOptions = this.DEFAULT_OPTIONS
    ): Promise<ObjectWithHeaders<S> | null> {
        return this.addHeadersToBody(
            this.httpClient.get<S>(`${this.url}/${identifier}`, {
                headers: this.buildRequestHeaders(options.headers || {}),
                observe: 'response'
            })
        ).toPromise();
    }
    get<S extends T = T>(
        searchParams: SearchParams,
        options: IRestOptions = this.DEFAULT_OPTIONS
    ): Promise<ObjectWithHeaders<S> | null> {
        const params = this.convertToTypeMapOfString(searchParams);
        return this.addHeadersToBody(
            this.httpClient.get<S>(this.interpolateParamsInURL(this.url, params), {
                params: this.formatQueryString(
                    this.determineTrueQueryStringParams(this.url, searchParams)
                ),
                headers: this.buildRequestHeaders(options.headers || {}),
                observe: 'response'
            })
        ).toPromise();
    }
    query<S extends T = T>(
        searchParams?: SearchParams,
        options: IRestOptions = this.DEFAULT_OPTIONS
    ): Promise<ObjectWithHeaders<S[]>> {
        const params = searchParams ? this.convertToTypeMapOfString(searchParams) : searchParams;
        return this.addHeadersToBody(
            this.httpClient.get<S[]>(this.interpolateParamsInURL(this.url, params), {
                params: this.formatQueryString(
                    this.determineTrueQueryStringParams(this.url, searchParams)
                ),
                headers: this.buildRequestHeaders(options.headers || {}),
                observe: 'response'
            })
        )
            .pipe(map((arr: S[] | null) => arr || []))
            .toPromise();
    }
    page<S extends Page<T>>(
        pageable: Pageable,
        options: IRestOptions = this.DEFAULT_OPTIONS
    ): Promise<ObjectWithHeaders<S>> {
        return (
            this.addHeadersToBody(
                this.httpClient.get<S>(this.interpolateParamsInURL(this.url, pageable), {
                    params: this.formatQueryString(
                        this.determineTrueQueryStringParams(this.url, pageable)
                    ),
                    headers: this.buildRequestHeaders(options.headers || {}),
                    observe: 'response'
                })
            )
                // force typing to accept the fact that a page is never null
                .pipe(map((arr: S | null) => arr as any as S))
                .toPromise()
        );
    }
    update<S extends T = T>(
        payload: Payload,
        options: IRestOptions = this.DEFAULT_OPTIONS
    ): Promise<S> {
        return this.performIdentifierCheck(payload)
            .then(() => this.buildUrlWithIdentifier(payload))
            .then((url: string) =>
                this.httpClient
                    .put<S>(url, payload, {
                        headers: this.buildRequestHeaders(options.headers || {})
                    })
                    .toPromise()
            );
    }
    save<S extends T = T>(
        payload: Payload,
        options: IRestOptions = this.DEFAULT_OPTIONS
    ): Promise<S> {
        return this.httpClient
            .post<S>(this.interpolateParamsInURL(this.url, payload), payload, {
                headers: this.buildRequestHeaders(options.headers || {})
            })
            .toPromise();
    }

    patch<S extends T = T>(
        payload: Payload,
        options: IRestOptions = this.DEFAULT_OPTIONS
    ): Promise<S> {
        return this.performIdentifierCheck(payload)
            .then(() => this.buildUrlWithIdentifier(payload))
            .then((url: string) =>
                this.httpClient
                    .patch<S>(url, payload, {
                        headers: this.buildRequestHeaders(options.headers || {})
                    })
                    .toPromise()
            );
    }

    remove(payload: string | Payload, options: IRestOptions = this.DEFAULT_OPTIONS): Promise<any> {
        return this.performIdentifierCheck(payload)
            .then(() => this.buildUrlWithIdentifier(payload))
            .then((url: string) =>
                this.httpClient
                    .delete(url, { headers: this.buildRequestHeaders(options.headers || {}) })
                    .toPromise()
            );
    }

    queryByPost<S extends T = T>(
        payload: Payload,
        searchParams: SearchParams,
        options: IRestOptions = this.DEFAULT_OPTIONS
    ): Promise<ObjectWithHeaders<S> | null> {
        const params = this.convertToTypeMapOfString(searchParams);

        return this.httpClient
            .post<S>(this.interpolateParamsInURL(this.url, params), payload, {
                params: this.formatQueryString(
                    this.determineTrueQueryStringParams(this.url, searchParams)
                ),
                headers: this.buildRequestHeaders(options.headers || {})
            })
            .toPromise();
    }

    /// ////////////////////////////////////////
    /// INTERNAL METHODS NEEDED FOR GATEWAY ///
    /// ////////////////////////////////////////

    getMethodForSingleInstance = (
        name: 'getById' | 'get' | 'update' | 'save' | 'remove' | 'patch'
    ): ((params: any) => Promise<any>) => {
        switch (name) {
            case 'getById':
                return (id: string, options: IRestOptions = this.DEFAULT_OPTIONS): Promise<any> =>
                    this.getById(id, options);
            case 'get':
                return (
                    searchParams: SearchParams,
                    options: IRestOptions = this.DEFAULT_OPTIONS
                ): Promise<any> => this.get(searchParams, options);
            case 'update':
                return (
                    payload: Payload,
                    options: IRestOptions = this.DEFAULT_OPTIONS
                ): Promise<any> => this.update(payload, options);
            case 'save':
                return (
                    payload: Payload,
                    options: IRestOptions = this.DEFAULT_OPTIONS
                ): Promise<any> => this.save(payload, options);
            case 'patch':
                return (
                    payload: Payload,
                    options: IRestOptions = this.DEFAULT_OPTIONS
                ): Promise<any> => this.patch(payload, options);
            case 'remove':
                return (
                    payload: string | Payload,
                    options: IRestOptions = this.DEFAULT_OPTIONS
                ): Promise<any> => this.remove(payload, options);
            default:
                return (): Promise<any> => Promise.reject();
        }
    };
    getMethodForArray = (name: 'query'): ((params: SearchParams) => Promise<T[] | null>) => {
        switch (name) {
            case 'query':
                return (
                    params: SearchParams,
                    options: IRestOptions = this.DEFAULT_OPTIONS
                ): Promise<ObjectWithHeaders<T[]>> => this.query(params, options);
            default:
                return (): Promise<ObjectWithHeaders<T[]>> => Promise.reject();
        }
    };
    activateMetadata(): void {
        this.metadataActivated = true;
    }

    private convertToTypeMapOfString(searchParams: SearchParams): TypedMap<string> {
        return lodash.mapValues(searchParams, (val: any) => String(val));
    }

    private formatQueryString(_params: SearchParams | Pageable): {
        [param: string]: string | string[];
    } {
        return this.sortByKeys(_params);
    }
    private addHeadersToBody<BodyType>(
        observable: Observable<HttpResponse<BodyType>>
    ): Observable<ObjectWithHeaders<BodyType> | null> {
        return observable.pipe(
            map((response: HttpResponse<ObjectWithHeaders<BodyType>>) => {
                const data = response.body;

                if (this.metadataActivated && data) {
                    // used by @Cached annotation
                    data.headers = response.headers;
                }
                return data;
            })
        );
    }

    /*
     * interpolation URL placeholders interpolation with potential matches in queryString
     */
    private interpolateParamsInURL(_url: string, payload?: string | TypedMap<any>): string {
        // only keep params to be found in the URI or query params
        if (payload && typeof payload !== 'string') {
            return new URIBuilder(_url).replaceParams(payload).sanitize().build();
        } else {
            return _url;
        }
    }

    /*
     * remove from queryString any param needed for URL placeholders interpolation
     */
    private determineTrueQueryStringParams(
        url: string,
        payload?: string | TypedMap<any>
    ): TypedMap<any> {
        return typeof payload === 'object'
            ? Object.keys(payload).reduce((prev: Payload, next: string) => {
                  if (
                      !new RegExp(':' + next + '/').test(url) &&
                      !new RegExp(':' + next + '$').test(url) &&
                      !new RegExp(':' + next + '&').test(url) &&
                      !lodash.isNil(payload[next])
                  ) {
                      prev[next] = payload[next];
                  }
                  return prev;
              }, {})
            : {};
    }

    private sortByKeys(obj: TypedMap<any>): lodash.Dictionary<any> {
        const keys = lodash.sortBy(lodash.keys(obj), (key: string) => key);

        return lodash.zipObject(
            keys,
            lodash.map(keys, (key: string) => obj[key])
        );
    }

    private performIdentifierCheck(payload: string | Payload): Promise<void> {
        const identifier = typeof payload === 'string' ? payload : payload[this.identifierName];

        if (!identifier) {
            return Promise.reject(
                'no data was found under the ' +
                    identifier +
                    ' field of object ' +
                    JSON.stringify(payload) +
                    ', it is necessary for update and remove operations'
            );
        }

        return Promise.resolve();
    }

    private buildUrlWithIdentifier(payload: string | Payload): Promise<string> {
        const identifier = typeof payload === 'string' ? payload : payload[this.identifierName];
        let url = this.interpolateParamsInURL(`${this.url}`, payload);

        url =
            url.includes('?') || this.url.includes(':' + this.identifierName)
                ? url
                : `${url}/${identifier}`;

        return Promise.resolve(url);
    }

    private buildRequestHeaders(headers: TypedMap<string>): HttpHeaders {
        return new HttpHeaders({ ...this.DEFAULT_HEADERS, ...headers });
    }
}
