/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { BehaviorSubject, Observable, Subscription } from 'rxjs';
import {
    EVENT_PERSPECTIVE_CHANGED,
    EVENT_PERSPECTIVE_REFRESHED,
    HIDE_TOOLBAR_ITEM_CONTEXT,
    SHOW_TOOLBAR_ITEM_CONTEXT,
    EVENTS
} from '../../utils/smarteditconstants';
import { CrossFrameEventService } from '../crossFrame/CrossFrameEventService';
import {
    IAlertService,
    IExperience,
    IExperienceService,
    IPageInfoService,
    IPageVersion
} from '../interfaces';
import { CMSModesService } from '../modes/CMSModesService';
import { PageVersioningService } from './PageVersioningService';

/**
 * This service is meant to be used internally by the page versions menu.
 * It allows selecting and deselecting a page version to be rendered in the Versioning Mode.
 */
@Injectable()
export class PageVersionSelectionService {
    private readonly PAGE_VERSIONS_TOOLBAR_ITEM_KEY = 'se.cms.pageVersionsMenu';
    private readonly PAGE_VERSION_UNSELECTED_MSG_KEY = 'se.cms.versions.unselect.version';
    private readonly selectedPageVersionSubject = new BehaviorSubject<IPageVersion>(null);
    private selectedPageVersion: IPageVersion;
    private readonly unSubEventPerspectiveChanged: () => void;
    private readonly unSubEventPerspectiveRefreshed: () => void;
    private readonly unSubEventPageChange: () => void;
    private readonly unSubSelectedPageVersion: Subscription;

    constructor(
        private readonly crossFrameEventService: CrossFrameEventService,
        private readonly alertService: IAlertService,
        private readonly experienceService: IExperienceService,
        private readonly cMSModesService: CMSModesService,
        private readonly pageInfoService: IPageInfoService,
        private readonly pageVersioningService: PageVersioningService,
        private readonly translateService: TranslateService
    ) {
        this.unSubEventPerspectiveChanged = this.crossFrameEventService.subscribe(
            EVENT_PERSPECTIVE_CHANGED,
            () => this.removePageVersionOnPerspectiveChange()
        );

        this.unSubEventPerspectiveRefreshed = this.crossFrameEventService.subscribe(
            EVENT_PERSPECTIVE_REFRESHED,
            () => this.resetPageVersionContext()
        );

        this.unSubEventPageChange = this.crossFrameEventService.subscribe(
            EVENTS.PAGE_CHANGE,
            (_eventId: string, experience: IExperience) => this.initOnPageChange(experience)
        );

        this.unSubSelectedPageVersion = this.selectedPageVersionSubject.subscribe(
            (value) => (this.selectedPageVersion = value)
        );
    }

    ngOnDestroy(): void {
        this.unSubEventPerspectiveChanged();
        this.unSubEventPerspectiveRefreshed();
        this.unSubEventPageChange();
        this.unSubSelectedPageVersion.unsubscribe();
    }

    public getSelectedPageVersion(): IPageVersion {
        return this.selectedPageVersion;
    }

    public getSelectedPageVersion$(): Observable<IPageVersion> {
        return this.selectedPageVersionSubject.asObservable();
    }

    public hideToolbarContextIfNotNeeded(): void {
        if (!this.selectedPageVersion) {
            this.crossFrameEventService.publish(
                HIDE_TOOLBAR_ITEM_CONTEXT,
                this.PAGE_VERSIONS_TOOLBAR_ITEM_KEY
            );
        }
    }

    public showToolbarContextIfNeeded(): void {
        if (this.selectedPageVersion) {
            this.crossFrameEventService.publish(
                SHOW_TOOLBAR_ITEM_CONTEXT,
                this.PAGE_VERSIONS_TOOLBAR_ITEM_KEY
            );
        }
    }

    public selectPageVersion(version: IPageVersion): void {
        if (!this.isSameVersion(this.selectedPageVersion, version)) {
            this.selectedPageVersionSubject.next(version);
            this.experienceService.updateExperience({
                versionId: version.uid
            });

            this.showToolbarContextIfNeeded();
            this.crossFrameEventService.publish(EVENTS.PAGE_SELECTED);
        }
    }

    public deselectPageVersion(showAlert = true): void {
        if (this.selectedPageVersion && showAlert) {
            const msgTranslated = this.translateService.instant(
                this.PAGE_VERSION_UNSELECTED_MSG_KEY
            );
            this.alertService.showInfo(msgTranslated);
        }

        this.selectedPageVersionSubject.next(null);
        this.experienceService.updateExperience({
            versionId: null
        });

        this.crossFrameEventService.publish(
            HIDE_TOOLBAR_ITEM_CONTEXT,
            this.PAGE_VERSIONS_TOOLBAR_ITEM_KEY
        );
    }

    public updatePageVersionDetails(version: IPageVersion): void {
        this.selectedPageVersionSubject.next(version);
    }

    /**
     * Required especially when a version is selected and you refresh the browser.
     */
    private async initOnPageChange(experience: IExperience): Promise<void> {
        if (experience.versionId && !this.selectedPageVersion) {
            const pageUuid = await this.pageInfoService.getPageUUID();
            const version = await this.pageVersioningService.getPageVersionForId(
                pageUuid,
                String(experience.versionId)
            );
            this.selectedPageVersionSubject.next(version);

            this.showToolbarContextIfNeeded();

            this.crossFrameEventService.publish(EVENTS.PAGE_SELECTED);
        }
    }

    private isSameVersion(selectedPageVersion: IPageVersion, newVersion: IPageVersion): boolean {
        return selectedPageVersion !== null && newVersion !== null
            ? this.selectedPageVersion.uid === newVersion.uid
            : false;
    }

    private async removePageVersionOnPerspectiveChange(): Promise<void> {
        const isVersioningModeActive = await this.cMSModesService.isVersioningPerspectiveActive();
        if (this.selectedPageVersion) {
            const pageUuid = await this.pageInfoService.getPageUUID();
            if (!isVersioningModeActive || this.selectedPageVersion.itemUUID !== pageUuid) {
                this.deselectPageVersion();
            }
        }
    }

    private async resetPageVersionContext(): Promise<void> {
        const experience = await this.experienceService.getCurrentExperience();
        if (!experience.versionId && this.selectedPageVersion) {
            this.selectedPageVersionSubject.next(null);
            this.hideToolbarContextIfNotNeeded();
        } else {
            this.showToolbarContextIfNeeded();
        }
    }
}
