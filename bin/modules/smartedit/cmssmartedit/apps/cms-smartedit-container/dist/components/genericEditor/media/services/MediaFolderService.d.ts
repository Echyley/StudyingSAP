import { FetchStrategy, SelectItem, Page, RestServiceFactory, ISettingsService } from 'smarteditcommons';
/**
 * Interface for media folder
 */
export interface IMediaFolder {
    qualifier: string;
}
export interface MediaFolderPage extends Page<IMediaFolder> {
    mediaFolders: IMediaFolder[];
}
export declare type MediaFolderSelectItem = IMediaFolder & SelectItem;
export declare type MediaFolderFetchStrategy = FetchStrategy<MediaFolderSelectItem>;
/**
 * The MediaFolderService is used to access media folders which been created in backoffice
 */
export declare class MediaFolderService {
    private restServiceFactory;
    private settingsService;
    private readonly mediaFolderListService;
    private _mediaFolderFetchStrategy;
    constructor(restServiceFactory: RestServiceFactory, settingsService: ISettingsService);
    /**
     * Strategy necessary to display media folder in a paged way.
     * It contains a method to retrieve pages of media folder.
     * Such strategy is necessary to work with media folder in SelectComponent.
     */
    get mediaFoldersFetchStrategy(): MediaFolderFetchStrategy;
    getDefaultFolder(): Promise<string>;
    /**
     * Returns a list of media folders that match the given mask
     */
    private getMediaFolders;
    private mediaFoldersFetchPage;
}
