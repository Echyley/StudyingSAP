/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, Inject, ChangeDetectorRef, ViewRef } from '@angular/core';
import {
    GenericEditorWidgetData,
    GENERIC_EDITOR_WIDGET_DATA,
    CmsitemsRestService
} from 'smarteditcommons';
import { PageInfoForViewing } from '../../pages/services';
import { RestrictionCMSItem } from '../../restrictions/types';

@Component({
    selector: 'se-restrictions-list',
    templateUrl: './RestrictionsListComponent.html',
    styleUrls: ['./RestrictionsListComponent.scss']
})
export class RestrictionsListComponent {
    public pageInfo: PageInfoForViewing;
    public restrictions: RestrictionCMSItem[];

    constructor(
        @Inject(GENERIC_EDITOR_WIDGET_DATA)
        public data: GenericEditorWidgetData<PageInfoForViewing>,
        private cmsitemsRestService: CmsitemsRestService,
        private cdr: ChangeDetectorRef
    ) {
        ({ model: this.pageInfo } = data);
        this.restrictions = [];
    }

    async ngOnInit(): Promise<void> {
        const restrictionsData = await this.cmsitemsRestService.getByIdsNoCache<RestrictionCMSItem>(
            this.pageInfo.restrictions,
            'FULL'
        );
        this.restrictions = (
            restrictionsData.response ? restrictionsData.response : [restrictionsData]
        ) as RestrictionCMSItem[];

        if (!(this.cdr as ViewRef).destroyed) {
            this.cdr.detectChanges();
        }
    }
}
