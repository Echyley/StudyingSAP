/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ChangeDetectorRef, Component, OnInit, ViewRef } from '@angular/core';
import { DialogRef } from '@fundamental-ngx/core';
import { CMSRestriction } from 'cmscommons';
import { RestrictionCMSItem } from 'cmssmarteditcontainer/components/restrictions/types';
import { CmsitemsRestService } from 'smarteditcommons';

@Component({
    selector: 'se-restrictions-modal',
    templateUrl: './RestrictionsModalComponent.html',
    styleUrls: ['./RestrictionsModalComponent.scss']
})
export class RestrictionsModalComponent implements OnInit {
    public restrictions: CMSRestriction[];

    constructor(
        private modalRef: DialogRef,
        private cmsitemsRestService: CmsitemsRestService,
        private cdr: ChangeDetectorRef
    ) {}

    async ngOnInit(): Promise<void> {
        const restrictionsData = await this.cmsitemsRestService.getByIdsNoCache<RestrictionCMSItem>(
            this.modalRef.data.modalData,
            'FULL'
        );
        this.restrictions = (
            restrictionsData.response ? restrictionsData.response : [restrictionsData]
        ) as CMSRestriction[];
        if (!(this.cdr as ViewRef).destroyed) {
            this.cdr.detectChanges();
        }
    }
}
