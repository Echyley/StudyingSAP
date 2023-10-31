/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import {
    perspectiveChangedEvictionTag,
    userEvictionTag,
    pageChangeEvictionTag,
    Cached,
    MEDIA_FOLDER_PATH,
    FetchStrategy,
    SelectItem,
    IRestService,
    Page,
    Pageable,
    RestServiceFactory,
    frequentlyChangingContent,
    ISettingsService,
    promiseUtils
} from 'smarteditcommons';

/**
 * Interface for media folder
 */
export interface IMediaFolder {
    qualifier: string;
}

export interface MediaFolderPage extends Page<IMediaFolder> {
    mediaFolders: IMediaFolder[];
}

export type MediaFolderSelectItem = IMediaFolder & SelectItem;
export type MediaFolderFetchStrategy = FetchStrategy<MediaFolderSelectItem>;

/**
 * The MediaFolderService is used to access media folders which been created in backoffice
 */
@Injectable()
export class MediaFolderService {
    private readonly mediaFolderListService: IRestService<MediaFolderPage>;
    private _mediaFolderFetchStrategy: MediaFolderFetchStrategy;

    constructor(
        private restServiceFactory: RestServiceFactory,
        private settingsService: ISettingsService
    ) {
        this.mediaFolderListService = this.restServiceFactory.get(MEDIA_FOLDER_PATH);
        this._mediaFolderFetchStrategy = {
            fetchPage: async (
                mask: string,
                pageSize: number,
                currentPage: number
            ): Promise<Page<MediaFolderSelectItem>> =>
                this.mediaFoldersFetchPage(mask, pageSize, currentPage),
            fetchEntity: async (id: string): Promise<MediaFolderSelectItem> =>
                Promise.resolve({ id, name: id, qualifier: id })
        };
    }

    /**
     * Strategy necessary to display media folder in a paged way.
     * It contains a method to retrieve pages of media folder.
     * Such strategy is necessary to work with media folder in SelectComponent.
     */
    get mediaFoldersFetchStrategy(): MediaFolderFetchStrategy {
        return this._mediaFolderFetchStrategy;
    }

    public async getDefaultFolder(): Promise<string> {
        const { error: getFolderError, data: defaultFolder } = await promiseUtils.attempt(
            this.settingsService.get('smartedit.mediaUploadDefaultFolder')
        );
        if (getFolderError) {
            return Promise.resolve('');
        }
        return defaultFolder;
    }

    /**
     * Returns a list of media folders that match the given mask
     */
    @Cached({
        actions: [frequentlyChangingContent],
        tags: [userEvictionTag, perspectiveChangedEvictionTag, pageChangeEvictionTag]
    })
    private async getMediaFolders(pageable: Pageable): Promise<MediaFolderPage> {
        const list = await this.mediaFolderListService.get({
            mask: pageable.mask,
            pageSize: pageable.pageSize,
            currentPage: pageable.currentPage
        });

        return list;
    }

    private async mediaFoldersFetchPage(
        mask: string,
        pageSize: number,
        currentPage: number
    ): Promise<Page<MediaFolderSelectItem>> {
        const { mediaFolders, pagination } = await this.getMediaFolders({
            mask,
            pageSize,
            currentPage
        });

        const items: MediaFolderSelectItem[] = mediaFolders.map((folder) => ({
            ...folder,
            id: folder.qualifier,
            name: folder.qualifier
        }));

        return {
            pagination,
            results: items
        };
    }
}
