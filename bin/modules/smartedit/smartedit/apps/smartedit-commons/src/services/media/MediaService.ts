/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { RestServiceFactory, Page } from '@smart/utils';
import {
    CONTEXT_CATALOG,
    CONTEXT_CATALOG_VERSION,
    MEDIA_PATH,
    MEDIAS_PATH
} from '../../utils/smarteditconstants';
import {
    IMediaService,
    Media,
    MediaDTO,
    ContextParams,
    MediaPage
} from '../interfaces/IMediaService';

/** Service to deal with media related CRUD operations. */
@Injectable()
export class MediaService extends IMediaService {
    constructor(private restServiceFactory: RestServiceFactory) {
        super();
    }

    /**
     * Fetches paged search results by making a REST call to the appropriate item endpoint.
     *
     * @param mask for filtering the search.
     * @param pageSize number of items in the page.
     * @param currentPage current page number.
     */
    public async getPage(
        mask: string,
        pageSize: number,
        currentPage: number,
        mimeType: string
    ): Promise<Page<Media>> {
        const contextParams: ContextParams = {
            catalogId: CONTEXT_CATALOG,
            catalogVersion: CONTEXT_CATALOG_VERSION
        };

        const subParams = this.contextParamsToCommaSeparated(contextParams);

        const payload = {
            params: subParams,
            mask,
            pageSize,
            currentPage,
            mimeType
        };
        const response = await this.restServiceFactory.get<MediaPage>(MEDIAS_PATH).get(payload);

        const page: Page<Media> = {
            results: [],
            pagination: response.pagination
        };
        page.results = response.media.map((media) => this.mediaDTOtoMedia(media));

        return page;
    }

    /**
     * This method fetches a Media by its UUID.
     * @param uuid uuid of a media (contains catalog information).
     */
    public async getMedia(uuid: string): Promise<Media> {
        // identifier is added to URI and not getByid argument because it contains slashes
        const url = `${MEDIA_PATH}/${uuid}`;
        const media = await this.restServiceFactory.get<MediaDTO>(url).get();

        return this.mediaDTOtoMedia(media);
    }

    /**
     * Returns comma separated params that will be attached to payload.
     *
     * E.g. "catalogId:CURRENT_CONTEXT_CATALOG,catalogVersion:CURRENT_CONTEXT_CATALOG_VERSION"
     */
    private contextParamsToCommaSeparated(params: ContextParams): string {
        return Object.keys(params)
            .reduce((accumulator, next) => {
                accumulator += `,${next}:${params[next]}`;
                return accumulator;
            }, '')
            .substring(1);
    }

    private mediaDTOtoMedia(mediaDTO: MediaDTO): Media {
        const needlessPropertiesOfMediaDTO = ['catalogId', 'catalogVersion', 'uuid'];
        const media: any = {};
        Object.getOwnPropertyNames(mediaDTO)
            .filter((propertyName) => needlessPropertiesOfMediaDTO.indexOf(propertyName) === -1)
            .forEach((propertyName) => (media[propertyName] = mediaDTO[propertyName]));
        media.id = mediaDTO.uuid;
        return media as Media;
    }
}
