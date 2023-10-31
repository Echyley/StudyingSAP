/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { UrlUtils, URIBuilder } from './url-utils';

describe('UrlUtils Test', () => {
    const urlUtils: UrlUtils = new UrlUtils();

    it('getQueryString will convert given object into query type', () => {
        const sampleObj = {
            key1: 'value1',
            key2: 'value2',
            key3: 'value3',
            key4: 'value4'
        };

        const queryString = urlUtils.getQueryString(sampleObj);

        expect(queryString).toBe('?key1=value1&key2=value2&key3=value3&key4=value4');
    });

    it('parseQuery will convert given query into an object of params', () => {
        const query = '?abc=abc&def=def&ijk=789';

        const resultObj = urlUtils.parseQuery(query);

        expect(resultObj as any).toEqual({
            abc: 'abc',
            def: 'def',
            ijk: '789'
        });
    });

    describe('getAbsoluteUrl', () => {
        const URL_PATH = 'https://127.0.0.1:9002/yacceleratorstorefront/?site=apparel-de';
        it('returns an absolute url for the given domain and url', () => {
            const url = urlUtils.getAbsoluteURL(
                'https://127.0.0.1:9002',
                '/yacceleratorstorefront/?site=apparel-de'
            );
            expect(url).toBe(URL_PATH);
        });

        it('returns an absolute url for the given url consisting of given domain', () => {
            const url = urlUtils.getAbsoluteURL('https://127.0.0.1:9002', URL_PATH);
            expect(url).toBe(URL_PATH);
        });

        it('returns an absolute url for the given url when given domain is empty', () => {
            const url = urlUtils.getAbsoluteURL('', URL_PATH);
            expect(url).toBe(URL_PATH);
        });
    });

    describe('URIBuilder Test', () => {
        it('sanitize will remove unresolved ":" prefixed placeholders only from absolute path and not touch port', () => {
            const query = 'http://localhost:3333/cmswebservices/:remove1/versions/:remove2';

            const resultURI = new URIBuilder(query).sanitize().build();

            expect(resultURI).toBe(`http://localhost:3333/cmswebservices/versions`);
        });
    });
});
