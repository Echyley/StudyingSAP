/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { IRestOptions, IRestService, RestServiceFactory, ICMSMedia } from 'smarteditcommons';
import { MediaUploaderService } from './MediaUploaderService';

describe('MediaUploaderService', () => {
    let seMediaService: MediaUploaderService;
    let restServiceFactory: jasmine.SpyObj<RestServiceFactory>;
    let restService: jasmine.SpyObj<IRestService<ICMSMedia>>;

    const file = jasmine.createSpyObj<File>('file', ['name']);

    const IMAGE_MOCK = {
        file,
        code: 'somecode',
        description: 'somedescription',
        altText: 'somealttext',
        folder: ''
    };

    const IMAGE_WITH_FOLDER_MOCK = {
        file,
        code: 'testCode',
        description: 'testDescription',
        altText: 'testAltText',
        folder: 'testFolder'
    };

    const response = {
        uuid: 'someuuid'
    } as ICMSMedia;

    beforeEach(() => {
        restService = jasmine.createSpyObj('restService', ['save']);
        restService.save.and.callFake((body: any, options: IRestOptions): any => {
            if (options.headers.enctype === 'multipart/form-data') {
                return Promise.resolve(response);
            }

            throw new Error(`unexpected parameters passed to IRestService.post`);
        });
        restServiceFactory = jasmine.createSpyObj('restServiceFactory', ['get']);
        restServiceFactory.get.and.returnValue(restService);

        seMediaService = new MediaUploaderService(restServiceFactory);
    });

    it('should post with proper headers and formData', async () => {
        const result = await seMediaService.uploadMedia(IMAGE_MOCK);
        expect(result).toBe(response);

        const formData: FormData = restService.save.calls.mostRecent().args[0] as any;

        expect(formData.has('file')).toBe(true);
        expect(formData.get('code')).toBe('somecode');
        expect(formData.get('description')).toBe('somedescription');
        expect(formData.get('altText')).toBe('somealttext');
        expect(formData.get('folder')).toBe('');
    });

    it('should post with proper headers and formData when setting folder', async () => {
        const result = await seMediaService.uploadMedia(IMAGE_WITH_FOLDER_MOCK);
        expect(result).toBe(response);

        const formData: FormData = restService.save.calls.mostRecent().args[0] as any;

        expect(formData.has('file')).toBe(true);
        expect(formData.get('code')).toBe('testCode');
        expect(formData.get('description')).toBe('testDescription');
        expect(formData.get('altText')).toBe('testAltText');
        expect(formData.get('folder')).toBe('testFolder');
    });
});
