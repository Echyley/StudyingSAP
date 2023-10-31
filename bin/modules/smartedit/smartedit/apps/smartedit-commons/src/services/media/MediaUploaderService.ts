/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { IRestService, RestServiceFactory } from '@smart/utils';
import * as lodash from 'lodash';
import { MEDIA_RESOURCE_URI } from '../../utils/smarteditconstants';
import { IMediaToUpload, MediaToUpload, ICMSMedia } from '../interfaces/IMediaToUpload';
/**
 * This service provides functionality to upload images and to fetch images by code for a specific catalog-catalog version combination.
 */
@Injectable()
export class MediaUploaderService extends IMediaToUpload {
    private readonly mediaRestService: IRestService<ICMSMedia>;

    constructor(private restServiceFactory: RestServiceFactory) {
        super();
        this.mediaRestService = this.restServiceFactory.get(MEDIA_RESOURCE_URI);
    }

    /**
     * Uploads the media to the catalog.
     *
     * @returns Promise that resolves with the media object if request is successful.
     * If the request fails, it resolves with errors from the backend.
     */
    uploadMedia(media: MediaToUpload): Promise<ICMSMedia> {
        const formData = new FormData();
        lodash.forEach(media, (value, key: string) => {
            formData.append(key, value);
        });

        return this.mediaRestService.save(formData as any, {
            headers: { enctype: 'multipart/form-data', fileSize: '' + media?.file?.size }
        });
    }
}
