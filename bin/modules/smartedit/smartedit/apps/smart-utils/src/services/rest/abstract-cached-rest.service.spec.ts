/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { Page, Payload } from '../../dtos';

import { coreAnnotationsHelper } from '../../unit';
import { CacheAction, CacheConfig, EvictionTag } from '../cache';
import { AbstractCachedRestService } from './abstract-cached-rest.service';
import { IRestService, SearchParams } from './i-rest-service';
import { IRestServiceFactory } from './i-rest-service.factory';

describe('AbstractCachedRestServiceTest ', () => {
    const cacheAction = new CacheAction('ANY_NAME');

    const evictionTag = {
        event: 'event0'
    } as EvictionTag;

    interface DTO {
        someKey: string;
    }

    @CacheConfig({ actions: [cacheAction], tags: [evictionTag] })
    class SomeRestClass extends AbstractCachedRestService<DTO> {
        constructor(rsf: IRestServiceFactory) {
            super(rsf, '/someURI', 'identifier');
        }
    }

    let service: SomeRestClass;
    let innerRestService: jasmine.SpyObj<IRestService<DTO>>;
    let restServiceFactory: jasmine.SpyObj<IRestServiceFactory>;

    beforeEach(() => {
        coreAnnotationsHelper.init();

        innerRestService = jasmine.createSpyObj<IRestService<DTO>>('innerRestService', [
            'activateMetadata',
            'getById',
            'get',
            'query',
            'page',
            'update',
            'save',
            'remove',
            'queryByPost'
        ]);
        restServiceFactory = jasmine.createSpyObj<IRestServiceFactory>('restServiceFactory', [
            'get'
        ]);
        restServiceFactory.get.and.callFake(((uri: string, identifier: string) => {
            if (uri === '/someURI' && identifier === 'identifier') {
                return innerRestService;
            } else {
                throw new Error(`unexpected uri ${uri} passed to restServiceFactory`);
            }
        }) as any);
        service = new SomeRestClass(restServiceFactory);
    });

    it('the underlying restService has response metadata retrieval activated', () => {
        expect(innerRestService.activateMetadata).toHaveBeenCalled();
    });

    it('getById delegates to underlying restService#getById', (done) => {
        const response: DTO = { someKey: 'someValue' };

        const promise = Promise.resolve(response);
        innerRestService.getById.and.returnValue(promise);

        service.getById('someId').then((result) => {
            expect(result).toBe(response);
            expect(innerRestService.getById).toHaveBeenCalledWith('someId', undefined);
            done();
        });
    });

    it('get delegates to underlying restService#get', (done) => {
        const searchParam: SearchParams = { searchKey: 'searchValue' };
        const response: DTO = { someKey: 'someValue' };

        const promise = Promise.resolve(response);
        innerRestService.get.and.returnValue(promise);

        service.get(searchParam).then((result) => {
            expect(result).toBe(response);
            expect(innerRestService.get).toHaveBeenCalledWith(searchParam, undefined);
            done();
        });
    });

    it('query delegates to underlying restService#query', (done) => {
        const searchParam: SearchParams = { searchKey: 'searchValue' };
        const response: DTO[] = [{ someKey: 'someValue' }];

        const promise = Promise.resolve(response);
        innerRestService.query.and.returnValue(promise);

        service.query(searchParam).then((result) => {
            expect(result).toBe(response);
            expect(innerRestService.query).toHaveBeenCalledWith(searchParam, undefined);
            done();
        });
    });

    it('page delegates to underlying restService#page', (done) => {
        const pageable = { currentPage: 5 };

        const response: Page<DTO> = {
            pagination: {
                totalCount: 10,
                count: 1,
                hasNext: false,
                hasPrevious: false,
                page: 2,
                totalPages: 10
            },
            results: [{ someKey: 'someValue' }]
        };

        const promise = Promise.resolve(response);
        innerRestService.page.and.returnValue(promise);

        service.page(pageable).then((result) => {
            expect(result).toBe(response);
            expect(innerRestService.page).toHaveBeenCalledWith(pageable, undefined);
            done();
        });
    });

    it('update delegates to underlying restService#update', (done) => {
        const payload: Payload = { someKey: 'someValue' };
        const response: DTO = { someKey: 'someValue' };

        const promise = Promise.resolve(response);
        innerRestService.update.and.returnValue(promise);

        service.update(payload).then((result) => {
            expect(result).toBe(response);
            expect(innerRestService.update).toHaveBeenCalledWith(payload, undefined);
            done();
        });
    });

    it('save delegates to underlying restService#save', (done) => {
        const payload: Payload = { someKey: 'someValue' };
        const response: DTO = { someKey: 'someValue' };

        const promise = Promise.resolve(response);
        innerRestService.save.and.returnValue(promise);

        service.save(payload).then((result) => {
            expect(result).toBe(response);
            expect(innerRestService.save).toHaveBeenCalledWith(payload, undefined);
            done();
        });
    });

    it('remove delegates to underlying restService#remove', (done) => {
        const payload: Payload = { someKey: 'someValue' };

        const promise = Promise.resolve();
        innerRestService.remove.and.returnValue(promise);

        service.remove(payload).then((result) => {
            expect(innerRestService.remove).toHaveBeenCalledWith(payload, undefined);
            done();
        });
    });

    it('queryByPost delegates to underlying restService#queryByPost', (done) => {
        const payload: Payload = { someKey: 'someValue' };
        const searchParam: SearchParams = { searchKey: 'searchValue' };
        const response: DTO = { someKey: 'someValue' };

        const promise = Promise.resolve(response);
        innerRestService.queryByPost.and.returnValue(promise);

        service.queryByPost(payload, searchParam).then((result) => {
            expect(result).toBe(response);
            expect(innerRestService.queryByPost).toHaveBeenCalledWith(
                payload,
                searchParam,
                undefined
            );
            done();
        });
    });
});
