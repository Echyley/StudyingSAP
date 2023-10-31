/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { AlertRef } from '@fundamental-ngx/core';
import { IExperienceService, TypedMap } from 'smarteditcommons';

@Component({
    selector: 'se-page-restored-alert-component',
    changeDetection: ChangeDetectionStrategy.OnPush,
    template: `<se-actionable-alert
        [description]="message"
        [hyperlinkLabel]="hyperlinkLabel"
        [hyperlinkDetails]="hyperLinkDetails"
        (hyperLinkClick)="onClick()"></se-actionable-alert>`
})
export class PageRestoredAlertComponent implements OnInit {
    public hyperlinkLabel = 'se.cms.page.restored.alert.info.hyperlink';
    public hyperLinkDetails: TypedMap<string>;
    public message = 'se.cms.page.restored.alert.info.description';

    constructor(private alertRef: AlertRef, private experienceService: IExperienceService) {}

    ngOnInit(): void {
        const { pageInfo } = this.alertRef.data;
        this.hyperLinkDetails = { pageName: pageInfo.name };
    }

    public onClick(): void {
        const { catalogVersion, pageInfo } = this.alertRef.data;

        this.alertRef.dismiss();
        this.experienceService.loadExperience({
            siteId: catalogVersion.siteId,
            catalogId: catalogVersion.catalogId,
            catalogVersion: catalogVersion.version,
            pageId: pageInfo.uid
        });
    }
}
