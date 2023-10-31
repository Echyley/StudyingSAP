/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {
    IMediaFolder,
    MediaFolderPage,
    MediaFolderSelectItem,
    MediaFolderService
} from 'cmssmarteditcontainer/components/genericEditor/media/services';

import { IRestService, ISettingsService, RestServiceFactory, Pagination } from 'smarteditcommons';

describe('MediaFolderService', () => {
    const mediaFolders: IMediaFolder[] = [{ qualifier: 'images' }, { qualifier: 'imagestest' }];
    const items: MediaFolderSelectItem[] = [
        { id: 'images', name: 'images', qualifier: 'images' },
        { id: 'imagestest', name: 'imagestest', qualifier: 'imagestest' }
    ];
    const pagination: Pagination = {
        count: 10,
        page: 0,
        totalCount: 20,
        totalPages: 1
    };

    let restServiceFactory: jasmine.SpyObj<RestServiceFactory>;
    let settingsService: jasmine.SpyObj<ISettingsService>;
    let mediaFolderListService: jasmine.SpyObj<IRestService<MediaFolderPage>>;
    let service: MediaFolderService;

    beforeEach(() => {
        mediaFolderListService = jasmine.createSpyObj<IRestService<MediaFolderPage>>(
            'mediaFolderListService',
            ['get']
        );
        settingsService = jasmine.createSpyObj<ISettingsService>('settingsService', ['get']);

        restServiceFactory = jasmine.createSpyObj<RestServiceFactory>('restServiceFactory', [
            'get'
        ]);
        restServiceFactory.get.and.returnValue(mediaFolderListService);
        service = new MediaFolderService(restServiceFactory, settingsService);
    });

    it('should get folders WHEN get mediaFoldersFetchStrategy', async () => {
        mediaFolderListService.get.and.returnValue({ mediaFolders, pagination } as any);

        const results = await service.mediaFoldersFetchStrategy.fetchPage('images', 10, 0);

        expect(mediaFolderListService.get).toHaveBeenCalledWith({
            mask: 'images',
            pageSize: 10,
            currentPage: 0
        });

        expect(results).toEqual({
            pagination,
            results: items
        });
    });

    it('shoule get default folder', async () => {
        settingsService.get.and.returnValue(Promise.resolve('root'));

        const folder = await service.getDefaultFolder();
        expect(folder).toBe('root');
    });
});
