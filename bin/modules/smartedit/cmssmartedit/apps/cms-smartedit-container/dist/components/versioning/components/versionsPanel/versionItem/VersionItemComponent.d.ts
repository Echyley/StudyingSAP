import { IPerspectiveService, CMSModesService, PageVersionSelectionService, IPageVersion } from 'smarteditcommons';
export declare class VersionItemComponent {
    private pageVersionSelectionService;
    private perspectiveService;
    private cMSModesService;
    pageVersion: IPageVersion;
    private VERSIONING_MODE_KEY;
    constructor(pageVersionSelectionService: PageVersionSelectionService, perspectiveService: IPerspectiveService, cMSModesService: CMSModesService);
    selectVersion(): Promise<void>;
    isSelectedVersion(): boolean;
    isVersionMenuEnabled(): boolean;
}
