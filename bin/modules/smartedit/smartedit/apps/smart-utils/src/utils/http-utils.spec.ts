/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import {
    HttpErrorResponse,
    HttpEvent,
    HttpHeaders,
    HttpParams,
    HttpRequest,
    HttpResponse
} from '@angular/common/http';
import { HttpUtils } from './http-utils';

describe('HttpUtils test', () => {
    let httpUtils: HttpUtils;
    const somecontenttype = 'somecontenttype';
    const someExtensionUrl = 'some.extension';
    const CONTENT_TYPE = 'Content-type';

    beforeEach(() => {
        httpUtils = new HttpUtils();
    });

    it('isGET returns true when method is GET', () => {
        expect(httpUtils.isGET(new HttpRequest('GET', 'someurl'))).toBe(true);
    });

    it('isGET returns false whem method is not GET', () => {
        expect(httpUtils.isGET(new HttpRequest('POST', 'someurl', {}))).toBe(false);
    });

    it('isResponseOfContentType returns false when no headers present', () => {
        expect(
            httpUtils.isResponseOfContentType(new HttpResponse({ url: 'someurl' }), somecontenttype)
        ).toBe(false);
    });

    it('isResponseOfContentType returns false when no Content-type header present', () => {
        expect(
            httpUtils.isResponseOfContentType(
                new HttpResponse({ url: 'someurl', headers: new HttpHeaders() }),
                somecontenttype
            )
        ).toBe(false);
    });

    it('isResponseOfContentType returns false when non-matching Content-type header present', () => {
        expect(
            httpUtils.isResponseOfContentType(
                new HttpResponse({
                    url: 'someurl',
                    headers: new HttpHeaders().set(CONTENT_TYPE, 'dsafasdf')
                }),
                somecontenttype
            )
        ).toBe(false);
    });

    it('isResponseOfContentType returns true when matching Content-type header present', () => {
        expect(
            httpUtils.isResponseOfContentType(
                new HttpResponse({
                    url: 'someurl',
                    headers: new HttpHeaders().set(CONTENT_TYPE, somecontenttype)
                }),
                somecontenttype
            )
        ).toBe(true);
    });

    it('isHTMLRequest returns false when not GET method', () => {
        expect(
            httpUtils.isHTMLRequest(
                new HttpRequest('POST', 'some.html', { headers: new HttpHeaders() }),
                new HttpResponse({ url: 'some.html' })
            )
        ).toBe(false);
    });

    it('isHTMLRequest returns false when neither html extension nor text/html content-type', () => {
        expect(
            httpUtils.isHTMLRequest(
                new HttpRequest('GET', someExtensionUrl, { headers: new HttpHeaders() }),
                new HttpResponse({ url: someExtensionUrl })
            )
        ).toBe(false);
    });

    it('isHTMLRequest returns true when GET on html extension', () => {
        expect(
            httpUtils.isHTMLRequest(
                new HttpRequest('GET', 'some.html', { headers: new HttpHeaders() }),
                new HttpResponse({ url: 'some.html' })
            )
        ).toBe(true);
    });

    it('isHTMLRequest returns true when GET on text/html content-type', () => {
        expect(
            httpUtils.isHTMLRequest(
                new HttpRequest('GET', someExtensionUrl, {
                    headers: new HttpHeaders().set('Accept', 'text/html')
                }),
                new HttpResponse({ url: someExtensionUrl })
            )
        ).toBe(true);
    });

    it('isJSONRequest returns false when not GET method', () => {
        expect(
            httpUtils.isJSONRequest(
                new HttpRequest('POST', 'some.json', {}),
                new HttpResponse({ url: 'some.json', headers: new HttpHeaders() })
            )
        ).toBe(false);
    });

    it('isJSONRequest returns false when neither json extension nor json content-type', () => {
        expect(
            httpUtils.isJSONRequest(
                new HttpRequest('GET', someExtensionUrl, {}),
                new HttpResponse({ url: someExtensionUrl, headers: new HttpHeaders() })
            )
        ).toBe(false);
    });

    it('isJSONRequest returns true when GET on json extension', () => {
        expect(
            httpUtils.isJSONRequest(
                new HttpRequest('GET', 'some.json', {}),
                new HttpResponse({ url: 'some.json', headers: new HttpHeaders() })
            )
        ).toBe(true);
    });

    it('isJSONRequest returns true when GET on json content-type', () => {
        expect(
            httpUtils.isJSONRequest(
                new HttpRequest('GET', someExtensionUrl, {}),
                new HttpResponse({
                    url: someExtensionUrl,
                    headers: new HttpHeaders().set(CONTENT_TYPE, 'json')
                })
            )
        ).toBe(true);
    });

    it('isJSRequest returns false when not GET method', () => {
        expect(httpUtils.isJSONRequest(new HttpRequest('POST', 'some.js', {}))).toBe(false);
    });

    it('isJSRequest returns false when not js extension', () => {
        expect(httpUtils.isJSRequest(new HttpRequest('GET', someExtensionUrl))).toBe(false);
    });

    it('isJSRequest returns true when GET on json extension', () => {
        expect(httpUtils.isJSRequest(new HttpRequest('GET', 'some.js'))).toBe(true);
    });

    it('copyHttpParamsOrHeaders will copy values as JSON', () => {
        const params = new HttpParams().set('key1', 'value1').set('key2', 'value2');

        expect(httpUtils.copyHttpParamsOrHeaders(params)).toEqual({
            key1: 'value1',
            key2: 'value2'
        });
    });

    it('transformHttpParams will substitute any matching key as partial name or partial value', () => {
        const params = new HttpParams()
            .set('key1', 'oldValue1')
            .set('key1_partial', 'oldValue1')
            .set('key3', 'key2')
            .set('key4', 'key2_partial');
        const transformedParams = httpUtils.transformHttpParams(params, {
            key1: 'value1',
            key2: 'value2'
        });

        expect(httpUtils.copyHttpParamsOrHeaders(transformedParams)).toEqual({
            value1: 'oldValue1',
            value1_partial: 'oldValue1',
            key3: 'value2',
            key4: 'value2_partial'
        });
    });

    it('buildHttpResponse will build a successful HttpResponse', (done) => {
        const originalPayload = { a: 'b' };
        const returnPayload = { c: 'd' };
        const request = new HttpRequest('POST', 'someurl', originalPayload);

        httpUtils.buildHttpResponse(request, [200, returnPayload]).subscribe(
            (_response: HttpEvent<any>) => {
                expect(_response instanceof HttpResponse).toBe(true);
                const response = _response as HttpResponse<any>;
                expect(response.status).toBe(200);
                expect(response.body).toEqual(returnPayload);
                expect(response.url).toEqual('someurl');
                done();
            },
            (error: HttpErrorResponse) => {
                fail('should have returned a successful http response');
            }
        );
    });

    it('buildHttpResponse will build a "failing" HttpResponse', (done) => {
        const originalPayload = { a: 'b' };
        const returnPayload = { c: 'd' };
        const request = new HttpRequest('POST', 'someurl', originalPayload);

        httpUtils.buildHttpResponse(request, [401, returnPayload]).subscribe(
            (response: HttpEvent<any>) => {
                fail('should have returned a successful http response');
            },
            (error: HttpErrorResponse) => {
                expect(error instanceof HttpErrorResponse).toBe(true);
                expect(error.status).toBe(401);
                expect(error.error).toEqual(returnPayload);
                expect(error.url).toEqual('someurl');
                done();
            }
        );
    });
});
