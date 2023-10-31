import { RestServiceFactory } from '@smart/utils';
import { IMediaToUpload, MediaToUpload, ICMSMedia } from '../interfaces/IMediaToUpload';
/**
 * This service provides functionality to upload images and to fetch images by code for a specific catalog-catalog version combination.
 */
export declare class MediaUploaderService extends IMediaToUpload {
    private restServiceFactory;
    private readonly mediaRestService;
    constructor(restServiceFactory: RestServiceFactory);
    /**
     * Uploads the media to the catalog.
     *
     * @returns Promise that resolves with the media object if request is successful.
     * If the request fails, it resolves with errors from the backend.
     */
    uploadMedia(media: MediaToUpload): Promise<ICMSMedia>;
}
