/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import {
    IPerspectiveService,
    CMSModesService,
    PageVersionSelectionService,
    IPageVersion
} from 'smarteditcommons';

/**
 * Represents a page version entry in the list of versions.
 */
@Component({
    selector: 'se-version-item',
    templateUrl: './VersionItemComponent.html',
    styleUrls: ['./VersionItemComponent.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class VersionItemComponent {
    @Input() pageVersion: IPageVersion;

    private VERSIONING_MODE_KEY = CMSModesService.VERSIONING_PERSPECTIVE_KEY;

    constructor(
        private pageVersionSelectionService: PageVersionSelectionService,
        private perspectiveService: IPerspectiveService,
        private cMSModesService: CMSModesService
    ) {}

    public async selectVersion(): Promise<void> {
        const isVersioningModeActive = await this.cMSModesService.isVersioningPerspectiveActive();
        if (!isVersioningModeActive) {
            this.perspectiveService.switchTo(this.VERSIONING_MODE_KEY);
        }
        this.pageVersionSelectionService.selectPageVersion(this.pageVersion);
    }

    public isSelectedVersion(): boolean {
        const selectedVersion = this.pageVersionSelectionService.getSelectedPageVersion();
        return selectedVersion && selectedVersion.uid === this.pageVersion.uid;
    }

    public isVersionMenuEnabled(): boolean {
        const activePerspective = this.perspectiveService.getActivePerspective();
        return activePerspective && activePerspective.key === this.VERSIONING_MODE_KEY;
    }
}
