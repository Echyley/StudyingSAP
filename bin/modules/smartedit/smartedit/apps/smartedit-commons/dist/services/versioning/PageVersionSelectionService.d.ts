import { TranslateService } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { CrossFrameEventService } from '../crossFrame/CrossFrameEventService';
import { IAlertService, IExperienceService, IPageInfoService, IPageVersion } from '../interfaces';
import { CMSModesService } from '../modes/CMSModesService';
import { PageVersioningService } from './PageVersioningService';
/**
 * This service is meant to be used internally by the page versions menu.
 * It allows selecting and deselecting a page version to be rendered in the Versioning Mode.
 */
export declare class PageVersionSelectionService {
    private readonly crossFrameEventService;
    private readonly alertService;
    private readonly experienceService;
    private readonly cMSModesService;
    private readonly pageInfoService;
    private readonly pageVersioningService;
    private readonly translateService;
    private readonly PAGE_VERSIONS_TOOLBAR_ITEM_KEY;
    private readonly PAGE_VERSION_UNSELECTED_MSG_KEY;
    private readonly selectedPageVersionSubject;
    private selectedPageVersion;
    private readonly unSubEventPerspectiveChanged;
    private readonly unSubEventPerspectiveRefreshed;
    private readonly unSubEventPageChange;
    private readonly unSubSelectedPageVersion;
    constructor(crossFrameEventService: CrossFrameEventService, alertService: IAlertService, experienceService: IExperienceService, cMSModesService: CMSModesService, pageInfoService: IPageInfoService, pageVersioningService: PageVersioningService, translateService: TranslateService);
    ngOnDestroy(): void;
    getSelectedPageVersion(): IPageVersion;
    getSelectedPageVersion$(): Observable<IPageVersion>;
    hideToolbarContextIfNotNeeded(): void;
    showToolbarContextIfNeeded(): void;
    selectPageVersion(version: IPageVersion): void;
    deselectPageVersion(showAlert?: boolean): void;
    updatePageVersionDetails(version: IPageVersion): void;
    /**
     * Required especially when a version is selected and you refresh the browser.
     */
    private initOnPageChange;
    private isSameVersion;
    private removePageVersionOnPerspectiveChange;
    private resetPageVersionContext;
}
