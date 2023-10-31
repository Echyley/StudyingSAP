/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import 'jasmine';
import { HttpClient } from '@angular/common/http';
import { RestServiceFactory } from './rest-service.factory';

describe('test RestServiceFactory ', () => {
    class DTO {}

    let restServiceFactory: RestServiceFactory;
    const uri = 'theuri';
    const PROTOCOL_URL = 'http://some/uri';

    let httpClient: jasmine.SpyObj<HttpClient>;

    beforeEach(() => {
        httpClient = jasmine.createSpyObj<HttpClient>('httpClient', [
            'get',
            'post',
            'put',
            'delete'
        ]);
        restServiceFactory = new RestServiceFactory(httpClient);
        // eslint-disable-next-line @typescript-eslint/ban-ts-comment
        // @ts-ignore
        RestServiceFactory.globalBasePath = null;
    });

    it('returns singleton for a given API key', () => {
        expect(restServiceFactory.get<DTO>(uri, 'someidentifier')).toBe(
            restServiceFactory.get<DTO>(uri, 'someidentifier')
        );
        expect(restServiceFactory.get<DTO>(uri, 'someidentifier')).not.toBe(
            restServiceFactory.get<DTO>(uri)
        );
    });

    it('returns different service for different identifier', () => {
        expect(restServiceFactory.get<DTO>(uri, 'someidentifier')).not.toBe(
            restServiceFactory.get<DTO>(uri, 'someotheridentifier')
        );
    });

    it('creates a RestClient with local base path for the relative path', () => {
        restServiceFactory.setBasePath('someBasePath');

        expect(restServiceFactory.get<DTO>(uri, 'someidentifier').url).toBe(`someBasePath/${uri}`);
    });

    it('creates a RestClient with local base path for the absolute path', () => {
        restServiceFactory.setBasePath('someBasePath');

        expect(restServiceFactory.get<DTO>('/some/uri', 'someidentifier').url).toBe(
            `someBasePath/some/uri`
        );
    });

    it('doesnt prefix with local base path when url with protocol', () => {
        restServiceFactory.setBasePath('someBasePath');

        expect(restServiceFactory.get<DTO>(PROTOCOL_URL, 'someidentifier').url).toBe(
            `http://some/uri`
        );
    });

    it('creates a RestClient with global base path for the relative path', () => {
        RestServiceFactory.setGlobalBasePath('someGlobalBasePath');

        expect(restServiceFactory.get<DTO>(uri, 'someidentifier').url).toBe(
            `someGlobalBasePath/${uri}`
        );
    });

    it('creates a RestClient with global base path for the absolute path', () => {
        RestServiceFactory.setGlobalBasePath('someGlobalBasePath');

        expect(restServiceFactory.get<DTO>('/some/uri', 'someidentifier').url).toBe(
            `someGlobalBasePath/some/uri`
        );
    });

    it('doesnt prefix with global base path when url with protocol', () => {
        RestServiceFactory.setGlobalBasePath('someGlobalBasePath');

        expect(restServiceFactory.get<DTO>(PROTOCOL_URL, 'someidentifier').url).toBe(
            `http://some/uri`
        );
    });

    it('creates a RestClient with local base path taking precedence over global base path for the absolute path', () => {
        restServiceFactory.setBasePath('someBasePath');
        RestServiceFactory.setGlobalBasePath('someGlobalBasePath');

        expect(restServiceFactory.get<DTO>('/some/uri', 'someidentifier').url).toBe(
            `someBasePath/some/uri`
        );
    });

    it('creates a RestClient with local base path taking precedence over global base path for the relative path', () => {
        restServiceFactory.setBasePath('someBasePath');
        RestServiceFactory.setGlobalBasePath('someGlobalBasePath');

        expect(restServiceFactory.get<DTO>('some/uri', 'someidentifier').url).toBe(
            `someBasePath/some/uri`
        );
    });

    describe('setDomain Test', () => {
        it('creates a RestClient for the given API prefixed with domain when set', () => {
            restServiceFactory.setDomain('someDomain');

            expect(restServiceFactory.get<DTO>(uri, 'someidentifier').url).toBe(
                `someDomain/${uri}`
            );
        });

        it('doesnt prefix with domain when absolute url', () => {
            restServiceFactory.setDomain('someDomain');

            expect(restServiceFactory.get<DTO>('/some/uri', 'someidentifier').url).toBe(
                `/some/uri`
            );
        });

        it('doesnt prefix with domain when url wiht protocol', () => {
            restServiceFactory.setDomain('someDomain');

            expect(restServiceFactory.get<DTO>(PROTOCOL_URL, 'someidentifier').url).toBe(
                `http://some/uri`
            );
        });

        it('creates a RestClient with global base path taking precedence over local domain for absolute url', () => {
            restServiceFactory.setDomain('someDomain');
            RestServiceFactory.setGlobalBasePath('someGlobalBasePath');

            expect(restServiceFactory.get<DTO>('/some/uri', 'someidentifier').url).toBe(
                `someGlobalBasePath/some/uri`
            );
        });

        it('creates a RestClient with local base path taking precedence over local domain for absolute url', () => {
            restServiceFactory.setDomain('someDomain');
            restServiceFactory.setBasePath('someBasePath');

            expect(restServiceFactory.get<DTO>('/some/uri', 'someidentifier').url).toBe(
                `someBasePath/some/uri`
            );
        });

        it('creates a RestClient with local domain taking precedence over local base path for relative url', () => {
            restServiceFactory.setDomain('someDomain');
            restServiceFactory.setBasePath('someBasePath');

            expect(restServiceFactory.get<DTO>(uri, 'someidentifier').url).toBe(
                `someDomain/${uri}`
            );
        });

        it('creates a RestClient with local domain taking absolute precedence for relative path', () => {
            restServiceFactory.setDomain('someDomain');
            restServiceFactory.setBasePath('someBasePath');
            RestServiceFactory.setGlobalBasePath('someGlobalBasePath');

            expect(restServiceFactory.get<DTO>(uri, 'someidentifier').url).toBe(
                `someDomain/${uri}`
            );
        });

        it('creates a RestClient with local base path taking absolute precedence for absolute path', () => {
            restServiceFactory.setDomain('someDomain');
            restServiceFactory.setBasePath('someBasePath');
            RestServiceFactory.setGlobalBasePath('someGlobalBasePath');

            expect(restServiceFactory.get<DTO>('/some/uri', 'someidentifier').url).toBe(
                `someBasePath/some/uri`
            );
        });

        it('doesnt prefix anything to the url with protocol', () => {
            restServiceFactory.setDomain('someDomain');
            restServiceFactory.setBasePath('someBasePath');
            RestServiceFactory.setGlobalBasePath('someGlobalBasePath');

            expect(restServiceFactory.get<DTO>(PROTOCOL_URL, 'someidentifier').url).toBe(
                `http://some/uri`
            );
        });
    });
});
