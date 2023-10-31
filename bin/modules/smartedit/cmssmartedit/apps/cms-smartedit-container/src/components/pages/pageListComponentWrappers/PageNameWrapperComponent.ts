/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, Inject, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { merge } from 'lodash';
import {
    IExperienceService,
    DATA_TABLE_COMPONENT_DATA,
    DataTableComponentData,
    IUriContext,
    IUrlService,
    ICMSPage
} from 'smarteditcommons';

@Component({
    selector: 'se-page-name-wrapper',
    template: `
        <a fd-link href="" (click)="goToPage($event)">
            <se-homepage-icon
                class="homepage-icon__page-list"
                [cmsPage]="item"
                [uriContext]="uriContext"></se-homepage-icon>
            {{ item.name }}
        </a>
    `
})
export class PageNameWrapperComponent implements OnInit {
    public item: ICMSPage;
    public uriContext: IUriContext;

    private siteUid: string;
    private catalogId: string;
    private catalogVersion: string;

    constructor(
        @Inject(DATA_TABLE_COMPONENT_DATA) public data: DataTableComponentData,
        private route: ActivatedRoute,
        private experienceService: IExperienceService,
        private urlService: IUrlService
    ) {}

    ngOnInit(): void {
        this.item = this.data.item as ICMSPage;
        ({
            siteId: this.siteUid,
            catalogId: this.catalogId,
            catalogVersion: this.catalogVersion
        } = this.route.snapshot.params);

        this.uriContext = merge(
            this.urlService.buildUriContext(this.siteUid, this.catalogId, this.catalogVersion),
            this.urlService.buildPageUriContext(this.siteUid, this.catalogId, this.catalogVersion)
        );
    }

    public goToPage(event: Event): void {
        event.preventDefault();

        if (this.item.uid) {
            this.experienceService.loadExperience({
                siteId: this.siteUid,
                catalogId: this.catalogId,
                catalogVersion: this.catalogVersion,
                pageId: this.item.uid
            });
        }
    }
}
