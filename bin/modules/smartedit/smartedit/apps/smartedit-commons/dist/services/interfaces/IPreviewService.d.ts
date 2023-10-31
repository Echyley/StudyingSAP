import { UrlUtils } from '@smart/utils';
import { IPreviewData } from './IPreview';
/**
 * Interface for previewService.
 *
 * This service is for managing the storefront preview ticket and is proxied across the gateway.
 */
export declare abstract class IPreviewService {
    protected urlUtils: UrlUtils;
    constructor(urlUtils: UrlUtils);
    /**
     * This method will create a new previewTicket for the given experience, using the preview API
     *
     *
     * This method does *NOT* update the current experience.
     *
     * @param previewData Data representing storefront preview
     *
     * @returns An object with the ticketId
     */
    createPreview(previewData: IPreviewData): Promise<IPreviewData>;
    /**
     * This method will update a previewTicket for the given the preview data, using the preview API
     *
     * @param previewData Data representing storefront preview containing the preview ticketId
     *
     * @returns An object with the ticketId
     */
    updatePreview(previewData: IPreviewData): Promise<IPreviewData>;
    /**
     * This method will preduce a resourcePath from a given preview url
     *
     *
     * This method does *NOT* update the current experience.
     */
    getResourcePathFromPreviewUrl(previewUrl: string): Promise<string>;
    /**
     * This method will create a new preview ticket, and return the given url with an updated previewTicketId query param
     *
     *
     * This method does *NOT* update the current experience.
     *
     * @param storefrontUrl Existing storefront url
     * @param previewData JSON representing storefront previewData (catalog, catalog vesion, etc...)
     *
     * @returns A new string with storefrontUrl having the new ticket ID inside
     */
    updateUrlWithNewPreviewTicketId(storefrontUrl: string, previewData: IPreviewData): Promise<string>;
}
