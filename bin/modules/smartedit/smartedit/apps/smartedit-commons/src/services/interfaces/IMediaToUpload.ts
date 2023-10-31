/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
export interface ICMSMedia {
    altText: string;
    catalogId: string;
    catalogVersion: string;
    code: string;
    description: string;
    downloadUrl: string;
    mime: string;
    url: string;
    uuid: string;
}

export interface MediaToUpload {
    /**
     * The {@link https://developer.mozilla.org/en/docs/Web/API/File File} object to be uploaded.
     */
    file?: File;
    /** An unique code identifier for the media. (Shall either have file or url)*/
    url?: string;
    /** An url representing media path from external resources. (Shall either have file or url)*/
    code: string;
    /** A description of the media. */
    description: string;
    /** An alternate text to be shown for the media. */
    altText: string;
    /** The specific folder that media will be upload to. */
    folder: string;
    /** Extendable fields */
    [index: string]: string | File;
}

/**
 * The IMediaToUpload provides abstract class to upload media content.
 */
export abstract class IMediaToUpload {
    /**
     * Uploads the media to the catalog.
     *
     * @returns Promise that resolves with the media object if request is successful.
     * If the request fails, it resolves with errors from the backend.
     */
    uploadMedia(media: MediaToUpload): Promise<ICMSMedia> {
        return null;
    }
}
