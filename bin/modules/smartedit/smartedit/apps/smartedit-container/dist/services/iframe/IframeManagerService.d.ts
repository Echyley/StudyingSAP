/// <reference types="jquery" />
import { HttpClient } from '@angular/common/http';
import { ISharedDataService, LogService, WindowUtils } from 'smarteditcommons';
import { DeviceOrientation } from './DeviceOrientationsValue';
import { DeviceSupport } from './DeviceSupportsValue';
/**
 * The iFrame Manager service provides methods to load the storefront into an iframe. The preview of the storefront can be loaded for a specified input homepage and a specified preview ticket. The iframe src attribute is updated with that information in order to display the storefront in SmartEdit.
 */
export declare class IframeManagerService {
    private logService;
    private httpClient;
    private yjQuery;
    private windowUtils;
    private sharedDataService;
    private static readonly DEFAULT_PREVIEW_ROUTE;
    private currentLocation;
    constructor(logService: LogService, httpClient: HttpClient, yjQuery: JQueryStatic, windowUtils: WindowUtils, sharedDataService: ISharedDataService);
    /**
     * This method sets the current page location and stores it in the service. The storefront will be loaded with this location.
     *
     * @param URL Location to be stored
     */
    setCurrentLocation(location: string): void;
    getIframe(): JQuery;
    isCrossOrigin(): boolean;
    /**
     * This method loads the storefront within an iframe by setting the src attribute to the specified input URL.
     * If this method is called within the context of a new or updated experience, prior to the loading, it will check if the page exists.
     * If the pages does not exist (the server returns a 404 and a content-type:text/html), the user will be redirected to the homepage of the storefront. Otherwise,
     * the user will be redirected to the requested page for the experience.
     *
     * @param URL The URL of the storefront.
     * @param checkIfFailingHTML Boolean indicating if we need to check if the page call returns a 404
     * @param homepageInPreviewMode URL of the storefront homepage in preview mode if it's a new experience
     *
     */
    load(url: string, checkIfFailingHTML?: boolean, pageInPreviewMode?: string): Promise<void>;
    /**
     * This method loads the preview of the storefront for a specified input homepage URL or a page from the page list, and for a specified preview ticket.
     * This method will add '/cx-preview' as specified in configuration.storefrontPreviewRoute to the URI and append the preview ticket in the query string.
     * <br/>If it is an initial load, [load]{@link IframeManagerService#load} will be called with this modified homepage or page from page list.
     * <br/>If it is a subsequent call, the modified homepage will be called through Ajax to initialize the preview (storefront constraint) and then
     * [load]{@link IframeManagerService#load} will be called with the current location.
     *
     * @param homePageOrPageFromPageList The URL of the storefront homepage or a page from the page list for a given experience context.
     * @param  previewTicket The preview ticket.
     */
    loadPreview(homePageOrPageFromPageList: string, previewTicket: string): Promise<void>;
    apply(deviceSupport?: DeviceSupport, deviceOrientation?: DeviceOrientation): void;
    applyDefault(): void;
    private _mustLoadAsSuch;
    private _getPageAsync;
    private _appendURISuffix;
}
