/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { CMSRestriction } from 'cmscommons';
import { LogService, ModalService } from 'smarteditcommons';
import { RestrictionsModalComponent } from './RestrictionsModalComponent';

@Component({
    selector: 'se-restrictions-viewer',
    templateUrl: './RestrictionsViewerComponent.html',
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class RestrictionsViewerComponent {
    @Input() restrictions: CMSRestriction[];

    constructor(private modalService: ModalService, private logService: LogService) {}

    public showRestrictions(event: Event): Promise<any> {
        event.preventDefault();

        return this.modalService
            .open({
                component: RestrictionsModalComponent,
                data: this.restrictions,
                templateConfig: {
                    title: 'se.cms.restrictionsviewer.title',
                    isDismissButtonVisible: true
                },
                config: {
                    dialogPanelClass: 'modal-dialog'
                }
            })
            .afterClosed.toPromise()
            .catch(() => {
                this.logService.warn('RestrictionsViewer - modal closed without any action');
            });
    }
}
