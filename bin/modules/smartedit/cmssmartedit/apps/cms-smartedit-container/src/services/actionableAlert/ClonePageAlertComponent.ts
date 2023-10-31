/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, ViewRef } from '@angular/core';
import { AlertRef } from '@fundamental-ngx/core';
import { take } from 'rxjs/operators';
import {
    ICatalogVersion,
    IExperienceService,
    L10nPipe,
    stringUtils,
    TypedMap,
    ICMSPage
} from 'smarteditcommons';

@Component({
    selector: 'se-clone-page-alert',
    template: `
        <se-actionable-alert
            [description]="description"
            [descriptionDetails]="descriptionDetails"
            [hyperlinkLabel]="hyperlinkLabel"
            (hyperLinkClick)="onClick()"></se-actionable-alert>
    `,
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [L10nPipe]
})
export class ClonePageAlertComponent {
    public description: string;
    public hyperlinkLabel: string;
    public descriptionDetails: TypedMap<any>;

    private catalogVersion: ICatalogVersion;
    private clonedPageInfo: ICMSPage;

    constructor(
        private alertRef: AlertRef,
        private experienceService: IExperienceService,
        private cdr: ChangeDetectorRef,
        private l10n: L10nPipe
    ) {
        this.description = 'se.cms.clonepage.alert.info.description';
        this.hyperlinkLabel = 'se.cms.clonepage.alert.info.hyperlink';
    }

    async ngOnInit(): Promise<void> {
        ({ clonedPageInfo: this.clonedPageInfo, catalogVersion: this.catalogVersion } =
            this.alertRef.data);

        this.description = 'se.cms.clonepage.alert.info.description';
        this.descriptionDetails = {
            catalogName: await this.getTranslatedName(this.catalogVersion.catalogName),
            catalogVersion: this.catalogVersion.version
        };
        this.hyperlinkLabel = 'se.cms.clonepage.alert.info.hyperlink';
        if (!(this.cdr as ViewRef).destroyed) {
            this.cdr.detectChanges();
        }
    }

    public onClick(): void {
        if (stringUtils.isBlank(this.clonedPageInfo.uid)) {
            throw new Error(
                "ClonePageAlertService.displayClonePageAlert - missing required parameter 'uid'"
            );
        }

        this.experienceService.loadExperience({
            siteId: this.catalogVersion.siteId,
            catalogId: this.catalogVersion.catalogId,
            catalogVersion: this.catalogVersion.version,
            pageId: this.clonedPageInfo.uid
        });
    }

    private getTranslatedName(name: TypedMap<string>): Promise<string> {
        return this.l10n.transform(name).pipe(take(1)).toPromise();
    }
}
