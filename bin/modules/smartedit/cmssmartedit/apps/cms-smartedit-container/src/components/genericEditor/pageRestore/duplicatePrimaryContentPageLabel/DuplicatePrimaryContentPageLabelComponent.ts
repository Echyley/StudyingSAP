/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, Inject } from '@angular/core';
import { GENERIC_EDITOR_WIDGET_DATA, GenericEditorWidgetData, ICMSPage } from 'smarteditcommons';

@Component({
    selector: 'se-component-duplicate-primary-content-page-label',
    templateUrl: './DuplicatePrimaryContentPageLabelComponent.html',
    styleUrls: ['./DuplicatePrimaryContentPageLabelComponent.scss']
})
export class DuplicatePrimaryContentPageLabelComponent {
    public conflictResolution: number = null;
    private page: ICMSPage;
    private readonly RESOLUTION_OPTIONS = {
        overwritePage: 1,
        renamePageLabel: 2
    };

    constructor(
        @Inject(GENERIC_EDITOR_WIDGET_DATA)
        public data: GenericEditorWidgetData<ICMSPage>
    ) {
        ({ model: this.page } = data);
        this.conflictResolution = this.RESOLUTION_OPTIONS.overwritePage;
        this.page.replace = true;
    }

    public selectResolution(resolutionSelected: number): void {
        this.page.replace = resolutionSelected === this.RESOLUTION_OPTIONS.overwritePage;
    }
}
